package org.uGustavoDev.crawler

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.uGustavoDev.model.VersaoTiss

class WebCrawler {

    final String URL = "https://www.gov.br/ans/pt-br"
    final String PATH = "downloads"

    Document getDocumento() {
        return Jsoup.connect(URL).get()
    }

    String getLink(Document documento, String text) {
        Element elemento = documento.select("a:contains($text)").first()

        if (elemento == null) {
            throw new IllegalStateException(
                    "Link não encontrado para o texto: $text"
            )
        }

        return elemento.absUrl("href")
    }

    Document acessarEspacoPrestador() {
        Document documento = getDocumento()
        String link = getLink(
                documento,
                "Espaço do Prestador de Serviços de Saúde"
        )

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
        Element elemento = documento
                .select("a")
                .find { Element link ->
                    link.text().startsWith(
                            "Clique aqui para acessar a versão"
                    )
                }

        if (elemento == null) {
            throw new IllegalStateException(
                    "Nenhuma versão do TISS foi encontrada"
            )
        }

        return elemento.absUrl("href")
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

    Document acessarHistoricoTiss() {
        Document documento = acessarTiss()

        String link = getLink(
                documento,
                "Clique aqui para acessar todas as versões dos Componentes"
        )

        return Jsoup.connect(link).get()
    }

    Element getTabelaHistorico(Document documento) {
        return documento.select("table").first()
    }

    Elements getLinhasTabelaHistorico(Element tabela) {
        return tabela.select("tbody tr")
    }

    List<VersaoTiss> extrairVersoesTiss() {
        Document documento = acessarHistoricoTiss()

        Element tabela = getTabelaHistorico(documento)
        Elements linhas = getLinhasTabelaHistorico(tabela)

        Map<String, Integer> indices = getIndicesColunas(tabela)

        List<VersaoTiss> versoes = []

        linhas.each { linha ->
            Elements colunas = linha.select("td")

            VersaoTiss versao = new VersaoTiss(
                    competencia: colunas.get(indices.competencia).text(),
                    publicacao: colunas.get(indices.publicacao).text(),
                    inicioVigencia: colunas.get(indices.inicioVigencia).text()
            )

            versoes.add(versao)
        }

        return versoes
    }

    Map<String, Integer> getIndicesColunas(Element tabela) {
        Elements cabecalho = tabela.select("thead tr").first().select("th")

        Map<String, Integer> indices = [:]

        cabecalho.eachWithIndex { Element coluna, int indice ->
            String nome = coluna.text().toLowerCase()

            if (nome.contains("competência")) {
                indices.competencia = indice
            }

            if (nome.contains("publicação")) {
                indices.publicacao = indice
            }

            if (nome.contains("início de vigência")) {
                indices.inicioVigencia = indice
            }
        }

        return indices
    }
}