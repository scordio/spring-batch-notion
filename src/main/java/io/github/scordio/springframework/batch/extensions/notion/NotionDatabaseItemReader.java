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

import io.github.scordio.springframework.batch.extensions.notion.mapping.NotionPropertiesMapper;
import notion.api.v1.NotionClient;
import notion.api.v1.http.JavaNetHttpClient;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotionDatabaseItemReader<T> extends AbstractPaginatedDataItemReader<T> implements InitializingBean {

	private String baseUrl = NotionClient.getDefaultBaseUrl();

	private String token;

	private NotionPropertiesMapper<T> propertiesMapper;

	private String databaseId;

	private List<QuerySort> sorts;

	private NotionClient client;

	private boolean hasMore;

	private String nextCursor;

	public void setToken(String token) {
		this.token = token;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setPropertiesMapper(NotionPropertiesMapper<T> propertiesMapper) {
		this.propertiesMapper = propertiesMapper;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public void setSorts(Sort... sorts) {
		this.sorts = Stream.of(sorts).map(Sort::toNotionSort).toList();
	}

	@Override
	public void setPageSize(int pageSize) {
		Assert.isTrue(pageSize <= 100, "pageSize must be less than or equal to 100");
		super.setPageSize(pageSize);
	}

	@Override
	protected Iterator<T> doPageRead() {
		if (!hasMore) {
			return null;
		}

		QueryDatabaseRequest request = new QueryDatabaseRequest(databaseId);
		// request.setFilter(filter); TODO
		request.setSorts(sorts);
		request.setStartCursor(nextCursor);
		request.setPageSize(pageSize);

		QueryResults queryResults = client.queryDatabase(request);

		hasMore = queryResults.getHasMore();
		nextCursor = queryResults.getNextCursor();

		return queryResults.getResults().stream() //
				.map(NotionDatabaseItemReader::getProperties) //
				.map(properties -> propertiesMapper.map(properties)) //
				.iterator();
	}

	private static Map<String, ?> getProperties(Page element) {
		return element.getProperties().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> getPropertyValue(entry.getValue())));
	}

	private static String getPropertyValue(PageProperty property) {
		return property.getTitle() != null ? getPlainText(property.getTitle()) : getPlainText(property.getRichText());
	}

	private static String getPlainText(List<RichText> texts) {
		return texts.isEmpty() ? "" : texts.get(0).getPlainText();
	}

	@Override
	protected void doOpen() {
		client = new NotionClient(token);
		client.setHttpClient(new JavaNetHttpClient());
		client.setLogger(new Slf4jLogger());
		client.setBaseUrl(baseUrl);

		hasMore = true;
	}

	@Override
	protected void doClose() {
		client.close();
	}

	@Override
	public void afterPropertiesSet() {
		Assert.state(databaseId != null, "'databaseId' must be set");
		Assert.state(propertiesMapper != null, "'propertyMapper' must be set");
		Assert.state(token != null, "'token' must be set");
	}

}
