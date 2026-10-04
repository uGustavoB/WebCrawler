package org.uGustavoDev.service

import jakarta.mail.Message
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import org.uGustavoDev.model.Email

class EmailService {

    void enviar(Email email) {

        String remetente = "seu-email@gmail.com"
        String senha = "sua-senha"

        Properties properties = new Properties()
        properties.put("mail.smtp.host", "smtp.gmail.com")
        properties.put("mail.smtp.port", "587")
        properties.put("mail.smtp.auth", "true")
        properties.put("mail.smtp.starttls.enable", "true")

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                remetente,
                                senha
                        )
                    }
                }
        )

        Message message = new MimeMessage(session)

        message.setFrom(new InternetAddress(remetente))
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(email.destinatario)
        )
        message.setSubject(email.assunto)
        message.setText(email.mensagem)

        Transport.send(message)

        println "Email enviado com sucesso para ${email.destinatario}"
    }
}
