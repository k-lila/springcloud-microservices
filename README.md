# Sistema de Microsserviços — ERP Simplificado

Este projeto é um conjunto de **microsserviços em Spring Boot** que simulam um pequeno sistema ERP, com os módulos de **Clientes**, **Produtos**, **Estoque** e **Vendas**.  
Os serviços se comunicam entre si via **Feign Clients** e possuem configuração centralizada pelo **Spring Cloud Config Server**.  
O banco de dados utilizado é o **MongoDB**, e toda a aplicação pode ser executada com **Docker Compose**.

---

http://localhost:9091/ - Eureka

---

Config Server 8888 — Central de configuração dos microsserviços
Client Service 8081 http://localhost:8081/swagger-ui.html
Product Service 8082 http://localhost:8082/swagger-ui.html
Stock Service 8083 http://localhost:8083/swagger-ui.html
Sale Service 8084 http://localhost:8084/swagger-ui.html

---

Para os testes, suba o banco de dados com **docker compose up** e o Config Server com **mvn spring-boot: run**
