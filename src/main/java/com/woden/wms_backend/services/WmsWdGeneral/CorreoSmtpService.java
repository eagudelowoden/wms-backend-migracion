package com.woden.wms_backend.services.WmsWdGeneral;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Envio de correo por SMTP desde la cuenta noreplyservices (Office 365, la misma de ReverseCore y el Contact Center).
 * Las credenciales llegan por variables de entorno, nunca en el repositorio:
 * SMTP_SERVER, SMTP_PORT (587), SMTP_USER, SMTP_PASSWORD, SMTP_FROM_NAME.
 */
@Service
public class CorreoSmtpService {

  /** id del logo del WMS incrustado en el correo: en la plantilla, src="cid:logo" */
  public static final String LOGO = "logo";

  @Value("${SMTP_SERVER:}")
  private String servidor;

  @Value("${SMTP_PORT:587}")
  private int puerto;

  @Value("${SMTP_USER:}")
  private String usuario;

  @Value("${SMTP_PASSWORD:}")
  private String clave;

  @Value("${SMTP_FROM_NAME:WMS Woden}")
  private String remitente;

  private volatile JavaMailSenderImpl sender;

  public boolean configurado() {
    return !servidor.isBlank() && !usuario.isBlank() && !clave.isBlank();
  }

  public String cuenta() {
    return usuario;
  }

  private JavaMailSenderImpl sender() {
    if (sender == null) {
      synchronized (this) {
        if (sender == null) {
          JavaMailSenderImpl s = new JavaMailSenderImpl();
          s.setHost(servidor);
          s.setPort(puerto);
          s.setUsername(usuario);
          s.setPassword(clave);
          s.setDefaultEncoding(StandardCharsets.UTF_8.name());
          Properties p = s.getJavaMailProperties();
          p.put("mail.transport.protocol", "smtp");
          p.put("mail.smtp.auth", "true");
          p.put("mail.smtp.starttls.enable", "true");
          p.put("mail.smtp.starttls.required", "true");
          p.put("mail.smtp.connectiontimeout", "20000");
          p.put("mail.smtp.timeout", "30000");
          p.put("mail.smtp.writetimeout", "30000");
          sender = s;
        }
      }
    }
    return sender;
  }

  /** Envia el correo HTML con el logo incrustado y los adjuntos (nombre -> contenido). */
  public void enviar(List<String> para, List<String> cc, String asunto, String html, Map<String, byte[]> adjuntos)
      throws MessagingException {
    if (!configurado()) {
      throw new IllegalStateException("Falta configurar SMTP_SERVER / SMTP_USER / SMTP_PASSWORD en el servidor");
    }
    if (para == null || para.isEmpty()) {
      throw new IllegalArgumentException("Sin destinatarios");
    }
    JavaMailSenderImpl s = sender();
    MimeMessage msg = s.createMimeMessage();
    MimeMessageHelper h = new MimeMessageHelper(msg, true, StandardCharsets.UTF_8.name());
    try {
      h.setFrom(new InternetAddress(usuario, remitente, StandardCharsets.UTF_8.name()));
    } catch (java.io.UnsupportedEncodingException e) {
      h.setFrom(usuario);
    }
    h.setTo(para.toArray(String[]::new));
    if (cc != null && !cc.isEmpty()) {
      h.setCc(cc.toArray(String[]::new));
    }
    h.setSubject(asunto);
    h.setText(html, true);
    h.addInline(LOGO, new ClassPathResource("mail/logo-wms.png"), "image/png");
    if (adjuntos != null) {
      for (Map.Entry<String, byte[]> a : adjuntos.entrySet()) {
        h.addAttachment(a.getKey(), new ByteArrayResource(a.getValue()));
      }
    }
    s.send(msg);
  }
}
