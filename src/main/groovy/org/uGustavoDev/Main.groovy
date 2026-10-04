package org.uGustavoDev

import org.jsoup.nodes.Document
import org.uGustavoDev.crawler.WebCrawler

class Main {
    static void main(String[] args) {
        WebCrawler webCrawler = new WebCrawler()

        String link = webCrawler.getLinkComponenteComunicacao()

        println link
    }
}
