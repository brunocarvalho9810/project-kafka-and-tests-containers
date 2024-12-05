package project.kafka.cpf_validator.integrationTest.config;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Testcontainers
@Slf4j
@Profile("test")
public class KafkaTestContainer {
    @Container
    public static KafkaContainer kafka;

    @Container
    public static GenericContainer<?> schemaRegistryContainer;

    static String DOCKER_KAFKA_IMAGE_NAME = "confluentinc/cp-kafka:7.8.0";
    static String DOCKER_SCHEMA_REGISTRY_IMAGE_NAME = "confluentinc/cp-schema-registry:7.7.2";
    public static String urlSchemaRegistry;
    public static String bootstrapServers;

    private static final Network NETWORK = Network.newNetwork();

    static {
        kafka = new KafkaContainer(DockerImageName.parse(DOCKER_KAFKA_IMAGE_NAME))
                .withEnv("KAFKA_AUTO_CREATE_TOPICS", "false")
                .withNetwork(NETWORK)
                .waitingFor(Wait.forListeningPort());

        log.info("\n#############################################"
                + "\n######### INICIANDO CONTAINER KAFKA #########"
                + "\n#############################################"
        );
        kafka.start();
    }

    static {
        schemaRegistryContainer = new GenericContainer<>(DockerImageName.parse(DOCKER_SCHEMA_REGISTRY_IMAGE_NAME))
                .withNetwork(NETWORK)
                .withExposedPorts(8081)
                .dependsOn(kafka)
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                        "PLAINTEXT://" + kafka.getNetworkAliases().get(0) + ":9092")
                .waitingFor(Wait.forHttp("/subjects").forStatusCode(200));

        log.info("\n#######################################################"
                + "\n######### INICIANDO CONTAINER SCHEMA REGISTRY #########"
                + "\n#######################################################"
        );
        schemaRegistryContainer.start();
    }

    static {
        bootstrapServers = kafka.getBootstrapServers();
        urlSchemaRegistry = "http://" + schemaRegistryContainer.getHost() + ":" + schemaRegistryContainer.getFirstMappedPort();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", bootstrapServers);
        System.setProperty("KAFKA_SCHEMA_REGISTRY", urlSchemaRegistry);
        log.info("\n#######################################################"
                + "\n######### bootstrapServers = {} #########"
                + "\n######### urlSchemaRegistry = {} #########"
                + "\n#######################################################",
                bootstrapServers,
                urlSchemaRegistry
        );
    }

    public static void registerSchema(String urlSchemaRegistry, String topic, String pathAvro) throws IOException, RestClientException {
        SchemaRegistryClient schemaRegistryClient = new CachedSchemaRegistryClient(urlSchemaRegistry, 10);
        String schemaString = new String(Files.readAllBytes(Paths.get(pathAvro)));
        Schema.Parser parser = new Schema.Parser();
        Schema avroSchema = parser.parse(schemaString);
        schemaRegistryClient.register(topic + "-value", avroSchema);
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.bootstrap-servers", () -> bootstrapServers);
        registry.add("spring.kafka.properties.schema.registry.url", () -> urlSchemaRegistry);
        registry.add("spring.kafka.properties.security.protocol", () -> "PLAINTEXT");
        registry.add("kafka.security.protocol",() -> "PLAINTEXT");
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        Map<String, Object> configs = kafkaAdmin.getConfigurationProperties();
        return AdminClient.create(configs);
    }

    @Bean
    public NewTopic createTopicDlt() {
        return new NewTopic("myTopic-test", 1, (short) 1);
    }
}
