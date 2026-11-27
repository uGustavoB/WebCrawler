package org.uGustavoDev.service

import org.uGustavoDev.model.EmailInteressado
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class EmailInteressadoServiceSpec extends Specification {

    @TempDir
    Path tempDir

    EmailInteressadoService service

    def setup() {
        String caminhoCsv = tempDir.resolve("emails_interessados.csv").toString()
        service = new EmailInteressadoService(new EmailInteressadoCsvService(caminhoCsv))
    }

    def "deve cadastrar e listar interessados com sucesso"() {
        when: "cadastrar um novo interessado"
        service.cadastrar(new EmailInteressado(nome: "Ana Silva", email: "ana@teste.com"))

        then: "ele deve constar na listagem"
        def lista = service.listar()
        lista.size() == 1
        lista[0].nome == "Ana Silva"
        lista[0].email == "ana@teste.com"
    }

    def "deve impedir cadastro de email duplicado independente de maiusculas ou minusculas"() {
        given: "um interessado já cadastrado"
        service.cadastrar(new EmailInteressado(nome: "Ana", email: "ana@teste.com"))

        when: "tentar cadastrar outro com o mesmo e-mail em caixa alta"
        service.cadastrar(new EmailInteressado(nome: "Outra Ana", email: "ANA@TESTE.COM"))

        then: "deve lançar exceção de argumento inválido"
        thrown(IllegalArgumentException)
    }

    def "deve editar nome e email de um interessado existente"() {
        given: "um interessado cadastrado"
        service.cadastrar(new EmailInteressado(nome: "João", email: "joao@teste.com"))

        when: "editar seus dados"
        service.editar("joao@teste.com", new EmailInteressado(nome: "João Pedro", email: "joaopedro@teste.com"))

        then: "os dados atualizados devem persistir"
        def lista = service.listar()
        lista.size() == 1
        lista[0].nome == "João Pedro"
        lista[0].email == "joaopedro@teste.com"
    }

    def "deve remover interessado existente e lancar erro ao remover inexistente"() {
        given: "um interessado cadastrado"
        service.cadastrar(new EmailInteressado(nome: "Carlos", email: "carlos@teste.com"))

        when: "remover o interessado"
        service.remover("carlos@teste.com")

        then: "a lista deve ficar vazia"
        service.listar().isEmpty()

        when: "tentar remover um e-mail que não existe"
        service.remover("inexistente@teste.com")

        then: "deve lançar IllegalArgumentException"
        thrown(IllegalArgumentException)
    }
}