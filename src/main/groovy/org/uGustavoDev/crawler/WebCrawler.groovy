package org.uGustavoDev.crawler

import org.jsoup.nodes.Document
import org.uGustavoDev.client.WebClient
import org.uGustavoDev.exporter.CsvExporter
import org.uGustavoDev.model.VersaoTiss

import java.time.YearMonth

class WebCrawler {

    private static final String URL_BASE_ANS = "https://www.gov.br/ans/pt-br"
    private static final String DIRETORIO_DOWNLOADS = "downloads"
    private static final YearMonth COMPETENCIA_CORTE = YearMonth.of(2016, 1)

    private static final String TEXTO_ESPACO_PRESTADOR = "Espaço do Prestador de Serviços de Saúde"
    private static final String TEXTO_PADRAO_TISS = "TISS - Padrão para Troca de Informação de Saúde Suplementar"
    private static final String TEXTO_COMPONENTE_COMUNICACAO = "Componente de Comunicação"
    private static final String TEXTO_TODAS_VERSOES = "Clique aqui para acessar todas as versões dos Componentes"
    private static final String TEXTO_PLANILHAS = "Clique aqui para acessar as planilhas"
    private static final String TEXTO_TABELA_ERROS = "tabela de erros"

    private final WebClient webClient
    private final AnsHtmlParser htmlParser
    private final CsvExporter csvExporter
    private final String diretorioSaida

    WebCrawler(
            WebClient webClient = new WebClient(),
            AnsHtmlParser htmlParser = new AnsHtmlParser(),
            CsvExporter csvExporter = new CsvExporter(),
            String diretorioSaida = DIRETORIO_DOWNLOADS
    ) {
        this.webClient = webClient
        this.htmlParser = htmlParser
        this.csvExporter = csvExporter
        this.diretorioSaida = diretorioSaida
    }

    Document acessarPaginaTiss() {
        Document paginaInicial = webClient.get(URL_BASE_ANS)
        String linkPrestador = htmlParser.extrairLinkPorTexto(paginaInicial, TEXTO_ESPACO_PRESTADOR)

        Document paginaPrestador = webClient.get(linkPrestador)
        String linkTiss = htmlParser.extrairLinkPorTexto(paginaPrestador, TEXTO_PADRAO_TISS)

        return webClient.get(linkTiss)
    }

    File baixarDocumentoDeComunicacao() {
        Document paginaTiss = acessarPaginaTiss()
        String linkVersaoRecente = htmlParser.extrairLinkVersaoMaisRecente(paginaTiss)

        Document paginaVersao = webClient.get(linkVersaoRecente)
        String linkComponente = htmlParser.extrairLinkPorTexto(paginaVersao, TEXTO_COMPONENTE_COMUNICACAO)

        return webClient.download(linkComponente, new File(diretorioSaida, "componente_comunicacao.zip"))
    }

    File obterHistoricoTiss() {
        Document paginaTiss = acessarPaginaTiss()
        String linkHistorico = htmlParser.extrairLinkPorTexto(paginaTiss, TEXTO_TODAS_VERSOES)

        Document paginaHistorico = webClient.get(linkHistorico)
        List<VersaoTiss> todasVersoes = htmlParser.extrairVersoesTiss(paginaHistorico)
        List<VersaoTiss> versoesFiltradas = filtrarVersoesDesdeCorte(todasVersoes)

        return csvExporter.exportar(versoesFiltradas, "${diretorioSaida}/historico_versoes_tiss.csv")
    }

    File baixarTabelaDeErros() {
        Document paginaTiss = acessarPaginaTiss()
        String linkTabelas = htmlParser.extrairLinkPorTexto(paginaTiss, TEXTO_PLANILHAS)

        Document paginaTabelas = webClient.get(linkTabelas)
        String linkTabelaErros = htmlParser.extrairLinkPorTexto(paginaTabelas, TEXTO_TABELA_ERROS)

        return webClient.download(linkTabelaErros, new File(diretorioSaida, "tabela_de_erros.xlsx"))
    }

    List<VersaoTiss> filtrarVersoesDesdeCorte(List<VersaoTiss> versoes) {
        return versoes.findAll { VersaoTiss versao ->
            htmlParser.converterCompetencia(versao.competencia) >= COMPETENCIA_CORTE
        }
    }
}