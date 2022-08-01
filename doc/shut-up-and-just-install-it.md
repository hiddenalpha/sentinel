
Shut Up And Just Install It
===========================

You don't care about maintenance? Here you go :)

1. Ensure a JRE (java runtime environment) is installed on your system. This
   docuemnt is NOT a tutorial about how to install java on 1000 different
   systems. So consult the java and OS documentation about how to install
   regular software.

2. Get a distribution archive. You can either use a [prebuilt
   release][L_RELEASE] or [build it yourself][L_BUILD].

3. Choose a directory where to install. This example here assumes
   `/opt/sentinel` to install the static files and `/var/opt/sentinel` to store
   application data.

4. Put files into place.
   ```sh
   mkdir /opt/sentinel
   tar -f your-sentinel.tgz -C /opt/sentinel -x
   ```

5. Create data directories (replace *1000* by the user sentinel will run with)
   ```sh
   sudo mkdir -p /var/opt/sentinel/server /var/opt/sentinel/client
   sudo chown 1000:1000 /var/opt/sentinel/server /var/opt/sentinel/client
   ```
   WARN: Make sure client and server have their own working directory. DO NOT
   USE THE SAME DIRECTORY! They will override each others config and log files!





6. Start the server (usually handy to have this command ready in a starter
   script somewhere).
   ```sh
   cd /var/opt/sentinel/server
   java -cp "/opt/sentinel/share/sentinel/cp:/opt/sentinel/share/sentinel/cp-external" ch.infbr5.sentinel.server.Main
   ```
   (for windows we have to replace the colon by a semi-colon to separete the
   paths)

7. Start the client application (usually handy to have this command ready in a
   starter script somewhre).
   ```sh
   cd /var/opt/sentinel/client
   java -cp "/opt/sentinel/share/sentinel/cp:/opt/sentinel/share/sentinel/cp-external" ch.infbr5.sentinel.client.Main
   ```
   (for windows we have to replace the colon by a semi-colon to separete the
   paths)

End of Quick-n-dirty-Install-Instruction. Below follows the documentation about
the archive structure.



[L_BUILD]: ../contrib/build-using-docker/README.md
[L_RELEASE]: https://github.com/hiddenalpha/sentinel/releases

