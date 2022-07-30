
ARG PARENT_IMAGE=alpine:3.16.0
FROM $PARENT_IMAGE

ARG GIT_TAG="master"
ARG REPO_URL="https://github.com/hiddenalpha/sentinel.git"
ARG PKGS_TO_ADD="curl=7.83.1-r2 fontconfig=2.14.0-r0 git=2.36.2-r0 openjdk11-jre=11.0.15_p10-r1 maven=3.8.5-r0 tar"
ARG PKGS_TO_DEL="curl git maven tar"

WORKDIR /work

RUN true \
    && PROJNAME="sentinel" \
    && PKGINIT="apk update" \
    && PKGADD="apk add" \
    && PKGDEL="apk del" \
    && PKGCLEAN=true \
    # install needed packages
    && $PKGINIT && $PKGADD $PKGS_TO_ADD \
    # get sentinel sourcecode
    && git clone --depth 42 --branch "${GIT_TAG:?}" "${REPO_URL:?}" . \
    # configure
    && VERSION="$(git describe --tags|sed 's,^v,,')" \
    && mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${VERSION:?}" \
    && export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED" \
    # make
    && mvn -Dmaven.repo.local=/var/tmp/mvnrepo -pl client -am clean install \
    && mvn -Dmaven.repo.local=/var/tmp/mvnrepo -pl client,server -am dependency:copy-dependencies -DexcludeGroupIds=ch.infbr5.sentinel \
    && mkdir build dist && mkdir -p \
        "build/bin" \
        "build/share/doc/${PROJNAME:?}" \
        "build/share/${PROJNAME:?}/cp" \
        "build/share/${PROJNAME:?}/cp-external" \
    && cp contrib/build-using-docker/sentinel-server build/bin/. \
    && cp contrib/build-using-docker/sentinel-client build/bin/. \
    && find client common server wsdl -wholename '*/target/*sentinel-*.jar' -not -name '*jar-with-dependencies*' -exec cp -t build/share/${PROJNAME:?}/cp/. {} + \
    && cp -rt build/share/doc/${PROJNAME:?}/. *.txt *.md doc/* \
    && find */target/dependency -type f -exec cp -t build/share/${PROJNAME:?}/cp-external/. {} + \
    && chmod 0755 build/bin/* \
    && (cd build && tar --owner=0 --group=0 -cz *) > dist/sentinel-${VERSION:?}.tgz \
    && (cd "/var/tmp/mvnrepo" && tar --owner=0 --group=0 -cz *) > dist/sentinel-mvnrepo-${VERSION:?}.tgz \
    # install (just as an example to have a reference for testing/debugging/etc)
    && mkdir -p "/opt/sentinel-${VERSION:?}" \
    && tar -f dist/sentinel-${VERSION:?}.tgz -C "/opt/sentinel-${VERSION:?}" -x \
    && ln -s "/opt/sentinel-${VERSION:?}" "/opt/sentinel" \
#    # Cleanup
    && find /work -not -wholename '/work/dist/*' -delete \
    && find /work -exec chown 1000:1000 {} + \
    && $PKGDEL $PKGS_TO_DEL \
    && $PKGCLEAN \
    && true

ENV PATH "/opt/sentinel/bin:$PATH"

USER 1000:1000

# run for max 10 hrs. Because this container is not meant to stay running
# forever.
CMD ["sleep", "36000"]

