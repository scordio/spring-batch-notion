# Spring Batch Notion

This project provides a [Spring Batch][] extension module that adds support for [Notion][].

## Compatibility

Spring Batch Notion is based on Spring Batch 5 and tested on Spring Boot 3, thus requiring at least Java 17.

## Getting Started

### Maven

```xml
<dependency>
  <groupId>io.github.scordio</groupId>
  <artifactId>spring-batch-notion</artifactId>
  <version>${spring-batch-notion.version}</version>
</dependency>
```

### Gradle

```kotlin
implementation("io.github.scordio:spring-batch-notion:${springBatchNotionVersion}")
```

## NotionDatabaseItemReader

The `NotionDatabaseItemReader` is a restartable `ItemReader` that reads entries from a [Notion Database] via a paging technique.

A minimal configuration of the item reader is as follows:

```java
NotionDatabaseItemReader<Item> itemReader() {
    NotionDatabaseItemReader<Item> reader = new NotionDatabaseItemReader<>();
    reader.setToken(System.getenv("NOTION_TOKEN"));
    reader.setDatabaseId("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"); // UUID
    reader.setPropertiesMapper(new CustomPropertyMapper());
    return reader;
}
```

The following configuration options are available:

| Property         | Required | Default                     | Description                                                                                                               |
|------------------|----------|-----------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `baseUrl`        | no       | `https://api.notion.com/v1` | Base URL of the Notion API. A custom value can be provided for testing purposes (e.g., the URL of a [WireMock][] server). |
| `databaseId`     | yes      | -                           | UUID of the database to read from.                                                                                        |
| `pageSize`       | no       | `100`                       | Number of items to be read with each page. Must be greater than zero and less than or equal to 100.                       |
| `propertyMapper` | yes      | -                           | The `PropertyMapper` responsible for mapping properties of a Notion item into a Java object.                              |
| `sorts`          | no       | `null`                      | `Sort` conditions to order the returned items. Each condition is applied following the declaration order.                 |
| `token`          | yes      | -                           | The Notion integration token.                                                                                             |

In addition to the Notion-specific configuration, all the configuration options of the Spring Batch
[`AbstractPaginatedDataItemReader`](https://docs.spring.io/spring-batch/docs/current/api/org/springframework/batch/item/data/AbstractPaginatedDataItemReader.html)
are supported.

### PropertyMapper

The `NotionDatabaseItemReader` requires a `PropertyMapper` to map the properties of a Notion item into an object.

Currently, only properties of type [Title](https://developers.notion.com/reference/property-object#title)
and [Rich Text](https://developers.notion.com/reference/property-object#rich-text) are supported,
and both are converted to strings.

The following `PropertyMapper` implementations are provided out of the box.

| Name                        | Description                                                                                                                                                                |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `BeanWrapperPropertyMapper` | Supports JavaBeans. Requires a default constructor and expects the setter names to match the Notion item property names (case-insensitive).                                |
| `ConstructorPropertyMapper` | Supports types with a constructor with arguments. Requires the constructor to be unique and its argument names to match the Notion item property names (case-insensitive). |
| `RecordPropertyMapper`      | Supports Java records. It uses the record canonical constructor and requires the record component names to match the Notion item property names (case-insensitive).        |

All implementations above offer two constructors:
* One accepting the `Class` instance of the type to be mapped
* One without parameters, for cases where the type to be mapped can be inferred by the generic type of the variable or method enclosing the constructor declaration

In case none of the provided implementations is suitable, a custom one can be provided.

## NotionDatabaseItemWriter

Currently not provided but will be added in the future.

## License

The Spring Batch Notion is released under version 2.0 of the [Apache License][].

[Apache License]: https://www.apache.org/licenses/LICENSE-2.0
[Notion]: https://notion.so/
[Notion Database]: https://www.notion.so/help/category/databases
[Spring Batch]: https://github.com/spring-projects/spring-batch
[WireMock]: https://wiremock.org/
