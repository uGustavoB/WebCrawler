package org.uGustavoDev

import org.jsoup.nodes.Document
import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.VersaoTiss

class Main {
    static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawler()

        webCrawler.baixarDocumentoDeComunicacao() // Tarefa 1

        List<VersaoTiss> versoes = webCrawler.extrairVersoesTiss()

        versoes.each { VersaoTiss versao ->
            println "${versao.competencia} | ${versao.publicacao} | ${versao.inicioVigencia}"
        }
    }
}
