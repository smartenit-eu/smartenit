# Maven preconfiguration

Set following system path as environment variable (otherwise you explicitly configure the absolute path in pom.xml). This can be achieved with

- export M2_HOME=/usr/local/apache-maven/apache-maven-3.2.3
- export M2=$M2_HOME/bin
- export MAVEN_OPTS="-Xms256m -Xmx512m"
- export PATH=$M2:$PATH
- export JAVA_HOME=$(/usr/libexec/java_home)
- export ANDROID_HOME=/Development/adt-bundle-mac-x86_64-20140702/sdk

on a UNIX based system. If you run Windows, you can set the path with

- ...
- set ANDROID_HOME=C:\opt\android-sdk-linux

and test your environment variable for instance with ```echo $ANDROID_HOME```.

# Maven deploy

To build the Android application just run the Maven command in the project folder `mvn clean install`. If you run Maven for the first time, it will download the android-maven-plugin and other dependent artifacts. Maven should create a **target** directory where the APK is stored.

# Maven instrumentation test

Execution of a test requires either an attached device or a running Android emulator. An emulator has to be created by yourself before you can run a test (use Eclipse for this task). It's necessary to specify the emulator in the pom.xml of the test project (default is 'avd'). Add new tests into the 'src/main/java' directory. Use following code to run a test within an emulator:

- mvn -P emu clean install

For testing on a device use

- mvn -P dev clean install
