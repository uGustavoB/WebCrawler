package org.uGustavoDev.exporter

import com.opencsv.CSVWriter
import org.uGustavoDev.model.VersaoTiss

import java.nio.charset.StandardCharsets

class CsvExporter {

    private static final String[] CABECALHO_HISTORICO_TISS = [
            "Competência",
            "Publicação",
            "Início de Vigência"
    ] as String[]

    File exportar(List<VersaoTiss> versoes, String caminho) {
        File arquivo = new File(caminho)
        garantirDiretorioExistente(arquivo.parentFile)

        arquivo.withWriter(StandardCharsets.UTF_8.name()) { Writer writer ->
            new CSVWriter(writer).withCloseable { CSVWriter csvWriter ->
                csvWriter.writeNext(CABECALHO_HISTORICO_TISS)

                versoes.each { VersaoTiss versao ->
                    csvWriter.writeNext([
                            versao.competencia,
                            versao.publicacao,
                            versao.inicioVigencia
                    ] as String[])
                }
            }
        }

        return arquivo
    }

    private void garantirDiretorioExistente(File pasta) {
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs()
        }
    }
}