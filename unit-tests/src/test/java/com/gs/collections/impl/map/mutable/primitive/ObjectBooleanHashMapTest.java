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

package com.gs.collections.impl.map.mutable.primitive;

import java.lang.reflect.Field;
import java.util.BitSet;

import com.gs.collections.api.block.function.primitive.BooleanToBooleanFunction;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class ObjectBooleanHashMapTest extends AbstractMutableObjectBooleanHashMapTestCase
{
    @Override
    protected ObjectBooleanHashMap<String> classUnderTest()
    {
        return ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4)
    {
        return ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    protected <T> ObjectBooleanHashMap<T> getEmptyMap()
    {
        return new ObjectBooleanHashMap<T>();
    }

    private final ObjectBooleanHashMap<String> map = this.classUnderTest();

    @Test
    public void defaultInitialCapacity() throws Exception
    {
        Field keys = ObjectBooleanHashMap.class.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = ObjectBooleanHashMap.class.getDeclaredField("values");
        values.setAccessible(true);

        ObjectBooleanHashMap<String> hashMap = new ObjectBooleanHashMap<String>();
        Assert.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
    }

    @Test
    public void newWithInitialCapacity() throws Exception
    {
        Field keys = ObjectBooleanHashMap.class.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = ObjectBooleanHashMap.class.getDeclaredField("values");
        values.setAccessible(true);

        ObjectBooleanHashMap<String> hashMap = new ObjectBooleanHashMap<String>(3);
        Assert.assertEquals(8L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());

        ObjectBooleanHashMap<String> hashMap2 = new ObjectBooleanHashMap<String>(15);
        Assert.assertEquals(32L, ((Object[]) keys.get(hashMap2)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newWithInitialCapacity_negative_throws()
    {
        new ObjectBooleanHashMap<String>(-1);
    }

    @Test
    public void newMap() throws Exception
    {
        Field keys = ObjectBooleanHashMap.class.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = ObjectBooleanHashMap.class.getDeclaredField("values");
        values.setAccessible(true);

        ObjectBooleanHashMap<String> hashMap = ObjectBooleanHashMap.newMap();
        Assert.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
        Assert.assertEquals(new ObjectBooleanHashMap<String>(), hashMap);
    }

    @Test
    public void removeKeyIfAbsent()
    {
        ObjectBooleanHashMap<String> map0 = ObjectBooleanHashMap.newWithKeysValues("0", false, "1", true);
        Assert.assertTrue(map0.removeKeyIfAbsent("1", false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues("0", false), map0);
        Assert.assertFalse(map0.removeKeyIfAbsent("0", true));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), map0);
        Assert.assertFalse(map0.removeKeyIfAbsent("1", false));
        Assert.assertTrue(map0.removeKeyIfAbsent("0", true));

        ObjectBooleanHashMap<String> map1 = ObjectBooleanHashMap.newWithKeysValues("0", true, "1", false);
        Assert.assertTrue(map1.removeKeyIfAbsent("0", false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues("1", false), map1);
        Assert.assertFalse(map1.removeKeyIfAbsent("1", true));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), map1);
        Assert.assertFalse(map1.removeKeyIfAbsent("0", false));
        Assert.assertTrue(map1.removeKeyIfAbsent("1", true));

        Assert.assertTrue(this.map.removeKeyIfAbsent("5", true));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false), this.map);
        Assert.assertTrue(this.map.removeKeyIfAbsent("0", false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues("1", true, "2", false), this.map);
        Assert.assertTrue(this.map.removeKeyIfAbsent("1", false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues("2", false), this.map);
        Assert.assertFalse(this.map.removeKeyIfAbsent("2", true));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        Assert.assertFalse(this.map.removeKeyIfAbsent("0", false));
        Assert.assertFalse(this.map.removeKeyIfAbsent("1", false));
        Assert.assertTrue(this.map.removeKeyIfAbsent("2", true));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        Verify.assertEmpty(this.map);

        this.map.put(null, true);

        Assert.assertTrue(this.map.get(null));
        Assert.assertTrue(this.map.removeKeyIfAbsent(null, false));
        Assert.assertFalse(this.map.get(null));
    }

    @Test
    public void putWithRehash() throws Exception
    {
        ObjectBooleanHashMap<Integer> hashMap = ObjectBooleanHashMap.newMap();
        for (int i = 2; i < 10; i++)
        {
            Assert.assertFalse(hashMap.containsKey(i));
            hashMap.put(i, (i & 1) == 0);
        }

        Field keys = ObjectBooleanHashMap.class.getDeclaredField("keys");
        Field values = ObjectBooleanHashMap.class.getDeclaredField("values");
        keys.setAccessible(true);
        values.setAccessible(true);
        Assert.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
        Verify.assertSize(8, hashMap);
        for (int i = 2; i < 10; i++)
        {
            Assert.assertTrue(hashMap.containsKey(i));
        }

        Assert.assertTrue(hashMap.containsValue(false));
        Assert.assertTrue(hashMap.containsValue(true));
        hashMap.put(10, true);
        Assert.assertEquals(32L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(64L, ((BitSet) values.get(hashMap)).size());

        for (int i = 11; i < 75; i++)
        {
            Assert.assertFalse(String.valueOf(i), hashMap.containsKey(i));
            hashMap.put(i, (i & 1) == 0);
        }
        Assert.assertEquals(256L, ((Object[]) keys.get(hashMap)).length);
        Assert.assertEquals(256L, ((BitSet) values.get(hashMap)).size());
    }

    @Test
    public void getIfAbsentPut()
    {
        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newMap();
        Assert.assertTrue(map1.getIfAbsentPut(0, true));
        Assert.assertTrue(map1.getIfAbsentPut(0, false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true), map1);
        Assert.assertTrue(map1.getIfAbsentPut(1, true));
        Assert.assertTrue(map1.getIfAbsentPut(1, false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true, 1, true), map1);

        ObjectBooleanHashMap<Integer> map2 = ObjectBooleanHashMap.newMap();
        Assert.assertFalse(map2.getIfAbsentPut(1, false));
        Assert.assertFalse(map2.getIfAbsentPut(1, true));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false), map2);
        Assert.assertFalse(map2.getIfAbsentPut(0, false));
        Assert.assertFalse(map2.getIfAbsentPut(0, true));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map2);

        ObjectBooleanHashMap<Integer> map3 = ObjectBooleanHashMap.newMap();
        Assert.assertTrue(map3.getIfAbsentPut(null, true));
        Assert.assertTrue(map3.getIfAbsentPut(null, false));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, true), map3);
    }

    @Test
    public void updateValue()
    {
        BooleanToBooleanFunction flip = new BooleanToBooleanFunction()
        {
            public boolean valueOf(boolean value)
            {
                return !value;
            }
        };

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newMap();
        Assert.assertTrue(map1.updateValue(0, false, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true), map1);
        Assert.assertFalse(map1.updateValue(0, false, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false), map1);
        Assert.assertFalse(map1.updateValue(1, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map1);
        Assert.assertTrue(map1.updateValue(1, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, true), map1);

        ObjectBooleanHashMap<Integer> map2 = ObjectBooleanHashMap.newMap();
        Assert.assertTrue(map2.updateValue(1, false, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true), map2);
        Assert.assertFalse(map2.updateValue(1, false, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false), map2);
        Assert.assertFalse(map2.updateValue(0, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map2);
        Assert.assertTrue(map2.updateValue(0, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true, 1, false), map2);

        ObjectBooleanHashMap<Integer> map3 = ObjectBooleanHashMap.newMap();
        Assert.assertFalse(map3.updateValue(null, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, false), map3);
        Assert.assertTrue(map3.updateValue(null, true, flip));
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, true), map3);
    }

    @Override
    @Test
    public void withKeysValues()
    {
        super.withKeysValues();
        ObjectBooleanHashMap<Integer> hashMap0 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, true, 2, false);
        ObjectBooleanHashMap<Integer> hashMap1 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, false, 2, false, 3, true);
        ObjectBooleanHashMap<Integer> hashMap2 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, true, 2, true, 3, false, 4, false);
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false), hashMap0);
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false, 2, false, 3, true), hashMap1);
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true, 2, true, 3, false, 4, false), hashMap2);
    }
}
