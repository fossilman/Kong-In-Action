FROM node:12.2.0-alpine
WORKDIR /app
VOLUME ["/static"]
COPY . .
RUN npm install
RUN npm run build
