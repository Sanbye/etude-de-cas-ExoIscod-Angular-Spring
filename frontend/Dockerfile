FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci
COPY . .

RUN npm run build
FROM nginx:alpine

# Copy built application from build stage
COPY --from=build /app/dist/project-management-frontend/browser /usr/share/nginx/html

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
