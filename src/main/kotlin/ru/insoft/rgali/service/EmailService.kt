package ru.insoft.rgali.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import ru.insoft.rgali.config.ApplicationProperties
import ru.insoft.rgali.controller.TourForm

@Service
class EmailService(
    val emailSender: JavaMailSender,
    val applicationProperties: ApplicationProperties
) {

    fun sendRegistrationEmail(toEmail: String, code: String) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        val htmlMsg = """
            <h3>Подтверждение пароля</h3>
            Добрый день, для окончания регистрации на сайте, перейдите по ссылке ниже:</br>
            <a href="${applicationProperties.host}/confirm/$code">подтверждение почты</a>
        """
        helper.setFrom(applicationProperties.emailFrom!!)
        helper.setTo(toEmail)
        helper.setSubject("Регистрация на сайте РГАЛИ")
        helper.setText(htmlMsg, true)

        emailSender.send(message);
    }

    fun sendResetPasswordEmail(toEmail: String, newPassword: String) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        val htmlMsg = """
            <h3>Смена пароля</h3>
            Добрый день, Ваш новый пароль пароль: $newPassword
        """
        helper.setFrom(applicationProperties.emailFrom!!)
        helper.setTo(toEmail)
        helper.setSubject("Смена пароля на сайте РГАЛИ")
        helper.setText(htmlMsg, true)

        emailSender.send(message);
    }

    fun sendTourForm(tourForm: TourForm) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        val htmlMsg = """
            Сообщение с сайта rgali.ru от ${tourForm.name}: 
            </br>
            ${tourForm.message}.
            </br>   
            E-mail: ${tourForm.email}
            </br>   
            Телефон: ${tourForm.phone}
        """
        helper.setFrom(applicationProperties.emailFrom!!)
        helper.setTo(applicationProperties.emailTour!!)
        helper.setSubject(tourForm.theme!!)
        helper.setText(htmlMsg, true)

        emailSender.send(message);
    }

}

