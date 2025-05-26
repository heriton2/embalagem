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
          "caixa_id": "Caixa 2",
          "produtos": ["PS5", "Volante"]
        }
      ]
    }
  ]
}
```

## Explicação do algoritmo

O algoritmo de empacotamento funciona da seguinte forma:

1. Os produtos são ordenados do maior para o menor com base no volume
2. Para cada produto:
    - Verifica-se se ele cabe em alguma das caixas já utilizadas
    - Se não couber, tenta-se alocar em uma nova caixa do menor tamanho possível
    - Se não couber em nenhuma caixa disponível, é marcado com uma observação
3. O algoritmo considera todas as possíveis orientações do produto (6 possibilidades)
4. A solução busca minimizar o número total de caixas utilizadas

## Testes

Para executar os testes unitários:

```bash
mvn test
```

## Estrutura do projeto

```
src/
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
│   │                   ├── EmbalagensService.java
│   │                   └── EmpacotamentoService.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/
        └── com/
            └── lojajogos/
                └── embalagem/
                    └── service/
                        └── EmpacotamentoServiceTest.java
```