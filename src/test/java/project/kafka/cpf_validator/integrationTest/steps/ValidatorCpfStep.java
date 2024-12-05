package project.kafka.cpf_validator.integrationTest.steps;

import com.cpf_validator.User;
import com.google.gson.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import project.kafka.cpf_validator.repository.UserRepository;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@AllArgsConstructor
public class ValidatorCpfStep {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private UserRepository userRepository;

    @Dado("Enviar uma mensagem para topico principal")
    public void EnviarUmaMensagemParaTopicoPrincipal() {
        var User = new GsonBuilder()
                .registerTypeAdapter(CharSequence.class, new CharSequenceAdapter())
                .create()
                .fromJson(RequestStep.MESSAGE, User.class);
        kafkaTemplate.send("myTopic-test", User);
    }

    @Entao("Verifico se a mensagem gravada com sucesso no banco de dados")
    public void verificoSeAMensagemGravadaComSucessoNoBancoDeDados() {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            var listEntity = userRepository.findByCpf("123.456.789-00");
            assertFalse(listEntity.isEmpty());
            assertTrue(listEntity.get(0).isValid());
        });
    }

    public static class CharSequenceAdapter implements JsonDeserializer<String>, JsonSerializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return json.getAsString(); // Converte para String
        }

        @Override
        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src); // Converte de volta para JSON
        }
    }
}
