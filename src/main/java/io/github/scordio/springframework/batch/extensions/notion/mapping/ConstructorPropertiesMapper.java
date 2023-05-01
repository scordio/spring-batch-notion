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

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorPropertiesMapper<T> extends ConstructorBasedPropertiesMapper<T> {

	public ConstructorPropertiesMapper(Class<T> type) {
		super(type);
	}

	@SafeVarargs
	public ConstructorPropertiesMapper(T... reified) {
		this(getClassOf(reified));
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

		Constructor<T> constructor = (Constructor<T>) constructors[0];

		ReflectionUtils.makeAccessible(constructor);

		return constructor;
	}

}
