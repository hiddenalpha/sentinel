
Distribution Bundle
========================================================================

Core of sentinel is distributed as a platform independent [tar][L_TAR] archive
with a structure based on the [standard filesystem hierarchy][L_HIER]. This
document gives some additional notes about what is placed where in the
distribution archive. This document talks about a compiled release. Basically
it is the result we get after it is built with docker and we "Grab distribution
archives" (see [contrib/build-using-docker/][L_BUILD]). If you're searching for
source code instead your search could start at [sentinel.git][L_REPOLINK].


## Shut up and just install the damn thing

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


## share/sentinel/cp/

Classpath for the resources used by sentinel itself.


## share/sentinel/cp-external/

Classpath for sentinel dependencies. This is only provided for convenience. For
example an admin does not want to install all the dependencies himself, he can
just add those jars here to the classpath. BTW the example above also uses this
shorthand.


## share/doc/sentinel/

Misc documentation for sentinel.


## Links

[L_BUILD]: https://github.com/hiddenalpha/sentinel/blob/master/contrib/build-using-docker/README.md
[L_HIER]: https://www.man7.org/linux/man-pages/man7/hier.7.html
[L_RELEASE]: https://github.com/hiddenalpha/sentinel/releases
[L_REPOLINK]: https://github.com/hiddenalpha/sentinel.git
[L_TAR]: https://en.wikipedia.org/wiki/Tar_%28file_format%29

