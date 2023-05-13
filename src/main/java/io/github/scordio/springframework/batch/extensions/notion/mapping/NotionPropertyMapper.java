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

import java.util.Map;

/**
 * Strategy interface for mapping Notion item properties into an object.
 *
 * @param <T> the object type
 */
@FunctionalInterface
public interface NotionPropertyMapper<T> {

	/**
	 * Map the given item properties into an object of type {@code T}.
	 * @param properties property value objects, keyed by property name
	 * @return the populated object
	 */
	T map(Map<String, ?> properties);

}
