package es.ucm.fdi.iw.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Pregunta;
import es.ucm.fdi.iw.model.Respuesta;
import es.ucm.fdi.iw.model.Titulacion;
import es.ucm.fdi.iw.model.Universidad;
import es.ucm.fdi.iw.model.User;

/**
 * Non-authenticated requests only.
 */
@Controller
public class RootController {

    private static final Logger log = LogManager.getLogger(RootController.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LocalData localData;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("buscador", "Busca entre archivos, asignaturas o preguntas...");
        model.addAttribute("path", "/busqueda");
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "log";
    }

    @GetMapping("/tutorial")
    public String tutorial(Model model, HttpSession session) {

        long userId = ((User) session.getAttribute("u")).getId();
        User user = entityManager.find(User.class, userId);
        if (user.isFirst()) {
            model.addAttribute("universityHolder", "Escribe el nombre de tu universidad");
            // model.addAttribute("careerHolder", "Escribe la carrera que estás cursando");
            model.addAttribute("path", "/");
            List<Universidad> universidades = entityManager.createNamedQuery("allUniversidades", Universidad.class).setMaxResults(6)
                    .getResultList();
            model.addAttribute("universidades", universidades);
            return "usuarios/registroTutorial";
        }

        return "index";
    }

    @GetMapping("/perfil/{id}")
    public String Perfil(@PathVariable long id, Model model, HttpSession session) {
        try {

            User user = entityManager.find(User.class, id);
            log.info(user);
            Integer npreguntas = user.getPreguntas().size();
            Integer nrespuestas = user.getRespuestas().size();
            Titulacion titulo = user.getTitulacion();
            List<Pregunta> ultPreguntas = entityManager.createNamedQuery("User.UltPreguntas", Pregunta.class)
                    .setParameter("id", id).setMaxResults(10).getResultList();
            List<Respuesta> ultRespuestas = entityManager.createNamedQuery("User.UltRespuestas", Respuesta.class)
                    .setParameter("id", id).setMaxResults(10).getResultList();
            if (session.getAttribute("u") != null) {
                long userIdSession = ((User) session.getAttribute("u")).getId();

                if (userIdSession == id) {
                    List<Universidad> universidades = entityManager
                            .createNamedQuery("allUniversidades", Universidad.class)
                            .getResultList();

                    Universidad uniAct = user.getTitulacion() == null ? null : user.getTitulacion().getUniversidad();
                    model.addAttribute("universidades", universidades);
                    model.addAttribute("uniactual", uniAct);
                }
            }

            model.addAttribute("preguntas", ultPreguntas);
            model.addAttribute("respuestas", ultRespuestas);
            model.addAttribute("pregunta", npreguntas);
            model.addAttribute("respuesta", nrespuestas);
            model.addAttribute("user", user);
            model.addAttribute("titulo", titulo);

        } catch (Exception e) {
            return "log";
        }

        return "usuarios/perfil";

    }

    @GetMapping("actividad")
    public String preguntas(Model model) {
        List<Pregunta> preguntas = entityManager.createNamedQuery("preguntasRecientes", Pregunta.class).getResultList();
        model.addAttribute("preguntas_rec", preguntas);
        model.addAttribute("logged", Boolean.TRUE);
        return "preguntas/actividadReciente";
    }

    @SuppressWarnings("unchecked") // da un warning porque getResultList() no puede deducir qué tipo se devolverá,
                                   // pongo esto para que ignore el warning
    @GetMapping("/busqueda")
    public String busqueda(@RequestParam String query, @RequestParam(required = false) Integer page, Model model) {
        page = page == null || page < 1 ? 1 : page;

        int firstResult = (page - 1) * 6;
        int maxResults = entityManager.createNamedQuery("buscarPreguntaPorTitulo").setParameter("query", query)
                .getResultList().size();

        List<Pregunta> preguntas = entityManager.createNamedQuery("buscarPreguntaPorTitulo")
                .setParameter("query", query).setFirstResult(firstResult).setMaxResults(6).getResultList();
        model.addAttribute("preguntas", preguntas);

        List<Pregunta> preguntasRecientes = entityManager.createNamedQuery("preguntasRecientes").setMaxResults(2)
                .getResultList();
        model.addAttribute("preguntasRecientes", preguntasRecientes);

        List<Pregunta> preguntasConFichero = entityManager.createNamedQuery("preguntasConFicheroQuery")
                .setParameter("query", query).setMaxResults(2).getResultList();
        model.addAttribute("preguntasConFichero", preguntasConFichero);

        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("maxResults", maxResults);

        return "preguntas/busqueda";
    }

    // https://www.baeldung.com/spring-controller-return-image-file
    // Duda Lunes Profe.
    @GetMapping(path = "/aportacion/{id}/file", produces = MediaType.APPLICATION_PDF_VALUE)
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("aportacion", "" + id);
        InputStream in = new BufferedInputStream(new FileInputStream(f));

        return os -> FileCopyUtils.copy(in, os);
    }

    @PostMapping("/register")
    @Transactional
    public String registro(Model model,
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmacion,
            HttpSession session) {

        List<User> existente = entityManager.createNamedQuery("User.byUsername", User.class).setParameter("username", name)
                .getResultList();

        if (!existente.isEmpty()) {

            String error = "El nombre de usuario ya existe";
            model.addAttribute("error", error);

            return "log";
        }

        if (!password.equals(confirmacion)) {

            String error = "Las contraseñas no coinciden";
            model.addAttribute("error", error);

            return "log";
        }

        if (name.length() == 0 || username.length() == 0 || password.length() == 0 || confirmacion.length() == 0) {

            String error = "No pueden haber atributos vacíos";
            model.addAttribute("error", error);

            return "log";
        } else if (username.length() > 25 || name.length() > 25) {
            String error = "Algún campo excede el límite";
            model.addAttribute("error", error);

            return "log";
        }

        User user = new User();
        user.setUsername(name);
        user.setNivel(0);
        user.setCorreo(username);
        user.setPassword(password);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles("USER");
        user.setEnabled(true);
        user.setFirst(true);
        user.setUploaded(0);
        Long def = (long) 0;
        Titulacion defi = entityManager.find(Titulacion.class, def);
        user.setTitulacion(defi);

        List<User> userList = entityManager.createNamedQuery("User.byCorreo", User.class).setParameter("correo", username)
                .getResultList();

        if (userList.isEmpty()) {
            entityManager.persist(user);
            model.addAttribute("success", "ok");

            return "log";

        } else {
            String error = "Correo ya existente";
            model.addAttribute("error", error);
            return "log";
        }

    }

    @GetMapping("/grados")
    @ResponseBody
    public String preguntas(Model model, @RequestParam long uniId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Universidad uni = entityManager.find(Universidad.class, uniId);

        List<Titulacion> grados = uni.getTitulaciones();
        List<Titulacion.Transfer> gradosTrans = new ArrayList<Titulacion.Transfer>();

        for (Titulacion grado : grados) {
            gradosTrans.add(grado.toTransfer());
        }

        return mapper.writeValueAsString(gradosTrans);
    }

    @GetMapping("/universidades")
    public String verUniversidades(Model model) {

        List<Universidad> universidades = entityManager.createNamedQuery("allUniversidades", Universidad.class).setMaxResults(6)
                .getResultList();

        model.addAttribute("universidades", universidades);

        return "universidades/universidadesUser";
    }

    @GetMapping("/universidades/{uniId}")
    public String verUniversidad(@PathVariable Long uniId, Model model) {

        Universidad universidad = entityManager.find(Universidad.class, uniId);

        model.addAttribute("universidad", universidad);

        return "universidades/universidadUser";
    }

    @GetMapping("/searchUniversidades")
    @ResponseBody
    public String searchUniversidades(Model model, @RequestParam String query) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Universidad> universidades = entityManager
                .createNamedQuery("buscarUniversidadesPorTitulo", Universidad.class)
                .setParameter("query", query)
                .setMaxResults(6)
                .getResultList();

        List<Universidad.Transfer> unisTrans = new ArrayList<Universidad.Transfer>();

        for (Universidad uni : universidades) {
            unisTrans.add(uni.toTransfer());
        }

        log.info(universidades);

        return mapper.writeValueAsString(unisTrans);
    }

    @GetMapping("/encuentraUsuarios")
    public String encuentraUsuarios(Model model) {
        List<User> usuarios = entityManager.createNamedQuery("User.All", User.class).getResultList();

        model.addAttribute("userr", usuarios);
        model.addAttribute("newPlaceHolder", "Buscar usuario");
        model.addAttribute("path", "busquedaUsuario");
        return "usuarios/encontrarUsuarios";
    }

    @GetMapping("/busquedaUsuario")
    public String BuscarUsuario(@RequestParam(required = true, name = "query") String search, Model model,
            HttpSession session) {
        List<User> usuarios = entityManager.createNamedQuery("User.search", User.class).setParameter("username", search)
                .getResultList();

        model.addAttribute("userr", usuarios);
        model.addAttribute("newPlaceHolder", "Buscar usuario");
        model.addAttribute("path", "busquedaUsuario");

        return "usuarios/encontrarUsuarios";
    }

    @GetMapping("/imagenUniversidad")
    @ResponseBody
    public String imagenUniversidad(Model model, @RequestParam Long query) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Universidad universidades = entityManager.find(Universidad.class, query);
        log.info(universidades);
        return mapper.writeValueAsString(universidades.toTransfer());
    }

}
