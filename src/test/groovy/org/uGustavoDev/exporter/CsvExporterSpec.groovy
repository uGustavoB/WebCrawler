package org.uGustavoDev.exporter

import org.uGustavoDev.model.VersaoTiss
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class CsvExporterSpec extends Specification {

    @TempDir
    Path tempDir

    def "deve exportar versoes TISS para CSV em UTF-8 criando subdiretorios"() {
        given: "um exportador e uma lista de versões"
        CsvExporter exporter = new CsvExporter()
        List<VersaoTiss> versoes = [
                new VersaoTiss(competencia: "fev/2024", publicacao: "12/02/2024", inicioVigencia: "01/03/2024")
        ]
        String caminhoSaida = tempDir.resolve("subpasta/historico.csv").toString()

        when: "exportar para CSV"
        File arquivo = exporter.exportar(versoes, caminhoSaida)

        then: "o arquivo deve existir com cabeçalho acentuado íntegro e os dados exportados"
        arquivo.exists()
        List<String> linhas = arquivo.readLines("UTF-8")
        linhas.size() == 2
        linhas[0].contains("Competência")
        linhas[1].contains("fev/2024")
    }
}