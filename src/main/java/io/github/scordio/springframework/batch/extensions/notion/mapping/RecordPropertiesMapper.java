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
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public class RecordPropertiesMapper<T extends Record> extends ConstructorBasedPropertiesMapper<T> {

	public RecordPropertiesMapper(Class<T> type) {
		super(type);
	}

	@SafeVarargs
	public RecordPropertiesMapper(T... reified) {
		this(getClassOf(reified));
	}

	@Override
	Constructor<T> getConstructor(Class<T> type) throws NoSuchMethodException {
		Class<?>[] parameterTypes = Arrays.stream(type.getRecordComponents()) //
				.map(RecordComponent::getType) //
				.toArray(Class[]::new);

		return ReflectionUtils.accessibleConstructor(type, parameterTypes);
	}

}
