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
   ```
   gcloud --project twisterrob-travel beta emulators datastore start --no-store-on-disk
   ```
4. In a separate window run:
   ```
   gcloud beta emulators datastore env-init
   ```
5. Set the output environment variables before running `gradlew :AppEngine:run`.

Note: step 4 and 5 are automated in `build.gradle`. 
