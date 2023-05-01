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

import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

abstract class ConstructorBasedPropertiesMapper<T> implements NotionPropertiesMapper<T> {

	private final Constructor<T> constructor;

	ConstructorBasedPropertiesMapper(Class<T> type) {
		try {
			this.constructor = getConstructor(type);
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	abstract Constructor<T> getConstructor(Class<T> type) throws NoSuchMethodException;

	@Override
	public T map(Map<String, ?> properties) {
		try {
			Map<String, Object> caseInsensitiveProperties = new LinkedCaseInsensitiveMap<>(properties.size());
			caseInsensitiveProperties.putAll(properties);

			Object[] parameterValues = Arrays.stream(constructor.getParameters()) //
					.map(Parameter::getName) //
					.map(caseInsensitiveProperties::get) //
					.toArray();

			return constructor.newInstance(parameterValues);
		}
		catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> Class<T> getClassOf(T[] reified) {
		Assert.isTrue(reified.length == 0,
				"Please don't pass any values here. The generic type will be detected automagically.");

		return (Class<T>) reified.getClass().getComponentType();
	}

}
