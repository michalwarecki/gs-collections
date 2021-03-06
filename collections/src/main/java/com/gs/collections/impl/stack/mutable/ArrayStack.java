/*
 * Copyright 2013 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.stack.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.IntObjectToIntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.LongObjectToLongFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.api.partition.stack.PartitionStack;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.stack.ImmutableStack;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.api.stack.StackIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.procedure.MutatingAggregationProcedure;
import com.gs.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.partition.stack.PartitionArrayStack;
import com.gs.collections.impl.utility.LazyIterate;

/**
 * ArrayStack is a MutableStack which contains a FastList of data. ArrayStack iterates from top to bottom (LIFO order).
 * It behaves like FastList in terms of runtime complexity. The method push() is amortized constant time like
 * FastList.add(). The backing data structure grows and shrinks by 50% at a time, and size is constant. ArrayStack does
 * not extend Vector, as does the Java Stack, which was one of the reasons to create this data structure.
 */
public class ArrayStack<T> implements MutableStack<T>, Externalizable
{
    private static final long serialVersionUID = 1L;
    private FastList<T> delegate;

    public ArrayStack()
    {
        this.delegate = FastList.newList();
    }

    public ArrayStack(int initialCapacity)
    {
        this.delegate = FastList.newList(initialCapacity);
    }

    public ArrayStack(Iterable<T> items)
    {
        this.delegate = FastList.newList(items);
    }

    public ArrayStack(T... items)
    {
        this.delegate = FastList.wrapCopy(items);
    }

    public static <T> ArrayStack<T> newStack()
    {
        return new ArrayStack<T>();
    }

    public static <T> ArrayStack<T> newStack(Iterable<? extends T> items)
    {
        return new ArrayStack<T>((Iterable<T>) items);
    }

    public static <T> ArrayStack<T> newStackWith(T... items)
    {
        return new ArrayStack<T>(items);
    }

    public static <T> ArrayStack<T> newStackFromTopToBottom(T... items)
    {
        ArrayStack<T> stack = new ArrayStack<T>(items.length);
        for (int i = items.length - 1; i >= 0; i--)
        {
            stack.push(items[i]);
        }
        return stack;
    }

    public static <T> ArrayStack<T> newStackFromTopToBottom(Iterable<? extends T> items)
    {
        ArrayStack<T> stack = newStack();
        stack.delegate = FastList.newList(items).reverseThis();
        return stack;
    }

    public void push(T item)
    {
        this.delegate.add(item);
    }

    public T pop()
    {
        this.checkEmptyStack();
        return this.delegate.remove(this.delegate.size() - 1);
    }

    private void checkEmptyStack()
    {
        if (this.delegate.isEmpty())
        {
            throw new EmptyStackException();
        }
    }

    public ListIterable<T> pop(int count)
    {
        this.checkNegativeCount(count);
        MutableList<T> result = FastList.newList(count);
        if (this.checkZeroCount(count))
        {
            return result;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            result.add(this.pop());
            count--;
        }
        return result;
    }

    public <R extends Collection<T>> R pop(int count, R targetCollection)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return targetCollection;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            targetCollection.add(this.pop());
            count--;
        }
        return targetCollection;
    }

    public <R extends MutableStack<T>> R pop(int count, R targetStack)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return targetStack;
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        while (count > 0)
        {
            targetStack.push(this.pop());
            count--;
        }
        return targetStack;
    }

    public void clear()
    {
        this.delegate.clear();
    }

    private boolean checkZeroCount(int count)
    {
        return count == 0;
    }

    public T peek()
    {
        this.checkEmptyStack();
        return this.delegate.getLast();
    }

    public ListIterable<T> peek(int count)
    {
        this.checkNegativeCount(count);
        if (this.checkZeroCount(count))
        {
            return FastList.newList();
        }
        this.checkEmptyStack();
        this.checkSizeLessThanCount(count);
        return FastList.newList(this.asLazy().take(count));
    }

    public T peekAt(int index)
    {
        this.checkNegativeCount(index);
        this.checkEmptyStack();
        this.checkSizeLessThanCount(index);
        return this.delegate.get(this.delegate.size() - 1 - index);
    }

    public int size()
    {
        return this.delegate.asReversed().size();
    }

    public boolean isEmpty()
    {
        return this.delegate.asReversed().isEmpty();
    }

    public boolean notEmpty()
    {
        return this.delegate.asReversed().notEmpty();
    }

    public T getFirst()
    {
        return this.peek();
    }

    public T getLast()
    {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object object)
    {
        return this.delegate.contains(object);
    }

    public boolean containsAllIterable(Iterable<?> source)
    {
        return this.delegate.containsAllIterable(source);
    }

    public boolean containsAll(Collection<?> source)
    {
        return this.delegate.containsAll(source);
    }

    public boolean containsAllArguments(Object... elements)
    {
        return this.delegate.containsAllArguments(elements);
    }

    public <V> ArrayStack<V> collect(Function<? super T, ? extends V> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collect(function));
    }

    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collect(function, target);
    }

    public <V> ArrayStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().collectIf(predicate, function).toList());
    }

    public <V, R extends Collection<V>> R collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().collectIf(predicate, function, target);
    }

    public <P, V, R extends Collection<V>> R collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().collectWith(function, parameter, targetCollection);
    }

    public <V> ArrayStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().flatCollect(function).toList());
    }

    public <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().flatCollect(function, target);
    }

    public ArrayStack<T> select(Predicate<? super T> predicate)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().select(predicate).toList());
    }

    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().select(predicate, target);
    }

    public <S> ArrayStack<S> selectInstancesOf(Class<S> clazz)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().selectInstancesOf(clazz).toList());
    }

    public <P, R extends Collection<T>> R selectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().selectWith(predicate, parameter, targetCollection);
    }

    public ArrayStack<T> reject(Predicate<? super T> predicate)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().reject(predicate).toList());
    }

    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        return this.delegate.asReversed().reject(predicate, target);
    }

    public <P, R extends Collection<T>> R rejectWith(Predicate2<? super T, ? super P> predicate, P parameter, R targetCollection)
    {
        return this.delegate.asReversed().rejectWith(predicate, parameter, targetCollection);
    }

    public T detect(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().detect(predicate);
    }

    public T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        return this.delegate.asReversed().detectIfNone(predicate, function);
    }

    public PartitionStack<T> partition(Predicate<? super T> predicate)
    {
        PartitionArrayStack<T> partitionMutableStack = new PartitionArrayStack<T>(predicate);
        this.delegate.asReversed().forEach(new PartitionArrayStack.PartitionProcedure<T>(predicate, partitionMutableStack));
        return partitionMutableStack;
    }

    public <S> ArrayStack<Pair<T, S>> zip(Iterable<S> that)
    {
        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(that).toList());
    }

    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return this.delegate.asReversed().zip(that, target);
    }

    public ArrayStack<Pair<T, Integer>> zipWithIndex()
    {
        int maxIndex = this.delegate.size() - 1;
        Interval indicies = Interval.fromTo(0, maxIndex);

        return ArrayStack.newStackFromTopToBottom(this.delegate.asReversed().zip(indicies).toList());
    }

    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return this.delegate.asReversed().zipWithIndex(target);
    }

    public int count(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().count(predicate);
    }

    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().anySatisfy(predicate);
    }

    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().allSatisfy(predicate);
    }

    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return this.delegate.asReversed().noneSatisfy(predicate);
    }

    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return this.delegate.asReversed().injectInto(injectedValue, function);
    }

    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> intObjectToIntFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, intObjectToIntFunction);
    }

    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> longObjectToLongFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, longObjectToLongFunction);
    }

    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> doubleObjectToDoubleFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, doubleObjectToDoubleFunction);
    }

    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> floatObjectToFloatFunction)
    {
        return this.delegate.asReversed().injectInto(injectedValue, floatObjectToFloatFunction);
    }

    public long sumOfInt(IntFunction<? super T> intFunction)
    {
        return this.delegate.asReversed().sumOfInt(intFunction);
    }

    public double sumOfFloat(FloatFunction<? super T> floatFunction)
    {
        return this.delegate.asReversed().sumOfFloat(floatFunction);
    }

    public long sumOfLong(LongFunction<? super T> longFunction)
    {
        return this.delegate.asReversed().sumOfLong(longFunction);
    }

    public double sumOfDouble(DoubleFunction<? super T> doubleFunction)
    {
        return this.delegate.asReversed().sumOfDouble(doubleFunction);
    }

    public T max()
    {
        return this.delegate.asReversed().max();
    }

    public T max(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().max(comparator);
    }

    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().maxBy(function);
    }

    public T min()
    {
        return this.delegate.asReversed().min();
    }

    public T min(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().min(comparator);
    }

    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toList().minBy(function);
    }

    public String makeString()
    {
        return this.delegate.asReversed().makeString();
    }

    public String makeString(String separator)
    {
        return this.delegate.asReversed().makeString(separator);
    }

    public String makeString(String start, String separator, String end)
    {
        return this.delegate.asReversed().makeString(start, separator, end);
    }

    public void appendString(Appendable appendable)
    {
        this.delegate.asReversed().appendString(appendable);
    }

    public void appendString(Appendable appendable, String separator)
    {
        this.delegate.asReversed().appendString(appendable, separator);
    }

    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        this.delegate.asReversed().appendString(appendable, start, separator, end);
    }

    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.<V, T>newMultimap());
    }

    public <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target)
    {
        return this.delegate.asReversed().groupBy(function, target);
    }

    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.<V, T>newMultimap());
    }

    public <V, R extends MutableMultimap<V, T>> R groupByEach(Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return this.delegate.asReversed().groupByEach(function, target);
    }

    public RichIterable<RichIterable<T>> chunk(int size)
    {
        return this.delegate.asReversed().chunk(size);
    }

    public void forEach(Procedure<? super T> procedure)
    {
        this.delegate.reverseForEach(procedure);
    }

    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.delegate.asReversed().forEachWith(procedure, parameter);
    }

    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.delegate.asReversed().forEachWithIndex(objectIntProcedure);
    }

    public MutableList<T> toList()
    {
        return this.delegate.asReversed().toList();
    }

    public MutableList<T> toSortedList()
    {
        return this.delegate.asReversed().toSortedList();
    }

    public MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedList(comparator);
    }

    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toSortedListBy(function);
    }

    public MutableSet<T> toSet()
    {
        return this.delegate.asReversed().toSet();
    }

    public MutableSortedSet<T> toSortedSet()
    {
        return this.delegate.asReversed().toSortedSet();
    }

    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        return this.delegate.asReversed().toSortedSet(comparator);
    }

    public MutableStack<T> toStack()
    {
        return ArrayStack.newStackFromTopToBottom(this);
    }

    public ImmutableStack<T> toImmutable()
    {
        throw new UnsupportedOperationException();
    }

    public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.delegate.asReversed().toSortedSetBy(function);
    }

    public MutableBag<T> toBag()
    {
        return this.delegate.asReversed().toBag();
    }

    public <NK, NV> MutableMap<NK, NV> toMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toMap(keyFunction, valueFunction);
    }

    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(keyFunction, valueFunction);
    }

    public <NK, NV> MutableSortedMap<NK, NV> toSortedMap(Comparator<? super NK> comparator, Function<? super T, ? extends NK> keyFunction, Function<? super T, ? extends NV> valueFunction)
    {
        return this.delegate.asReversed().toSortedMap(comparator, keyFunction, valueFunction);
    }

    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    public MutableStack<T> asUnmodifiable()
    {
        return UnmodifiableStack.of(this);
    }

    public MutableStack<T> asSynchronized()
    {
        return SynchronizedStack.of(this);
    }

    public Object[] toArray()
    {
        return this.delegate.asReversed().toArray();
    }

    public <T> T[] toArray(T[] a)
    {
        return this.delegate.asReversed().toArray(a);
    }

    public Iterator<T> iterator()
    {
        return this.delegate.asReversed().iterator();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof StackIterable<?>))
        {
            return false;
        }

        StackIterable<?> that = (StackIterable<?>) o;

        if (that instanceof ArrayStack<?>)
        {
            return this.delegate.equals(((ArrayStack<?>) that).delegate);
        }
        Iterator<T> thisIterator = this.iterator();
        Iterator<?> thatIterator = that.iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext())
        {
            if (!Comparators.nullSafeEquals(thisIterator.next(), thatIterator.next()))
            {
                return false;
            }
        }
        return !thisIterator.hasNext() && !thatIterator.hasNext();
    }

    @Override
    public String toString()
    {
        return this.delegate.asReversed().makeString("[", ", ", "]");
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (T each : this)
        {
            hashCode = 31 * hashCode + (each == null ? 0 : each.hashCode());
        }
        return hashCode;
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        for (T each : this.delegate.asReversed())
        {
            out.writeObject(each);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        T[] array = (T[]) new Object[size];
        for (int i = size - 1; i >= 0; i--)
        {
            array[i] = (T) in.readObject();
        }
        this.delegate = FastList.newListWith(array);
    }

    private void checkSizeLessThanCount(int count)
    {
        if (this.delegate.size() < count)
        {
            throw new IllegalArgumentException("Count must be less than size: Count = " + count + " Size = " + this.delegate.size());
        }
    }

    private void checkNegativeCount(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be positive but was " + count);
        }
    }

    public <K, V> MutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        this.forEach(new MutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    public <K, V> MutableMap<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        this.forEach(new NonMutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map;
    }
}
