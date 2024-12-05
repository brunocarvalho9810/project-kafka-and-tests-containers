package project.kafka.cpf_validator.events;

import com.cpf_validator.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import project.kafka.cpf_validator.entities.UserEntity;
import project.kafka.cpf_validator.feign.InvertextoFeignClient;
import project.kafka.cpf_validator.feign.response.ResponseInverTexto;
import project.kafka.cpf_validator.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CpfValidatorListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvertextoFeignClient invertextoFeignClient;

    @InjectMocks
    private CpfValidatorListener cpfValidatorListener;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListen_Success() throws Exception {
        ConsumerRecord<String, User> record = mock(ConsumerRecord.class);
        User userAvro = new User();
        userAvro.setCpf("12345678901");
        userAvro.setName("John Connor");
        when(record.value()).thenReturn(userAvro);

        ResponseInverTexto responseInverTexto = new ResponseInverTexto();
        responseInverTexto.setValid(true);
        responseInverTexto.setFormatted("123.456.789-01");
        when(invertextoFeignClient.validateValue("12345678901")).thenReturn(responseInverTexto);

        cpfValidatorListener.listen(record);

        verify(userRepository).save(userEntityCaptor.capture());
        UserEntity savedUser = userEntityCaptor.getValue();
        assertEquals("123.456.789-01", savedUser.getCpf());
        assertTrue(savedUser.isValid());
    }

    @Test
    void testListen_InvalidMessage() {
        ConsumerRecord<String, User> record = mock(ConsumerRecord.class);
        when(record.value()).thenReturn(null);

        assertThrows(Exception.class, () -> cpfValidatorListener.listen(record));
        verifyNoInteractions(userRepository, invertextoFeignClient);
    }

    @Test
    void testListen_FeignClientError() {
        ConsumerRecord<String, User> record = mock(ConsumerRecord.class);
        User userAvro = new User();
        userAvro.setCpf("12345678901");
        userAvro.setName("John Connor");
        when(record.value()).thenReturn(userAvro);

        when(invertextoFeignClient.validateValue("12345678901"))
                .thenThrow(new RuntimeException("Erro no FeignClient"));

        Exception exception = assertThrows(RuntimeException.class, () -> cpfValidatorListener.listen(record));
        assertEquals("Erro no FeignClient", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    void testListen_RepositoryError() {
        ConsumerRecord<String, User> record = mock(ConsumerRecord.class);
        User userAvro = new User();
        userAvro.setCpf("12345678901");
        userAvro.setName("John Connor");
        when(record.value()).thenReturn(userAvro);

        ResponseInverTexto responseInverTexto = new ResponseInverTexto();
        responseInverTexto.setValid(true);
        responseInverTexto.setFormatted("123.456.789-01");
        when(invertextoFeignClient.validateValue("12345678901")).thenReturn(responseInverTexto);

        doThrow(new RuntimeException("Erro ao salvar no banco"))
                .when(userRepository)
                .save(any(UserEntity.class));

        Exception exception = assertThrows(RuntimeException.class, () -> cpfValidatorListener.listen(record));
        assertEquals("Erro ao salvar no banco", exception.getMessage());

        verify(userRepository).save(any(UserEntity.class));
    }
}
