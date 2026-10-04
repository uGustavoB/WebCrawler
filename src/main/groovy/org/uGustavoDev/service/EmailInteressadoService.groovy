package org.uGustavoDev.service

import org.uGustavoDev.model.EmailInteressado

class EmailInteressadoService {
    private final EmailInteressadoCsvService service

    EmailInteressadoService(
            EmailInteressadoCsvService service
    ) {
        this.service = service
    }

    List<EmailInteressado> listar() {
        return service.listar()
    }

    void cadastrar(EmailInteressado interessado) {
        service.salvar(interessado)
    }

    void remover(String email) {
        service.remover(email)
    }

    void editar(String emailOriginal, EmailInteressado novosDados) {
        service.editar(emailOriginal, novosDados)
    }
}
