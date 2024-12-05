package project.kafka.cpf_validator.feign.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseInverTexto {
    private boolean valid;
    private String formatted;
}
