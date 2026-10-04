<h1 align="center">TISS Web Crawler</h1>

## Descrição

Bem-vindo ao projeto **TISS Web Crawler**. O objetivo desta plataforma é automatizar o acesso e a extração de dados do site oficial da ANS (Agência Nacional de Saúde Suplementar), focando na documentação do Padrão TISS (Troca de Informação de Saúde Suplementar).

Trata-se de uma aplicação interativa via console escrita em **Groovy**, onde o sistema navega pelas páginas da ANS, extrai o histórico de versões do TISS, faz o download automático de arquivos anexos (como componentes de comunicação e planilhas de erros) e envia relatórios atualizados com os anexos, por e-mail, para uma lista de pessoas interessadas.

O projeto segue um padrão arquitetural limpo com forte separação de responsabilidades, dividindo lógicas de UI (`ConsoleUI`), regras de negócio (`Services`), e extração de dados (`WebCrawler` e `WebClient`).

---

## Funcionalidades

- **Web Scraping Dinâmico**: Acessa e interpreta páginas HTML em tempo real no site da ANS para localizar links atualizados automaticamente.
- **Extração e Histórico**: Lê a tabela de histórico de versões do TISS (desde 2016) e exporta os dados formatados para um arquivo CSV estruturado.
- **Download Automatizado**: Baixa os arquivos vitais de forma automática (ZIP de comunicação e Planilha XLSX de erros).
- **Gerenciamento de Interessados**: Cadastro, listagem e remoção de interessados (salvos em CSV) para recebimento do relatório.
- **Envio de Relatórios via E-mail**: Integração com protocolo SMTP para enviar o relatório consolidado por e-mail com todos os três arquivos baixados em anexo.

---

## Estrutura do Projeto

```
src/
└── main/
    └── groovy/
        └── org/
            └── uGustavoDev/
                ├── Main.groovy                    # Ponto de entrada (Roteador principal)
                ├── client/
                │   └── WebClient.groovy           # Encapsula requisições base de conexão
                ├── crawler/
                │   └── WebCrawler.groovy          # Lógica principal de scraping e navegação
                ├── exporter/
                │   └── CsvExporter.groovy         # Exporta coleções de dados para arquivos CSV
                ├── service/                       # Camada de regras de negócio
                │   ├── EmailInteressadoCsvService.groovy
                │   ├── EmailInteressadoService.groovy
                │   ├── EmailService.groovy
                │   └── RelatorioService.groovy    # Integra os arquivos baixados com o envio de emails
                ├── model/                         # Classes de Entidade / Modelos
                │   ├── Email.groovy
                │   ├── EmailInteressado.groovy
                │   ├── SmtpConfig.groovy
                │   └── VersaoTiss.groovy
                └── ui/
                    └── ConsoleUI.groovy           # Único local que interage com o terminal (Inputs/Prints)
```

---

## Entidades e Modelos

| Entidade               | Propriedades Principais                                                                                         |
|------------------------|-----------------------------------------------------------------------------------------------------------------|
| **VersaoTiss**         | Contém `competencia`, `publicacao` e `inicioVigencia`. Representa uma linha extraída do histórico.              |
| **EmailInteressado**   | Entidade do destinatário que receberá o relatório. Contém `nome` e `email`.                                     |
| **Email**              | Estrutura de disparo contendo `destinatario`, `assunto`, `mensagem` e `anexos` (Lista de Arquivos gerados).     |
| **SmtpConfig**         | Estrutura que guarda as credenciais e portas SMTP carregadas do ambiente.                                       |

---

## Tecnologias

- **Groovy 3** (Linguagem escolhida para produtividade e scripts expressivos na JVM)
- **Gradle** (Gerenciador de dependências e automação de build)
- **Jsoup** (Biblioteca oficial para conexão e interpretação DOM/HTML das páginas da ANS)
- **HttpBuilder-NG** (Cliente HTTP robusto usado para realizar os downloads dos anexos)
- **OpenCSV** (Escrita confiável do histórico em formato estruturado CSV)
- **Jakarta Mail** (Integração oficial para comunicação SMTP e manipulação de anexos)
- **Dotenv** (Leitura segura das variáveis de ambiente e senhas a partir do `.env`)

---

## Como Executar

1. Clone o repositório:
    ```bash
    git clone https://github.com/uGustavoB/WebCrawler.git
    ```

2. Navegue até o diretório do projeto:
    ```bash
    cd WebCrawler
    ```

3. Configure o arquivo de variáveis de ambiente:
    - Crie um arquivo `.env` na raiz do projeto (como configurado no `SmtpConfig`) contendo as credenciais da sua conta de e-mail usada para os envios.

4. Execute a Aplicação usando o Gradle Wrapper:
    - **No Windows:**
      ```bash
      gradlew.bat -q --console plain run
      ```
    - **No Linux/Mac:**
      ```bash
      ./gradlew -q --console plain run
      ```

---

## Autores

Este projeto foi desenvolvido por:

- **Gustavo Gabriel** - [GitHub](https://github.com/uGustavoB)
