package org.uGustavoDev.model

import io.github.cdimascio.dotenv.Dotenv

class SmtpConfig {
    String host
    int port
    String username
    String password
    String remetente

    static SmtpConfig carregar() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load()

        return new SmtpConfig(
                host: dotenv.get("SMTP_HOST"),
                port: Integer.parseInt(dotenv.get("SMTP_PORT", "587")),
                username: dotenv.get("SMTP_USERNAME"),
                password: dotenv.get("SMTP_PASSWORD"),
                remetente: dotenv.get("SMTP_FROM")
        )
    }
}
