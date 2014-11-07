--To test the SBox application through eclipse:

1. We assume that db file already exists (e.g. $HOME/smartenit.db) and is filled with entries from the web module.

2. Edit the sbox.properties file at /src/main/resources directory, with relevant parameters, e.g. path to db file, etc.

3. Run SBox.class.


--To run the official SBox application jar executable (main-1.0-jar-with-dependencies.jar)

1. The assumption that the db file already exists in a specific folder (e.g. $HOME/smartenit.db) and is filled with data also applies.

2. Place the sbox.properties file in the same folder as the jar file.

3. Edit the sbox.properties with the required parameters.

4. From the command line, execute: "java -jar main-1.0-jar-with-dependencies.jar"

4b. To modify logging level, place the logback.xml file in the same folder as the jar, change the line:
<logger name="eu.smartenit.sbox" level="X">, where X the preferred logging level, e.g. DEBUG, ALL, etc. 
and execute: "java -Dlogback.configurationFile=./logback.xml -jar main-1.0-jar-with-dependencies.jar"
