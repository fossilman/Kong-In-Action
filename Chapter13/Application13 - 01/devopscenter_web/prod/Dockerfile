FROM nginx:latest
WORKDIR /opt
ADD ./dist.tar ./
RUN rm -rf /etc/nginx/nginx.conf
ADD ./nginx.conf /etc/nginx/