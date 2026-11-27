package org.uGustavoDev.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Specification
import spock.lang.Unroll

import java.time.YearMonth

class AnsHtmlParserSpec extends Specification {

    AnsHtmlParser parser = new AnsHtmlParser()

    def "deve extrair link absoluto pelo texto da ancora"() {
        given: "um documento HTML contendo o link esperado"
        String html = '''
            <html><body>
                <a href="/ans/prestador">Espaço do Prestador de Serviços de Saúde</a>
            </body></html>
        '''
        Document doc = Jsoup.parse(html, "https://www.gov.br")

        when: "extrair o link pelo texto"
        String link = parser.extrairLinkPorTexto(doc, "Espaço do Prestador de Serviços de Saúde")

        then: "deve retornar a URL absoluta correta"
        link == "https://www.gov.br/ans/prestador"
    }

    def "deve lancar excecao quando o link nao existir no documento"() {
        given: "um documento HTML sem o link procurado"
        Document doc = Jsoup.parse("<html><body><a href='/outro'>Outro link</a></body></html>")

        when: "tentar extrair um link inexistente"
        parser.extrairLinkPorTexto(doc, "Link Inexistente")

        then: "deve lançar IllegalStateException"
        thrown(IllegalStateException)
    }

    def "deve extrair lista de versoes TISS da tabela HTML"() {
        given: "uma página HTML com a tabela de histórico do TISS"
        String html = '''
            <table>
                <thead>
                    <tr>
                        <th>Competência</th>
                        <th>Publicação</th>
                        <th>Início de Vigência</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>jan/2024</td>
                        <td>15/01/2024</td>
                        <td>01/02/2024</td>
                    </tr>
                    <tr>
                        <td>dez/2015</td>
                        <td>10/12/2015</td>
                        <td>01/01/2016</td>
                    </tr>
                </tbody>
            </table>
        '''
        Document doc = Jsoup.parse(html)

        when: "extrair as versões"
        def versoes = parser.extrairVersoesTiss(doc)

        then: "deve mapear todas as linhas e colunas corretamente"
        versoes.size() == 2
        versoes[0].competencia == "jan/2024"
        versoes[0].publicacao == "15/01/2024"
        versoes[0].inicioVigencia == "01/02/2024"
        versoes[1].competencia == "dez/2015"
    }

    @Unroll
    def "deve converter a competencia '#textoCompetencia' para #esperado"() {
        expect:
        parser.converterCompetencia(textoCompetencia) == esperado

        where:
        textoCompetencia | esperado
        "jan/2024"       | YearMonth.of(2024, 1)
        "mai/2023"       | YearMonth.of(2023, 5)
        "set/2020"       | YearMonth.of(2020, 9)
        "dez/2016"       | YearMonth.of(2016, 12)
        "03/2022"        | YearMonth.of(2022, 3)
    }
}