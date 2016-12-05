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

##GCspy Data Collection

### ART Host Mode

An command line option '-XX:EnableGcSpy' is added to ART to start GCspy data collector. To start host-mode art with GCspy server enabled, run

```bash
$ cd $TOP/vender/intel/art-extension/tests
```

```bash
$ ./run-test --gdb --host --64 --runtime-option -XX:EnableGcSpy 002-sleep/
```

Make sure to build build-art-host before run test.

### ART Target Mode

For ART on device, if we enable -XX:EnableGcSpy flag then every App running on ART will start a GCspy server, which is not what we want. 

So we added a signal handler to ART, when a signal SIGUSR2 send to a specific process, it will start GCspy server.

To connect a GCspy server running on device or emulator, we need to a adb forwarding.

```bash
$ adb forward tcp:3000 tcp:3000
```
