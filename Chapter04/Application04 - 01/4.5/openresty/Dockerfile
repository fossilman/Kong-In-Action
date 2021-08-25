FROM centos:7
RUN yum install -y perl wget pcre-devel openssl-devel gcc curl postgresql-devel make
RUN wget https://openresty.org/download/openresty-1.17.8.1.tar.gz && tar xf openresty-1.17.8.1.tar.gz -C /root
RUN cd /root/openresty-1.17.8.1 && ./configure --prefix=/opt/openresty --with-http_postgres_module --with-luajit --without-http_redis2_module --with-http_iconv_module && make && gmake install
RUN ln -s /opt/openresty/bin/openresty /usr/bin/openresty
RUN mkdir /opt/openresty/nginx/lua
ADD nginx.version1.conf /opt/openresty/nginx/conf/
ADD lua/* /opt/openresty/nginx/lua/
ADD init.sql /
CMD ["openresty","-c","conf/nginx.version1.conf","&"]
