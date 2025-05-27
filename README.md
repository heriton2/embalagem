# API de Embalagens - Loja de Jogos Online

API desenvolvida para automatizar o processo de embalagem dos pedidos de uma loja de jogos online. Esta API determina qual o tamanho de caixa deve ser usado para cada pedido e quais produtos devem ser colocados em cada caixa.

## Funcionalidades

- Recebe pedidos com múltiplos produtos e suas dimensões
- Determina o tamanho de caixa ideal para cada conjunto de produtos
- Otimiza o número de caixas utilizadas
- Identifica produtos que não cabem em nenhuma caixa disponível
- Documentação completa via Swagger
- Segurança via autenticação básica

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.2.1
- Spring Security
- SpringDoc OpenAPI 3
- JUnit 5
- Docker
- Maven

## Requisitos

- Java 17+
- Maven 3.6+
- Docker e Docker Compose (para execução em container)

## Como executar

### Usando Docker (recomendado)

1. Clone o repositório:
```bash
git clone https://github.com/seunome/lojajogos-embalagens-api.git
cd lojajogos-embalagens-api
```

2. Construa e execute o container:
```bash
mvn clean package
docker-compose up
```

3. A API estará disponível em: http://localhost:8080
4. A documentação Swagger estará em: http://localhost:8080/swagger-ui.html

### Usando Maven

1. Clone o repositório:
```bash
git clone https://github.com/seunome/lojajogos-embalagens-api.git
cd lojajogos-embalagens-api
```

2. Execute o projeto:
```bash
mvn spring-boot:run
```

## Autenticação

A API está protegida por autenticação básica:
- Usuário: admin
- Senha: admin

## Tipos de Caixas Disponíveis
O algoritmo considera os seguintes tipos de caixas pré-definidas, listadas da menor para a maior em termos de volume e estratégia de tentativa:
- Caixa 1: Dimensões 30 (altura) x 40 (largura) x 80 (comprimento)
- Caixa 2: Dimensões 80 (altura) x 50 (largura) x 40 (comprimento)
- Caixa 3: Dimensões 50 (altura) x 80 (largura) x 60 (comprimento)

## Exemplo de uso

### Requisição

```bash
curl -X POST http://localhost:8080/api/v1/embalagens \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{
    "pedidos": [
      {
        "pedido_id": 1,
        "produtos": [
          {"produto_id": "PS5", "dimensoes": {"altura": 40, "largura": 10, "comprimento": 25}},
          {"produto_id": "Volante", "dimensoes": {"altura": 40, "largura": 30, "comprimento": 30}}
        ]
      }
    ]
  }'
```

### Resposta

```json
{
   "pedidos": [
      {
         "pedido_id": 1,
         "caixas": [
            {
               "caixa_id": "Caixa 1",
               "produtos": ["Volante", "PS5"],
               "observacao": null
            }
         ]
      }
   ]
}
```

## Explicação do algoritmo

O algoritmo de empacotamento (EmpacotamentoServiceImpl) funciona da seguinte forma para cada pedido:

1. *Ordenação Inicial*: Os produtos do pedido são ordenados em ordem decrescente de volume.
2. *Tentativa de Combinação*: Para cada tipo de caixa (da menor para a maior):
   - Gera todas as possíveis combinações de produtos, começando com os maiores grupos
   - Verifica se cada combinação cabe na caixa atual, considerando todas as 6 possíveis orientações do produto
   - Seleciona a combinação que maximiza o uso da caixa (maior número de produtos ou maior volume)
3. *Alocação Recursiva*:
   - Após alocar produtos em uma caixa, o algoritmo continua com os produtos restantes
   - Tenta usar a menor caixa possível para cada grupo de produtos
4. *Produtos não Alocáveis*: Produtos que não cabem em nenhuma caixa disponível são marcados com uma observação especial e colocados em uma "caixa especial"

O algoritmo utiliza uma abordagem recursiva e combinatória para encontrar a melhor alocação possível de produtos em caixas, minimizando o número total de caixas necessárias.

## Testes

Para executar os testes unitários:

```bash
mvn test
```

## Estrutura do projeto

```
.
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── lojajogos/
    │   │           └── embalagem/
    │   │               ├── EmbalagensApiApplication.java
    │   │               ├── config/
    │   │               │   ├── SecurityConfig.java
    │   │               │   └── SwaggerConfig.java
    │   │               ├── controller/
    │   │               │   └── EmbalagemController.java
    │   │               ├── dto/
    │   │               │   ├── request/
    │   │               │   │   ├── DimensaoDTO.java
    │   │               │   │   ├── PedidoDTO.java
    │   │               │   │   └── ProdutoDTO.java
    │   │               │   └── response/
    │   │               │       ├── CaixaDTO.java
    │   │               │       ├── PedidoResponseDTO.java
    │   │               │       └── ResponseDTO.java
    │   │               ├── model/
    │   │               │   ├── Caixa.java
    │   │               │   ├── Dimensao.java
    │   │               │   ├── Pedido.java
    │   │               │   └── Produto.java
    │   │               └── service/
    │   │                   ├── EmbalagensService.java      # Interface
    │   │                   ├── EmpacotamentoService.java   # Interface
    │   │                   └── impl/
    │   │                       ├── EmbalagensServiceImpl.java
    │   │                       └── EmpacotamentoServiceImpl.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/
            └── com/
                └── lojajogos/
                    └── embalagem/
                        ├── EmbalagemApplicationTests.java
                        ├── controller/
                        │   └── EmbalagemControllerTest.java
                        └── service/
                            └── impl/
                                ├── EmbalagensServiceImplTest.java
                                └── EmpacotamentoServiceImplTest.java
```