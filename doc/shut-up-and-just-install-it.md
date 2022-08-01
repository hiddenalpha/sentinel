
Shut Up And Just Install It
===========================

You don't care about maintenance? Here you go :)

1. Get a distribution archive. You can either use a [prebuilt
   release][L_RELEASE] or [build it yourself][L_BUILD].

2. Choose a directory where to install. This example here assumes
   "/opt/sentinel".

3. Put files into place.
   ```sh
   mkdir /opt/sentinel
   tar -f your-sentinel.tgz -C /opt/sentinel -x
   ```

4. Ensure a JRE (java runtime environment) is installed on your system. This
   docuemnt is NOT a tutorial about how to install java on 1000 different
   systems. So consult the java and OS documentation about how to install
   regular software.

5. Start the server (usually handy to have this command ready in a starter
   script somewhere).
   ```sh
   cd your/servers/working/directory
   java -cp "/opt/sentinel/share/sentinel/cp:/opt/sentinel/share/sentinel/cp-external" ch.infbr5.sentinel.server.Main
   ```
   (for windows we have to replace the colon by a semi-colon to separete the
   paths)

6. Start the client application (usually handy to have this command ready in a
   starter script somewhre).
   ```sh
   cd your/clients/working/directory
   java -cp "/opt/sentinel/share/sentinel/cp:/opt/sentinel/share/sentinel/cp-external" ch.infbr5.sentinel.client.Main
   ```
   (for windows we have to replace the colon by a semi-colon to separete the
   paths)

WARN: Make sure client and server have their own working directory. DO NOT USE
      THE SAME DIRECTORY! They will override each others config and log files!

End of Quick-n-dirty-Install-Instruction. Below follows the documentation about
the archive structure.



[L_BUILD]: ../contrib/build-using-docker/README.md
[L_RELEASE]: https://github.com/hiddenalpha/sentinel/releases

