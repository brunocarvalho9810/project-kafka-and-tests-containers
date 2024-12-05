package project.kafka.cpf_validator.entities;

import com.cpf_validator.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import project.kafka.cpf_validator.feign.response.ResponseInverTexto;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cpf;
    private boolean valid;

    public UserEntity(User userAvro, ResponseInverTexto responseInverTexto) {
        this.setName(userAvro.getName().toString());
        this.setValid(responseInverTexto.isValid());
        this.setCpf(responseInverTexto.getFormatted() != null ?
                responseInverTexto.getFormatted() :
                userAvro.getCpf().toString());
    }
}

