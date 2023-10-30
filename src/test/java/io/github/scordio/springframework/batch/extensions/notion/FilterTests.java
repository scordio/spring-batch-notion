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

import notion.api.v1.model.databases.query.filter.CompoundFilter;
import notion.api.v1.model.databases.query.filter.PropertyFilter;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.filter.condition.CheckboxFilter;
import notion.api.v1.model.databases.query.filter.condition.MultiSelectFilter;
import notion.api.v1.model.databases.query.filter.condition.NumberFilter;
import notion.api.v1.model.databases.query.filter.condition.SelectFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.scordio.springframework.batch.extensions.notion.Filter.where;
import static java.util.function.Function.identity;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FilterTests {

	@ParameterizedTest
	@MethodSource({ "propertyFilters", "compoundFilters", "nestedFilters" })
	void toQueryTopLevelFilter(Filter underTest, QueryTopLevelFilter expected) {
		// WHEN
		QueryTopLevelFilter result = underTest.toQueryTopLevelFilter();
		// THEN
		then(result).usingRecursiveComparison().isEqualTo(expected);
	}

	static Stream<Arguments> propertyFilters() {
		return Stream.of( //
				checkboxFilters(), //
				multiSelectFilters(), //
				numberFilters(), //
				selectFilters()) //
			.flatMap(identity());
	}

	static Stream<Arguments> checkboxFilters() {
		return Stream.of(true, false)
			.flatMap(value -> Stream.of( //
					arguments( //
							where().checkbox("property").isEqualTo(value), //
							supply(() -> {
								CheckboxFilter checkboxFilter = new CheckboxFilter();
								checkboxFilter.setEquals(value);
								PropertyFilter propertyFilter = new PropertyFilter("property");
								propertyFilter.setCheckbox(checkboxFilter);
								return propertyFilter;
							})),
					arguments( //
							where().checkbox("property").isNotEqualTo(value), //
							supply(() -> {
								CheckboxFilter checkboxFilter = new CheckboxFilter();
								checkboxFilter.setDoesNotEqual(value);
								PropertyFilter propertyFilter = new PropertyFilter("property");
								propertyFilter.setCheckbox(checkboxFilter);
								return propertyFilter;
							}))));
	}

	static Stream<Arguments> multiSelectFilters() {
		return Stream.of( //
				arguments( //
						where().multiSelect("property").contains("value"), //
						supply(() -> {
							MultiSelectFilter multiSelectFilter = new MultiSelectFilter();
							multiSelectFilter.setContains("value");
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setMultiSelect(multiSelectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().multiSelect("property").doesNotContain("value"), //
						supply(() -> {
							MultiSelectFilter multiSelectFilter = new MultiSelectFilter();
							multiSelectFilter.setDoesNotContain("value");
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setMultiSelect(multiSelectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().multiSelect("property").isEmpty(), //
						supply(() -> {
							MultiSelectFilter multiSelectFilter = new MultiSelectFilter();
							multiSelectFilter.setEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setMultiSelect(multiSelectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().multiSelect("property").isNotEmpty(), //
						supply(() -> {
							MultiSelectFilter multiSelectFilter = new MultiSelectFilter();
							multiSelectFilter.setNotEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setMultiSelect(multiSelectFilter);
							return propertyFilter;
						})));
	}

	static Stream<Arguments> numberFilters() {
		return Stream.of( //
				arguments( //
						where().number("property").isEqualTo(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setEquals(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isNotEqualTo(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setDoesNotEqual(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isGreaterThan(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setGreaterThan(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isGreaterThanOrEqualTo(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setGreaterThanOrEqualTo(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isLessThan(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setLessThan(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isLessThanOrEqualTo(42), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setLessThanOrEqualTo(42);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isEmpty(), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})),
				arguments( //
						where().number("property").isNotEmpty(), //
						supply(() -> {
							NumberFilter numberFilter = new NumberFilter();
							numberFilter.setNotEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setNumber(numberFilter);
							return propertyFilter;
						})));
	}

	static Stream<Arguments> selectFilters() {
		return Stream.of( //
				arguments( //
						where().select("property").isEqualTo("value"), //
						supply(() -> {
							SelectFilter selectFilter = new SelectFilter();
							selectFilter.setEquals("value");
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setSelect(selectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().select("property").isNotEqualTo("value"), //
						supply(() -> {
							SelectFilter selectFilter = new SelectFilter();
							selectFilter.setDoesNotEqual("value");
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setSelect(selectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().select("property").isEmpty(), //
						supply(() -> {
							SelectFilter selectFilter = new SelectFilter();
							selectFilter.setEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setSelect(selectFilter);
							return propertyFilter;
						})),
				arguments( //
						where().select("property").isNotEmpty(), //
						supply(() -> {
							SelectFilter selectFilter = new SelectFilter();
							selectFilter.setNotEmpty(true);
							PropertyFilter propertyFilter = new PropertyFilter("property");
							propertyFilter.setSelect(selectFilter);
							return propertyFilter;
						})));
	}

	static Stream<Arguments> compoundFilters() {
		return Stream.of(andFilters(), orFilters()).flatMap(identity());
	}

	static Stream<Arguments> andFilters() {
		return Stream.of( //
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
							.and().select("another").isNotEmpty(), //
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setAnd(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})),
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
							.and(where().select("another").isNotEmpty()),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setAnd(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})),
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
							.and(where().select("another").isNotEmpty())
							.and().checkbox("one-more").isNotEqualTo(true)
							.and(where().select("another-more").isEmpty()),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setAnd(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setDoesNotEqual(true);
										PropertyFilter propertyFilter = new PropertyFilter("one-more");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another-more");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})));
	}

	static Stream<Arguments> orFilters() {
		return Stream.of( //
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
							.or().select("another").isNotEmpty(),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setOr(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})),
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
							.or(where().select("another").isNotEmpty()),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setOr(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})),
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(false)
								.or(where().select("another").isNotEmpty())
								.or().checkbox("one-more").isNotEqualTo(true)
								.or(where().select("another-more").isEmpty()),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setOr(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(false);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setNotEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setDoesNotEqual(true);
										PropertyFilter propertyFilter = new PropertyFilter("one-more");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("another-more");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})));
	}

	static Stream<Arguments> nestedFilters() {
		return Stream.of( //
				arguments( // @formatter:off
						where(where().checkbox("active").isEqualTo(true)),
						// @formatter:on
						supply(() -> {
							CheckboxFilter checkboxFilter = new CheckboxFilter();
							checkboxFilter.setEquals(true);
							PropertyFilter propertyFilter = new PropertyFilter("active");
							propertyFilter.setCheckbox(checkboxFilter);
							return propertyFilter;
						})),
				arguments( // @formatter:off
						where().checkbox("active").isEqualTo(true)
							.and(where().select("another").isEmpty().or().select("another").isEqualTo("value")),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setAnd(List.of( //
									supply(() -> {
										CheckboxFilter checkboxFilter = new CheckboxFilter();
										checkboxFilter.setEquals(true);
										PropertyFilter propertyFilter = new PropertyFilter("active");
										propertyFilter.setCheckbox(checkboxFilter);
										return propertyFilter;
									}), //
									supply(() -> {
										CompoundFilter innerFilter = new CompoundFilter();

										innerFilter.setOr(List.of( //
												supply(() -> {
													SelectFilter selectFilter = new SelectFilter();
													selectFilter.setEmpty(true);
													PropertyFilter propertyFilter = new PropertyFilter("another");
													propertyFilter.setSelect(selectFilter);
													return propertyFilter;
												}), //
												supply(() -> {
													SelectFilter selectFilter = new SelectFilter();
													selectFilter.setEquals("value");
													PropertyFilter propertyFilter = new PropertyFilter("another");
													propertyFilter.setSelect(selectFilter);
													return propertyFilter;
												})));

										return innerFilter;
									})));

							return compoundFilter;
						})),
				arguments( // @formatter:off
						where(where().checkbox("active").isEqualTo(false)
								.or().select("another").isNotEmpty())
							.and().select("one-more").isEmpty(),
						// @formatter:on
						supply(() -> {
							CompoundFilter compoundFilter = new CompoundFilter();

							compoundFilter.setAnd(List.of( //
									supply(() -> {
										CompoundFilter innerFilter = new CompoundFilter();

										innerFilter.setOr(List.of( //
												supply(() -> {
													CheckboxFilter checkboxFilter = new CheckboxFilter();
													checkboxFilter.setEquals(false);
													PropertyFilter propertyFilter = new PropertyFilter("active");
													propertyFilter.setCheckbox(checkboxFilter);
													return propertyFilter;
												}), //
												supply(() -> {
													SelectFilter selectFilter = new SelectFilter();
													selectFilter.setNotEmpty(true);
													PropertyFilter propertyFilter = new PropertyFilter("another");
													propertyFilter.setSelect(selectFilter);
													return propertyFilter;
												})));

										return innerFilter;
									}), //
									supply(() -> {
										SelectFilter selectFilter = new SelectFilter();
										selectFilter.setEmpty(true);
										PropertyFilter propertyFilter = new PropertyFilter("one-more");
										propertyFilter.setSelect(selectFilter);
										return propertyFilter;
									})));

							return compoundFilter;
						})));
	}

	private static <T> T supply(Supplier<T> supplier) {
		return supplier.get();
	}

}
