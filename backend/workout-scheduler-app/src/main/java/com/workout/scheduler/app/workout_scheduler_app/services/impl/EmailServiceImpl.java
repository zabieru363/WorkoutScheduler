package com.workout.scheduler.app.workout_scheduler_app.services.impl;

import com.workout.scheduler.app.workout_scheduler_app.models.dtos.NewEmailDTO;
import com.workout.scheduler.app.workout_scheduler_app.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    /**
     * Envía un email genérico a un usuario. Recibe una función que devuelve el
     * objeto con el mensaje ya preparado, de manera que el mensaje ya viene preparado
     * cuando la función devuelve el objeto.
     * @param function La función que devolverá el objeto NewEmailDTO
     * @param templateName El nombre de la plantilla que se quiere usar,
     * @throws MessagingException Por si hay algún error al enviar el mensaje
     */
    @Override
    public void sendGenericEmail(Supplier<NewEmailDTO> function, String templateName) throws MessagingException {
        NewEmailDTO data = function.get();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

        mimeMessageHelper.setTo(data.to().toLowerCase());
        mimeMessageHelper.setSubject(data.subject());

        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("message", data.message());
        String html = templateEngine.process(templateName, thymeleafContext);
        mimeMessageHelper.setText(html, true);

        javaMailSender.send(message);
    }

    @Override
    public void sendConfirmationCodeEmail(NewEmailDTO data) throws MessagingException {
        String text = data.message()
                .replace("{username}", String.valueOf(data.params().get("username")))
                .replace("{code}", String.valueOf(data.params().get("code")));

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

        mimeMessageHelper.setTo(data.to().toLowerCase());
        mimeMessageHelper.setSubject(data.subject());

        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("message", text);
        String html = templateEngine.process("completeRegister", thymeleafContext);
        mimeMessageHelper.setText(html, true);

        javaMailSender.send(message);
    }
}
