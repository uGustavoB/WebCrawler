package org.uGustavoDev.crawler

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.uGustavoDev.model.VersaoTiss

import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AnsHtmlParser {

    private static final Map<String, String> MESES = [
            "jan": "01", "fev": "02", "mar": "03", "abr": "04",
            "mai": "05", "jun": "06", "jul": "07", "ago": "08",
            "set": "09", "out": "10", "nov": "11", "dez": "12"
    ]
    private static final DateTimeFormatter COMPETENCIA_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy")

    String extrairLinkPorTexto(Document documento, String texto) {
        Element elemento = documento.select("a:contains($texto)").first()
        if (elemento == null) {
            throw new IllegalStateException("Link não encontrado para o texto: $texto")
        }
        return elemento.absUrl("href")
    }

    String extrairLinkVersaoMaisRecente(Document documento) {
        Element elemento = documento.select("a").find { Element link ->
            link.text().startsWith("Clique aqui para acessar a versão")
        }
        if (elemento == null) {
            throw new IllegalStateException("Nenhuma versão do TISS foi encontrada")
        }
        return elemento.absUrl("href")
    }

    List<VersaoTiss> extrairVersoesTiss(Document documento) {
        Element tabela = documento.select("table").first()
        if (tabela == null) {
            throw new IllegalStateException("Tabela de histórico TISS não encontrada")
        }

        Map<String, Integer> indices = mapearIndicesColunas(tabela)
        Elements linhas = tabela.select("tbody tr")

        return linhas.collect { Element linha ->
            Elements colunas = linha.select("td")
            new VersaoTiss(
                    competencia: colunas.get(indices.competencia).text(),
                    publicacao: colunas.get(indices.publicacao).text(),
                    inicioVigencia: colunas.get(indices.inicioVigencia).text()
            )
        }
    }

    YearMonth converterCompetencia(String competencia) {
        String normalizada = competencia.toLowerCase().trim()
        MESES.each { String abreviacao, String numero ->
            if (normalizada.startsWith(abreviacao)) {
                normalizada = normalizada.replaceFirst(abreviacao + "[a-z]*", numero)
            }
        }
        return YearMonth.parse(normalizada, COMPETENCIA_FORMATTER)
    }

    private Map<String, Integer> mapearIndicesColunas(Element tabela) {
        Element linhaCabecalho = tabela.select("thead tr").first()
        if (linhaCabecalho == null) {
            throw new IllegalStateException("Cabeçalho da tabela de histórico não encontrado")
        }

        Map<String, Integer> indices = [:]
        linhaCabecalho.select("th").eachWithIndex { Element coluna, int indice ->
            String nome = coluna.text().toLowerCase()
            if (nome.contains("competência")) indices.competencia = indice
            if (nome.contains("publicação")) indices.publicacao = indice
            if (nome.contains("início de vigência")) indices.inicioVigencia = indice
        }

        if (indices.size() < 3) {
            throw new IllegalStateException("Colunas obrigatórias ausentes na tabela de histórico TISS")
        }
        return indices
    }
}