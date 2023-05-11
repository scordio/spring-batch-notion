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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.lang.reflect.Constructor;

/**
 * {@link NotionPropertyMapper} implementation that supports JavaBeans.
 * <p>
 * It requires a default constructor and expects the setter names to match the Notion item
 * property names (case-insensitive).
 *
 * @param <T> the target type
 * @since 1.0
 */
public class BeanWrapperPropertyMapper<T> extends CaseInsensitivePropertyMapper<T> {

	private final Constructor<T> constructor;

	/**
	 * Create a new {@link BeanWrapperPropertyMapper}.
	 * @param type type of the target object
	 */
	public BeanWrapperPropertyMapper(Class<T> type) {
		this.constructor = BeanUtils.getResolvableConstructor(type);
	}

	/**
	 * Create a new {@link BeanWrapperPropertyMapper}.
	 * @param reified don't pass any values to it. It's a trick to detect the type you
	 * want to map to.
	 */
	@SafeVarargs
	public BeanWrapperPropertyMapper(T... reified) {
		this(ClassResolver.getClassOf(reified));
	}

	@Override
	T mapCaseInsensitive(LinkedCaseInsensitiveMap<?> properties) {
		T instance = BeanUtils.instantiateClass(constructor);
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(instance);
		beanWrapper.setPropertyValues(properties);
		return instance;
	}

}
