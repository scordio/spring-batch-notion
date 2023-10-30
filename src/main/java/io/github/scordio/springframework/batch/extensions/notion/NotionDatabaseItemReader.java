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

import io.github.scordio.springframework.batch.extensions.notion.mapping.PropertyMapper;
import notion.api.v1.NotionClient;
import notion.api.v1.http.JavaNetHttpClient;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter;
import notion.api.v1.model.databases.query.sort.QuerySort;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.AbstractPaginatedDataItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Restartable {@link ItemReader} that reads entries from a Notion database via a paging
 * technique.
 * <p>
 * The query is executed using paged requests of a size specified in
 * {@link #setPageSize(int)}, which defaults to {@value #DEFAULT_PAGE_SIZE}. Additional
 * pages are requested as needed when the {@link #read()} method is called. On restart,
 * the reader will begin again at the same number item it left off at.
 * <p>
 * This implementation is thread-safe between calls to {@link #open(ExecutionContext)},
 * but remember to set <code>saveState</code> to <code>false</code> if used in a
 * multi-threaded environment (no restart available).
 *
 * @param <T> Type of item to be read
 */
public class NotionDatabaseItemReader<T> extends AbstractPaginatedDataItemReader<T> implements InitializingBean {

	private static final String DEFAULT_BASE_URL = "https://api.notion.com/v1";

	private static final int DEFAULT_PAGE_SIZE = 100;

	private String baseUrl;

	private String token;

	private String databaseId;

	private PropertyMapper<T> propertyMapper;

	private QueryTopLevelFilter filter;

	private List<QuerySort> sorts;

	private NotionClient client;

	private boolean hasMore;

	private String nextCursor;

	/**
	 * Create a new {@link NotionDatabaseItemReader} with the following defaults:
	 * <ul>
	 * <li>{@code baseUrl} = {@value #DEFAULT_BASE_URL}</li>
	 * <li>{@code pageSize} = {@value #DEFAULT_PAGE_SIZE}</li>
	 * </ul>
	 */
	public NotionDatabaseItemReader() {
		this.baseUrl = DEFAULT_BASE_URL;
		this.pageSize = DEFAULT_PAGE_SIZE;
	}

	/**
	 * The base URL of the Notion API.
	 * <p>
	 * Defaults to {@value #DEFAULT_BASE_URL}.
	 * <p>
	 * A custom value can be provided for testing purposes (e.g., the URL of a WireMock
	 * server).
	 * @param baseUrl the base URL
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = Objects.requireNonNull(baseUrl);
	}

	/**
	 * The Notion integration token.
	 * <p>
	 * Always required.
	 * @param token the token
	 */
	public void setToken(String token) {
		this.token = Objects.requireNonNull(token);
	}

	/**
	 * UUID of the database to read from.
	 * <p>
	 * Always required.
	 * @param databaseId the database UUID
	 */
	public void setDatabaseId(String databaseId) {
		this.databaseId = Objects.requireNonNull(databaseId);
	}

	/**
	 * The {@link PropertyMapper} responsible for mapping Notion item properties into a
	 * Java object.
	 * <p>
	 * Always required.
	 * @param propertyMapper the property mapper
	 */
	public void setPropertyMapper(PropertyMapper<T> propertyMapper) {
		this.propertyMapper = Objects.requireNonNull(propertyMapper);
	}

	/**
	 * {@link Filter} condition to limit the returned items.
	 * <p>
	 * If no filter is provided, all the items in the database will be returned.
	 * @param filter the {@link Filter} conditions
	 * @see Filter#where()
	 * @see Filter#where(Filter)
	 */
	public void setFilter(Filter filter) {
		this.filter = filter.toQueryTopLevelFilter();
	}

	/**
	 * {@link Sort} conditions to order the returned items.
	 * <p>
	 * Each condition is applied following the declaration order, i.e., earlier sorts take
	 * precedence over later ones.
	 * @param sorts the {@link Sort} conditions
	 * @see Sort#by(String)
	 * @see Sort#by(Sort.Timestamp)
	 */
	public void setSorts(Sort... sorts) {
		this.sorts = Stream.of(sorts).map(Sort::toQuerySort).toList();
	}

	/**
	 * The number of items to be read with each page.
	 * <p>
	 * Defaults to {@value #DEFAULT_PAGE_SIZE}.
	 * @param pageSize the number of items. Must be greater than 0 and less than or equal
	 * to 100.
	 */
	@Override
	public void setPageSize(int pageSize) {
		Assert.isTrue(pageSize <= 100, "pageSize must be less than or equal to 100");
		super.setPageSize(pageSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<T> doPageRead() {
		if (!hasMore) {
			return null;
		}

		QueryDatabaseRequest request = new QueryDatabaseRequest(databaseId);
		request.setFilter(filter);
		request.setSorts(sorts);
		request.setStartCursor(nextCursor);
		request.setPageSize(pageSize);

		QueryResults queryResults = client.queryDatabase(request);

		hasMore = queryResults.getHasMore();
		nextCursor = queryResults.getNextCursor();

		return queryResults.getResults()
			.stream()
			.map(NotionDatabaseItemReader::getProperties)
			.map(properties -> propertyMapper.map(properties))
			.iterator();
	}

	private static Map<String, String> getProperties(Page element) {
		return element.getProperties()
			.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(Entry::getKey, entry -> getPropertyValue(entry.getValue())));
	}

	private static String getPropertyValue(PageProperty property) {
		return switch (property.getType()) {
			case RichText -> getPlainText(property.getRichText());
			case Title -> getPlainText(property.getTitle());
			default -> throw new IllegalArgumentException("Unsupported type: " + property.getType());
		};
	}

	private static String getPlainText(List<RichText> texts) {
		return texts.isEmpty() ? "" : texts.get(0).getPlainText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doOpen() {
		client = new NotionClient(token);
		client.setHttpClient(new JavaNetHttpClient());
		client.setLogger(new Slf4jLogger());
		client.setBaseUrl(baseUrl);

		hasMore = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doClose() {
		client.close();
		client = null;

		hasMore = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void jumpToItem(int itemIndex) throws Exception {
		for (int i = 0; i < itemIndex; i++) {
			read();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() {
		Assert.state(token != null, "'token' must be set");
		Assert.state(databaseId != null, "'databaseId' must be set");
		Assert.state(propertyMapper != null, "'propertyMapper' must be set");
	}

}
