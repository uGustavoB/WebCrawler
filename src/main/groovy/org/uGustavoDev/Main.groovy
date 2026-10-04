package org.uGustavoDev


import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado
import org.uGustavoDev.model.SmtpConfig
import org.uGustavoDev.service.EmailInteressadoCsvService
import org.uGustavoDev.service.EmailInteressadoService
import org.uGustavoDev.service.EmailService
import org.uGustavoDev.service.RelatorioService
import org.uGustavoDev.ui.ConsoleUI

class Main {

    static void main(String[] args) {

        WebCrawler webCrawler = new WebCrawler()

        SmtpConfig config = SmtpConfig.carregar()
        EmailService emailService = new EmailService(config)
        EmailInteressadoCsvService csvService = new EmailInteressadoCsvService()
        EmailInteressadoService interessadoService = new EmailInteressadoService(csvService)

        int opcao

        do {
            opcao = ConsoleUI.pedirOpcaoPrincipal()

            switch (opcao) {

                case 1:
                    gerenciarInteressados(interessadoService)
                    break

                case 2:
                    File arquivoHistorico = webCrawler.obterHistoricoTiss()

                    Email email = ConsoleUI.pedirEmailComHistorico(
                            arquivoHistorico
                    )

                    emailService.enviar(email)

                    ConsoleUI.aguardarContinuacao()
                    break

                case 3:
                    RelatorioService relatorioService = new RelatorioService(webCrawler, emailService, interessadoService)
                    relatorioService.baixarEEnviarParaInteressados()
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
                    List<EmailInteressado> interessados = service.listar()

                    if (interessados.isEmpty()) {
                        println "Nenhum interessado cadastrado."
                        ConsoleUI.aguardarContinuacao()
                        break
                    } else {
                        println "Lista de interessados:"
                    }
                    interessados.each {
                        println "${it.nome} - ${it.email}"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break

                case 2:
                    def interessado =
                            ConsoleUI.pedirEmailInteressado()

                    try {
                        service.cadastrar(interessado)
                        println "Interessado cadastrado com sucesso!"
                    } catch (IllegalArgumentException e) {
                        println e.message
                    }

                    ConsoleUI.aguardarContinuacao()
                    break

                case 3:
                    String email =
                            ConsoleUI.lerTexto("Email para remover: ")

                    service.remover(email)

                    println "Interessado removido!"
                    ConsoleUI.aguardarContinuacao()
                    break

                case 4:
                    String emailOriginal = ConsoleUI.lerTexto("Email para editar: ")

                    println "Digite os novos dados:"
                    String novoNome = ConsoleUI.lerTexto("Novo Nome: ")
                    String novoEmail = ConsoleUI.lerTexto("Novo Email: ")

                    try {
                        service.editar(emailOriginal, new EmailInteressado(nome: novoNome, email: novoEmail))
                        println "Interessado atualizado com sucesso!"
                    } catch (IllegalArgumentException e) {
                        println e.message
                    }

                    ConsoleUI.aguardarContinuacao()
                    break
            }

        } while (opcao != 0)
    }
}
