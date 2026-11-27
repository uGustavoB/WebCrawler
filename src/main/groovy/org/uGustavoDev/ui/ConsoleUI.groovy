package org.uGustavoDev.ui

import org.uGustavoDev.model.EmailInteressado

import java.nio.charset.StandardCharsets

class ConsoleUI {

    private static final String SEPARADOR = "========================================"
    private static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8.name())

    static void imprimirCabecalho(String titulo) {
        println "\n${SEPARADOR}"
        println "  ${titulo.toUpperCase()}"
        println SEPARADOR
    }

    static void exibirMensagem(String mensagem) {
        println mensagem
    }

    static void exibirErro(String mensagem) {
        println "[ERRO] ${mensagem}"
    }

    static void limparTela() {
        print "\033[H\033[2J"
        System.out.flush()
    }

    static void aguardarContinuacao() {
        println "\nPressione Enter para continuar..."
        SCANNER.nextLine()
        limparTela()
    }

    static String lerTexto(String mensagem, boolean permiteVazio = false) {
        while (true) {
            print mensagem
            String entrada = SCANNER.nextLine()

            if (!permiteVazio && entrada.trim().isEmpty()) {
                exibirErro("Este campo não pode ser vazio. Tente novamente.")
                continue
            }

            return entrada.trim()
        }
    }

    static int lerEscolha(String mensagem, int min, int max) {
        while (true) {
            print mensagem
            String entrada = SCANNER.nextLine().trim()

            try {
                int escolha = Integer.parseInt(entrada)
                if (escolha >= min && escolha <= max) {
                    return escolha
                }
                exibirErro("Opção inválida. Escolha entre $min e $max.")
            } catch (NumberFormatException ignored) {
                exibirErro("Entrada inválida. Digite um número.")
            }
        }
    }

    static int pedirOpcaoPrincipal() {
        imprimirCabecalho("TISS Crawler")

        println "Escolha uma ação:"
        println "1 - Gerenciar emails interessados"
        println "2 - Baixar arquivos e enviar relatório para todos os interessados"
        println "0 - Sair"

        return lerEscolha("Sua escolha: ", 0, 2)
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
        println "4 - Editar"
        println "0 - Voltar"

        return lerEscolha("Sua escolha: ", 0, 4)
    }
}