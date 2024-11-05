# Website

0. Check [live][live] version is operational.
1. Ensure latest `main` in git clone.
1. Change `testDeployment` to `false` in `web/status-history/build.gradle.kts`.
1. `gradlew :web:status-history:appengineDeploy :web:status-history:appengineDeployCron`
1. Verify new version is created in [Google Cloud Console][versions].
1. Check [live][live] version is operational.
1. `git tag -f live` on `main` and `git push origin -f live`.
1. Clean up old versions in [Google Cloud Console][versions].

[live]: https://twisterrob-london.appspot.com/
[versions]: https://console.cloud.google.com/appengine/versions?serviceId=default&project=twisterrob-london

# Android

For the full process see [.github/release.md][releasing].

1. Double-check the version number in `android/app/range/version.properties` is the same as the milestone, if not, PR.
1. Ensure clean latest working copy.
   ```shell
   git checkout main
   git pull
   git status
   git reset --hard origin/main
   ```
1. Create artifacts
   ```shell
   gradlew clean :android:app:range:release
   ```
1. Upload `%RELEASE_HOME%\android\net.twisterrob.blt.range*@*.zip` (latest):
   * `net.twisterrob.blt.range@*+release.apk`
     @ Developer Console
     \> Release
     \> Testing
     \> Closed Testing
     \> [Alpha][alpha]
   * `proguard_mapping.txt`
     @ Developer Console
     \> Release
     \> [App bundle explorer][bundle-explorer]
     \> Downloads tab
     \> Assets
     \> ReTrace mapping file
1. Publish to Alpha.
   1. Check [Pre-launch Report][pre-launch-report]  
      Generated in about 15 minutes after upload, if errors, then start again.
   1. Wait until alpha stage is propagated and update current release version on my phone.
   1. Smoke test for no errors.
1. If no errors, Promote to Beta or Prod with staged rollout.
   1. Set up a reminder in calendar to check for crashes and bump rollout.

[releasing]: https://github.com/TWiStErRob/.github/blob/main/RELEASE.md
[alpha]: https://play.google.com/console/developers/7995455198986011414/app/4972239006863689375/tracks/4697686677597567422
[bundle-explorer]: https://play.google.com/console/developers/7995455198986011414/app/4972239006863689375/bundle-explorer-selector
[pre-launch-report]: https://play.google.com/console/developers/7995455198986011414/app/4972239006863689375/pre-launch-report/overview

## Prepare next release
1. Update version number in `android/app/range/version.properties` anticipating minor and PR to `main`.
