## PROCESSO SELETIVO CONJUNTO Nº 001/2026/SEPLAG e demais Órgãos - Engenheiro da Computação- Sênior
## NOME: VANDO ANTÔNIO JUNQUEIRA
## Nº INSCRIÇÃO: 16445
## Cargo: ANALISTA DE TECNOLOGIA DA INFORMAÇÃO
## Perfil: ENGENHEIRO DA COMPUTAÇÃO - SÊNIOR

# Documentação do Projeto

## Visão Geral
Este projeto é uma API REST para gerenciamento de Artistas e Álbuns, implementada em Java (Spring Boot 3) com conformidade total aos Requisitos para Backend Sênior.

## Funcionalidades Implementadas
- **Arquitetura**: Capacidades de Domínio (Services, Repositories, Entities).
- **Segurança**: Autenticação JWT (Login/Registro), Endpoints Públicos/Privados, tratamento de CORS.
- **Banco de Dados**: Postgres (Prod/Dev) via Docker, H2 (Testes). Migrations com Flyway ativadas.
- **Armazenamento**: MinIO para upload de imagens (Imagens de Artistas, Capas de Álbuns).
- **Observabilidade**: Health Checks do Actuator, Rate Limiting (Filtro).
- **Documentação**: Swagger UI (`/swagger-ui.html`).

## Pré-requisitos
- **Java 17+**
- **Docker** & **Docker Compose**
- **Maven** (ou use o wrapper incluído, se disponível)

## Como Executar

### 1. Iniciar Infraestrutura
Inicie o Banco de Dados (Postgres) e o Object Storage (MinIO) usando Docker.
```bash
docker-compose up -d
```
Isso iniciará:
- Postgres na porta `5432`
- MinIO na porta `9000` (Console: `9001`)

### 2. Executar a Aplicação
```bash
mvn spring-boot:run
```
(Alternativamente, importe no IntelliJ/Eclipse e execute `MusicApiApplication.java`)

### 3. Acessar a API
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Console do MinIO**: [http://localhost:9001](http://localhost:9001) (Usuário: `minioadmin`, Senha: `minioadmin`)

## Estrutura de Dados

O banco de dados foi modelado para garantir integridade e performance, utilizando as seguintes tabelas:

### Tabela: `artists`
Armazena as informações principais dos artistas.
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | `BIGSERIAL` | Chave primária auto-incrementada. |
| `name` | `VARCHAR(255)` | Nome do artista (Único). |
| `image_url` | `VARCHAR(500)` | URL da imagem no MinIO. |
| `created_at` | `TIMESTAMP` | Auditoria de criação. |
| `updated_at` | `TIMESTAMP` | Auditoria de atualização. |

### Tabela: `albums`
Armazena os álbuns cadastrados.
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | `BIGSERIAL` | Chave primária auto-incrementada. |
| `title` | `VARCHAR(255)` | Título do álbum. |
| `release_year` | `INTEGER` | Ano de lançamento. |
| `cover_url` | `VARCHAR(500)` | URL da capa no MinIO. |
| `created_at` | `TIMESTAMP` | Auditoria de criação. |
| `updated_at` | `TIMESTAMP` | Auditoria de atualização. |

### Tabela: `artist_album`
Tabela associativa para relacionamento N:N entre artistas e álbuns.
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `artist_id` | `BIGINT` | FK para `artists.id` (ON DELETE CASCADE). |
| `album_id` | `BIGINT` | FK para `albums.id` (ON DELETE CASCADE). |

### Tabela: `regionais`
Armazena as regionais cadastradas no sistema.
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | `SERIAL` | Chave primária auto-incrementada. |
| `nome` | `VARCHAR(200)` | Nome da regional. |
| `ativo` | `BOOLEAN` | Status de ativação (Default: TRUE). |
| `external_id` | `INTEGER` | ID externo para integração. |

### Tabela: `users`
Gerencia autenticação e autorização.
| Coluna | Tipo | Descrição |
| :--- | :--- | :--- |
| `id` | `BIGSERIAL` | Chave primária. |
| `username` | `VARCHAR(255)` | Login do usuário (Único). |
| `password` | `VARCHAR(255)` | Senha criptografada (BCrypt). |
| `role` | `VARCHAR(50) ` | Papel de acesso (USER, ADMIN). |
