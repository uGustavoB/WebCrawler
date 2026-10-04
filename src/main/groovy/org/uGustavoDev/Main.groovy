package org.uGustavoDev


import org.uGustavoDev.crawler.WebCrawler
import org.uGustavoDev.model.Email
import org.uGustavoDev.service.EmailService
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
                    Email email = ConsoleUI.pedirEmail()

                    EmailService emailService = new EmailService()
                    emailService.enviar(email)

                    ConsoleUI.aguardarContinuacao()
                    break

                case 0:
                    println "Encerrando aplicação..."
                    break
            }

        } while (opcao != 0)
    }
}
