See [DEVELOPMENT.md](docs/DEVELOPMENT.md) for more info.


Clean startup:
```shell
jps && jps 2>NUL | grep DevAppServerMain | cut -d " " -f1 > %TEMP%\java_ae_pid && set /P PID=<%TEMP%\java_ae_pid && del %TEMP%\java_ae_pid && taskkill /PID %PID% /F & gradlew :AE:aStop :AE:aRun
```
