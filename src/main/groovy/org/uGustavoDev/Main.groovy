package org.uGustavoDev


import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.ui.ConsoleUI

class Main {
    static void main(String[] args) {
        int opcao

        do {
            opcao = ConsoleUI.pedirOpcaoPrincipal()

            switch (opcao) {
                case 1:
                    println "Download da documentação..."
                    break

                case 2:
                    String email = ConsoleUI.pedirEmail()
                    println "Email cadastrado: $email"
                    break

                case 0:
                    println "Encerrando aplicação..."
                    break
            }

        } while (opcao != 0)
    }
}
