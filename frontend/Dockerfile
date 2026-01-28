FROM node:18-alpine AS build
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY package*.json ./
RUN npm ci
COPY . .

RUN npm run build

FROM nginx:alpine

# Serve assets with UTF-8 by default
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# Copy built application from build stage
COPY --from=build /app/dist/project-management-frontend/browser /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
