package project.kafka.cpf_validator.integrationTest.steps;

import io.cucumber.java.pt.E;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestStep {

    public static String MESSAGE;

    @E("Criar um request generico para o topico main")
    public void criarUmRequestGenericoParaOTopicoMain() throws IOException {
        MESSAGE = new String(Files.readAllBytes(Paths.get("src/test/resources/json/generic-request.json")));
    }
}
