package org.uGustavoDev.service

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import org.uGustavoDev.model.EmailInteressado

import java.nio.charset.StandardCharsets

class EmailInteressadoCsvService {

    private static final String CAMINHO_PADRAO = "downloads/emails_interessados.csv"
    private static final String[] CABECALHO = ["Nome", "Email"] as String[]

    private final File arquivo

    EmailInteressadoCsvService(String caminhoArquivo = CAMINHO_PADRAO) {
        this.arquivo = new File(caminhoArquivo)
    }

    List<EmailInteressado> listar() {
        if (!arquivo.exists()) {
            return []
        }

        return arquivo.withReader(StandardCharsets.UTF_8.name()) { Reader reader ->
            new CSVReader(reader).withCloseable { CSVReader csvReader ->
                List<String[]> linhas = csvReader.readAll()
                if (linhas.size() <= 1) {
                    return []
                }

                return linhas.drop(1)
                        .findAll { String[] linha -> linha.length >= 2 }
                        .collect { String[] linha ->
                            new EmailInteressado(
                                    nome: linha[0].trim(),
                                    email: linha[1].trim()
                            )
                        }
            }
        }
    }

    void salvarTodos(List<EmailInteressado> interessados) {
        File pasta = arquivo.parentFile
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs()
        }

        arquivo.withWriter(StandardCharsets.UTF_8.name()) { Writer writer ->
            new CSVWriter(writer).withCloseable { CSVWriter csvWriter ->
                csvWriter.writeNext(CABECALHO)

                interessados.each { EmailInteressado interessado ->
                    csvWriter.writeNext([
                            interessado.nome,
                            interessado.email
                    ] as String[])
                }
            }
        }
    }
}