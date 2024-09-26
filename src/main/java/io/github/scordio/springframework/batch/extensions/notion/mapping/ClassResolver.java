/*
 * Copyright © 2024 Stefano Cordio
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

class ClassResolver {

	@SuppressWarnings("unchecked")
	static <T> Class<T> getClassOf(T[] reified) {
		Assert.isTrue(reified.length == 0,
				"Please don't pass any values here. The type will be detected automagically.");

		return (Class<T>) reified.getClass().getComponentType();
	}

}
