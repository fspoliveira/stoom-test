# Stoom Test

## Como usar
Requisitos de ambiente:
1. Docker e docker-compose instalados;
1. Maven 3.6+ para build;
1. Configurar o volume do banco de dados: (`docker volume create postgresql-volume`)
1. Plugin do Lombok instalado na IDE

Como executar:
1. Para compilar o projeto e criar a imagem docker: `mvn clean install` ou `./mvnw clean install`;
1. Após build processar com sucesso, basta executar um `docker-compose up`.

## Decisões técnicas e _Trade Offs_

#### 

#### Spring Data JPA vs JDBC Template
Embora ambos sejam fáceis de configurar com o Spring, e o JDBC Template seja mais rápido no que diz respeito a performance o volume de código para criar um simples CRUD com o JDBC Template seria maior, também deixaria a aplicação mais acoplada à um único banco de dados.
Desse modo, o Spring Data JPA foi escolhido. 

#### Liquibase como gerenciador de DB:
**Pros**

Os motivos da escolha do Liquibase foram dois:
- Ele ser agnostico ao banco, de modo que, para desenvolvimento utilizei o Postgres mas, para utilizar outro DB bastaria mudar as configurações do JPA e adicionar o driver do novo banco que as migrações também ocorreriam. Deixando, assim, a aplicação o mais próximo de um banco agnóstico também.
- Outro motivo foi o fato de vir configurado _"out-of-the-box"_ com o Spring Boot.

**Contras**

- Os testes de repositório precisam ter a anotação `@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)` de modo que o Liquibase aceite usar um DB sem ser o H2 embedded.

#### Testcontainers vs H2
A ideia por trás do Testcontainers é usar o mesmo banco utilizado pela aplicação ao invés de usar o h2 que é mais genérico.
Também é possível utilizar o Redis em memória para fazer os testes de integração já utilizando o cache se necessário e criar um ambiente mais próximo do ambiente real para os testes.

#### TDD / BDD
Os testes foram quebrados por camada (Repositório, Serviço, Controller) sendo que os testes unitários acontecem para Repositório e Serviços de forma isolada e mockada e foram guiados seguindo TDD.
Já os testes de controller foram guiados seguindo um modelo parecido ao BDD e são testes integrados, ou seja, testam o fluxo inteiro desde o Controller até o banco de dados.
Os testes de serviços, a principio, não são testes Spring de modo a serem mais rápidos na execução. Em um eventual pipeline de testes, podemos rodar primeiro os testes unitários simples, depois os testes integrados e de repositório assim podemos ter um primeiro filtro de qualidade de código mais rápido. 

#### Fixtures
A package de `br.com.stoom.fixtures` dentro dos testes tem como propósito facilitar a criação de alguns cenários / mocks para os testes.

#### Fabric8 Docker Plugin
A imagem docker é gerada pelo plugin da Fabric8 que fica configurado no POM do projeto. No momento do build da aplicação, o plugin gera uma nova versão da imagem.
Idealmente criação e upload da imagem ficariam em um pipeline de CI/CD e o plugin não seria necessário mas, para facilitar o uso do projeto, optei por utilizar o plugin configurado com um Dockerfile, ou seja, para mudar o comportamento e remover o plugin basta removê-lo do POM e a imagem pode ser gerada a partir do comando `docker build -t stoom/stoom-test .` 