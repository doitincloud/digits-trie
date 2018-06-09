About digits-trie
-----------------

digits-trie is a microservice to provide fast number looking up, longest matching and starting with list API services. It uses the rdbcache code to provide three layers (local memory, redis and mysql) of cache and persistency.
trie is a tree like data structure with the property of the worst case time complexity of O(key length).

How to run
----------
### Requirments

It requires Java version 1.8+, maven 3.5+, redis 4.0+, mysql 5+ and authserver.

### Configuration

edit src/main/resources/application.properties, change following section according to your settings:

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

    1) search number for exactly matching

    /v1/get/{number}[/table]

    2) search number for longest matching

    /v1/longest-match/{number}[/table]

    3) list numbers starting with a number

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

### Examples

    #to get access_token & refresh_token
    
    curl -sS -X POST -u 'ccf681950c2f4ce8b12fc37fd35481a6:secret' 'http://localhost:8282/oauth/v1/token' -d 'grant_type=password&username=admin@example.com&password=123&scope=read%20write%20delete'
    {
        "access_token" : "faf6cfaf-f29b-47d9-b212-d97f9c20c086",
        "token_type" : "bearer",
        "refresh_token" : "e6e5d932-396d-47ae-8290-1e78ed56dfc3",
        "expires_in" : 43199,
        "scope" : "delete read write"
    }

    # get
    
    curl -sS -H 'Authorization: Bearer faf6cfaf-f29b-47d9-b212-d97f9c20c086' 'http://localhost:8080/v1/get/123'
    {
      "timestamp" : 1528565619893,
      "duration" : "0.003745",
      "data" : {
        "digits" : "123",
        "name" : "abc",
        "created_at" : 1528363182,
        "updated_at" : 1528363182
      },
      "trace_id" : "2b887029ec5c4b28bce6038174be4f22"
    }
    
    # longest-match
    
    curl -sS -H 'Authorization: Bearer faf6cfaf-f29b-47d9-b212-d97f9c20c086' 'http://localhost:8080/v1/longest-match/123123123'
    {
      "timestamp" : 1528565685005,
      "duration" : "0.001176",
      "data" : {
        "digits" : "123",
        "name" : "abc",
        "created_at" : 1528363182,
        "updated_at" : 1528363182
      },
      "trace_id" : "a2654ce838174d4884a6fe29d84cbbf4"
    }
    
    # start-with
    
    curl -sS -H 'Authorization: Bearer faf6cfaf-f29b-47d9-b212-d97f9c20c086' 'http://localhost:8080/v1/start-with/123'
    {
      "timestamp" : 1528565735251,
      "duration" : "0.002839",
      "data" : {
        "123" : {
          "digits" : "123",
          "name" : "abc",
          "created_at" : 1528363182,
          "updated_at" : 1528363182
        },
        "1234" : {
          "digits" : "1234",
          "name" : "abcd",
          "created_at" : 1528363182,
          "updated_at" : 1528363182
        },
        "12345" : {
          "digits" : "12345",
          "name" : "abcde",
          "created_at" : 1528363182,
          "updated_at" : 1528363182
        }
      },
      "trace_id" : "2c0f4ec4afd949bd94381af337c62921"
    }