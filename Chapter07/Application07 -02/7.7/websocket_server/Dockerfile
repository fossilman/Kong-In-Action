FROM node:latest
WORKDIR /root
ADD ./server.js ./
RUN npm install nodejs-websocket --save
CMD ["node","server.js"]
