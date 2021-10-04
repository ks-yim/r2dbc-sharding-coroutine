# R2DBC Sharding Example with Kotlin Coroutine

* A minimal sharding example with R2DBC and coroutine, where `user` table is divided into
  2 different shards.
* To which shard a `user` belongs is judged by modulo operation on its id.
* Connection pool for each shard.
* Following curl command makes a db entry to either shard in a round-robin manner
```bash
curl -X POST "http://localhost:8080/users" \
-H "Content-Type: application/json" \
-d '{"name": "foo"}'
```
* And, and a user can be retrieved by their id:
```bash
curl -X POST "http://localhost:8080/users/1" # DB query is automatically routed to `shard1`.
curl -X POST "http://localhost:8080/users/2" # Routed to `shard0`.
```

### Pre-requisite
* MySQL or MariaDB instance running at `127.0.0.1:3306` and initialized with the following SQL.
```sql
CREATE DATABASE `shard0`;
CREATE DATABASE `shard1`;
CREATE TABLE shard0.`user`(
    id BIGINT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE shard1.`user`(
    id BIGINT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE USER 'foo' IDENTIFIED BY 'bar';
GRANT ALL PRIVILEGES ON * . * TO 'foo';
```
* URI for each shard is configurable in `application.yaml`.

### Tech Stack
* Armeria
  * Server framework
  * Use kotlin coroutine integration - `armeria-kotlin`
  * Use Springboot integration - `armeria-spring-boot2-starter`
* Springboot
  * Minimal use only for DI and to use spring-based libraries (e.g. `spring-data-r2dbc`)
* R2DBC
  * `spring-boot-starter-data-r2dbc`
  * Driver - `r2dbc-mariadb`
  * Connection pool - `r2dbc:pool`
  * Sharding with `AbstractRoutineConnectionFactory`

### How DB Routing Logic for Sharding Works?
* `AbstractRoutingConnectionFactory` is R2DBC equivalent for `AbstractRoutingDataSource`
  in JDBC.
* While typical `AbstractRoutingDataSource` implementations usually leverage `ThreadLocal`
  to store and retrieve DB routing context, `AbstractRoutingConnectionFactory` let users access Reactor's
  subscription context and store the routing context in there so that it's not lost in an asynchronous
  execution flow.
* DB routing context can be written like..:
```kotlin
val dbClient: DatabaseClient

suspend fun findUserById(id: Long): User? = 
    dbClient
      .sql("SELECT * FROM `user` WHERE id = :id")
      .bind("id", id)
      .map { row -> row.toUser() }
      .one()
      // Write a routing context which will later be used in an
      // `AbstractRoutingConnectionFactory` implementation to judge the right `ConnectionFactory`.
      .contextWrite { context -> context.put(ROUTING_KEY, id % 1024) }
      .awaitFirstOrNull()
```
* See `RoutingConnectionFactory` and `UserModelMapper` for more details.

### Advanced Topics
#### Sharding with Master / Slave
* Make your own routing rule by adding more context information to Reactor context.
  For instance, you may want to have `isWriteQuery` boolean flag in the context and
  use it as a hint to route the query to slave DB instances.

#### Logical Shards and Physical shards
* This example already demonstrates sharding with 2 logical shards in 1 DB instance,
  but in a bit inefficient way since connections are made for each logical shard.
  
  In a real world example, you may want to have only 1 connection pool for each physical shard
  and this can also be achieved by modifying `AbstractRoutingConnectionFactory` implementation.
  ...