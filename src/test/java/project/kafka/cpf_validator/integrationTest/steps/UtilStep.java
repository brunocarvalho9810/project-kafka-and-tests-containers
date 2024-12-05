package project.kafka.cpf_validator.integrationTest.steps;

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.context.annotation.Profile;
import project.kafka.cpf_validator.entities.UserEntity;
import project.kafka.cpf_validator.integrationTest.config.CucumberSpringConfiguration;
import project.kafka.cpf_validator.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static project.kafka.cpf_validator.integrationTest.config.WireMockTestContainer.wireMockContainer;
import static project.kafka.cpf_validator.integrationTest.config.KafkaTestContainer.*;

@AllArgsConstructor
@Profile("test")
public class UtilStep extends CucumberSpringConfiguration {

    private UserRepository userRepository;

    private final AdminClient adminClient;
    @Dado("Que eu tenho um container kafka em execução")
    public void queEuTenhoUmContainerKafka() {
        var containerKafkaIsUp = kafka.isRunning();
        if (!containerKafkaIsUp) {
            kafka.start();
            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertTrue(kafka.isRunning()));
        }
    }

    @E("Topicos kafka em execucao")
    public void topicosKafkaEmExecucao() throws ExecutionException, InterruptedException {
        var topics = getTopics();
        assertTrue(topics.contains("myTopic-test"));
    }

    @E("Registro meu avro no schema registry")
    public void registroMeuAvroNoSchemaRegistry() throws RestClientException, IOException {
        registerSchema(urlSchemaRegistry, "myTopic-test", "src/main/avro/User.avsc");
    }

    @Dado("Que eu tenho um container wiremock")
    public void queEuTenhoUmContainerWiremock() {
        var containerWireMockIsUp = wireMockContainer.isRunning();
        if (!containerWireMockIsUp) {
            wireMockContainer.start();
            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> assertTrue(wireMockContainer.isRunning()));
        }
    }

    @E("Garantir que meu objeto nao existe no banco de dados")
    public void garantirQueMeuObjetoNaoExisteNoBancoDeDados() {
        List<UserEntity> listUsers = userRepository.findByCpf("123.456.789.00");
        if (!listUsers.isEmpty()) {
            for (UserEntity user : listUsers) {
                var id = user.getId();
                userRepository.deleteById(id);
            }
        }
    }

    public Set<String> getTopics() throws InterruptedException, ExecutionException {
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(false);
        var topics = adminClient.listTopics(options);
        KafkaFuture<Set<String>> names = topics.names();
        return names.get();
    }
}
