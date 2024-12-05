package project.kafka.cpf_validator.events;

import com.cpf_validator.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import project.kafka.cpf_validator.entities.UserEntity;
import project.kafka.cpf_validator.feign.InvertextoFeignClient;
import project.kafka.cpf_validator.feign.response.ResponseInverTexto;
import project.kafka.cpf_validator.repository.UserRepository;

import static project.kafka.cpf_validator.config.KafkaConfig.CPF_VALIDATOR_CONTAINER_FACTORY;

@Service
@Slf4j
public class CpfValidatorListener {
    private final UserRepository userRepository;
    private final InvertextoFeignClient invertextoFeignClient;

    public CpfValidatorListener(UserRepository userRepository, InvertextoFeignClient invertextoFeignClient) {
        this.userRepository = userRepository;
        this.invertextoFeignClient = invertextoFeignClient;
    }

    @KafkaListener(topics = "${main.topic}",
            groupId = "${main.group}",
            containerFactory = CPF_VALIDATOR_CONTAINER_FACTORY)
    public void listen(ConsumerRecord<String, User> record) {
        log.info("Mensagem consumida com sucesso. -> {}", record.value());
        try {
            User userAvro = record.value();
            ResponseInverTexto responseInverTexto = invertextoFeignClient.validateValue(userAvro.getCpf().toString());
            log.info("Retorno invertexto. -> {}", responseInverTexto.toString());

            UserEntity user = new UserEntity(userAvro, responseInverTexto);
            userRepository.save(user);
            log.info("Usuario inserido com sucesso. -> {}", user);
        } catch (Exception ex) {
            log.error("Erro ao processar mensagem. -> {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
