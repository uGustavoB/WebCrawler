package org.uGustavoDev.exporter

import com.opencsv.CSVWriter
import org.uGustavoDev.model.VersaoTiss

class CsvExporter {
    File exportar(List<VersaoTiss> versoes, String caminho) {
        File arquivo = new File(caminho)

        File pasta = arquivo.parentFile

        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs()
        }

        arquivo.withWriter { writer ->
            CSVWriter csvWriter = new CSVWriter(writer)

            csvWriter.writeNext([
                    "Competência",
                    "Publicação",
                    "Início de Vigência"
            ] as String[])

            versoes.each { VersaoTiss versao ->
                csvWriter.writeNext([
                        versao.competencia,
                        versao.publicacao,
                        versao.inicioVigencia
                ] as String[])
            }

            csvWriter.close()
        }

        println "Histórico exportado com sucesso: ${arquivo.path}"

        return arquivo
    }
}
