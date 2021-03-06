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

import com.gs.collections.impl.test.Verify;
import org.junit.Test;

public class UnmodifiableCharBooleanHashMapTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAExjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAubXV0YWJsZS5wcmltaXRpdmUuVW5t\n"
                        + "b2RpZmlhYmxlQ2hhckJvb2xlYW5IYXNoTWFwAAAAAAAAAAECAAFMAANtYXB0ADxMY29tL2dzL2Nv\n"
                        + "bGxlY3Rpb25zL2FwaS9tYXAvcHJpbWl0aXZlL011dGFibGVDaGFyQm9vbGVhbk1hcDt4cHNyAEBj\n"
                        + "b20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAubXV0YWJsZS5wcmltaXRpdmUuQ2hhckJvb2xlYW5I\n"
                        + "YXNoTWFwAAAAAAAAAAEMAAB4cHcIAAAAAD8AAAB4",
                new UnmodifiableCharBooleanHashMap(new CharBooleanHashMap()));
    }
}