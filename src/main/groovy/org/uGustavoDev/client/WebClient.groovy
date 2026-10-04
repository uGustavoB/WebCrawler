package org.uGustavoDev.client

import org.jsoup.nodes.Document

class WebClient {
    Document get(String url) {
        return Jsoup.connect(url).get()
    }
}
