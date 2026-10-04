package org.uGustavoDev.service

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import org.uGustavoDev.model.EmailInteressado

class EmailInteressadoCsvService {
    private final File arquivo = new File("downloads/emails_interessados.csv")

    List<EmailInteressado> listar() {
        if (!arquivo.exists()) {
            return []
        }

        arquivo.withReader { reader ->
            CSVReader csvReader = new CSVReader(reader)

            List<String[]> linhas = csvReader.readAll()

            if (linhas.isEmpty()) {
                return []
            }

            return linhas.drop(1).collect { linha ->
                new EmailInteressado(
                        nome: linha[0],
                        email: linha[1]
                )
            }
        }
    }

    void salvar(EmailInteressado interessado) {
        List<EmailInteressado> interessados = listar()

        if (buscarPorEmail(interessado.email, interessados) != null) {
            throw new IllegalArgumentException("O email ${interessado.email} já está cadastrado.")
        }

        interessados.add(interessado)

        salvarTodos(interessados)
    }

    EmailInteressado buscarPorEmail(String email, List<EmailInteressado> interessados) {
        interessados.find { it.email.equalsIgnoreCase(email) }
    }

    void remover(String email) {
        List<EmailInteressado> interessados = listar()

        interessados.removeAll {
            it.email.equalsIgnoreCase(email)
        }

        salvarTodos(interessados)
    }

    void editar(String emailOriginal, EmailInteressado novosDados) {
        List<EmailInteressado> interessados = listar()

        def interessado = buscarPorEmail(emailOriginal, interessados)
        if (interessado == null) {
            throw new IllegalArgumentException("O email ${emailOriginal} não foi encontrado.")
        }

        if (!emailOriginal.equalsIgnoreCase(novosDados.email) && buscarPorEmail(novosDados.email, interessados) != null) {
            throw new IllegalArgumentException("O email ${novosDados.email} já está cadastrado para outro interessado.")
        }

        interessado.nome = novosDados.nome
        interessado.email = novosDados.email

        salvarTodos(interessados)
    }

    private void salvarTodos(List<EmailInteressado> interessados) {
        arquivo.parentFile.mkdirs()

        arquivo.withWriter { writer ->
            CSVWriter csvWriter = new CSVWriter(writer)

            csvWriter.writeNext(["Nome", "Email"] as String[])

            interessados.each {
                csvWriter.writeNext([
                        it.nome,
                        it.email
                ] as String[])
            }

            csvWriter.close()
        }
    }
}
