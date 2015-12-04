/**
 * Copyright (C) 2015 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.sbox.db.dto.util;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * Simple ID generator using short class name and integer sequence number.
 *
 * @author Piotr Wydrych
 * @version 1.0
 */
public class ClassNameAndIntSequenceGenerator extends ObjectIdGenerator<String> {

    private final Class<?> scope;
    private transient int nextValue;

    public ClassNameAndIntSequenceGenerator() {
        this(Object.class, 1);
    }

    public ClassNameAndIntSequenceGenerator(Class<?> scope, int nextValue) {
        this.scope = scope;
        this.nextValue = nextValue;
    }

    @Override
    public final Class<?> getScope() {
        return scope;
    }

    @Override
    public boolean canUseFor(ObjectIdGenerator<?> gen) {
        return (gen.getClass() == getClass()) && (gen.getScope() == scope);
    }

    @Override
    public ObjectIdGenerator<String> forScope(Class<?> scope) {
        return (this.scope == scope) ? this : new ClassNameAndIntSequenceGenerator(scope, nextValue);
    }

    @Override
    public ObjectIdGenerator<String> newForSerialization(Object context) {
        return new ClassNameAndIntSequenceGenerator(scope, 1);
    }

    @Override
    public IdKey key(Object key) {
        return new IdKey(getClass(), scope, key);
    }

    @Override
    public String generateId(Object forPojo) {
        int id = nextValue;
        ++nextValue;
        return scope.getSimpleName() + "@" + id;
    }
}
