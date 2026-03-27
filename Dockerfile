FROM maven:3.9.9-eclipse-temurin-21

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/nexus-backend-1.0.0.jar"]
