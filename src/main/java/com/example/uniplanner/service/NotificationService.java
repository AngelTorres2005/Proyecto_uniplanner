package com.example.uniplanner.service;


import com.example.uniplanner.model.tareas;
import com.example.uniplanner.repository.tareasRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private tareasRepository tareasRepo;

    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 8 * * *") //8AM de cada dia
    @Transactional
    public void enviarRecordatoriosDiarios() {
        LocalDate mañana = LocalDate.now().plusDays(1);
        List<tareas> tareasPendientes = tareasRepo.buscarTareasParaRecordatorio(mañana);

        for (tareas tarea : tareasPendientes) {
            String correoDestino = tarea.getUsuario().getCorreo();
            String nombreEstudiante = tarea.getUsuario().getNombre();
            String tituloTarea = tarea.getTitulo();
            String nombreMateria = tarea.getMateria().getNombre();


            String mensaje = "Hola " + nombreEstudiante + ",\n\n" +
                    "Este es un recordatorio de UniPlanner. Tu tarea \"" + tituloTarea + "\" " +
                    "de la materia " + nombreMateria + " vence mañana " + mañana + ".\n\n" +
                    "¡Mucho éxito!";

            enviarCorreo(correoDestino, "Recordatorio: Tarea para mañana", mensaje);
        }
    }

    public boolean enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            MimeMessage mail = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mail, true);

            helper.setFrom("m386bbb14c651@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje);

            mailSender.send(mail);
            System.out.println("Correo enviado con éxito a: " + destinatario);

            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            return false;
        }
    }
}
