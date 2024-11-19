package project.kafka.cpf_validator;

import org.springframework.boot.SpringApplication;

public class TestCpfValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.from(CpfValidatorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
