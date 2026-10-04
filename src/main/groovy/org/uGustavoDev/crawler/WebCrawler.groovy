package org.uGustavoDev.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebCrawler {
    String URL  = "https://www.gov.br/ans/pt-br"

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
        String link = getLink(documento, "Clique aqui para acessar a versão Setembro/2026")

        return Jsoup.connect(link).get()
    }

    String getLinkComponenteComunicacao() {
        Document documento = acessarPadraoTiss()

        return getLink(
                documento,
                "Componente de Comunicação"
        )
    }
}
