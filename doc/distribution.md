
Distribution Bundle
===================

You do not care about maintenance and just want the damn thing installed? See
[doc/shutup-and-just-install-it.md][L_INSTALL_DIRTY].

Core of sentinel is distributed as a platform independent [tar][L_TAR] archive
with a structure based on the [standard filesystem hierarchy][L_HIER]. This
document gives some additional notes about what is placed where in the
distribution archive. This document talks about a compiled release. Basically
it is the result we get after it is built with docker and we "Grab distribution
archives" (see [contrib/build-using-docker/][L_BUILD]). If you're searching for
source code instead your search could start at [sentinel.git][L_REPOLINK].


## share/sentinel/cp/

Classpath for the resources used by sentinel itself.


## share/sentinel/cp-external/

Classpath for sentinel dependencies. This is only provided for convenience. For
example an admin does not want to install all the dependencies himself, he can
just add those jars here to the classpath. BTW the example above also uses this
shorthand.


## share/doc/sentinel/

Misc documentation for sentinel.



[L_BUILD]: https://github.com/hiddenalpha/sentinel/blob/master/contrib/build-using-docker/README.md
[L_HIER]: https://www.man7.org/linux/man-pages/man7/hier.7.html
[L_INSTALL_DIRTY]: https://github.com/hiddenalpha/sentinel/blob/wip-InstallHintInReadme/doc/shut-up-and-just-install-it.md
[L_RELEASE]: https://github.com/hiddenalpha/sentinel/releases
[L_REPOLINK]: https://github.com/hiddenalpha/sentinel.git
[L_TAR]: https://en.wikipedia.org/wiki/Tar_%28file_format%29

