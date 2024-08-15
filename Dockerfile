ARG JRE_IMAGE=openjdk:17
FROM ${JRE_IMAGE}
WORKDIR /deployment/
COPY target/bff-*.jar ./app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-Duser.timezone=Europe/Moscow -Dfile.encoding=UTF8 -Dorg.freemarker.loggerLibrary=SLF4J"
ARG SLUG
ENV CI_BUILD=$SLUG
LABEL ci.build=$SLUG
EXPOSE 9002
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar $JAVA_ARGS"]
