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
package io.github.scordio.springframework.batch.extensions.notion.it;

import io.github.scordio.springframework.batch.extensions.notion.Sort.Direction;
import io.github.scordio.springframework.batch.extensions.notion.Sort.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class RequestBodies {

	public static String queryRequest(int pageSize, JSONObject... sorts) {
		return queryRequest(null, pageSize, sorts);
	}

	public static String queryRequest(UUID startCursor, int pageSize, JSONObject... sorts) {
		JSONObject jsonObject = new JSONObject();

		if (sorts.length > 0) {
			jsonObject.put("sorts", new JSONArray(sorts));
		}

		return jsonObject //
			.put("page_size", pageSize)
			.putOpt("start_cursor", startCursor != null ? startCursor.toString() : null)
			.toString();
	}

	public static JSONObject sortByProperty(String property, Direction direction) {
		return new JSONObject() //
			.put("property", property)
			.put("direction", direction.name().toLowerCase());
	}

	public static JSONObject sortByTimestamp(String property, Timestamp timestamp) {
		return new JSONObject() //
			.put("property", property)
			.put("timestamp", timestamp.name().toLowerCase());
	}

}
