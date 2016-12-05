# GCspy: visualizing the heap

GCspy uses a client-server model to serve the purpose of visualizing of memory
management process including garbage collector and memory allocator. The server
module is embedded in targeting runtime to collect and transfer heap data to
client, while the client visualize these data.

## Client

Source code of GCspy client exists in `$TOP/client`.

To run client,
```bash
cd client
./gradlew run
```

To generate a jar file in `client/build/libs/client-all-1.013.jar`
```bash
cd client && ./gradlew dist
```

## Server

Source code of GCspy server is in `$TOP/server`.

To make server library on linux,
```bash
cd server && make
```

To run a dummy server for testing,
```bash
cd server/test && make run-server
```
