FROM ubuntu:latest
LABEL authors="nuggu"

# Instalar dependências
RUN apt-get update && apt-get install -y \
    openjdk-8-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Criar diretório de trabalho
WORKDIR /app

# Copiar os arquivos do projeto para dentro do contêiner
COPY ./pom.xml /app/pom.xml
COPY ./src /app/src

# Resolver dependências e compilar o projeto
RUN mvn dependency:resolve
RUN mvn clean package -DskipTests

# Verificar o conteúdo do diretório target
RUN ls -al target

# Executar o aplicativo
CMD ["java", "-jar", "target/contador_palavras-1.0-SNAPSHOT.jar"]
