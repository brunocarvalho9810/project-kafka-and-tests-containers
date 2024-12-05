# project-kafka-and-tests-containers

## CPF Validator <img src="./pt-br.png" alt="Ícone do Brasil" width="50" height="50" />

Projeto em Java para validação de CPFs recebidos de um tópico Kafka. O sistema verifica se o CPF é valido utilizando a API **Invertexto** e armazena os resultados em um banco de dados MySQL. Este projeto também implementa testes integrados realistas utilizando **BDD** com **Cucumber** e **containers Docker**, simulando o cenário real de produção.
<br>Durante a execução dos testes integrados, containers para Kafka, Schema Registry e MySQL serão criados automaticamente para simular a integração com a aplicação.
<hr>

## Tecnologias Utilizadas

- **Java 17**
- **Spring Framework**:
    - Spring Kafka
    - Spring Boot
    - Spring Data JPA
- **Apache Kafka**
- **MySQL**
- **API Invertexto** (validação de CPF)
- **Docker** e **Docker Compose**
- **Maven**
- **Wiremock** para simular chamadas à API Invertexto
- **Cucumber** para testes de BDD
- **JUnit 5** para execução de testes
- **Testcontainers** para simulação de serviços no ambiente de testes
<hr>

## Funcionalidades

1. **Consumo de Mensagens Kafka**:
    - Consome mensagens de um tópico Kafka chamado **myTopic** contendo informações de CPF e nome.

2. **Validação de CPF**:
    - Valida o CPF recebido por meio da API do **Invertexto**.

3. **Persistência de Dados**:
    - Salva os resultados da validação (CPF válido ou inválido) no banco de dados MySQL.

4. **Testes Integrados (BDD)**:
    - Utiliza o **Cucumber** para criar cenários de teste.
    - Simula o Kafka, MySQL e Wiremock utilizando **Testcontainers** para garantir um ambiente próximo ao real durante os testes.
<hr>

## Estrutura do Projeto

Abaixo está a estrutura do projeto, destacando os principais diretórios e arquivos.

```plaintext
cpf-validator/
├── src/
│   ├── main/
│   │   ├── avro/
│   │   │   ├── User.avsc                     # Avro da mensagem
│   │   ├── java/
│   │   │   ├── project.kafka.cpf_validator/
│   │   │   │   ├── config/                   # Configurações do Projeto
│   │   │   │   ├── entities/                 # Entidades
│   │   │   │   ├── events/                   # Listeners
│   │   │   │   ├── feign/                    # Chamadas Rest API
│   │   │   │   ├── repository/               # Repositórios JPA
│   │   ├── resources/
│   │   │   ├── application.properties        # Configurações de ambiente
│   │   │   ├── application-test.properties   # Configurações de ambiente para teste
│   ├── test/
│   │   ├── java/
│   │   │   ├── project.kafka.cpf_validator/
│   │   │   │   ├── entities/                 # Testes Entidades
│   │   │   │   ├── events/                   # Testes Listeners
│   │   │   │   ├── integrationTest/          # Testes de integração
│   │   │   │   │   ├── config/               # Configurações para testes integrados
│   │   │   │   │   ├── steps/                # Steps Cucumber
├── docker-compose.yml                        # Configurações Docker Compose
├── pom.xml                                   # Dependencias do Projeto
└── README.md
```
<hr>

## Como Executar o Projeto

1. Instale o Java 17 e Maven na sua maquina e configure as variáveis de ambiente
2. Instale Docker Desktop e Docker Compose
3. Execute os containers do MySQL, Kafka, Zookeeper e Schema Registry utilizando o arquivo do docker compose, execute o comando abaixo na raiz do projeto.
   ```shell
    docker-compose up 
   ```
    Verifique se os containers foram executados com sucesso no Docker Desktop
4. Agora podemos executar nosso projeto: classe <br>```CpfValidatorApplication```<br>
5. Com o projeto em execução, limpe o terminal para facilitar a identificação das mensagens consumidas no tópico Kafka.
6. Abra o Docker Desktop, entre dentro do container do Schema Registry na aba "Exec" execute o comando abaixo para criar um **Producer** para nosso tópico kafka.
   ```shell
   kafka-avro-console-producer --broker-list kafka:9092 --topic myTopic --property value.schema="$(cat /home/appuser/avro/User.avsc)" --property schema.registry.url=http://schema-registry:8081 --property value.serializer=io.confluent.kafka.serializers.KafkaAvroSerializer
   ```
7. Agora temos um Producer genérico para enviar mensagens para o tópico kafka, cole o json abaixo de exemplo de mensagem.
    <br><br>
    CPF falso: 
    ```shell
    {"cpf": "12345678901", "name": "John Connor"}
    ```
    CPF verdadeiro: 
    ```shell
    {"cpf": "24731603099", "name": "John Connor"}
    ```
8. Após enviar a mensagem, podemos ver os logs no terminal da nossa aplicação e consultar nossa base de dados, na tabela **tb_users** utilizando o comando
    ```shell   
    SELECT * FROM tb_users;
   ```

![Representa o processo de validação do CPF, desde o consumo da mensagem no Kafka até o armazenamento no banco de dados.](/cpf-validator-fluxo-app.png)

<hr>

## Testes Integrados

![ Ilustração do ambiente de teste criado com Testcontainers.](/cpf-validator-fluxo-teste.png)

<hr>

## CPF Validator <img src="./en.png" width="50" height="50" />

A Java project for CPF validation received from a Kafka topic. The system validates the CPF using the Invertexto API and stores the results in a MySQL database. This project also implements realistic integration tests using BDD with Cucumber and Docker containers, simulating a real production environment.
<br>During the execution of the integration tests, containers for Kafka, Schema Registry, and MySQL will be automatically created to simulate the integration with the application.
<hr>

## Technologies Used

- **Java 17**
- **Spring Framework**:
    - Spring Kafka
    - Spring Boot
    - Spring Data JPA
- **Apache Kafka**
- **MySQL**
- **API Invertexto**  (CPF validation)
- **Docker** e **Docker Compose**
- **Maven**
- **Wiremock** to simulate calls to the Invertexto API
- **Cucumber** for BDD testing
- **JUnit 5** for test execution
- **Testcontainers** to simulate services in the testing environment
<hr>

## Features

1. **Kafka Message Consumption:**
    - Consumes messages from a Kafka topic called **myTopic** containing CPF and name information.

2. **CPF Validation:**
    - Validates the CPF received through the **Invertexto** API.

3. **Data Persistence:**
    - Saves the validation results (valid or invalid CPF) into the MySQL database.

4. **Integrated Tests (BDD):**:
    - Uses **Cucumber** to create test scenarios.
    - Simulates Kafka, MySQL, and Wiremock using **Testcontainers** to ensure a close-to-production environment during testing.
<hr>

## Project Structure

Below is the project structure, highlighting the main directories and files.

```plaintext
cpf-validator/
├── src/
│   ├── main/
│   │   ├── avro/
│   │   │   ├── User.avsc                     # Avro schema for the message
│   │   ├── java/
│   │   │   ├── project.kafka.cpf_validator/
│   │   │   │   ├── config/                   # Project configurations
│   │   │   │   ├── entities/                 # Entities
│   │   │   │   ├── events/                   # Listeners
│   │   │   │   ├── feign/                    # REST API calls
│   │   │   │   ├── repository/               # JPA repositories
│   │   ├── resources/
│   │   │   ├── application.properties        # Environment configurations
│   │   │   ├── application-test.properties   # Test environment configurations
│   ├── test/
│   │   ├── java/
│   │   │   ├── project.kafka.cpf_validator/
│   │   │   │   ├── entities/                 # Entity tests
│   │   │   │   ├── events/                   # Listener tests
│   │   │   │   ├── integrationTest/          # Integration tests
│   │   │   │   │   ├── config/               # Configurations for integration tests
│   │   │   │   │   ├── steps/                # Cucumber steps
├── docker-compose.yml                        # Docker Compose configurations
├── pom.xml                                   # Project dependencies
└── README.md
```
<hr>

## How to Run the Project

1. Install Java 17 and Maven on your machine and configure the environment variables.
2. Install Docker Desktop and Docker Compose.
3. Run the containers for MySQL, Kafka, Zookeeper, and Schema Registry using the docker-compose file by running the following command at the root of the project:
```shell
    docker-compose up 
   ```
Check if the containers were successfully started in Docker Desktop.

4. Now, you can run the project: class <br>```CpfValidatorApplication```<br>
5. Once the project is running, clear the terminal to make it easier to see the messages consumed from the Kafka topic.
6. Open Docker Desktop, go to the Schema Registry container, and in the "Exec" tab, run the command below to create a **Producer** for our Kafka topic.
   ```shell
   kafka-avro-console-producer --broker-list kafka:9092 --topic myTopic --property value.schema="$(cat /home/appuser/avro/User.avsc)" --property schema.registry.url=http://schema-registry:8081 --property value.serializer=io.confluent.kafka.serializers.KafkaAvroSerializer
   ```
7. Now we have a generic Producer to send messages to the Kafka topic. Paste the JSON message below as an example.
   <br><br> 
Invalid CPF:
    ```shell
    {"cpf": "12345678901", "name": "John Connor"}
    ```
    Valid CPF:
    ```shell
    {"cpf": "24731603099", "name": "John Connor"}
    ```
8. After sending the message, you can check the logs in your application terminal and query the database at the **tb_users** table using the following command:
    ```shell   
    SELECT * FROM tb_users;
   ```

![Representa o processo de validação do CPF, desde o consumo da mensagem no Kafka até o armazenamento no banco de dados.](/cpf-validator-fluxo-app.png)

<hr>

## Integrated Tests

![ Ilustração do ambiente de teste criado com Testcontainers.](/cpf-validator-fluxo-teste.png)

<hr>