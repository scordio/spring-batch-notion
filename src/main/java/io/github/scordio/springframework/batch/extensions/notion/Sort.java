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

import java.util.Objects;

/**
 * Sort conditions to order the entries returned from a database query. The direction
 * defaults to {@link Direction#DEFAULT_DIRECTION}.
 */
public abstract sealed class Sort {

	public static final Direction DEFAULT_DIRECTION = Direction.ASCENDING;

	/**
	 * Sort condition that orders the database query by a particular property.
	 * @param property the name of the property to sort against
	 * @param direction the {@code Direction} to sort
	 * @return the {@code Sort} instance
	 */
	public static Sort by(String property, Direction direction) {
		return new PropertySort(property, direction);
	}

	/**
	 * Sort condition that orders the database query by a particular property, in
	 * ascending direction.
	 * @param property the name of the property to sort against
	 * @return the {@code Sort} instance
	 */
	public static Sort by(String property) {
		return new PropertySort(property, DEFAULT_DIRECTION);
	}

	/**
	 * Sort condition that orders the database query by the timestamp associated with a
	 * database entry.
	 * @param timestamp the {@code Timestamp} to sort against
	 * @param direction the {@code Direction} to sort
	 * @return the {@code Sort} instance
	 */
	public static Sort by(Timestamp timestamp, Direction direction) {
		return new TimestampSort(timestamp, direction);
	}

	/**
	 * Sort condition that orders the database query by the timestamp associated with a
	 * database entry, in ascending direction.
	 * @param timestamp the {@code Timestamp timestamp} to sort against
	 * @return the {@code Sort} instance
	 */
	public static Sort by(Timestamp timestamp) {
		return new TimestampSort(timestamp, DEFAULT_DIRECTION);
	}

	/**
	 * Timestamps associated with database entries.
	 */
	public enum Timestamp {

		/**
		 * The time the entry was created.
		 */
		CREATED_TIME(QuerySortTimestamp.CreatedTime),

		/**
		 * The time the entry was last edited.
		 */
		LAST_EDITED_TIME(QuerySortTimestamp.LastEditedTime);

		private final QuerySortTimestamp notionTimestamp;

		Timestamp(QuerySortTimestamp notionTimestamp) {
			this.notionTimestamp = notionTimestamp;
		}

		private QuerySortTimestamp getNotionTimestamp() {
			return notionTimestamp;
		}

	}

	/**
	 * Sort directions.
	 */
	public enum Direction {

		/**
		 * Ascending direction.
		 */
		ASCENDING(QuerySortDirection.Ascending),

		/**
		 * Descending direction.
		 */
		DESCENDING(QuerySortDirection.Descending);

		private final QuerySortDirection notionDirection;

		Direction(QuerySortDirection notionDirection) {
			this.notionDirection = notionDirection;
		}

		private QuerySortDirection getNotionDirection() {
			return notionDirection;
		}

	}

	abstract QuerySort toNotionSort();

	private static final class PropertySort extends Sort {

		private final String property;

		private final Direction direction;

		private PropertySort(String property, Direction direction) {
			this.property = Objects.requireNonNull(property);
			this.direction = Objects.requireNonNull(direction);
		}

		@Override
		QuerySort toNotionSort() {
			return new QuerySort(property, null, direction.getNotionDirection());
		}

		@Override
		public String toString() {
			return "%s: %s".formatted(property, direction);
		}

	}

	private static final class TimestampSort extends Sort {

		private final Timestamp timestamp;

		private final Direction direction;

		private TimestampSort(Timestamp timestamp, Direction direction) {
			this.timestamp = Objects.requireNonNull(timestamp);
			this.direction = Objects.requireNonNull(direction);
		}

		@Override
		QuerySort toNotionSort() {
			return new QuerySort(null, timestamp.getNotionTimestamp(), direction.getNotionDirection());
		}

		@Override
		public String toString() {
			return "%s: %s".formatted(timestamp, direction);
		}

	}

}
