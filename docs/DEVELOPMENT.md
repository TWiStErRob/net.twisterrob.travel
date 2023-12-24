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

### Update AppEngine

```shell
$ gradlew :web:status-history:appengineRun
> Task :web:status-history:checkCloudSdk FAILED

FAILURE: Build failed with an exception.

* What went wrong:
  Execution failed for task ':web:status-history:checkCloudSdk'.
> Specified Cloud SDK version (347.0.0) does not match installed version (319.0.0).
```

```shell
$ gcloud version
Google Cloud SDK 319.0.0
app-engine-java 1.9.83
app-engine-python 1.9.91
bq 2.0.62
cloud-datastore-emulator 2.1.0
core 2020.11.13
gsutil 4.55
```

```shell
$ gcloud components update --version 347.0.0
Your current Cloud SDK version is: 319.0.0
You will be upgraded to version: 347.0.0
...
Do you want to continue (Y/n)? Y
```

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
