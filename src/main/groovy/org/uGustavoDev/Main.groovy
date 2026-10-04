package org.uGustavoDev


import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado
import org.uGustavoDev.model.SmtpConfig
import org.uGustavoDev.service.EmailInteressadoCsvService
import org.uGustavoDev.service.EmailInteressadoService
import org.uGustavoDev.service.EmailService
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
                    println "Baixando arquivos atualizados..."
                    File arqComunicacao = webCrawler.baixarDocumentoDeComunicacao()
                    File arqErros = webCrawler.baixarTabelaDeErros()
                    File arqHistorico = webCrawler.obterHistoricoTiss()
                    
                    List<File> anexos = [arqComunicacao, arqErros, arqHistorico]
                    
                    def interessados = interessadoService.listar()
                    if (interessados.isEmpty()) {
                        println "Nenhum interessado cadastrado. Cadastre interessados na opção 1."
                    } else {
                        interessados.each { interessado ->
                            println "Enviando relatório para ${interessado.nome} (${interessado.email})..."
                            Email emailRelatorio = new Email(
                                destinatario: interessado.email,
                                assunto: "Relatório Atualizado - Padrão TISS",
                                mensagem: "Olá ${interessado.nome},\n\nSegue em anexo o relatório mais recente contendo o componente de comunicação, tabela de erros e histórico de versões do Padrão TISS.\n\nAtenciosamente,\nRobô Crawler",
                                anexos: anexos
                            )
                            emailService.enviar(emailRelatorio)
                        }
                        println "Relatórios enviados com sucesso!"
                    }
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
