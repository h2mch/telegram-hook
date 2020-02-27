# Step 1: build the native image
FROM oracle/graalvm-ce:19.3.1-java11 as graalVM-build
#FROM oracle/graalvm-ce:19.2.1 as graalVM-build

# Download and install Maven
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG SHA=c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN gu install native-image \
  && mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV GRAALVM_HOME $JAVA_HOME

# https://quarkus.io/guides/native-and-ssl
RUN mkdir -p /tmp/ssl-libs/lib \
  && cp $GRAALVM_HOME./lib/security/cacerts /tmp/ssl-libs \
  && cp $GRAALVM_HOME./lib/libsunec.so /tmp/ssl-libs/lib/

WORKDIR /home/app

# Cache required dependencies as long as the pom.xml does not change
# -B : Batch Mode / no user interactin
# -C : Fail the build if checksums don’t match
# -T 1C : Use one core
COPY pom.xml .
RUN $MAVEN_HOME/bin/mvn -B -C -T 1C dependency:go-offline dependency:resolve-plugins

COPY ./src ./src
RUN $MAVEN_HOME/bin/mvn -B -C -T 1C package -Pnative

# Step 2: build the running container
FROM registry.fedoraproject.org/fedora-minimal

# see: https://github.com/quarkusio/quarkus/issues/4262
ENV DISABLE_SIGNAL_HANDLERS false

WORKDIR /work/
COPY --from=graalVM-build /home/app/target/*-runner /work/application
COPY --from=graalVM-build /tmp/ssl-libs/ /work/
RUN chmod 775 /work
EXPOSE 8080
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0", "-Djava.library.path=/work/lib", "-Djavax.net.ssl.trustStore=/work/cacerts"]
