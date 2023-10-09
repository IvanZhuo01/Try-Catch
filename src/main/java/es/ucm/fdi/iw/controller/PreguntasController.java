package es.ucm.fdi.iw.controller;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Aportacion;
import es.ucm.fdi.iw.model.Notificacion;
import es.ucm.fdi.iw.model.Notificacion.Tipo;
import es.ucm.fdi.iw.model.NotificationGenerator;
import es.ucm.fdi.iw.model.Pregunta;
import es.ucm.fdi.iw.model.Respuesta;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Votacion;
import es.ucm.fdi.iw.model.VotacionID;


@Controller
@RequestMapping("preguntas")
public class PreguntasController {
    private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;


    @GetMapping("{pregId}")
    public String pregunta(@PathVariable Long pregId, Model model) {

        try{
            // pregunta
            Pregunta pregunta = entityManager.find(Pregunta.class, pregId);
    
            Parser parser = Parser.builder().build();
            Node document = parser.parse(pregunta.getCuerpo());
            HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
            pregunta.setCuerpo(renderer.render(document));
    
            File f = localData.getFile("aportacion", "" + pregunta.getId());
    
            if (f.exists()) {
                log.info("Esta pregunta tiene fichero");
                model.addAttribute("fichero", pregunta.getFichero());
            } else {
                log.info("Esta pregunta NO tiene fichero");
            }
    
            model.addAttribute("pregunta", pregunta);
        }catch(Exception e){
            model.addAttribute("error", "not found");
        }

        return "preguntas/pregunta";
    }

    @GetMapping("{pregId}/api")
    @ResponseBody
    public String preguntaApi(@PathVariable Long pregId, Model model) throws JsonProcessingException {
        // pregunta
        Pregunta pregunta = entityManager.find(Pregunta.class, pregId);
        ObjectMapper mapper = new ObjectMapper();

        // Parser parser = Parser.builder().build();
        // Node document = parser.parse(pregunta.getCuerpo());
        // HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
        // pregunta.setCuerpo(renderer.render(document));

        // File f = localData.getFile("aportacion", "" + pregunta.getId());

        // if (f.exists()) {
        //     log.info("Esta pregunta tiene fichero");
        //     model.addAttribute("fichero", pregunta.getFichero());
        // } else {
        //     log.info("Esta pregunta NO tiene fichero");
        // }

        // model.addAttribute("pregunta", pregunta);



        return mapper.writeValueAsString(pregunta.toTransfer());
    }

    @GetMapping("/respuestas/{resId}/api")
    @ResponseBody
    public String respuestaApi(@PathVariable Long resId, Model model) throws JsonProcessingException {
        // pregunta
        Respuesta respuesta = entityManager.find(Respuesta.class, resId);
        ObjectMapper mapper = new ObjectMapper();

        // Parser parser = Parser.builder().build();
        // Node document = parser.parse(pregunta.getCuerpo());
        // HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
        // pregunta.setCuerpo(renderer.render(document));

        // File f = localData.getFile("aportacion", "" + pregunta.getId());

        // if (f.exists()) {
        //     log.info("Esta pregunta tiene fichero");
        //     model.addAttribute("fichero", pregunta.getFichero());
        // } else {
        //     log.info("Esta pregunta NO tiene fichero");
        // }

        // model.addAttribute("pregunta", pregunta);



        return mapper.writeValueAsString(respuesta.toTransfer());
    }

    @PostMapping("/responder/{pregId}")
    @Transactional
    public String respuesta(@PathVariable Long pregId, Model model,
            @RequestParam(required = true, name = "cuerpo") String cuerpo, HttpSession session)
            throws JsonProcessingException {

        Respuesta respuesta = new Respuesta();
        respuesta.setCuerpo(cuerpo);

        long userId = ((User) session.getAttribute("u")).getId();
        User user = entityManager.find(User.class, userId);
        respuesta.setUsuario(user);

        Pregunta pregunta = entityManager.find(Pregunta.class, pregId);
        respuesta.setPregunta(pregunta);

        entityManager.persist(respuesta);

        Notificacion m = new Notificacion();
        m = NotificationGenerator.respuesta(m, user.getUsername(), pregunta.getId());
        
        //Para poder meter un link a la pregunta respondida en markdown
        Parser parser = Parser.builder().build();
        Node documentTitle = parser.parse(m.getTitulo());
        Node documentBody = parser.parse(m.getCuerpo());
        HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
        m.setTitulo(renderer.render(documentTitle));
        m.setCuerpo(renderer.render(documentBody));

        m.setRecipient(pregunta.getUsuario());
        entityManager.persist(m);
        entityManager.flush(); // to get Id before commit

        ObjectMapper mapper = new ObjectMapper();

        log.info(m);

        String json = mapper.writeValueAsString(m.toTransfer());

        messagingTemplate.convertAndSend("/user/" + pregunta.getUsuario().getUsername() + "/queue/updates", json);

        return "redirect:/preguntas/" + pregId;
    }

    @PostMapping("/votacion/{res_id}")
    @Transactional
    @ResponseBody
    public String votacion(@PathVariable long res_id, Model model,
            @RequestBody JsonNode o,
            HttpSession session) {

        long userId = ((User) session.getAttribute("u")).getId();
        User user = entityManager.find(User.class, userId);
        Aportacion aportacion;
        try {
            aportacion = entityManager.find(Aportacion.class, res_id);
        } catch (Exception e) {
            return "log";
        }

        VotacionID votacionId = new VotacionID();
        votacionId.setAportacion_id(aportacion.getId());
        votacionId.setUsuario_id(userId);
        Votacion vot = entityManager.find(Votacion.class, votacionId);

        if (vot == null) {

            Votacion votacion = new Votacion();

            if (o.has("like")) {
                aportacion.setNumVotaciones(aportacion.getNumVotaciones() + 1);
                votacion.setTipo_voto(true);
                aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() + 5);
            }

            else if (o.has("dislike")) {
                aportacion.setNumVotaciones(aportacion.getNumVotaciones() - 1);
                votacion.setTipo_voto(false);
                aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() - 5);
                user.setNivel(user.getNivel() - 1);
            }
            votacion.setAportacion(aportacion);
            votacion.setUsuario(user);
            entityManager.persist(votacion);
            entityManager.persist(aportacion);
        } else {

            if (vot.isTipo_voto()) {
                if (o.has("like")) {
                    aportacion.setNumVotaciones(aportacion.getNumVotaciones() - 1);
                    aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() - 5);
                    entityManager.remove(vot);
                } else if (o.has("dislike")) {
                    log.info(aportacion.getNumVotaciones());
                    aportacion.setNumVotaciones(aportacion.getNumVotaciones() - 2);
                    log.info(aportacion.getNumVotaciones());
                    vot.setTipo_voto(false);
                    log.info(aportacion.getNumVotaciones());
                    aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() - 10);
                    user.setNivel(user.getNivel() - 1);
                    entityManager.persist(vot);
                }
            } else {
                if (o.has("like")) {
                    aportacion.setNumVotaciones(aportacion.getNumVotaciones() + 2);
                    aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() + 10);
                    vot.setTipo_voto(true);
                    user.setNivel(user.getNivel() + 1);
                    entityManager.persist(vot);
                } else if (o.has("dislike")) {
                    aportacion.getUsuario().setNivel(aportacion.getUsuario().getNivel() + 5);
                    user.setNivel(user.getNivel() + 1);
                    aportacion.setNumVotaciones(aportacion.getNumVotaciones() + 1);
                    entityManager.remove(vot);
                }

            }
            entityManager.persist(aportacion);
        }

        Notificacion m = new Notificacion();
        m.setTipo(Tipo.INFO);
        m.setRecipient(aportacion.getUsuario());
        m.setDateSent(LocalDateTime.now());
        m.setDateRead(LocalDateTime.now());
        m.setTitulo("Han votado su pregunta");
        m.setCuerpo("Te han votado");
        entityManager.persist(m);
        entityManager.flush();
        ObjectMapper mens = new ObjectMapper();
        String json =null;
        try {
            json =  mens.writeValueAsString(m.toTransfer());
           } catch (Exception e) {
               System.out.print("ERROR VOTACION");
           }
        messagingTemplate.convertAndSend("/user/" + aportacion.getUsuario().getUsername() + "/queue/updates", json);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ObjectNode childNode1 = mapper.createObjectNode();
        childNode1.put("numVotos", String.valueOf(aportacion.getNumVotaciones()));
        childNode1.put("numNivel", String.valueOf(user.getNivel()));
        childNode1.put("numNivelAportacion", String.valueOf(aportacion.getUsuario().getNivel()));
        childNode1.put("idUsu", String.valueOf(user.getId()));
        childNode1.put("idUsuApor", String.valueOf(aportacion.getUsuario().getId()));
        rootNode.set("obj1", childNode1);
        String s=null;
        try {
         s = mapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            System.out.print("ERROR VOTACION");
        }

        
        return s;
    }

    @PostMapping("/")
    @Transactional
    public String crearPregunta(Model model,
            @RequestParam(required = true, name = "titulo") String titulo,
            @RequestParam(required = false, name = "cuerpo") String cuerpo,
            @RequestParam(required = true, name = "etiquetas") String etiquetas,
            @RequestParam(required = false, name = "embebImg") String embebImg,
            @RequestParam(required = false, name = "filedoc") MultipartFile filedoc, HttpSession session) {

        List<String> errors = new ArrayList<String>();

        if (titulo.length() > 50) {
            errors.add("El titulo debe tener entre 0 y 50 caracteres.");
        }
        if (etiquetas.split(",").length > 10) {
            errors.add("La pregunta debe contener menos de 10 etiquedas.");
        }

        // if (filedoc != null && filedoc.isEmpty()) {
        // log.info("Crear Pregunta: failed to upload photo: emtpy file?");
        // errors.add("El fichero no se ha subido correctamente.");
        // }

        if (errors.size() > 0) {
            model.addAttribute("errors", errors);
            return "ultpreguntas";
        }

        Pregunta pregunta = new Pregunta();
        pregunta.setTitulo(titulo);
        pregunta.setCuerpo(cuerpo);
        pregunta.setEtiquetas(etiquetas);
        pregunta.setNumVotaciones(0);

        if(embebImg != null){
            pregunta.setEmbebImg(true);
        }

        // Esto es para formatear el codigo
        Parser parser = Parser.builder().build();
        Node document = parser.parse(cuerpo);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String renderedText = renderer.render(document);

        log.info(renderedText);

        long userId = ((User) session.getAttribute("u")).getId();
        User user = entityManager.find(User.class, userId);

        pregunta.setUsuario(user);

        entityManager.persist(pregunta);

        long id = pregunta.getId();

        // guardar el fichero (si lo hay)
        File f = localData.getFile("aportacion", "" + id);

        if (filedoc.isEmpty()) {
            log.info("Crear Pregunta: failed to upload photo: emtpy file?");
        } else {
            // if(mas de un mb) --> tira error;
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
                byte[] bytes = filedoc.getBytes();
                stream.write(bytes);
                log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
                pregunta.setFichero(filedoc.getOriginalFilename());
            } catch (Exception e) {
                // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.warn("Error uploading " + id + " ", e);
            }
        }

        return "redirect:/preguntas/" + pregunta.getId();
    }


    @PostMapping("{pregId}/editar")
    @Transactional
    public String editarPregunta(@PathVariable Long pregId, 
            Model model,
            @RequestParam(required = true, name = "titulo") String titulo,
            @RequestParam(required = false, name = "cuerpo") String cuerpo,
            @RequestParam(required = false, name = "embebImg") String embebImg,
            @RequestParam(required = true, name = "etiquetas") String etiquetas, HttpSession session){

        
        Pregunta pregunta = entityManager.find(Pregunta.class, pregId);

        if(pregunta.getUsuario().getId() != ((User) session.getAttribute("u")).getId()){
            return "redirect:/";
        }
        
        pregunta.setTitulo(titulo);
        pregunta.setCuerpo(cuerpo);
        pregunta.setEtiquetas(etiquetas);

        if(embebImg != null){
            pregunta.setEmbebImg(true);
        }else{
            pregunta.setEmbebImg(false);
        }

        log.info("ETIQUETAS" + etiquetas);

        return "redirect:/preguntas/" + pregunta.getId();
    }

    @PostMapping("/respuestas/{resId}/editar")
    @Transactional
    public String editarRespuesta(@PathVariable Long resId, 
            Model model,
            @RequestParam(required = false, name = "cuerpo") String cuerpo,
            HttpSession session){

        
        Respuesta respuesta = entityManager.find(Respuesta.class, resId);

        if(respuesta.getUsuario().getId() != ((User) session.getAttribute("u")).getId()){
            return "redirect:/";
        }

        respuesta.setCuerpo(cuerpo);

        return "redirect:/preguntas/" + respuesta.getPregunta().getId();
    }

    @PostMapping("/{pregId}/borrar")
    @Transactional
    public String borrarAportacion(@PathVariable long pregId, HttpSession session, Model model) {
        try{
            Aportacion apor = entityManager.find(Aportacion.class, pregId);
    
            if(apor != null && apor.getUsuario().getId() == ((User) session.getAttribute("u")).getId()){
                entityManager.remove(apor);  
            }
        }catch(Exception e){
            log.info("[Borrar Aportacion] La aportacion a borrar no existe");
        }
    
        return "redirect:/";
    }
}



