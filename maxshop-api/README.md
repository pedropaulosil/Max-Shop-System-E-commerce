# MaxShop API

Backend do marketplace MaxShop. Java 21 + Spring Boot 3 + Spring Security (JWT) + Spring Data JPA + PostgreSQL + Flyway. Maven como build tool.

## Aviso sobre hospedagem

**Vercel não suporta aplicações Spring Boot** (roda apenas funções serverless Node.js/Python/Go/Edge e frontends estáticos, não processos JVM de longa duração). Este projeto está preparado para deploy em **Render** (usado abaixo), mas funciona da mesma forma em Railway, Fly.io ou qualquer serviço que rode containers Docker. O frontend (a página MaxShop em HTML) pode continuar hospedado no Vercel normalmente e apontar para a URL desta API.

## Arquitetura

```
controller  -> recebe a requisição HTTP, valida entrada (DTO)
service     -> regra de negócio, transações
repository  -> acesso a dados (Spring Data JPA)
entity      -> mapeamento das tabelas
security    -> JWT (geração, validação, filtro por requisição)
config      -> SecurityConfig (CORS, rotas públicas/protegidas, RBAC por papel)
exception   -> tratamento centralizado de erros (GlobalExceptionHandler)
```

Papéis (`role`): `CLIENTE`, `VENDEDOR`, `ADMIN` — aplicados via `hasRole()` no `SecurityConfig` e `@AuthenticationPrincipal` nos controllers. Escalabilidade: API stateless (sem sessão em memória, JWT carrega o contexto), pronta para rodar múltiplas instâncias atrás de um load balancer; banco relacional com índices nas colunas de busca/filtro (`products.category_id`, `products.seller_id`, busca full-text em `name`).

## Rodando localmente

Pré-requisitos: Java 21, Maven, Docker (para o Postgres).

```bash
docker compose up -d db
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Documentação interativa (Swagger) em `http://localhost:8080/docs`.

Ou suba tudo (API + banco) via Docker:

```bash
docker compose up --build
```

## Variáveis de ambiente

| Variável | Descrição | Padrão local |
|---|---|---|
| `DATABASE_URL` | URL JDBC do Postgres | `jdbc:postgresql://localhost:5432/maxshop` |
| `DATABASE_USERNAME` | Usuário do banco | `maxshop` |
| `DATABASE_PASSWORD` | Senha do banco | `maxshop` |
| `JWT_SECRET` | Chave de assinatura do token (mín. 32 caracteres) | — |
| `JWT_ACCESS_EXP` | Expiração do access token (ms) | `3600000` (1h) |
| `JWT_REFRESH_EXP` | Expiração do refresh token (ms) | `2592000000` (30d) |
| `CORS_ALLOWED_ORIGINS` | Origens liberadas (separadas por vírgula) | `http://localhost:3000` |
| `PORT` | Porta da aplicação | `8080` |

## Principais endpoints

```
POST   /api/auth/register        cadastro (cliente ou vendedor)
POST   /api/auth/login           login -> access token + refresh token
POST   /api/auth/refresh         renova o access token

GET    /api/categories           lista categorias
GET    /api/products             busca com filtros (q, categoryId, minPrice, maxPrice) e paginação
GET    /api/products/{id}        detalhe do produto

POST   /api/sellers/products     cria produto            [VENDEDOR/ADMIN]
PUT    /api/sellers/products/{id} atualiza produto        [VENDEDOR/ADMIN]
DELETE /api/sellers/products/{id} desativa produto        [VENDEDOR/ADMIN]

GET    /api/cart                 carrinho do usuário logado   [autenticado]
POST   /api/cart/items           adiciona item ao carrinho    [autenticado]
DELETE /api/cart/items/{id}      remove item do carrinho      [autenticado]

POST   /api/orders/checkout      finaliza o pedido (calcula frete, baixa estoque)  [autenticado]
GET    /api/orders               histórico de pedidos do cliente                   [autenticado]
GET    /api/orders/{id}          detalhe do pedido                                 [autenticado]
```

Rotas autenticadas exigem o header `Authorization: Bearer <accessToken>`.

## Deploy no Render

1. Suba este repositório no GitHub.
2. No Render, escolha **New > Blueprint** e aponte para o repositório — ele lê o `render.yaml` e cria automaticamente o serviço web (via `Dockerfile`) e o banco Postgres gerenciado.
3. Confira as variáveis geradas em **Environment**: se a interpolação de `DATABASE_URL` não vier preenchida automaticamente, monte-a manualmente como `jdbc:postgresql://<host>:<port>/<database>` usando os dados exibidos na aba do banco Postgres.
4. Faça o deploy. O Flyway roda as migrations automaticamente na primeira inicialização.
5. Atualize `CORS_ALLOWED_ORIGINS` com o domínio real do frontend hospedado no Vercel.

## Deploy manual via Docker (qualquer provedor)

```bash
docker build -t maxshop-api .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://<host>:5432/maxshop \
  -e DATABASE_USERNAME=maxshop \
  -e DATABASE_PASSWORD=<senha> \
  -e JWT_SECRET=<chave-forte> \
  -e CORS_ALLOWED_ORIGINS=https://seu-frontend.vercel.app \
  maxshop-api
```

## Próximos módulos sugeridos (não incluídos nesta versão)

Avaliações, chat, cupons/cashback, carteira digital, notificações, relatórios/BI, integrações de pagamento (Pix/gateway) e frete real (correios/transportadora), painel administrativo completo e IA de recomendação — a estrutura em camadas atual foi pensada para receber esses módulos como novas entidades/services/controllers sem refatoração do núcleo.
