package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;

import es.ucm.fdi.iw.model.Notificacion.Tipo;

public class NotificationGenerator {

    public static Notificacion respuesta(Notificacion m, String usuario, long idPreg) {
        m.setTipo(Tipo.INFO);
        m.setTitulo("Has recibido una [respuesta](/preguntas/" + idPreg + "). ");
        m.setCuerpo("El usuario " + usuario + " ha respondido a tu [pregunta](/preguntas/" + idPreg + ").");
        m.setTipo(Tipo.INFO);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());

        return m;
    }

    public static Notificacion requestProfesor(Notificacion m, User usuario, String cuerpo) {
        
        m.setTitulo("Solicitud para ser Profesor: ");
        m.setCuerpo("El usuario " + usuario.getUsername()
                + " ha solicitado tener el rol de profesor por las siguientes razones: \n"
                + cuerpo);
        m.setTipo(Tipo.GESTION);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());
        m.setSender(usuario);

        return m;
    }

    public static Notificacion reportUser(Notificacion m, User usuario, String reportado, String cuerpo) {
        m.setTitulo("Reporte de usuario: ");
        m.setCuerpo(
                "El usuario " + usuario.getUsername() + " ha reportado un mal comportamiento del usuario " + reportado +
                        "por las siguientes razones: " + cuerpo);
        m.setTipo(Tipo.GESTION);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());
        m.setSender(usuario);

        return m;
    }

    public static Notificacion solicitud(Notificacion m, String titulo, String detalles, String topic,
            User Usuarioemisor) {
        m.setTopic(topic);
        m.setTitulo(titulo);
        m.setCuerpo(detalles);
        m.setTipo(Tipo.GESTION);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());
        m.setSender(Usuarioemisor);

        return m;
    }

}
