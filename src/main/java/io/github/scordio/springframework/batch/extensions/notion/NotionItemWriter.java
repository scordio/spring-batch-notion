package io.github.scordio.springframework.batch.extensions.notion;

import notion.api.v1.NotionClient;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamWriter;

import java.util.Objects;

public class NotionItemWriter<T> implements ItemStreamWriter<T> {

	private final NotionClient client;

	public NotionItemWriter(NotionClient client) {
		this.client = Objects.requireNonNull(client);
	}

	@Override
	public void write(Chunk<? extends T> chunk) throws Exception {
	}

}
