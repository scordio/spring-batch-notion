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
package io.github.scordio.springframework.batch.extensions.notion;

import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.databases.query.sort.QuerySortDirection;
import notion.api.v1.model.databases.query.sort.QuerySortTimestamp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.github.scordio.springframework.batch.extensions.notion.Sort.Direction.ASCENDING;
import static io.github.scordio.springframework.batch.extensions.notion.Sort.Direction.DESCENDING;
import static io.github.scordio.springframework.batch.extensions.notion.Sort.Timestamp.CREATED_TIME;
import static io.github.scordio.springframework.batch.extensions.notion.Sort.Timestamp.LAST_EDITED_TIME;
import static notion.api.v1.model.databases.query.sort.QuerySortDirection.Ascending;
import static notion.api.v1.model.databases.query.sort.QuerySortDirection.Descending;
import static notion.api.v1.model.databases.query.sort.QuerySortTimestamp.CreatedTime;
import static notion.api.v1.model.databases.query.sort.QuerySortTimestamp.LastEditedTime;
import static org.assertj.core.api.BDDAssertions.from;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SortTests {

	@ParameterizedTest
	@MethodSource
	void toNotionSort(Sort underTest, String property, QuerySortTimestamp timestamp, QuerySortDirection direction) {
		// WHEN
		QuerySort result = underTest.toNotionSort();
		// THEN
		then(result) //
			.returns(direction, from(QuerySort::getDirection)) //
			.returns(property, from(QuerySort::getProperty)) //
			.returns(timestamp, from(QuerySort::getTimestamp));
	}

	static Stream<Arguments> toNotionSort() {
		return Stream.of( //
				arguments(Sort.by("property"), "property", null, Ascending),
				arguments(Sort.by("property", ASCENDING), "property", null, Ascending),
				arguments(Sort.by("property", DESCENDING), "property", null, Descending),
				arguments(Sort.by(CREATED_TIME), null, CreatedTime, Ascending),
				arguments(Sort.by(CREATED_TIME, ASCENDING), null, CreatedTime, Ascending),
				arguments(Sort.by(CREATED_TIME, DESCENDING), null, CreatedTime, Descending),
				arguments(Sort.by(LAST_EDITED_TIME), null, LastEditedTime, Ascending),
				arguments(Sort.by(LAST_EDITED_TIME, ASCENDING), null, LastEditedTime, Ascending),
				arguments(Sort.by(LAST_EDITED_TIME, DESCENDING), null, LastEditedTime, Descending));
	}

}
