FROM kong:2.0.5
VOLUME ["/static"]
ADD ./todolists.conf /etc/kong
CMD ["kong","start","--nginx-conf","/etc/kong/todolists.conf","&"]
