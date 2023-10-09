package es.ucm.fdi.iw.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Aportacion;
import es.ucm.fdi.iw.model.Notificacion;
import es.ucm.fdi.iw.model.Notificacion.Tipo;
import es.ucm.fdi.iw.model.Pregunta;
import es.ucm.fdi.iw.model.Titulacion;
import es.ucm.fdi.iw.model.Universidad;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

/**
 * Site administration.
 *
 * Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {
    private static final Logger log = LogManager.getLogger(AdminController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LocalData localData;

    @GetMapping("/")
    public String vistaP(Model model, HttpSession session) {
        List<User> usuarios = entityManager.createNamedQuery("User.All", User.class).getResultList();

        List<Pregunta> Listapreguntas = entityManager.createNamedQuery("allPreguntas", Pregunta.class).getResultList();
        int numero = Listapreguntas.size();
        for (int i = 0; i < Listapreguntas.size(); i++) {
            numero += Listapreguntas.get(i).getRespuestas().size();
        }
        List<Integer> numFicheross = entityManager.createNamedQuery("fichero").getResultList();

        long userId = ((User) session.getAttribute("u")).getId();
        User currentUser = entityManager.find(User.class, userId);
        List<User> users = entityManager.createNamedQuery("allUsers", User.class).getResultList();
        ArrayList<User> admins = new ArrayList<User>();
        for (User user : users) {

            if (user.hasRole(Role.ADMIN)) {
                admins.add(user);
            }

        }

        model.addAttribute("userr", usuarios);
        model.addAttribute("newPlaceHolder", "Buscar usuario");
        model.addAttribute("path", "busquedaUsuario");
        model.addAttribute("user", currentUser);
        model.addAttribute("preguntas", numero);
        model.addAttribute("ficheros", numFicheross.get(0));
        model.addAttribute("admins", admins);
        return "admin/vistaPrincipalAdmin";
    }

    @GetMapping("/universidades")
    public String verUniversidades(Model model, HttpSession session) {
        try {

            long userId = ((User) session.getAttribute("u")).getId();
            User currentUser = entityManager.find(User.class, userId);

            model.addAttribute("user", currentUser);

            List<User> users = entityManager.createNamedQuery("allUsers", User.class).getResultList();

            ArrayList<User> admins = new ArrayList<User>();

            for (User user : users) {

                if (user.hasRole(Role.ADMIN)) {
                    admins.add(user);
                }
            }

            model.addAttribute("admins", admins);
            List<Universidad> universidades = entityManager.createNamedQuery("allUniversidades", Universidad.class).setMaxResults(6)
                    .getResultList();

            model.addAttribute("universidades", universidades);
        } catch (Exception e) {
            return "log";
        }

        return "universidades/universidadesAdmin";
    }

    @PostMapping("/universidades")
    @Transactional
    public String crearUniversidad(Model model,
            @RequestParam(required = true, name = "nombre") String nombre,
            @RequestParam(required = true, name = "localizacion") String localizacion,
            @RequestParam(required = true, name = "descripcion") String descripcion,
            @RequestParam(name = "foto") MultipartFile foto, HttpSession session) {

        
        Universidad universidad = new Universidad();
        universidad.setNombre(nombre);
        universidad.setDescripcion(descripcion);
        universidad.setLocalizacion(localizacion);
        entityManager.persist(universidad);
        if (!foto.isEmpty()) {
            try {
                File f = localData.getFile("universidad", universidad.getId() + ".jpg");
                foto.transferTo(new File(f.getAbsolutePath()));
            } catch (Exception e) {
                log.info("Error guardando foto");
            }
        }
        entityManager.persist(universidad);

        return "redirect:/admin/universidades";
    }

    @GetMapping("/universidades/{uniId}")
    public String verUniversidad(@PathVariable Long uniId, Model model, HttpSession session) {

        try {

            long userId = ((User) session.getAttribute("u")).getId();
            User currentUser = entityManager.find(User.class, userId);

            model.addAttribute("user", currentUser);

            List<User> users = entityManager.createNamedQuery("allUsers", User.class).getResultList();

            ArrayList<User> admins = new ArrayList<User>();

            for (User user : users) {

                if (user.hasRole(Role.ADMIN)) {
                    admins.add(user);
                }

            }

            model.addAttribute("admins", admins);
            List<Universidad> universidades = entityManager.createNamedQuery("allUniversidades", Universidad.class).setMaxResults(6)
                    .getResultList();

            model.addAttribute("universidades", universidades);
            Universidad universidad = entityManager.find(Universidad.class, uniId);

            model.addAttribute("universidad", universidad);

        } catch (Exception e) {
            return "log";
        }

        return "universidades/universidadAdmin";
    }

    @PostMapping("/crearTitulacion")
    @Transactional
    public String crearTitulacion(Model model,
            @RequestParam(required = true, name = "titulacion") String nombreTit,
            @RequestParam(required = true, name = "universidad") long uniId,
            HttpSession session) {

        Titulacion titulacion = new Titulacion();

        Universidad universidad = entityManager.find(Universidad.class, uniId);

        titulacion.setUniversidad(universidad);
        titulacion.setGrado(nombreTit);

        entityManager.persist(titulacion);

        return "redirect:/admin/universidades/" + uniId;
    }

    @PostMapping("/aportacion/{pregId}/borrar")
    @Transactional
    public String borrarPregunta(@PathVariable long pregId, HttpSession session, Model model) {
        try {
            Aportacion apor = entityManager.find(Aportacion.class, pregId);

            if (apor != null) {
                entityManager.remove(apor);
            }
        } catch (Exception e) {
            log.info("[Borrar Aportacion] La aportacion a borrar no existe");
        }

        return "redirect:/";
    }

    @PostMapping("/ayadirProfe/{id}")
    @Transactional
    @ResponseBody
    public String ayadirProfe(@PathVariable long id, @RequestBody JsonNode data, Model model, HttpSession session)
            throws JsonProcessingException {
        User recipient = entityManager.find(User.class, id);
        boolean isProfe = data.get("prof").asBoolean();

        Notificacion m = new Notificacion();
        m.setTipo(Tipo.INFO);

        m.setRecipient(recipient);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());

        if (recipient != null && isProfe) {

            String roles = recipient.getRoles();
            if (roles.length() > 0)
                roles += ",PROFESOR";
            else
                roles += "PROFESOR";

            recipient.setRoles(roles);

            m.setTitulo("Enhorabuena, su rol ahora es de profesor");
            m.setCuerpo("Felicitaciones, su rol ha pasado a ser el de profesor.");

            entityManager.persist(recipient);

        } else if (recipient != null && !isProfe) {

            List<String> arrayRoles = Arrays.asList(recipient.getRoles().split(","));
            List<String> arrR = new ArrayList<>(arrayRoles);
            int index = arrayRoles.indexOf("PROFESOR");

            if (index != -1) {
                arrR.remove(index);
                recipient.setRoles(String.join(",", arrR));

                m.setTitulo("Ya no es profesor");
                m.setCuerpo("Lo sentimos, usted ya no es profesor");
                entityManager.persist(recipient);

            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(m.toTransfer());
        messagingTemplate.convertAndSend("/user/" + recipient.getUsername() + "/queue/updates", json);
        entityManager.persist(m);
        return "1";
    }

    @PostMapping("/desactivarUsuario/{id}")
    @Transactional
    @ResponseBody
    public String desactivarUsuario(@PathVariable long id, @RequestBody JsonNode data, Model model, HttpSession session)
            throws JsonProcessingException {
        User recipient = entityManager.find(User.class, id);
        boolean isActivo = data.get("acti").asBoolean();

        Notificacion m = new Notificacion();
        m.setTipo(Tipo.AVISO);
        m.setRecipient(recipient);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());

        if (recipient != null && isActivo) {
            recipient.setEnabled(true);

            m.setTitulo("Se ha activo su cuenta");
            m.setCuerpo("Felicitaciones, su cuenta ha sido activada");

            entityManager.persist(recipient);
        } else if (recipient != null && !isActivo) {

            recipient.setEnabled(false);

            m.setTitulo("Se ha desactivo su cuenta");
            m.setCuerpo("Lo sentimos, hemos desactivado su cuenta");

            entityManager.persist(recipient);
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(m.toTransfer());
        messagingTemplate.convertAndSend("/user/" + recipient.getUsername() + "/queue/updates", json);
        entityManager.persist(m);

        return "1";
    }

    @PostMapping("/serAdmin/{id}")
    @Transactional
    @ResponseBody
    public String serAdmin(@PathVariable long id, @RequestBody JsonNode data, Model model, HttpSession session)
            throws JsonProcessingException {
        User recipient = entityManager.find(User.class, id);
        boolean isAdmin = data.get("admin").asBoolean();

        Notificacion m = new Notificacion();
        m.setTipo(Tipo.INFO);
        m.setRecipient(recipient);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());

        if (recipient != null && isAdmin) {
            String roles = recipient.getRoles();
            if (roles.length() > 0)
                roles += ",ADMIN";
            else
                roles += "ADMIN";

            recipient.setRoles(roles);

            m.setTitulo("Ascenso de Rol");
            m.setCuerpo("Felicitaciones, se ha añadido el rol de ADMIN.");

            entityManager.persist(recipient);
        } else if (recipient != null && !isAdmin) {

            List<String> arrayRoles = Arrays.asList(recipient.getRoles().split(","));
            List<String> arrR = new ArrayList<>(arrayRoles);
            int index = arrayRoles.indexOf("ADMIN");

            if (index != -1) {
                arrR.remove(index);
                recipient.setRoles(String.join(",", arrR));

                m.setTitulo("Descenso de Rol");
                m.setCuerpo("Lo sentimos, usted ya no es ADMIN");
                entityManager.persist(recipient);

            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(m.toTransfer());
        messagingTemplate.convertAndSend("/user/" + recipient.getUsername() + "/queue/updates", json);
        entityManager.persist(m);

        String jsonn = mapper.writeValueAsString(recipient.toTransfer());

        return jsonn;
    }

    @GetMapping("/busquedaUsuario")
    public String BuscarUsuario(@RequestParam(required = true, name = "query") String search, Model model,
            HttpSession session) {

        List<User> usuarios = entityManager.createNamedQuery("User.search", User.class).setParameter("username", search)
                .getResultList();
        List<Pregunta> Listapreguntas = entityManager.createNamedQuery("allPreguntas", Pregunta.class).getResultList();
        int numero = Listapreguntas.size();
        for (int i = 0; i < Listapreguntas.size(); i++) {
            numero += Listapreguntas.get(i).getRespuestas().size();
        }
        List<Integer> numFicheross = entityManager.createNamedQuery("fichero", Integer.class).getResultList();

        long userId = ((User) session.getAttribute("u")).getId();
        User currentUser = entityManager.find(User.class, userId);
        List<User> users = entityManager.createNamedQuery("allUsers", User.class).getResultList();
        ArrayList<User> admins = new ArrayList<User>();
        for (User user : users) {

            if (user.hasRole(Role.ADMIN)) {
                admins.add(user);
            }

        }

        model.addAttribute("userr", usuarios);
        model.addAttribute("newPlaceHolder", "Buscar usuario");
        model.addAttribute("path", "busquedaUsuario");
        model.addAttribute("preguntas", numero);
        model.addAttribute("ficheros", numFicheross.get(0));
        model.addAttribute("admins", admins);
        model.addAttribute("user", currentUser);
        return "admin/vistaPrincipalAdmin";
    }

    @GetMapping("/notificaciones")
    public String notificacionesPendientes(Model model, HttpSession session) {

        long userId = ((User) session.getAttribute("u")).getId();
        User currentUser = entityManager.find(User.class, userId);
        List<User> users = entityManager.createNamedQuery("allUsers", User.class).getResultList();
        ArrayList<User> admins = new ArrayList<User>();
        for (User user : users) {

            if (user.hasRole(Role.ADMIN)) {
                admins.add(user);
            }

        }

        List<Notificacion> notificaciones = entityManager.createNamedQuery("Notificacion.getNotificionesAdmin", Notificacion.class)
                .getResultList();

        model.addAttribute("user", currentUser);
        model.addAttribute("admins", admins);
        model.addAttribute("notificaciones", notificaciones);
        return "admin/notificacionesAdmin";
    }

    @PostMapping("/reporte/{idRecipt}")
    @Transactional
    public String reporte(@PathVariable Long idRecipt, @RequestParam String cuerpo, HttpSession session)
            throws JsonProcessingException {

        User recipient = entityManager.find(User.class, idRecipt);
        User sender = entityManager.find(User.class, ((User) session.getAttribute("u")).getId());

        // Crear notificaion de Profesor para admin
        Notificacion m = new Notificacion();
        m.setTipo(Tipo.AVISO);
        m.setRecipient(recipient);
        m.setSender(sender);
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());
        m.setTitulo("Aviso de reporte");
        m.setCuerpo(cuerpo);

        entityManager.persist(m);
        entityManager.flush(); // to get Id before commit

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(m.toTransfer());
        messagingTemplate.convertAndSend("/user/" + recipient.getUsername() + "/queue/updates", json);

        return "redirect:/admin/";
    }

    @PostMapping("{id}/notificacion/asignar/{notiId}")
    @Transactional
    @ResponseBody
    public String asignarNotificacion(@PathVariable long id, @PathVariable long notiId,
            HttpServletResponse response, HttpSession session, Model model) throws IOException {

        User currentUser = entityManager.find(User.class, id);
        Notificacion n = entityManager.find(Notificacion.class, notiId);

        n.setRecipient(currentUser); // asignamos la notificacion a un admin concreto

        entityManager.persist(n);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(n.toTransfer());

        return json;
    }
}
