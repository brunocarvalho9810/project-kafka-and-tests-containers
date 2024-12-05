package project.kafka.cpf_validator.integrationTest.config;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static project.kafka.cpf_validator.integrationTest.config.KafkaTestContainer.bootstrapServers;
import static project.kafka.cpf_validator.integrationTest.config.KafkaTestContainer.urlSchemaRegistry;

@Configuration
@Profile("test")
public class GenericKafkaProducerConfig {
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, urlSchemaRegistry);
        props.put("security.protocol", "PLAINTEXT");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean()
    public KafkaTemplate<String, Object>  kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}