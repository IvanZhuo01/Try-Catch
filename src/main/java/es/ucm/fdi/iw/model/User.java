package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An authorized user of the system.
 */
@Entity
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "User.byUsername", query = "SELECT u FROM User u "
                + "WHERE u.username = :username AND u.enabled = TRUE"),
        @NamedQuery(name = "User.byEmail", query = "SELECT u FROM User u "
                + "WHERE u.correo = :username AND u.enabled = TRUE"),
        @NamedQuery(name = "User.byCorreo", query = "SELECT u FROM User u "
                + "WHERE u.correo = :correo"),
        @NamedQuery(name = "User.hasUsername", query = "SELECT COUNT(u) "
                + "FROM User u "
                + "WHERE u.username = :username"),
        @NamedQuery(name = "User.UltPreguntas", query = "SELECT t FROM Pregunta t "
                + "WHERE t.usuario.id = :id ORDER BY t.fecha desc"),
        @NamedQuery(name = "User.UltRespuestas", query = "SELECT t FROM Respuesta t "
                + "WHERE t.usuario.id = :id ORDER BY t.fecha desc"),
        @NamedQuery(name = "User.titulaciones", query = "SELECT t FROM Titulacion t WHERE t.universidad = :univer"),
        @NamedQuery(name = "allUsers", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.All", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.search", query = "SELECT u FROM User u "
                + "WHERE UPPER(u.username) LIKE CONCAT('%',UPPER(:username),'%')"),
})
@Table(name = "IWUser")
public class User implements Transferable<User.Transfer> {

    public enum Role {
        USER, // normal users
        ADMIN, // admin users
        PROFESOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String correo;
    private Integer uploaded;
    private Integer nivel;
    private boolean enabled;
    private int grado;
    private int downloaded;
    private String roles; // split by ',' to separate roles
    private boolean first;

    @OneToMany
    @JoinColumn(name = "sender_id")
    private List<Notificacion> sent = new ArrayList<>();
    @OneToMany
    @JoinColumn(name = "recipient_id")
    private List<Notificacion> received = new ArrayList<>();

    @ManyToOne
    private Titulacion titulacion;

    @OneToMany
    @JoinColumn(name = "usuario_id")
    private List<Aportacion> aportaciones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Votacion> votaciones;

    @OneToMany
    @JoinColumn(name = "usuario_id")
    private List<Pregunta> preguntas = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "usuario_id")
    private List<Respuesta> respuestas = new ArrayList<>();

    /**
     * Checks whether this user has a given role.
     * 
     * @param role to check
     * @return true iff this user has that role.
     */
    public boolean hasRole(Role role) {
        String roleName = role.name();
        return Arrays.asList(roles.split(",")).contains(roleName);
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String username;
        private String correo;
        private Titulacion titulacion;
        private int nivel;
        private boolean enabled;
        private int totalReceived;
        private int totalSent;
        private boolean first;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, username, correo, titulacion, nivel, enabled, received.size(), sent.size(), first);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }
}
