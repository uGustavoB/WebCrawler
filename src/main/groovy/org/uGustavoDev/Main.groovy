package org.uGustavoDev

import org.jsoup.nodes.Document
import org.uGustavoDev.crawler.WebCrawler

class Main {
    static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawler()

        Document documentoTiss = webCrawler.acessarTiss()

        println documentoTiss.title()
    }
}
