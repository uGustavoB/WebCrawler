package org.uGustavoDev.ui

import org.uGustavoDev.model.Email
import org.uGustavoDev.model.EmailInteressado

class ConsoleUI {

    private static final Scanner scanner = new Scanner(System.in)

    static void imprimirCabecalho(String titulo) {
        println "\n========================================"
        println "  ${titulo.toUpperCase()}"
        println "========================================"
    }

    static void limparTela() {
        print "\033[H\033[2J"
        System.out.flush()
    }

    static void aguardarContinuacao() {
        println "\nPressione Enter para continuar..."
        scanner.nextLine()
        limparTela()
    }

    static String lerTexto(String mensagem, boolean permiteVazio = false) {
        while (true) {
            print mensagem

            String entrada = scanner.nextLine()

            if (!permiteVazio && entrada.trim().isEmpty()) {
                println "Este campo não pode ser vazio. Tente novamente."
                continue
            }

            return entrada.trim()
        }
    }

    static int lerEscolha(String mensagem, int min, int max) {
        while (true) {
            print mensagem

            String entrada = scanner.nextLine()

            try {
                int escolha = Integer.parseInt(entrada)

                if (escolha >= min && escolha <= max) {
                    return escolha
                }

                println "Opção inválida. Escolha entre $min e $max."

            } catch (NumberFormatException ignored) {
                println "Entrada inválida. Digite um número."
            }
        }
    }

    static int pedirOpcaoPrincipal() {
        imprimirCabecalho("TISS Crawler")

        println "Escolha uma ação:"
        println "1 - Baixar documentação TISS"
        println "2 - Enviar histórico por email"
        println "0 - Sair"

        return lerEscolha("Sua escolha: ", 0, 2)
    }

    static Email pedirEmailComHistorico(File arquivo) {
        imprimirCabecalho("Envio do Histórico TISS")

        String destinatario = lerTexto("Email do destinatário: ")
        String assunto = lerTexto("Assunto: ")
        String mensagem = lerTexto("Mensagem: ")

        return new Email(
                destinatario: destinatario,
                assunto: assunto,
                mensagem: mensagem,
                anexos: [arquivo]
        )
    }

    static EmailInteressado pedirEmailInteressado() {
        imprimirCabecalho("Cadastrar Interessado")

        String nome = lerTexto("Nome: ")
        String email = lerTexto("Email: ")

        return new EmailInteressado(
                nome: nome,
                email: email
        )
    }

    static int pedirOpcaoInteressados() {
        imprimirCabecalho("Emails Interessados")

        println "1 - Listar"
        println "2 - Cadastrar"
        println "3 - Remover"
        println "0 - Voltar"

        return lerEscolha("Sua escolha: ", 0, 3)
    }
}