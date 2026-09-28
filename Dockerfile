# Multi-stage Dockerfile para FocusFlow API (Spring Boot 3 + Java 21)

# Estágio 1: Build da aplicação
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Copia arquivos do Maven Wrapper e POM primeiro para aproveitar o cache de camadas do Docker
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia o código-fonte e compila o pacote executável
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Estágio 2: Imagem final leve para execução em produção
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Cria usuário não-root para segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copia o JAR compilado do estágio anterior
COPY --from=builder --chown=spring:spring /workspace/target/app.jar app.jar

# Porta padrão (Render, Railway, Heroku ou Docker local)
ENV PORT=8080
EXPOSE 8080

# Otimizações de memória para contêineres Java
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
