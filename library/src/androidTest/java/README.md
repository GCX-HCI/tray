# Test folder info

### net.grandcentrix.tray

the `net.grandcentrix.tray` package contains all android tests need to be run on a device.

### test.net.grandcentrix.tray

There is also a symbolic link `test` referencing the tests in `src/test/java/net/grandcentrix/tray` where all tests belong not have to run on the emulator. They work on the JVM and are faster. There are two reasons why they are referenced here:
- the mock classes can be used for the device tests. No need to have them in two versions.
- when running the code coverage report `gradle createDebugCoverageReport` the JVM tests get included. There is currently no other way to proof the 100% test coverage.