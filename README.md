netty-scala-http
----------------------------------------

HTTP Server/Client examples using Netty with Scala.

### helloworld
Code in package `com.kenjih.helloworld` shows how to implement:

* toy HTTP server
* efficient HTTP client for the purpose of stress tests with `ChannelPool` and `BlockingQueue`
* throughput tracking utility on server side

To start up the server:

```
$ sbt "run-main com.kenjih.helloworld.server.HttpServer"
```

To start the client:

```
$ sbt "run-main com.kenjih.helloworld.client.HttpClient"
```

### jsonrest
