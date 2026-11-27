package org.uGustavoDev

import org.uGustavoDev.crawler.WebCrawler
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
        RelatorioService relatorioService = new RelatorioService(webCrawler, emailService, interessadoService)

        executarMenuPrincipal(interessadoService, relatorioService)
    }

    private static void executarMenuPrincipal(
            EmailInteressadoService interessadoService,
            RelatorioService relatorioService
    ) {
        int opcao
        do {
            opcao = ConsoleUI.pedirOpcaoPrincipal()
            switch (opcao) {
                case 1:
                    gerenciarInteressados(interessadoService)
                    break
                case 2:
                    println "Processando relatórios e verificando interessados..."
                    int totalEnviados = relatorioService.baixarEEnviarParaInteressados()
                    if (totalEnviados == 0) {
                        println "Nenhum interessado cadastrado. Cadastre interessados na opção 1."
                    } else {
                        println "${totalEnviados} relatório(s) baixado(s) e enviado(s) com sucesso!"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break
                case 0:
                    println "Encerrando aplicação..."
                    break
            }
        } while (opcao != 0)
    }

    private static void gerenciarInteressados(EmailInteressadoService service) {
        int opcao
        do {
            opcao = ConsoleUI.pedirOpcaoInteressados()
            switch (opcao) {
                case 1:
                    exibirInteressados(service.listar())
                    ConsoleUI.aguardarContinuacao()
                    break
                case 2:
                    executarComTratamento {
                        EmailInteressado interessado = ConsoleUI.pedirEmailInteressado()
                        service.cadastrar(interessado)
                        println "Interessado cadastrado com sucesso!"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break
                case 3:
                    executarComTratamento {
                        String email = ConsoleUI.lerTexto("Email para remover: ")
                        service.remover(email)
                        println "Interessado removido!"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break
                case 4:
                    executarComTratamento {
                        String emailOriginal = ConsoleUI.lerTexto("Email para editar: ")
                        println "Digite os novos dados:"
                        String novoNome = ConsoleUI.lerTexto("Novo Nome: ")
                        String novoEmail = ConsoleUI.lerTexto("Novo Email: ")
                        service.editar(emailOriginal, new EmailInteressado(nome: novoNome, email: novoEmail))
                        println "Interessado atualizado com sucesso!"
                    }
                    ConsoleUI.aguardarContinuacao()
                    break
            }
        } while (opcao != 0)
    }

    private static void exibirInteressados(List<EmailInteressado> interessados) {
        if (interessados.isEmpty()) {
            println "Nenhum interessado cadastrado."
            return
        }
        println "Lista de interessados:"
        interessados.each { println "${it.nome} - ${it.email}" }
    }

    private static void executarComTratamento(Closure acao) {
        try {
            acao.call()
        } catch (IllegalArgumentException e) {
            println e.message
        }
    }
}