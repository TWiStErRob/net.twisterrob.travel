## Running a datastore locally

 * Docs: https://cloud.google.com/datastore/docs/tools/datastore-emulator#windows
 * Old docs: https://cloud.google.com/appengine/docs/legacy/standard/java/tools/using-local-server#datastore

1. Install the [Google Cloud SDK](https://cloud.google.com/sdk/).
2. Install the emulator:
   ```shell
   gcloud components install cloud-datastore-emulator
   gcloud components install beta
   ```
3. Run the datastore emulator in the background:
   ```shell
   gcloud --project twisterrob-travel beta emulators datastore start --no-store-on-disk
   ```
4. In a separate window run:
   ```shell
   gcloud beta emulators datastore env-init
   ```
5. Set the output environment variables before running `gradlew :web:status-history:run`.

Note: step 4 and 5 are automated in `build.gradle` and in "Run status-history server" IDEA run configuration.

## Running with secrets access

 * Docs: https://cloud.google.com/docs/authentication/provide-credentials-adc#local-dev

1. Install the [Google Cloud SDK](https://cloud.google.com/sdk/).
2. Provide Application Default Credentials (ADC)
   ```shell
   gcloud auth application-default login
   ```
   This stores credentials at %APPDATA%\gcloud\application_default_credentials.json
3. There might be a warning like this:
   > Cannot find a quota project to add to ADC.
   > You might receive a "quota exceeded" or "API not enabled" error.
   > Run $ gcloud auth application-default set-quota-project to add a quota project.
   
   This is safe to ignore for local secrets usage.
