package project.kafka.cpf_validator.integrationTest.config;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Profile;

@RunWith(Cucumber.class)
@Profile("test")
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"project.kafka.cpf_validator.integrationTest.steps", "project.kafka.cpf_validator.integrationTest.config"},
        plugin = {"pretty", "html:target/cucumber-reports"},
        monochrome = true
)
public class RunCucumberTest {
}
