## IDE
IntelliJ IDEA 2019.3.3

Plugins:
 * Google App Engine

## Run App Engine

```console
gradlew appengineRun
```

Note: `JAVA_HOME` for Gradle has to be Java 7.

After this runs hit:
 * http://localhost:8888 for examples, or
 * http://localhost:8888/LineStatusHistory?current=true&errors=true for live data
