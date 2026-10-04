package org.uGustavoDev

import org.jsoup.nodes.Document
import org.uGustavoDev.crawler.WebCrawler

class Main {
    static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawler()

        webCrawler.baixarDocumentoDeComunicacao() // Tarefa 1

        webCrawler.imprimirDadosHistorico()
    }
}
