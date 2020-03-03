FROM maven:3-jdk-6

COPY ./conf/squid_myCA.crt .

RUN keytool -importcert -noprompt -alias repo.maven.apache.org \
  -keystore /usr/lib/jvm/java-6-openjdk-amd64/jre/lib/security/cacerts \
  -storepass changeit -file ./squid_myCA.crt
