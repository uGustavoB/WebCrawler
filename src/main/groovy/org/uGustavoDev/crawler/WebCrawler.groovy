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
        Document docAns = getDocumento()
        String linkEspacoPrestador = getLink(docAns, "Espaço do Prestador de Serviços de Saúde")
        return Jsoup.connect(linkEspacoPrestador).get()
    }

    Document acessarTiss() {
        Document docPrestadores = acessarEspacoPrestador()
        String linkTiss = getLink(docPrestadores, "TISS - Padrão para Troca de Informação de Saúde Suplementar")
        return Jsoup.connect(linkTiss).get()
    }
}
