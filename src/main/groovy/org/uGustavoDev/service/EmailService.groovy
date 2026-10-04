package org.uGustavoDev.service

import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.internet.MimeMultipart
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.SmtpConfig

class EmailService {

    private final SmtpConfig config

    EmailService(SmtpConfig config) {
        this.config = config
    }

    void enviar(Email email) {

        Properties properties = new Properties()

        properties.put("mail.smtp.host", config.host)
        properties.put("mail.smtp.port", config.port.toString())
        properties.put("mail.smtp.auth", "true")
        properties.put("mail.smtp.starttls.enable", "true")

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                config.username,
                                config.password
                        )
                    }
                }
        )

        Message message = new MimeMessage(session)

        message.setFrom(new InternetAddress(config.remetente))

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(email.destinatario)
        )

        message.setSubject(email.assunto)

        MimeBodyPart corpo = new MimeBodyPart()
        corpo.setText(email.mensagem)

        Multipart multipart = new MimeMultipart()
        multipart.addBodyPart(corpo)

        email.anexos.each { File arquivo ->
            MimeBodyPart anexo = new MimeBodyPart()

            anexo.attachFile(arquivo)

            multipart.addBodyPart(anexo)
        }

        message.setContent(multipart)

        Transport.send(message)

        println "Email enviado com sucesso para ${email.destinatario}"
    }
}
