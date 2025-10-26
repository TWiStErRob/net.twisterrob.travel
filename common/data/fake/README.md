Fake data can be served via
```shell
gradlew :common:data:fake:farmRun --no-configuration-cache
```
Change `net.twisterrob.blt.android.App.ALLOW_MOCK_URLS = true`

TODEL `--no-configuration-cache` when https://github.com/gretty-gradle-plugin/gretty/issues/313 is resolved.
