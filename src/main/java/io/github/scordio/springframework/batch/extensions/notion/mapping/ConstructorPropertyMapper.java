/*
 * Copyright © 2023 Stefano Cordio (stefano.cordio@gmail.com)
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
package io.github.scordio.springframework.batch.extensions.notion.mapping;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * {@link NotionPropertyMapper} implementation for types with a constructor with
 * arguments.
 * <p>
 * It requires the constructor to be unique and its argument names to match the Notion
 * item property names (case-insensitive).
 *
 * @param <T> the target type
 * @since 1.0
 */
public class ConstructorPropertyMapper<T> extends ConstructorBasedPropertyMapper<T> {

	/**
	 * Create a new {@link ConstructorPropertyMapper}.
	 * @param type type of the target object
	 */
	public ConstructorPropertyMapper(Class<T> type) {
		super(type);
	}

	/**
	 * Create a new {@link ConstructorPropertyMapper}.
	 * @param reified don't pass any values to it. It's a trick to detect the type you
	 * want to map to.
	 */
	@SafeVarargs
	public ConstructorPropertyMapper(T... reified) {
		this(ClassResolver.getClassOf(reified));
	}

	@SuppressWarnings("unchecked")
	@Override
	Constructor<T> getConstructor(Class<T> type) throws NoSuchMethodException {
		Constructor<?>[] constructors = type.getDeclaredConstructors();

		if (constructors.length == 0) {
			throw new NoSuchMethodException("No constructor found for type: " + type);
		}

		if (constructors.length > 1) {
			throw new NoSuchMethodException("Multiple constructors available: " + Arrays.toString(constructors));
		}

		return (Constructor<T>) constructors[0];
	}

}
