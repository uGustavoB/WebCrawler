package org.uGustavoDev.crawler

import groovyx.net.http.HttpBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.uGustavoDev.client.WebClient
import org.uGustavoDev.exporter.CsvExporter
import org.uGustavoDev.model.VersaoTiss

import java.time.YearMonth
import java.time.format.DateTimeFormatter

class WebCrawler {

    final String URL = "https://www.gov.br/ans/pt-br"
    final String PATH = "downloads"
    final WebClient webClient = new WebClient()

    Document getDocumento() {
        return webClient.get(URL)
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

        return webClient.get(link)
    }

    Document acessarTiss() {
        Document documento = acessarEspacoPrestador()
        String link = getLink(
                documento,
                "TISS - Padrão para Troca de Informação de Saúde Suplementar"
        )

        return webClient.get(link)
    }

    Document acessarPadraoTiss() {
        Document documento = acessarTiss()
        String link = getLinkVersaoMaisRecente(documento)

        return webClient.get(link)
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

        return webClient.get(link)
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

    YearMonth converterCompetencia(String competencia) {
        def mapMeses = [
            "jan": "01", "fev": "02", "mar": "03", "abr": "04",
            "mai": "05", "jun": "06", "jul": "07", "ago": "08",
            "set": "09", "out": "10", "nov": "11", "dez": "12"
        ]
        
        String normalizada = competencia.toLowerCase().trim()
        mapMeses.each { key, value ->
            if (normalizada.startsWith(key)) {
                normalizada = normalizada.replaceFirst(key + "[a-z]*", value)
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy")
        return YearMonth.parse(normalizada, formatter)
    }

    List<VersaoTiss> filtrarVersoesDesde2016(List<VersaoTiss> versoes) {
        YearMonth inicio = YearMonth.of(2016, 1)

        return versoes.findAll { VersaoTiss versao ->
            converterCompetencia(versao.competencia) >= inicio
        }
    }

    void obterHistoricoTiss() {
        List<VersaoTiss> versoes = extrairVersoesTiss()

        List<VersaoTiss> versoesFiltradas =
                filtrarVersoesDesde2016(versoes)

        CsvExporter exporter = new CsvExporter()

        exporter.exportar(
                versoesFiltradas,
                "${PATH}/historico_versoes_tiss.csv"
        )
    }
}