package project.kafka.cpf_validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "project.kafka.cpf_validator.feign")
public class CpfValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpfValidatorApplication.class, args);
	}

}
