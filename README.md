<p>
  <img src="https://github.com/sannonaragao/HiperBoot/assets/19274273/3c9ca2bd-eb98-4f18-80af-c3836f9c1518" width="100" height="100" alt="HiperBoot Logo" style="float: left; margin-right: 10px;" />
  <span style="vertical-align: middle; display: inline-block; height: 100px; line-height: 100px; font-size: 32px; font-weight: bold;">HiperBoot: </span>
  <span style="vertical-align: middle; display: inline-block; height: 100px; line-height: 100px; font-size: 32px;">Powerful with simplicity and efficiency</span>
</p>

---

## Introduction
***Easier than JPA Query Methods and much more powerful!***

If you're working with Spring and JPA you've always needed something like HiperBoot and from now on it will be an essential part of your projects.

HiperBoot simplifies query building by utilizing existing relationships between entities, providing advanced features like filtering, pagination, and sorting. Designed for compatibility with various SQL databases, it delivers optimized query performance and strong protection against SQL Injection attacks.

---

## Integrating HiperBoot into Your Project

### Adding dependency

Just, use your favorite build tool.

**Maven**
```xml
<dependency>
    <groupId>io.github.sannonaragao</groupId>
    <name>hiperboot</name>
    <version>[LATEST]</version>
</dependency>
```
**Gradle**
```gradle
implementation group: 'io.github.sannonaragao', name: 'hiperboot', version: '0.5.0+'
```

### Choose how to integrate: Extension of Spring Repository or Standalone Service
There are 2 ways to use HiperBoot in your Spring Boot application: 1) Using as an extension of the Spring repository or 2) as a Standalone Service.

---
#### Using as a Standalone Service
Add to the Spring repository you want to retrieve data the Spring interface JpaSpecificationExecutor as the example below:

```java
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>
{
}
```
Using as a standalone service the data should be retrieved like this:

```java
@Autowired
private BookRepository bookRepository;
private HiperBootService<Book> hiperBootService;

@PostConstruct
public void initialize()
{
  this.hiperBootService = new HiperBootService<>(bookRepository);
}

@PostMapping("/books-list")
public ResponseEntity<List<Book>> getBooks(@RequestBody Map<String, Object> body)
{
  return new ResponseEntity<>(hiperBootService.hiperBootFilter(Book.class, body), HttpStatus.OK);
}
```
Example: To see an example of this method of implementation check  ***[this GitHub repository](https://github.com/sannonaragao/hiperboot-service-example/)***

---
#### Using as an Extension of the Spring Repository

To use this method is vital to sets up the necessary repository factory bean class for HiperBoot.

```java
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = HiperBootRepositoryFactoryBean.class)
public class HiperbootExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiperbootExampleApplication.class, args);
    }
}
```

Extend from HiperBootRepository, mirroring the familiar Spring repository pattern. This extension not only equips your repository with all the standard JpaRepository methods, but also enriches it with HiperBoot's advanced features.

```java
import com.hiperboot.db.repository.HiperBootRepository;
import com.hiperbootexample.entity.Book;

public interface BookRepository extends HiperBootRepository<Book>, JpaRepository<Book, Long> {
}
```

Using as a HiperBootRepository the data should be retrieved like this:
```java
@Autowired
private BookRepository bookRepository;

@PostMapping("/books-list")
public ResponseEntity<List<Book>> getBooks(@RequestBody Map<String, Object> body) {
    return new ResponseEntity<>(bookRepository.hiperBootFilter(Book.class, body), HttpStatus.OK);
}
```
Example: To see an example of this method of implementation check  ***[this GitHub repository](https://github.com/sannonaragao/hiperboot-example/)***

---

## Overview of Capabilities


**IMPORTANT**:  In the examples below works exactly the same for both types of configuration (extending the repository or as a standalone service).

HiperBoot enhances the capability of Spring Data JPA by extending the JpaRepository interface. It adds additional query methods and mechanisms, making it easier to work with queries in a more streamlined and efficient manner.

Enables efficient data filtering by converting JSON objects into a Map structure for query criteria. Each JSON key-value pair represents a field and its filter condition in your entity classes.

For instance, in the JSON:
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
    return new ResponseEntity<>(hiperBootService.hiperBootFilter(Book.class, body), HttpStatus.OK);
  }
```

Or you can provide a more controlled access and use  logical helpers to make the process easier:
```java
  @PostMapping("/books-author-price")
  public ResponseEntity<List<Book>> getBooksByAuthorPrice(@RequestBody String name, String price) {
    var body = hbAnd(hbEquals("author.name", name), greaterThan("price", price));
    return new ResponseEntity<>(hiperBootService.hiperBootFilter(Book.class, body), HttpStatus.OK);
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

With the provided code snippets, along with the book and author entities, you're all set to explore the examples below. To get started, simply visit ***[this GitHub repository](https://github.com/sannonaragao/hiperboot-example/)***, clone and run the example project. Once it's up and running, you can access the Swagger HTML interface. From there, easily copy and paste the example requests from this documentation into the relevant endpoints to see HiperBoot in action.

### **Filter**

#### Equals / In
This method is used to filter records where a specified column equals a given value. It's useful for straightforward equality checks.
```json 
{
  "id": 6
}
```
```java
  var list = hiperBootService.hiperBootFilter(Book.class, hbEquals("id", "6") ); 
```

When multiple values are provided, it fetches records where the column matches any of the values working as the "in" operator of the SQL.
```json
{
  "id": ["1", "6", "3"]
}
```

```java
  var list = hiperBootService.hiperBootFilter(Book.class,  hbEquals("id", "1", "6", "3"));
```
#### IsNull
Fetches records where the specified column value is null. This is useful for finding records with null value in a particular column.
```json
{
  "price": null
}
```
```java
  var list = hiperBootService.hiperBootFilter(Book.class,  hbIsNull("price"));
```
#### Greater Than
Used to retrieve records where the value of a specified column is greater or equals than a given value.

```json
{
  "price": {
    "from" : "5"
  }
}
```

```java
  var list = hiperBootService.hiperBootFilter(Book.class, greaterThan("price", "5") );
```

#### Smaller Than
Opposite of greaterThan, it fetches records where the column value is less or equals than the specified value.
```json
{
  "price": {
    "to" : "10"
  }
}
```

```java
  var list = hiperBootService.hiperBootFilter(Book.class, smallerThan("price", "10") );
```

#### Between
This method is used for range queries. It fetches records where the column value falls between two specified values.

```json
{
  "price": {
    "from" : "5",
    "to" : "10"
  }
}
```

```java
  var list = hiperBootService.hiperBootFilter(Book.class, between("price", "5", "10"));
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
        var list = hiperBootService.hiperBootFilter(Book.class, hbEquals("author.name", "J%"));
```

#### Not
This is used to negate a filter condition. If you want to fetch records that do not meet a certain criterion, you wrap that condition in hbNot.  To use NOT you can combine with other criteria, like the example below:

```json
{
  "NOT": {
    "id": ["1", "6", "3"]
  }
}
```

```java
  var list = hiperBootService.hiperBootFilter(Book.class, hbNot(hbEquals("id", "1", "6", "3")));
```
#### AND
Combines multiple filter conditions. All conditions must be met for a record to be included in the result. It's like using 'AND' in SQL where multiple criteria are specified.

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
  var list = hiperBootService.hiperBootFilter(Book.class, hbAnd(hbEquals("author.id", "4"), hbNot(hbIsNull("price") )));
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
  var pageTest = hiperBootService.hiperBootPageFilter(Book.class, sortedBy("title"));
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
  var pageTest = hiperBootService.hiperBootPageFilter(Book.class, sortedBy("author.name, -price"));
```
To concatenate a filter criteria with a sort, you can use this syntax:
```java
  var pageTest = hiperBootService.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("-title"));
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
  var pageTest = hiperBootService.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("title, published").offset(0).limit(5));
```

## **Advanced Features**

### Query Capabilities
The ExtraCriteriaStrategy interface provided by HiperBoot, allows you to dynamically modify or add extra criteria to a query at runtime. This strategy is particularly useful for applying additional filtering conditions based on various requirements, such as user roles, specific business rules, or other dynamic conditions.

#### Example of how to implement ExtraCriteriaStrategy for a specific entity

Suppose you have an entity Book and you want to add a criteria where only non-deleted books should be fetched. Here's how you can implement the ExtraCriteriaStrategy for Book:

```java
  import com.hiperbootexample.entity.Book;
  import org.springframework.data.jpa.domain.Specification;
  import javax.persistence.criteria.Predicate;

  public class BookExtraCriteriaStrategy implements ExtraCriteriaStrategy<Book> {
    @Override
    public Specification<Book> getExtraCriteria(Specification<Book> existingSpec, Class<Book> type) {
      return (root, query, criteriaBuilder) -> {
        Predicate notDeletedPredicate = criteriaBuilder.isFalse(root.get("deleted"));
        return existingSpec == null ? notDeletedPredicate : criteriaBuilder.and(existingSpec.toPredicate(root, query, criteriaBuilder), notDeletedPredicate);
      };
    }
  }
```
This will be applied at ALL THE QUERY'S from the Book entity.

### RetrievalStrategy Annotation Usage
`@RetrievalStrategy` enables customized data fetching strategies in Java applications, optimizing performance beyond the defaults of ORM frameworks like HyperBoot.

#### Strategies
- **JOIN (`Strategy.JOIN`)**: Fetch related entities in a single query. Ideal for scenarios where related data is consistently used with the main entity.
- **FETCH (`Strategy.FETCH`)**: Eagerly load related entities. Suitable when immediate access to related data is necessary.
- **DEFAULT (`Strategy.DEFAULT`)**: Rely on the ORM's standard fetching strategy. Good for general use cases.

#### Benefits
- **Performance Optimization**: Tailor data fetching to specific needs, reducing database queries and data transfer.
- **Flexibility**: Gain control over how data is retrieved, allowing for fine-tuning of application performance.

#### Use Cases
- Use `JOIN` for efficient single-query data retrieval.
- Opt for `FETCH` when related data is always required immediately.
- Default to `DEFAULT` for typical scenarios without specialized fetching needs.

#### Implementation
```java																	
public class ExampleEntity {																	
    @RetrievalStrategy(Strategy.JOIN)																	
    private RelatedEntity relatedEntity;																	
}																	
```
Example use of `@RetrievalStrategy` to specify JOIN fetching strategy on `relatedEntity`.																	

---

### Miscellaneous
HiperBoot works as CASE-INSENSITIVE.

HiperBoot support the following datetime formats: ISO_DATETIME, ISO_DATETIME_TZ, SQL_DATETIME, ISO_DATETIME_UTC and RFC_1123
```json
{
"published": ["1950-06-30T18:27:24", "1942-06-24T02:45:45+00:00", "1946-08-15 12:37:47.0","1954-05-20T19:54:05Z","Wed, 22 Dec 1954 16:55:37 GMT"]
}
```
---

Did I remember to invite you to check out a cool, runnable example? I'm not entirely sure, so just in case I didn't: You're warmly invited to explore the  [Standalone Service Example](https://github.com/sannonaragao/hiperboot-service-example/) or [Repository Extension Example](https://github.com/sannonaragao/hiperboot-example/). It's waiting for you to dive in!

Also, if you find the project intriguing or useful, don't forget to leave a ⭐ on [this repository](https://github.com/sannonaragao/hiperboot/). Your star would be a great encouragement for us to continue improving!

---

## License

HiperBoot is under [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

## About the Developer

**Sannon Aragão**  
[GitHub](https://github.com/sannonaragao/)  
[LinkedIn](https://www.linkedin.com/in/sannonaragao/)

Feel free to contribute to the project and raise issues on the [GitHub repository](https://github.com/sannonaragao/hiperboot).

