package com.woden.wms_backend.services.ClienteServices;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import jakarta.mail.internet.MimeMessage;

/**
 * Envía el correo con el enlace de aprobación por SMTP (Office 365).
 * Configuración (application.properties o variables de entorno):
 * MAIL_HOST, MAIL_PORT, MAIL_USER, MAIL_PASS.
 *
 * El remitente SMTP se crea aquí y NO se registra como bean: un bean
 * JavaMailSender activaría el notificador de correo de Spring Boot Admin,
 * que en este proyecto no tiene servidor y rompe el arranque.
 *
 * Si MAIL_HOST está vacío no falla: devuelve false y deja el enlace en el log
 * para poder probar en desarrollo.
 */
@Service
public class AutogestionCorreoService {
  private static final Logger logger = LoggerFactory.getLogger(AutogestionCorreoService.class);

  private final String host;
  private final int port;
  private final String user;
  private final String pass;
  private final String from;

  public AutogestionCorreoService(
      @Value("${MAIL_HOST:}") String host,
      @Value("${MAIL_PORT:587}") int port,
      @Value("${MAIL_USER:}") String user,
      @Value("${MAIL_PASS:}") String pass,
      @Value("${autogestion.mail.from:}") String from) {
    this.host = host;
    this.port = port;
    this.user = user;
    this.pass = pass;
    this.from = from;
  }

  public boolean enviarSolicitud(String destinatario, String nombreAprobador, String solicitante,
      String resumen, String justificacion, String enlace, int horasVigencia) {
    if (host == null || host.isBlank()) {
      logger.warn("[Autogestion] MAIL_HOST no configurado: no se envió correo a {}. Enlace de aprobación (solo pruebas): {}",
          destinatario, enlace);
      return false;
    }
    try {
      JavaMailSenderImpl sender = crearSender();
      MimeMessage message = sender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
      helper.setFrom(from != null && !from.isBlank() ? from : user);
      helper.setTo(destinatario);
      helper.setSubject("WMS · Solicitud de movimiento pendiente de aprobación");
      helper.setText(cuerpo(nombreAprobador, solicitante, resumen, justificacion, enlace, horasVigencia), true);
      sender.send(message);
      return true;
    } catch (Exception e) {
      logger.error("[Autogestion] No se pudo enviar el correo a {}: {}", destinatario, e.getMessage());
      return false;
    }
  }

  private JavaMailSenderImpl crearSender() {
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setHost(host);
    sender.setPort(port);
    sender.setUsername(user);
    sender.setPassword(pass);
    sender.setDefaultEncoding("UTF-8");

    Properties props = sender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.connectiontimeout", "10000");
    props.put("mail.smtp.timeout", "15000");
    props.put("mail.smtp.writetimeout", "15000");
    return sender;
  }

  private String cuerpo(String nombre, String solicitante, String resumen, String justificacion,
      String enlace, int horas) {
    return "<div style=\"font-family:Arial,sans-serif;max-width:560px;margin:auto;color:#1f2937\">"
        + "<h2 style=\"color:#2563eb\">Solicitud de movimiento</h2>"
        + "<p>Hola " + HtmlUtils.htmlEscape(nombre == null ? "" : nombre) + ",</p>"
        + "<p><b>" + HtmlUtils.htmlEscape(solicitante) + "</b> solicita autorización para:</p>"
        + "<p style=\"background:#f3f4f6;padding:12px;border-radius:8px\">" + HtmlUtils.htmlEscape(resumen) + "</p>"
        + "<p><b>Justificación:</b><br>" + HtmlUtils.htmlEscape(justificacion) + "</p>"
        + "<p style=\"margin:24px 0\"><a href=\"" + HtmlUtils.htmlEscape(enlace) + "\" "
        + "style=\"background:#2563eb;color:#fff;padding:12px 22px;border-radius:8px;text-decoration:none;font-weight:bold\">"
        + "Revisar y autorizar</a></p>"
        + "<p style=\"font-size:12px;color:#6b7280\">El enlace es personal y de un solo uso, y vence en " + horas
        + " horas. Al abrirlo verá el detalle y podrá autorizar o rechazar. No lo reenvíe.</p>"
        + "</div>";
  }
}
