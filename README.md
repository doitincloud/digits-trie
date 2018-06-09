About digits-trie
-----------------

digits-trie is a microservice to provide fast number looking up, longest matching and start with list API services. It uses the rdbcache code to provide three layers (memory, redis and mysql) of cache and persistency.
trie is a tree like data structure with the property of the worst case time complexity of O(key length).

How to run
----------
### Requirments

It requires Java version 1.8+, maven 3.5+, redis 4.0+, mysql 5+ and authserver.

### Configuration

edit src/main/resources/application.properties
change following section according to your settings:

    # for oauth2 server
    #
    oauth2.server_url=http://localhost:8282
    oauth2.client_id=fff007a807304b9a8d983f5eaa095c98
    oauth2.client_secret=secret

    # for redis
    #
    spring.redis.url=redis://localhost:6379

    # for system database
    #
    spring.datasource.url=jdbc:mysql://localhost/doitincloud_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&useUnicode=true

    spring.datasource.username=dbuser
    spring.datasource.password=rdbcache

### Run

mvn clean spring-boot:run

### API endpoints

    1) search number for exactly match

    /v1/get/{number}[/table]

    2) search number for longest match

    /v1/longest-match/{number}[/table]

    3) list numbers start with a number

    /v1/start-with/{number}[/table]

    4) save data asynchronously

    /v1/save/{number}[/table]

    5) delete data asynchronously

    /v1/delete/{number}[/table]

    6) save data synchronously

    /v1/sync-save/{number}[/table]

    7) delete data synchronously

    /v1/sync-delete/{number}[/table]

    8) get asynchronously error by trace id

    /v1/trace/{traceId}


    
