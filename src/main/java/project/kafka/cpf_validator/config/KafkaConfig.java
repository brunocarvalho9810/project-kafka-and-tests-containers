package project.kafka.cpf_validator.config;

import com.cpf_validator.User;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistry;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${main.group}")
    private String group;

    @Value("${spring.kafka.consumer.properties.specific.avro.reader}")
    private String specificAvro;


    public static final String CPF_VALIDATOR_CONTAINER_FACTORY = "cpfValidatorContainerFactory";

    @Bean
    public ConsumerFactory<String, User> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        props.put("specific.avro.reader", specificAvro);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = CPF_VALIDATOR_CONTAINER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, User>
    userContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(userErrorHandler());
        return factory;
    }

    @Bean
    public CommonErrorHandler userErrorHandler() {
        long WAITING_MILLISECONDS = 10000L;
        double MULTIPLIER = 1.0;
        int NUMBER_ATTEMPTS = 3;

        ExponentialBackOff backOff = new ExponentialBackOff(WAITING_MILLISECONDS, MULTIPLIER);
        backOff.setMaxAttempts(NUMBER_ATTEMPTS);
        return new DefaultErrorHandler(
                (ConsumerRecord<?, ?> record, Exception exception) -> {
                    log.error("Failed to process record after retries: {}", record, exception);
                },
                backOff
        );
    }
}
