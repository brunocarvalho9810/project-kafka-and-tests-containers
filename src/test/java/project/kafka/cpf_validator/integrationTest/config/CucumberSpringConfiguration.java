package project.kafka.cpf_validator.integrationTest.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import project.kafka.cpf_validator.CpfValidatorApplication;

@ActiveProfiles("test")
@CucumberContextConfiguration
@SpringBootTest(classes = CpfValidatorApplication.class)
public class CucumberSpringConfiguration {
}
