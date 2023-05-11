# Spring Batch Notion

This project provides a [Spring Batch][] extension module that adds support for [Notion][].

Currently, only an `ItemReader` is provided but more will be added in the future.

## Compatibility

Spring Batch Notion is based on Spring Batch 5 and tested on Spring Boot 3, thus requiring Java 17 or later.

## Getting Started

### Maven

```xml
<dependencies>
  <dependency>
    <groupId>io.github.scordio</groupId>
    <artifactId>spring-batch-notion</artifactId>
    <version>${spring-batch-notion.version}</version>
  </dependency>
</dependencies>
```

### Gradle

```kotlin
implementation("io.github.scordio:spring-batch-notion:${springBatchNotionVersion}")
```

## NotionDatabaseItemReader

The `NotionDatabaseItemReader` allows to read items from a [Notion Database].

A minimal configuration of the item reader is as follows:

```java
NotionDatabaseItemReader<Item> itemReader() {
    NotionDatabaseItemReader<Item> reader = new NotionDatabaseItemReader<>();
    reader.setToken(System.getenv("NOTION_TOKEN"));
    reader.setDatabaseId("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"); // UUID
    reader.setPropertiesMapper(new BeanWrapperPropertyMapper()); // assuming `Item` is a JavaBean, see also below
    return reader;
}
```

The following configuration options are available:

| Property         | Required | Default                     | Description                                                                                                                                         |
|------------------|----------|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `baseUrl`        | no       | `https://api.notion.com/v1` | The base URL to send all API requests. A different value can be provided for testing purposes (e.g., the URL of a [WireMock][] server).             |
| `databaseId`     | yes      |                             |                                                                                                                                                     |
| `pageSize`       | no       | 100                         | The number of items to be read with each page. Must be greater than zero and less than or equal to 100. Follows the [Notion pagination parameters]. |
| `propertyMapper` | yes      |                             |                                                                                                                                                     |
| `sorts`          | no       |                             |                                                                                                                                                     |
| `token`          | yes      |                             | The Notion integration token.                                                                                                                       |

In addition to the Notion specific configuration options, all the configuration options of the Spring Batch [`AbstractPaginatedDataItemReader`](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/item/data/AbstractPaginatedDataItemReader.html) are also supported.

## Property Mappers

The `NotionDatabaseItemReader` requires a `NotionPropertyMapper` to map the properties of a Notion item to an object.

The following `NotionPropertyMapper` implementations are provided out of the box.

| Name                        | Description                                                                                                                                                                |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `BeanWrapperPropertyMapper` | Supports JavaBeans. Requires a default constructor and expects the setter names to match the Notion item property names (case-insensitive).                                |
| `ConstructorPropertyMapper` | Supports types with a constructor with arguments. Requires the constructor to be unique and its argument names to match the Notion item property names (case-insensitive). |
| `RecordPropertyMapper`      | Supports Java records. It uses the record canonical constructor and requires the record component names to match the Notion item property names (case-insensitive).        |

All implementations expose two constructors:
* One accepting the `Class` instance of the type to be mapped
* One without parameters, for cases where the type to be mapped can be inferred by the generic type of the variable or method enclosing the constructor declaration

In case none of the provided implementations is suitable, a custom one can be provided.

## License

The Spring Batch Notion is released under version 2.0 of the [Apache License][].

[Apache License]: https://www.apache.org/licenses/LICENSE-2.0
[Notion]: https://notion.so/
[Notion Database]: https://www.notion.so/help/category/databases
[Notion pagination parameters]: https://developers.notion.com/reference/intro#parameters-for-paginated-requests
[Spring Batch]: https://github.com/spring-projects/spring-batch
[WireMock]: https://wiremock.org/
