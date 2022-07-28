
ARG PARENT_IMAGE=alpine:3.16.0
FROM $PARENT_IMAGE

ARG GIT_TAG=master
ARG MVN_OPTS=-Dmaven.repo.local=/mvnrepo
ARG PKGS_TO_ADD="curl git maven openjdk11 tar"
ARG PKGS_TO_DEL="curl git maven openjdk11 tar"
ARG PKGINIT=true
ARG PKGADD="apk add"
ARG PKGDEL="apk del"
ARG PKGCLEAN=true

WORKDIR /work

RUN true \
    && $PKGINIT \
    && $PKGADD $PKGS_TO_ADD \
    && git clone --depth 42 --branch "${GIT_TAG:?}" https://github.com/hiddenalpha/sentinel.git . \
    && mvn ${MVN_OPTS:?} versions:set -DgenerateBackupPoms=false -DnewVersion="$(git describe --tags)" \
    && mvn ${MVN_OPTS:?} -pl client -am clean install \
    && mkdir dist \
    && cp -t dist/. client/target/sentinel-client-*-jar-with-dependencies.jar \
    && cp -t dist/. client/target/sentinel-client-*.jar \
    && cp -t dist/. common/target/sentinel-common-*.jar \
    && cp -t dist/. server/target/sentinel-server-*-jar-with-dependencies.jar \
    && cp -t dist/. server/target/sentinel-server-*.jar \
    && cp -t dist/. wsdl/target/sentinel-wsdl-*.jar \
    && (cd dist && tar --owner=0 --group=0 -cz *.jar) > dist/sentinel-jars.tgz \
    && (cd /mvnrepo && tar --owner=0 --group=0 -cz *) > dist/mvnrepo.tgz \
    && find /work -not -wholename '/work/dist/*' -delete \
    && find /work -exec chown 1000:1000 {} + \
    && $PKGDEL $PKGS_TO_DEL \
    && $PKGCLEAN \
    && true

USER 1000:1000

# run for max 10 hrs. Because this container is not meant to stay running
# forever.
CMD ["sleep 36000"]

