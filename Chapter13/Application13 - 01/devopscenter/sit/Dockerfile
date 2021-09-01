FROM openjdk:8u121-jre-alpine
EXPOSE 8080
WORKDIR /opt
ADD entrypoint.sh ./
ADD ./app* ./
ADD *.jar ./
CMD ["/bin/sh", "entrypoint.sh"]