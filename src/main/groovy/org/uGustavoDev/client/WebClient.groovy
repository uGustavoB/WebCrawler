package org.uGustavoDev.client

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebClient {

    Document get(String url) {
        return Jsoup.connect(url).get()
    }

    File download(String url, File destino) {
        File diretorio = destino.parentFile
        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs()
        }

        HttpBuilder.configure {
            request.uri = url
        }.get {
            Download.toFile(delegate, destino)
        }

        return destino
    }
}