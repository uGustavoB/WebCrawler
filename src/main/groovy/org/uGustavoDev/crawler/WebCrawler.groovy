package org.uGustavoDev.crawler

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class WebCrawler {

    final String URL = "https://www.gov.br/ans/pt-br"
    final String PATH = "downloads"

    Document getDocumento() {
        return Jsoup.connect(URL).get()
    }

    String getLink(Document documento, String text) {
        return documento.select("a:contains($text)").first().absUrl("href")
    }

    Document acessarEspacoPrestador() {
        Document documento = getDocumento()
        String link = getLink(documento, "Espaço do Prestador de Serviços de Saúde")

        return Jsoup.connect(link).get()
    }

    Document acessarTiss() {
        Document documento = acessarEspacoPrestador()
        String link = getLink(
                documento,
                "TISS - Padrão para Troca de Informação de Saúde Suplementar"
        )

        return Jsoup.connect(link).get()
    }

    Document acessarPadraoTiss() {
        Document documento = acessarTiss()
        String link = getLinkVersaoMaisRecente(documento)

        return Jsoup.connect(link).get()
    }

    String getLinkVersaoMaisRecente(Document documento) {
        return documento
                .select("a")
                .find { Element link ->
                    link.text().startsWith("Clique aqui para acessar a versão")
                }
                .absUrl("href")
    }

    String getLinkComponenteComunicacao() {
        Document documento = acessarPadraoTiss()

        return getLink(
                documento,
                "Componente de Comunicação"
        )
    }

    void baixarDocumentoDeComunicacao() {
        String link = getLinkComponenteComunicacao()

        File pasta = new File(PATH)

        if (!pasta.exists()) {
            pasta.mkdirs()
        }

        File arquivo = new File(
                pasta,
                "componente_comunicacao.zip"
        )

        HttpBuilder.configure {
            request.uri = link
        }.get {
            Download.toFile(delegate, arquivo)
        }

        println "Arquivo baixado com sucesso: ${arquivo.path}"
    }
}