package org.uGustavoDev.service

import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado

class RelatorioService {

    private final WebCrawler webCrawler
    private final EmailService emailService
    private final EmailInteressadoService interessadoService

    RelatorioService(WebCrawler webCrawler, EmailService emailService, EmailInteressadoService interessadoService) {
        this.webCrawler = webCrawler
        this.emailService = emailService
        this.interessadoService = interessadoService
    }

    void baixarEEnviarParaInteressados() {
        println "Baixando arquivos atualizados..."
        File arqComunicacao = webCrawler.baixarDocumentoDeComunicacao()
        File arqErros = webCrawler.baixarTabelaDeErros()
        File arqHistorico = webCrawler.obterHistoricoTiss()
        
        List<File> anexos = [arqComunicacao, arqErros, arqHistorico]
        
        List<EmailInteressado> interessados = interessadoService.listar()
        if (interessados.isEmpty()) {
            println "Nenhum interessado cadastrado. Cadastre interessados na opção 1."
            return
        }

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
}
