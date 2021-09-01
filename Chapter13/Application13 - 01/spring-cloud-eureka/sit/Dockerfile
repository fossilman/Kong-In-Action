FROM openjdk:8u121-jre-alpine
EXPOSE 7777
WORKDIR /opt
ADD entrypoint.sh ./
ADD ./app* ./
ADD *.jar ./
CMD ["/bin/sh", "entrypoint.sh"]