# DIO Spring Boot - Projeto Final 05: Spring AI (Budgeting)

## 📌 Descrição do Projeto

Este projeto é o módulo final da trilha **DIO Spring Boot Learning Track**, implementando um **Assistente Financeiro Inteligente por Voz** baseado em **Spring AI**.

A aplicação combina recursos avançados de Inteligência Artificial Generativa com uma arquitetura em camadas orientada a **Domain-Driven Design (DDD)** e **Clean Architecture**, permitindo que usuários interajam por texto ou voz para registrar gastos, listar transações e consultar o saldo acumulado.

---

## 🎯 Fluxo Principal de Processamento por Voz

```text
[Áudio do Usuário (m4a/mp3)]
           │
           ▼
[TranscriptionModel (OpenAI Whisper-1)]
           │ (Texto Transcrito)
           ▼
[ChatClient com Tool Calling (OpenAI GPT-4o-mini)]
     ├──> persist-transaction (PersistTransactionUseCase)
     ├──> list-transactions-by-category (ListTransactionsByCategoryUseCase)
     └──> calculate-total-balance (CalculateTotalBalanceUseCase)  <-- NOVA FEATURE
           │
           ▼
[TextToSpeechModel (OpenAI TTS)]
           │
           ▼
[Resposta em Áudio MP3 gerada para o Usuário]
```

---

## 🚀 Melhoria Implementada: Ferramenta de Consulta de Saldo (`CalculateTotalBalanceUseCase`)

Foi desenvolvida e integrada uma nova funcionalidade via **Spring AI Tool Calling**:

* **Classe do Caso de Uso**: [`CalculateTotalBalanceUseCase`](src/main/java/dio/budgeting/application/CalculateTotalBalanceUseCase.java)
* **Anotação de Tool**: `@Tool(name = "calculate-total-balance", description = "Calcula o saldo total acumulado de todas as transações financeiras cadastradas")`
* **Comportamento**:
  1. Recupera todas as transações salvas via `TransactionRepository.findAll()`.
  2. Realiza o somatório preciso dos valores com `BigDecimal`.
  3. Formata os valores monetários no padrão brasileiro (`R$`) e gera a contagem total de transações.
  4. Retorna um record [`BalanceOutput`](src/main/java/dio/budgeting/application/output/BalanceOutput.java) estruturado e amigável.
* **Integração no ChatClient**: Registrado no `ChatClient.Builder` em [`TransactionController`](src/main/java/dio/budgeting/infrastructure/http/TransactionController.java) e orientado pelo prompt de sistema [`system-message.st`](src/main/resources/prompts/system-message.st), permitindo que o modelo decida acionar a ferramenta automaticamente quando o usuário perguntar *"Qual é o meu saldo?"*, *"Quanto eu gastei até agora?"* ou *"Me dê o balanço geral"*.
* **Endpoint REST Direto**: Disponibilizado o endpoint `GET /transactions/balance` para testes diretos via HTTP.

---

## 🛠️ Tecnologias Utilizadas

* **Java 25**
* **Spring Boot 4.0.5**
* **Spring AI 2.0.0-M4** (`spring-ai-starter-model-openai`)
* **Spring Data JPA & Hibernate**
* **MySQL 9.6** (via Docker Compose)
* **OpenAI APIs**:
  * Chat: `gpt-4o-mini`
  * Transcrição de Áudio (STT): `whisper-1`
  * Síntese de Áudio (TTS): `gpt-4o-mini-tts`
* **Lombok**
* **JUnit 5 & Mockito**

---

## 📋 Pré-requisitos

1. **Java JDK 25** instalado e configurado no `PATH`.
2. **Docker** e **Docker Compose** ativos para subir o banco de dados MySQL.
3. **Chave de API da OpenAI** (`OPENAI_API_KEY`).

---

## ⚙️ Como Executar a Aplicação

### 1. Subir o Banco de Dados com Docker Compose

Na pasta `05-spring-ai`:

```bash
docker compose up -d
```

### 2. Configurar a Variável de Ambiente da OpenAI

No Linux / macOS:
```bash
export OPENAI_API_KEY="sk-proj-sua-chave-openai-aqui"
```

No Windows (PowerShell):
```powershell
$env:OPENAI_API_KEY="sk-proj-sua-chave-openai-aqui"
```

No Windows (CMD):
```cmd
set OPENAI_API_KEY=sk-proj-sua-chave-openai-aqui
```

### 3. Executar o Projeto com Gradle

```bash
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 🧪 Exemplos de Requisições (cURL)

### 1. Criar Transação Manualmente (REST)

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Supermercado Semanal",
    "category": "FOOD",
    "amount": 15000
  }'
```

### 2. Consultar Saldo e Total de Gastos (Nova Feature)

```bash
curl -X GET http://localhost:8080/transactions/balance
```

**Exemplo de Resposta:**
```json
{
  "transactionCount": 1,
  "totalAmount": 150.0,
  "formattedTotal": "R$ 150,00",
  "message": "O total de gastos acumulado é de R$ 150,00 em um total de 1 transação(ões)."
}
```

### 3. Listar Transações por Categoria

```bash
curl -X GET http://localhost:8080/transactions/FOOD
```

### 4. Processamento Inteligente por Voz (Spring AI STT + Tool Calling + TTS)

Envie um arquivo de áudio (ex: `recording.m4a` dizendo *"Gastei cinquenta reais na farmácia"* ou *"Qual é o meu saldo total?"*):

```bash
curl -X POST http://localhost:8080/transactions/ai \
  -F "file=@caminho/para/gravacao.m4a" \
  --output resposta.mp3
```

O arquivo `resposta.mp3` retornado conterá a resposta sintetizada em voz do assistente financeiro.

---

## 💡 Principais Aprendizados

* **Model-Agnostic Tools**: Separação clara entre a lógica de negócio (Use Cases com `@Tool`) e a infraestrutura do Spring AI.
* **Arquitetura em Camadas (DDD)**: O domínio (`Transaction`, `Category`, `TransactionRepository`) permanece puro e independente do framework de IA.
* **Orquestração Multimodal**: Conexão fluida de transcrição de áudio, raciocínio baseado em funções/ferramentas e resposta sintetizada por voz.
