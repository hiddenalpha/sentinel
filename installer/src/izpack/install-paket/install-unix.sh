mkdir /tmp/portablejvm
cp -R JVM-UNIX/* /tmp/portablejvm
chmod 777 /tmp/portablejvm/bin/java
/tmp/portablejvm/bin/java -jar installer.jar