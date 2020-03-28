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

## Build commands
Full Build without lint
```
gradlew build --continue -x :Android:lint -x :Android:lintVitalRangeRelease -x :Android:lintVitalFullRelease -x :twister-lib-android:lint
```
Android full compilation
```
gradlew :Android:assembleDebug :Android:asFDAT :Android:asRDAT :Android:compileFDebugUnitTestSources :Android:compileRDebugUnitTestSources
```

## Deploy to production
Direct, creates a new version and activates it as 100%.
```
gradlew appengineDeploy
```
