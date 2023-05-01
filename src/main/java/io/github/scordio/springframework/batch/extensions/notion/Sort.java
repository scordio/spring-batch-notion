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

public class Sort {

	public static final Direction DEFAULT_DIRECTION = Direction.ASC;

	private final String property;
	private final Timestamp timestamp;
	private final Direction direction;

	public static Sort by(String property, Direction direction) {
		return new Sort(property, null, direction);
	}

	public static Sort by(String property) {
		return new Sort(property, null, DEFAULT_DIRECTION);
	}

	public static Sort by(Timestamp timestamp, Direction direction) {
		return new Sort(null, timestamp, direction);
	}

	public static Sort by(Timestamp timestamp) {
		return new Sort(null, timestamp, DEFAULT_DIRECTION);
	}

	private Sort(String property, Timestamp timestamp, Direction direction) {
		this.property = property;
		this.timestamp = timestamp;
		this.direction = direction;
	}

	QuerySort toNotionSort() {
		return new QuerySort(property, timestamp != null ? timestamp.getNotionTimestamp() : null,
				direction != null ? direction.getNotionDirection() : null);
	}

	public enum Timestamp {

		CREATED_TIME(QuerySortTimestamp.CreatedTime), LAST_EDITED_TIME(QuerySortTimestamp.LastEditedTime);

		private final QuerySortTimestamp notionTimestamp;

		Timestamp(QuerySortTimestamp notionTimestamp) {
			this.notionTimestamp = notionTimestamp;
		}

		QuerySortTimestamp getNotionTimestamp() {
			return notionTimestamp;
		}

	}

	public enum Direction {

		ASC(QuerySortDirection.Ascending), DESC(QuerySortDirection.Descending);

		private final QuerySortDirection notionDirection;

		Direction(QuerySortDirection notionDirection) {
			this.notionDirection = notionDirection;
		}

		QuerySortDirection getNotionDirection() {
			return notionDirection;
		}

	}

}
