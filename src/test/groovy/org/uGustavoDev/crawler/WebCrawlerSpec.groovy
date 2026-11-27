package org.uGustavoDev.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.uGustavoDev.client.WebClient
import org.uGustavoDev.exporter.CsvExporter
import org.uGustavoDev.model.VersaoTiss
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class WebCrawlerSpec extends Specification {

    @TempDir
    Path tempDir

    def "deve filtrar apenas versoes a partir de janeiro de 2016"() {
        given: "um crawler e uma lista mista de versões anteriores e posteriores a 2016"
        WebCrawler crawler = new WebCrawler()
        List<VersaoTiss> versoes = [
                new VersaoTiss(competencia: "dez/2015", publicacao: "10/12/2015", inicioVigencia: "01/01/2016"),
                new VersaoTiss(competencia: "jan/2016", publicacao: "15/01/2016", inicioVigencia: "01/02/2016"),
                new VersaoTiss(competencia: "ago/2024", publicacao: "20/08/2024", inicioVigencia: "01/09/2024")
        ]

        when: "aplicar o filtro de corte"
        List<VersaoTiss> filtradas = crawler.filtrarVersoesDesdeCorte(versoes)

        then: "deve manter apenas versões >= 01/2016"
        filtradas*.competencia == ["jan/2016", "ago/2024"]
    }

    def "deve navegar pelas paginas simuladas e exportar historico TISS para CSV"() {
        given: "um WebClient stubado retornando as páginas HTML da ANS"
        WebClient webClient = Stub(WebClient) {
            get("https://www.gov.br/ans/pt-br") >> Jsoup.parse('<a href="https://ans.test/prestador">Espaço do Prestador de Serviços de Saúde</a>')
            get("https://ans.test/prestador") >> Jsoup.parse('<a href="https://ans.test/tiss">TISS - Padrão para Troca de Informação de Saúde Suplementar</a>')
            get("https://ans.test/tiss") >> Jsoup.parse('<a href="https://ans.test/historico">Clique aqui para acessar todas as versões dos Componentes</a>')
            get("https://ans.test/historico") >> Jsoup.parse('''
                <table>
                    <thead><tr><th>Competência</th><th>Publicação</th><th>Início de Vigência</th></tr></thead>
                    <tbody>
                        <tr><td>nov/2015</td><td>01/11/2015</td><td>01/12/2015</td></tr>
                        <tr><td>mar/2024</td><td>10/03/2024</td><td>01/04/2024</td></tr>
                    </tbody>
                </table>
            ''')
        }
        WebCrawler crawler = new WebCrawler(webClient, new AnsHtmlParser(), new CsvExporter(), tempDir.toString())

        when: "obter o histórico TISS"
        File csvGerado = crawler.obterHistoricoTiss()

        then: "deve gerar o arquivo CSV apenas com a versão filtrada de 2024"
        csvGerado.exists()
        List<String> linhas = csvGerado.readLines("UTF-8")
        linhas.size() == 2
        linhas[1].contains("mar/2024")
    }
}