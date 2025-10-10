## IDE
IntelliJ IDEA 2025.2.1

Plugins:
 * Google App Engine

## Run App Engine

```shell
gradlew appengineRun
```

Note: `JAVA_HOME` for Gradle has to be Java 7.

After this runs hit:
 * http://localhost:8888 for examples, or
 * http://localhost:8888/LineStatusHistory?current=true&errors=true for live data

### Update AppEngine

```shell
gradlew :web:status-history:appengineRun
```
```
> Task :web:status-history:checkCloudSdk FAILED

FAILURE: Build failed with an exception.

* What went wrong:
  Execution failed for task ':web:status-history:checkCloudSdk'.
> Specified Cloud SDK version (347.0.0) does not match installed version (319.0.0).
```

To check the versions:
```shell
gcloud version
```
```console
Google Cloud SDK 319.0.0
app-engine-java 1.9.83
app-engine-python 1.9.91
bq 2.0.62
cloud-datastore-emulator 2.1.0
core 2020.11.13
gsutil 4.55
```

* If the installed version is higher than the "Specified" version, update [libs.versions.toml](../gradle/libs.versions.toml). 
* If the installed version is lower than the "Specified" version, update `gcloud`: 
    ```shell
    gcloud components update --version 347.0.0
    ```
    ```console
    Your current Cloud SDK version is: 319.0.0
    You will be upgraded to version: 347.0.0
    ...
    Do you want to continue (Y/n)? Y
    ```

## Build commands
Full Build without lint
```shell
gradlew build --continue -x :Android:lint -x :android:app:range:lintVitalRelease -x :android:app:full:lintVitalRelease
```

Android full compilation
```shell
gradlew :android:app:range:assemble :android:app:full:assemble compileDebugUnitTestSources
```

## Deploy to AppEngine

This will do a test deployment available at https://test-dot-twisterrob-london.appspot.com/.
```shell
gradlew :web:status-history:appengineDeploy
```
