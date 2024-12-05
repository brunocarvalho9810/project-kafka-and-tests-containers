# language: pt

Funcionalidade: Validar CPF atraves da API do invertexto

  Contexto:
    Dado Que eu tenho um container kafka em execução
    E Topicos kafka em execucao
    E Registro meu avro no schema registry
    E Que eu tenho um container wiremock
    E Criar um request generico para o topico main
    E Garantir que meu objeto nao existe no banco de dados

  Cenario: Verificar um CPF verdadeiro e salvar no banco de dados como true
    Dado Que a API do invertexto retorna sucesso
    Quando Enviar uma mensagem para topico principal
    Entao Verifico se a mensagem gravada com sucesso no banco de dados
