# 🌵 database-wrapper
A very old project of mine but that I think will save some lives... it is a system that facilitates the use of databases (MySQL and SQLITE), based on primary keys, its use is recommended for simple and non-relational projects (but it is suitable for passing the limits) It is aimed at the spigot
## How to use?
First of all, create a constructor for your database, it could be in your main class or somewhere else, because you'll need it...

#### StorageBuilder
```java
Database database = new StorageBuilder(DatabaseType.SQLITE) // Or MYSQL
  .setConnectionData(new File(System.getProperty("user.dir"))) // Or "host" "database" "username" "password"
  .useSingleThreadQueue(10) // This is optional, I will explain soon
  .selectDatabaseModel(new StoreClass()) // Here you initialize the class that has the sql creation methods
  .build(); // Returns with the database object
```

#### Connection data (setConnectionData)
<strong>setConnectionData</strong> defines the connection parameters, if you use SQLITE, pass a path where the file will be created, just the path, because it already creates a directory and file, if you use MYSQL, put the main connection data in the parameters
```java
.setConnectionData(new File(System.getProperty("user.dir")))
```

```java
.setConnectionData("host" "database" "username" "password")
```

#### Blocked Queue to store data (useSingleThreadQueue)
<strong>useSingleThreadQueue</strong> Here I made a queue that runs every second, which is defined by the user, now why is that? in spigot connections with databases are a bit complicated and sometimes misused, I made this tool for the data to be saved after a while so as not to disturb other server events like another connection, imagine, two plugins saving heavy data at the same time.
```java
.useSingleThreadQueue(10) // 10 is the number of seconds
```
### Maven
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.22</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
```gradle
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.22</version>
    <scope>provided</scope>
</dependency>
```
