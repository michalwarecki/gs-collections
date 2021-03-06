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

package com.gs.collections.impl.stack.mutable.primitive;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.block.procedure.primitive.BooleanProcedure;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.stack.primitive.MutableBooleanStack;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link UnmodifiableBooleanStack}.
 */
public class UnmodifiableBooleanStackTest
{
    private final MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true));

    @Test(expected = UnsupportedOperationException.class)
    public void push()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        stack.push(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void pop()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        stack.pop();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void popWithCount()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        stack.pop(2);
    }

    @Test
    public void peek()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        Verify.assertSize(5, stack);
        Assert.assertTrue(stack.peek());
    }

    @Test
    public void peekWithCount()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        Verify.assertSize(5, stack);
        Assert.assertEquals(BooleanArrayList.newListWith(), stack.peek(0));
        Assert.assertEquals(BooleanArrayList.newListWith(true, true), stack.peek(2));
    }

    @Test
    public void peekAtIndex()
    {
        MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
        Verify.assertSize(5, stack);
        Assert.assertTrue(stack.peekAt(0));
        Assert.assertTrue(stack.peekAt(1));
        Assert.assertFalse(stack.peekAt(2));
        Assert.assertTrue(stack.peekAt(3));
        Assert.assertFalse(stack.peekAt(4));
    }

    @Test(expected = EmptyStackException.class)
    public void peek_empty_stack_throws_exception()
    {
        new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom()).peek();
    }

    @Test(expected = IllegalArgumentException.class)
    public void peek_at_index_less_than_zero_throws_exception()
    {
        new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom()).peekAt(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void peek_at_index_greater_than_size_throws_exception()
    {
        new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom()).peekAt(1);
    }

    @Test
    public void newStackWith()
    {
        MutableBooleanStack stack1 = this.stack;
        Verify.assertSize(3, stack1);
        Assert.assertTrue(stack1.containsAll(true, false, true));
    }

    @Test
    public void newStack()
    {
        Assert.assertEquals(BooleanArrayStack.newStackWith(true, false, true), this.stack);
    }

    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(new UnmodifiableBooleanStack(new BooleanArrayStack()));
        Verify.assertNotEmpty(this.stack);
    }

    @Test
    public void notEmpty()
    {
        Assert.assertFalse(new UnmodifiableBooleanStack(new BooleanArrayStack()).notEmpty());
        Assert.assertTrue(this.stack.notEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clear()
    {
        this.stack.clear();
    }

    @Test
    public void containsAllArray()
    {
        MutableBooleanStack stack1 = this.stack;
        Assert.assertTrue(stack1.containsAll(true));
        Assert.assertTrue(stack1.containsAll(true, false, true));
        MutableBooleanStack emptyStack = new UnmodifiableBooleanStack(new BooleanArrayStack());
        Assert.assertFalse(emptyStack.containsAll(true));
    }

    @Test
    public void containsAllIterable()
    {
        MutableBooleanStack emptyStack = new UnmodifiableBooleanStack(new BooleanArrayStack());
        Assert.assertTrue(emptyStack.containsAll(new BooleanArrayList()));
        Assert.assertFalse(emptyStack.containsAll(BooleanArrayList.newListWith(true)));
    }

    @Test
    public void iterator()
    {
        BooleanIterator iterator = this.stack.booleanIterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertFalse(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertTrue(this.stack.booleanIterator().next());
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_throws()
    {
        BooleanIterator iterator = this.stack.booleanIterator();
        while (iterator.hasNext())
        {
            iterator.next();
        }

        iterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_throws_non_empty_list()
    {
        BooleanArrayStack modifiableStack = new BooleanArrayStack();
        MutableBooleanStack unmodifiableStack = new UnmodifiableBooleanStack(modifiableStack);
        modifiableStack.push(true);
        modifiableStack.push(true);
        modifiableStack.push(true);
        BooleanIterator iterator = unmodifiableStack.booleanIterator();
        while (iterator.hasNext())
        {
            Assert.assertTrue(iterator.next());
        }
        iterator.next();
    }

    @Test
    public void forEach()
    {
        final long[] sum = new long[1];
        this.stack.forEach(new BooleanProcedure()
        {
            public void value(boolean each)
            {
                sum[0] += each ? 1 : 0;
            }
        });

        Assert.assertEquals(2L, sum[0]);
    }

    @Test
    public void size()
    {
        Verify.assertSize(0, new UnmodifiableBooleanStack(new BooleanArrayStack()));
        Verify.assertSize(3, this.stack);
    }

    @Test
    public void count()
    {
        Assert.assertEquals(2L, new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true)).count(BooleanPredicates.isTrue()));
    }

    @Test
    public void anySatisfy()
    {
        Assert.assertTrue(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true)).anySatisfy(BooleanPredicates.isTrue()));
        Assert.assertFalse(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(false)).anySatisfy(BooleanPredicates.isTrue()));
    }

    @Test
    public void allSatisfy()
    {
        Assert.assertFalse(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false)).allSatisfy(BooleanPredicates.isTrue()));
        Assert.assertTrue(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, true, true)).allSatisfy(BooleanPredicates.isTrue()));
    }

    @Test
    public void noneSatisfy()
    {
        Assert.assertTrue(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, true)).noneSatisfy(BooleanPredicates.isFalse()));
        Assert.assertFalse(new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, true)).noneSatisfy(BooleanPredicates.isTrue()));
    }

    @Test
    public void select()
    {
        Verify.assertSize(2, this.stack.select(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.stack.select(BooleanPredicates.isFalse()));
    }

    @Test
    public void reject()
    {
        Verify.assertSize(1, this.stack.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(2, this.stack.reject(BooleanPredicates.isFalse()));
    }

    @Test
    public void detectIfNone()
    {
        Assert.assertFalse(this.stack.detectIfNone(BooleanPredicates.isFalse(), true));
        Assert.assertTrue(this.stack.detectIfNone(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), true));
    }

    @Test
    public void collect()
    {
        Assert.assertEquals(ArrayStack.newStackWith(1, 0, 1), this.stack.collect(new BooleanToObjectFunction<Object>()
        {
            public Object valueOf(boolean value)
            {
                return Integer.valueOf(value ? 1 : 0);
            }
        }));
    }

    @Test
    public void toArray()
    {
        Assert.assertTrue(Arrays.equals(new boolean[]{false, true}, new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false)).toArray()));
        Assert.assertTrue(Arrays.equals(new boolean[]{true, false, true, false},
                new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, false, true, false)).toArray()));
    }

    @Test
    public void testEquals()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true, false));
        MutableBooleanStack stack2 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true, false));
        MutableBooleanStack stack3 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(false, true, false, true));
        MutableBooleanStack stack4 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(false, false, true, true));
        MutableBooleanStack stack5 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, true, true));

        Verify.assertEqualsAndHashCode(stack1, stack2);
        Verify.assertPostSerializedEqualsAndHashCode(stack1);
        Assert.assertNotEquals(stack1, stack3);
        Assert.assertNotEquals(stack1, stack4);
        Assert.assertNotEquals(stack1, stack5);
    }

    @Test
    public void testHashCode()
    {
        Assert.assertEquals(ArrayStack.newStackWith(true, false, true).hashCode(),
                new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true)).hashCode());
        Assert.assertEquals(ArrayStack.newStack().hashCode(), new UnmodifiableBooleanStack(new BooleanArrayStack()).hashCode());
    }

    @Test
    public void testToString()
    {
        Assert.assertEquals("[]", new UnmodifiableBooleanStack(new BooleanArrayStack()).toString());
        Assert.assertEquals("[true]", new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true)).toString());
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false));
        Assert.assertEquals("[false, true]", stack1.toString());
        Assert.assertEquals("[true, false, true]", this.stack.toString());
    }

    @Test
    public void makeString()
    {
        Assert.assertEquals("true", new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true)).makeString("/"));
        Assert.assertEquals("", new UnmodifiableBooleanStack(new BooleanArrayStack()).makeString());
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false));
        Assert.assertEquals("false, true", stack1.makeString());
        Assert.assertEquals("false/true", stack1.makeString("/"));
        Assert.assertEquals("[false/true]", stack1.makeString("[", "/", "]"));
        Assert.assertEquals(this.stack.toString(), this.stack.makeString("[", ", ", "]"));
        Assert.assertEquals("true, false, true", this.stack.makeString());
        Assert.assertEquals("true/false/true", this.stack.makeString("/"));
    }

    @Test
    public void appendString()
    {
        StringBuilder appendable = new StringBuilder();
        new UnmodifiableBooleanStack(new BooleanArrayStack()).appendString(appendable);
        Assert.assertEquals("", appendable.toString());
        StringBuilder appendable1 = new StringBuilder();
        new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true)).appendString(appendable1);
        Assert.assertEquals("true", appendable1.toString());
        StringBuilder appendable2 = new StringBuilder();
        this.stack.appendString(appendable2);
        Assert.assertEquals("true, false, true", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.stack.appendString(appendable3, "/");
        Assert.assertEquals("true/false/true", appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.stack.appendString(appendable4, "[", ", ", "]");
        Assert.assertEquals(this.stack.toString(), appendable4.toString());
    }

    @Test
    public void toList()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false));
        Assert.assertEquals(BooleanArrayList.newListWith(false, true), stack1.toList());
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true), this.stack.toList());

    }

    @Test
    public void toSet()
    {
        Assert.assertEquals(BooleanHashSet.newSetWith(true, false, true), this.stack.toSet());
    }

    @Test
    public void toBag()
    {
        Assert.assertEquals(BooleanHashBag.newBagWith(true, false, true), this.stack.toBag());
    }

    @Test
    public void asLazy()
    {
        Assert.assertEquals(this.stack.toList(), this.stack.asLazy().toList());
    }

    @Test
    public void asUnmodifiable()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true));
        Assert.assertSame(stack1, stack1.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true));
        Assert.assertEquals(stack1, stack1.asSynchronized());
    }
}
