package org.uGustavoDev.service

import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class RelatorioServiceSpec extends Specification {

    @TempDir
    Path tempDir

    WebCrawler webCrawler = Mock()
    EmailService emailService = Mock()
    EmailInteressadoService interessadoService = Mock()

    RelatorioService relatorioService = new RelatorioService(webCrawler, emailService, interessadoService)

    def "nao deve acionar o crawler nem enviar emails quando nao houver interessados"() {
        given: "nenhum interessado cadastrado"
        interessadoService.listar() >> []

        when: "executar o fluxo de envio de relatórios"
        int totalEnviados = relatorioService.baixarEEnviarParaInteressados()

        then: "nenhuma chamada ao crawler ou serviço de e-mail deve ocorrer"
        totalEnviados == 0
        0 * webCrawler._
        0 * emailService._
    }

    def "deve baixar os tres arquivos e enviar relatorio para cada interessado"() {
        given: "dois interessados cadastrados e arquivos gerados pelo crawler"
        File dummy = tempDir.resolve("dummy.txt").toFile()
        dummy.text = "dados"

        interessadoService.listar() >> [
                new EmailInteressado(nome: "Maria", email: "maria@teste.com"),
                new EmailInteressado(nome: "Pedro", email: "pedro@teste.com")
        ]
        webCrawler.baixarDocumentoDeComunicacao() >> dummy
        webCrawler.baixarTabelaDeErros() >> dummy
        webCrawler.obterHistoricoTiss() >> dummy

        when: "executar o fluxo de envio"
        int totalEnviados = relatorioService.baixarEEnviarParaInteressados()

        then: "deve enviar 2 e-mails com os 3 anexos cada"
        totalEnviados == 2
        1 * emailService.enviar({ Email e -> e.destinatario == "maria@teste.com" && e.anexos.size() == 3 })
        1 * emailService.enviar({ Email e -> e.destinatario == "pedro@teste.com" && e.anexos.size() == 3 })
    }
}