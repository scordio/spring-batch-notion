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

import io.github.scordio.springframework.batch.extensions.notion.mapping.TestData.AllPropertiesSource;
import io.github.scordio.springframework.batch.extensions.notion.mapping.TestData.PartialPropertiesSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.from;
import static org.assertj.core.api.BDDAssertions.then;

class RecordPropertyMapperTests {

	@Nested
	class using_record_without_additional_constructors {

		private record TestRecord(String field1, String field2) {
		}

		@ParameterizedTest
		@AllPropertiesSource
		void should_map_all_properties(Map<String, ?> properties) {
			// GIVEN
			NotionPropertiesMapper<TestRecord> underTest = new RecordPropertiesMapper<>(TestRecord.class);
			// WHEN
			TestRecord result = underTest.map(properties);
			// THEN
			then(result) //
					.returns("Value1", from(TestRecord::field1)) //
					.returns("Value2", from(TestRecord::field2));
		}

		@ParameterizedTest
		@AllPropertiesSource
		void should_map_all_properties_without_type_parameter(Map<String, ?> properties) {
			// GIVEN
			NotionPropertiesMapper<TestRecord> underTest = new RecordPropertiesMapper<>();
			// WHEN
			TestRecord result = underTest.map(properties);
			// THEN
			then(result) //
					.returns("Value1", from(TestRecord::field1)) //
					.returns("Value2", from(TestRecord::field2));
		}

		@Test
		void should_fail_with_vararg_constructor_parameter() {
			// WHEN
			Throwable thrown = catchThrowable(() -> new RecordPropertiesMapper<>(new TestRecord("value", "value")));
			// THEN
			then(thrown) //
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Please don't pass any values here. The generic type will be detected automagically.");
		}

		@ParameterizedTest
		@PartialPropertiesSource
		void should_map_partial_properties(Map<String, ?> properties) {
			// GIVEN
			NotionPropertiesMapper<TestRecord> underTest = new RecordPropertiesMapper<>(TestRecord.class);
			// WHEN
			TestRecord result = underTest.map(properties);
			// THEN
			then(result) //
					.returns("Value1", from(TestRecord::field1)) //
					.returns(null, from(TestRecord::field2));
		}

	}

	@Nested
	class using_record_with_additional_constructors {

		private record TestRecord(String field1, String field2) {

			@SuppressWarnings("unused")
			private TestRecord() {
				this(null, null);
			}

			@SuppressWarnings("unused")
			private TestRecord(String field1) {
				this(field1, null);
			}

		}

		@ParameterizedTest
		@AllPropertiesSource
		void should_map_all_properties(Map<String, ?> properties) {
			// GIVEN
			NotionPropertiesMapper<TestRecord> underTest = new RecordPropertiesMapper<>(TestRecord.class);
			// WHEN
			TestRecord result = underTest.map(properties);
			// THEN
			then(result) //
					.returns("Value1", from(TestRecord::field1)) //
					.returns("Value2", from(TestRecord::field2));
		}

	}

}
