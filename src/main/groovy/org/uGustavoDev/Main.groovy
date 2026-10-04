package org.uGustavoDev


import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.SmtpConfig
import org.uGustavoDev.service.EmailInteressadoService
import org.uGustavoDev.service.EmailService
import org.uGustavoDev.ui.ConsoleUI

class Main {

    static void main(String[] args) {

        WebCrawler webCrawler = new WebCrawler()

        SmtpConfig config = SmtpConfig.carregar()
        EmailService emailService = new EmailService(config)

        int opcao

        do {
            opcao = ConsoleUI.pedirOpcaoPrincipal()

            switch (opcao) {

                case 1:
                    webCrawler.baixarDocumentoDeComunicacao()
                    break

                case 2:
                    File arquivoHistorico = webCrawler.obterHistoricoTiss()

                    Email email = ConsoleUI.pedirEmailComHistorico(
                            arquivoHistorico
                    )

                    emailService.enviar(email)

                    ConsoleUI.aguardarContinuacao()
                    break

                case 0:
                    println "Encerrando aplicação..."
                    break
            }

        } while (opcao != 0)
    }

    static void gerenciarInteressados(EmailInteressadoService service) {
        int opcao

        do {
            opcao = ConsoleUI.pedirOpcaoInteressados()

            switch (opcao) {

                case 1:
                    service.listar().each {
                        println "${it.nome} - ${it.email}"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break

                case 2:
                    def interessado =
                            ConsoleUI.pedirEmailInteressado()

                    service.cadastrar(interessado)

                    println "Interessado cadastrado!"
                    ConsoleUI.aguardarContinuacao()
                    break

                case 3:
                    String email =
                            ConsoleUI.lerTexto("Email para remover: ")

                    service.remover(email)

                    println "Interessado removido!"
                    ConsoleUI.aguardarContinuacao()
                    break
            }

        } while (opcao != 0)
    }
}
