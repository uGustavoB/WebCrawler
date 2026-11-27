package org.uGustavoDev.service

import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado

class RelatorioService {

    private static final String ASSUNTO_RELATORIO = "Relatório Atualizado - Padrão TISS"

    private final WebCrawler webCrawler
    private final EmailService emailService
    private final EmailInteressadoService interessadoService

    RelatorioService(WebCrawler webCrawler, EmailService emailService, EmailInteressadoService interessadoService) {
        this.webCrawler = webCrawler
        this.emailService = emailService
        this.interessadoService = interessadoService
    }

    int baixarEEnviarParaInteressados() {
        List<EmailInteressado> interessados = interessadoService.listar()
        if (interessados.isEmpty()) {
            return 0
        }

        List<File> anexos = coletarArquivosAtualizados()

        interessados.each { EmailInteressado interessado ->
            Email emailRelatorio = criarEmailRelatorio(interessado, anexos)
            emailService.enviar(emailRelatorio)
        }

        return interessados.size()
    }

    private List<File> coletarArquivosAtualizados() {
        File arqComunicacao = webCrawler.baixarDocumentoDeComunicacao()
        File arqErros = webCrawler.baixarTabelaDeErros()
        File arqHistorico = webCrawler.obterHistoricoTiss()
        return [arqComunicacao, arqErros, arqHistorico]
    }

    private Email criarEmailRelatorio(EmailInteressado interessado, List<File> anexos) {
        return new Email(
                destinatario: interessado.email,
                assunto: ASSUNTO_RELATORIO,
                mensagem: "Olá ${interessado.nome},\n\nSegue em anexo o relatório mais recente contendo o componente de comunicação, tabela de erros e histórico de versões do Padrão TISS.\n\nAtenciosamente,\nRobô Crawler",
                anexos: anexos
        )
    }
}