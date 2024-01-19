<p>
  <img src="https://github.com/sannonaragao/HiperBoot/assets/19274273/3c9ca2bd-eb98-4f18-80af-c3836f9c1518" width="100" height="100" alt="HiperBoot Logo" style="float: left; margin-right: 10px;" />
  <span style="font-size: 24px; line-height: 100px; font-weight: bold;">HiperBoot: </span>
  <span style="font-size: 24px; line-height: 100px;">Powerful with simplicity and efficiency</span>
</p>

## Introduction

If you're working with Spring and JPA, there are two things you should know:
1) You've always needed something like HiperBoot.
2) From now on, it will be an essential part of your projects.


HiperBoot is an innovative library designed to enhance your interaction with Hibernate mappings. It simplifies the process of executing queries, offering advanced features like filtering, pagination, and sorting. Built for compatibility with multiple SQL databases, HiperBoot ensures optimized queries and robust protection against SQL Injection attacks.

## Installation and Getting Started

### Integrating HiperBoot into Your Project

Integrating HiperBoot into your Spring Boot application is straightforward. You can add HiperBoot to your project using Maven or Gradle.

#### Maven

```xml
<dependency>
    <groupId>io.github.sannonaragao</groupId>
    <name>hiperboot</name>
    <version>latest.release</version>
</dependency>
```

#### Gradle

```gradle
implementation group: 'io.github.sannonaragao', name: 'hiperboot', version: 'latest.release'
```

### Setting Up

To start using HiperBoot, include the following essential setup in your Spring Boot application:

```java
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = HiperBootRepositoryFactoryBean.class)
public class HiperbootExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiperbootExampleApplication.class, args);
    }
}
```
This configuration is vital as it sets up the necessary repository factory bean class for HiperBoot.

Last thing to do is simply extend from the HiperBootRepository interface, mirroring the familiar Spring repository pattern. This extension not only equips your repository with all the standard JpaRepository methods, but also enriches it with HiperBoot's advanced features.

```java
package com.hiperbootexample.repository;

import com.hiperboot.db.repository.HiperBootRepository;
import com.hiperbootexample.entity.Book;

public interface BookRepository extends HiperBootRepository<Book, Long> {
}
```


## Overview

HiperBoot enhances the capability of Spring Data JPA by extending the JpaRepository interface. It adds additional query methods and mechanisms, making it easier to work with complex queries in a more streamlined and efficient manner.

Enables efficient data filtering by converting JSON objects into a Map structure for query criteria. Each JSON key-value pair represents a field and its filter condition in your entity classes. For instance, in the JSON:

```json
{
  "author": {
    "name": "J%"
  },
  "price": {
    "from": "5"
  }
}
```
In case you didn't notice, "price" is being sent as string, but it is not a string. It is because you don't have to care about datatype, you can send everything as string and HiperBoot will take care of it for you.

Here, `author.name` is filtered to match names starting with "J", and `price` to include values from 5 upwards. HiperBoot interprets this JSON, mapping it directly to entity fields for streamlined, dynamic querying.

You can call the query directly using the json object, having all the options available without effort:
```java
  @PostMapping("/books-list")
  public ResponseEntity<List<Book>> getBooks(@RequestBody Map<String, Object> body) {
    return new ResponseEntity<>(bookRepository.hiperBootFilter(Book.class, body), HttpStatus.OK);
  }
```

Or you can provide a more controlled access and use  logical helpers to make the process easier:
```java
  @PostMapping("/books-author-price")
  public ResponseEntity<List<Book>> getBooksByAuthorPrice(@RequestBody String name, String price) {
    var body = hbAnd(hbEquals("author.name", name), greaterThan("price", price));
    return new ResponseEntity<>(bookRepository.hiperBootFilter(Book.class, body), HttpStatus.OK);
  }
```

### HiperBoot Methods

HiperBoot offers 4 methods to apply filters to your queries, each suited for different scenarios:

- **`hiperBootFilter`**: Retrieves a list of entities based on filters. Use this for filtered results where you don't need much information about the pagination.  You still can have sort and pagination information sent to the filter and it will be correctly processed, but the result will be a list with no extra information, like how many pages do you have or how many records in total.
  ```java
  public List<T> hiperBootFilter(Class<T> entity, Map<String, Object> filters);
  ```

- **`hiperBootPageFilter`**: Similar to `hiperBootFilter`, but returns paginated results (`Page<T>`). Ideal for when you need the standard Spring object Page to be returned with your data.
  ```java
  public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters);
  ```

- **`hiperBootPageFilter` (with `Pageable`)**: Just like `hiperBootFilter` above, but you send the Pageable standard Spring object with the information about how should be the pages you want back.
  ```java
  public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable);
  ```

- **`hiperBootBasePageFilter` (with `Pageable`)**: Applies filters and provides paginated results (`BasePage`) with custom pagination control via `Pageable`.
  ```java
  public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable);
  ```

- **`hiperBootBasePageFilter` (without `Pageable`)**: Offers filtered, paginated results (`BasePage`).
  ```java
  public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters);
  ```
BasePage have a list with the result of the query that you can access via getData() and pagination information through the custom object Pagination :
  ```java
  public interface BasePage {
    List getData();
    Pagination getPagination();
  }
  public interface Pagination {
    Integer getCurrentPage();
    Integer getTotalPages();
    Integer getPageSize();
    Long getTotalRows();
  }
  ```
### **Usage Examples**

To test those examples and your own, run the example project from [here](https://github.com/sannonaragao/hiberboot-example/), access the Swagger HTML interface, cut and paste the examples from this documentation into the body of one endpoints present there .

### **Filter**

**IMPORTANT**: HiperBoot works as CASE-INSENSITIVE.

#### Equals
```json
{
  "id": 6
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbEquals("id", "6") );

  
```

#### In
```json
{
  "id": ["1", "6", "3"]
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class,  hbEquals("id", "1", "6", "3"));
```
#### IsNull
```json
{
  "price": null
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class,  hbIsNull("price"));

```
#### Greater Than
```json
{
  "price": {
    "from" : "5"
  }
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, greaterThan("price", "5") );
```

#### Smaller Than
```json
{
  "price": {
    "to" : "10"
  }
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, smallerThan("price", "10") );
```

#### Between
```json
{
  "price": {
    "from" : "5",
    "to" : "10"
  }
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, between("price", "5", "10"));
```

#### LIKE
The LIKE operator works just like in databases, where it is used for pattern matching in text fields. The % symbol is a wildcard that represents any sequence of characters:

LIKE '%abc': Matches strings ending with 'abc'.
LIKE 'abc%': Matches strings starting with 'abc'.
LIKE '%abc%': Matches strings containing 'abc' anywhere.

Also, it is obvious but worth recalling, that it works just with STRINGS.

```json
{
  "author": {
    "name" : "J%"
  }
}
```

```java
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbEquals("author.name", "J%"));
```

#### Not
 To use NOT you can combine with other criteria, like the example below:

```json
{
  "NOT": {
    "id": ["1", "6", "3"]
  }
}
```

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbNot(hbEquals("id", "1", "6", "3")));


```
#### AND
And it is the default filter operation.  When you put several conditions together they must all match.  

```json
{
  "author": {
    "id": "4"
  },
  "price": null
}
```
To build that on Java you can use a helper to concatenate those multiple conditions, like the example below:

```java
  var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbAnd(hbEquals("author.id", "4"), hbNot(hbIsNull("price") )));
```

---

### Sort

To sort by a specific order you have just to put together a list of columns, in the "sort" key inside the "_page" object, with minus (-) if it is descending, like the examples below:

Sorted by title:
```json
{
  "_page": {
    "sort": "title"
  }
}
```
```java
  var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, sortedBy("title"));
```


Sorted by Autor's name and price descending:
```json
{
  "_page": {
    "sort": "author.name, -price"
  }
}
```
```java
  var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, sortedBy("author.name, -price"));
```
To concatenate a filter criteria with a sort, you can use this syntax:
```java
  var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("-title"));
```

### Pagination

When using pagination with filters like offset and limit in Spring, these values will be mapped to the Spring's Pageable object.
In this context:

Offset is used to determine the starting point of the data you want to retrieve.
Limit directly corresponds to the page size, indicating the maximum number of records to be returned on each page.
The Pageable object in Spring is then used to handle pagination in repository queries, allowing you to fetch data in manageable chunks as defined by the offset and limit values.

In the example below I will combine the pagination with other criterias we already learn:

```json
{
  "author": {
    "id": 3
  },
  "_page": {
    "offset": 0,
    "limit": 5,
    "sort": "title, published"
  }
}
```

```java
  var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("title, published").offset(0).limit(5));
```
---

To see a runnable implementation, visit the [example project repository](https://github.com/sannonaragao/hiberboot-example/).

## License

HiperBoot is under [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

## About the Developer

**Sannon Aragão**  
[GitHub](https://github.com/sannonaragao/)  
[LinkedIn](https://www.linkedin.com/in/sannonaragao/)

Feel free to contribute to the project and raise issues on the [GitHub repository](https://github.com/sannonaragao/hiperboot).

