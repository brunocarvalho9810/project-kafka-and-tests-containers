package project.kafka.cpf_validator.entities;

import com.cpf_validator.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import project.kafka.cpf_validator.feign.response.ResponseInverTexto;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testUserEntityConstructor() {
        // Mock da classe User
        User userAvro = Mockito.mock(User.class);
        Mockito.when(userAvro.getName()).thenReturn("João");
        Mockito.when(userAvro.getCpf()).thenReturn("12345678900");

        // Mock da classe ResponseInverTexto
        ResponseInverTexto responseInverTexto = Mockito.mock(ResponseInverTexto.class);
        Mockito.when(responseInverTexto.isValid()).thenReturn(true);
        Mockito.when(responseInverTexto.getFormatted()).thenReturn("123.456.789-00");

        // Construindo o objeto UserEntity
        UserEntity userEntity = new UserEntity(userAvro, responseInverTexto);

        // Validações
        assertEquals("João", userEntity.getName());
        assertTrue(userEntity.isValid());
        assertEquals("123.456.789-00", userEntity.getCpf());

        // Caso a formatação do CPF esteja nula, deve-se pegar o valor original do userAvro
        Mockito.when(responseInverTexto.getFormatted()).thenReturn(null);
        userEntity = new UserEntity(userAvro, responseInverTexto);

        // Validação
        assertEquals("12345678900", userEntity.getCpf());
    }
}
