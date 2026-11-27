package org.uGustavoDev.service

import org.uGustavoDev.model.EmailInteressado

class EmailInteressadoService {

    private final EmailInteressadoCsvService csvService

    EmailInteressadoService(EmailInteressadoCsvService csvService) {
        this.csvService = csvService
    }

    List<EmailInteressado> listar() {
        return csvService.listar()
    }

    void cadastrar(EmailInteressado interessado) {
        validarDados(interessado)
        List<EmailInteressado> interessados = listar()

        if (buscarPorEmail(interessado.email, interessados) != null) {
            throw new IllegalArgumentException("O email ${interessado.email} já está cadastrado.")
        }

        interessados.add(interessado)
        csvService.salvarTodos(interessados)
    }

    void remover(String email) {
        List<EmailInteressado> interessados = listar()
        boolean removido = interessados.removeAll { it.email.equalsIgnoreCase(email?.trim()) }

        if (!removido) {
            throw new IllegalArgumentException("O email ${email} não foi encontrado.")
        }

        csvService.salvarTodos(interessados)
    }

    void editar(String emailOriginal, EmailInteressado novosDados) {
        validarDados(novosDados)
        List<EmailInteressado> interessados = listar()

        EmailInteressado interessadoExistente = buscarPorEmail(emailOriginal, interessados)
        if (interessadoExistente == null) {
            throw new IllegalArgumentException("O email ${emailOriginal} não foi encontrado.")
        }

        boolean emailAlterado = !emailOriginal.equalsIgnoreCase(novosDados.email)
        if (emailAlterado && buscarPorEmail(novosDados.email, interessados) != null) {
            throw new IllegalArgumentException("O email ${novosDados.email} já está cadastrado para outro interessado.")
        }

        interessadoExistente.nome = novosDados.nome.trim()
        interessadoExistente.email = novosDados.email.trim()

        csvService.salvarTodos(interessados)
    }

    private EmailInteressado buscarPorEmail(String email, List<EmailInteressado> interessados) {
        return interessados.find { it.email.equalsIgnoreCase(email?.trim()) }
    }

    private void validarDados(EmailInteressado interessado) {
        if (interessado == null || !interessado.nome?.trim() || !interessado.email?.trim()) {
            throw new IllegalArgumentException("Nome e email são obrigatórios.")
        }
    }
}