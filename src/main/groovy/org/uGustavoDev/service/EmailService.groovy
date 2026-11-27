package org.uGustavoDev.service

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.Multipart
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.uGustavoDev.model.Email
import org.uGustavoDev.model.SmtpConfig

class EmailService {

    private final SmtpConfig config

    EmailService(SmtpConfig config) {
        this.config = config
    }

    void enviar(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Os dados do email não podem ser nulos.")
        }

        Session session = criarSessaoSmtp()
        Message message = construirMensagem(session, email)
        Transport.send(message)
    }

    private Session criarSessaoSmtp() {
        Properties properties = new Properties()
        properties.put("mail.smtp.host", config.host)
        properties.put("mail.smtp.port", config.port.toString())
        properties.put("mail.smtp.auth", "true")
        properties.put("mail.smtp.starttls.enable", "true")

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.username, config.password)
            }
        })
    }

    private Message construirMensagem(Session session, Email email) {
        Message message = new MimeMessage(session)
        message.setFrom(new InternetAddress(config.remetente))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.destinatario))
        message.setSubject(email.assunto)
        message.setContent(criarCorpoComAnexos(email))
        return message
    }

    private Multipart criarCorpoComAnexos(Email email) {
        MimeBodyPart corpo = new MimeBodyPart()
        corpo.setText(email.mensagem ?: "")

        Multipart multipart = new MimeMultipart()
        multipart.addBodyPart(corpo)

        (email.anexos ?: []).each { File arquivo ->
            if (arquivo != null && arquivo.exists()) {
                MimeBodyPart anexo = new MimeBodyPart()
                anexo.attachFile(arquivo)
                multipart.addBodyPart(anexo)
            }
        }

        return multipart
    }
}