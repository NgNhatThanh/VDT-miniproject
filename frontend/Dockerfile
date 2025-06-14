FROM node:20-alpine
WORKDIR /app
COPY package.json .
RUN npm i
COPY . .
EXPOSE 4200
CMD ["npm", "start"]
