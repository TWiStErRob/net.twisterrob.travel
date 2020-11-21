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
gradlew build --continue -x :Android:lint -x :Android:lintVitalRangeRelease -x :Android:lintVitalFullRelease
```
Android full compilation
```
gradlew :Android:assembleDebug :Android:assembleFullDebugAndroidTest :Android:assembleRangeDebugAndroidTest :Android:compileFullDebugUnitTestSources :Android:compileRangeDebugUnitTestSources
```

## Deploy to production
May need to log in with papp.robert.s@gmail.com.
```
gcloud auth login
```

Make sure the right project is selected!
```
gcloud config set project twisterrob-london
```

Directly creates a new version and activates it as 100%.
```
gradlew appengineDeployAll
```

Verify new version at https://twisterrob-london.appspot.com/
and delete old version at [Google Cloud Platform > App Engine > Versions](
https://console.cloud.google.com/appengine/versions?project=twisterrob-london).
