package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Notificacion;
import es.ucm.fdi.iw.model.NotificationGenerator;
import es.ucm.fdi.iw.model.Titulacion;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User management.
 *
 * Access to this end-point is authenticated.
 */
@Controller()
@RequestMapping("user")
public class UserController {

	private static final Logger log = LogManager.getLogger(UserController.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Exception to use when denying access to unauthorized users.
	 * 
	 * In general, admins are always authorized, but users cannot modify
	 * each other's profiles.
	 */
	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "No eres administrador, y éste no es tu perfil") // 403
	public static class NoEsTuPerfilException extends RuntimeException {
	}

	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * 
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 *         for example, a possible encoding of "test" is
	 *         {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	/**
	 * Generates random tokens. From https://stackoverflow.com/a/44227131/15472
	 * 
	 * @param byteLength
	 * @return
	 */
	public static String generateRandomBase64Token(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] token = new byte[byteLength];
		secureRandom.nextBytes(token);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(token); // base64 encoding
	}

	/**
	 * Landing page for a user profile
	 */
	@GetMapping("{id}")
	public String index(@PathVariable long id, Model model, HttpSession session) {
		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);
		return "user";
	}

	/**
	 * Alter or create a user
	 */
	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id,
			@ModelAttribute User edited,
			@RequestParam(required = false) String pass2,
			Model model, HttpSession session) throws IOException {

		User requester = (User) session.getAttribute("u");
		User target = null;
		if (id == -1 && requester.hasRole(Role.ADMIN)) {
			// create new user with random password
			target = new User();
			target.setPassword(encodePassword(generateRandomBase64Token(12)));
			target.setEnabled(true);
			entityManager.persist(target);
			entityManager.flush(); // forces DB to add user & assign valid id
			id = target.getId(); // retrieve assigned id from DB
		}

		// retrieve requested user
		target = entityManager.find(User.class, id);
		model.addAttribute("user", target);

		if (requester.getId() != target.getId() &&
				!requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}

		if (edited.getPassword() != null) {
			if (!edited.getPassword().equals(pass2)) {
				// FIXME: complain
			} else {
				// save encoded version of password
				target.setPassword(encodePassword(edited.getPassword()));
			}
		}
		target.setUsername(edited.getUsername());
		// target.setFirstName(edited.getFirstName());
		// target.setLastName(edited.getLastName());

		// update user session so that changes are persisted in the session, too
		if (requester.getId() == target.getId()) {
			session.setAttribute("u", target);
		}

		return "user";
	}

	/**
	 * Returns the default profile pic
	 * 
	 * @return
	 */
	private static InputStream defaultPic() {
		return new BufferedInputStream(Objects.requireNonNull(
				UserController.class.getClassLoader().getResourceAsStream(
						"static/img/default-pic.jpg")));
	}

	private static InputStream defaultPicUniversidad() {
		return new BufferedInputStream(Objects.requireNonNull(
				UserController.class.getClassLoader().getResourceAsStream(
						"static/img/default-universidad.jpg")));
	}

	/**
	 * Downloads a profile pic for a user id
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@GetMapping("{id}/pic")
	public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
		File f = localData.getFile("user", "" + id + ".jpg");
		InputStream in = new BufferedInputStream(f.exists() ? new FileInputStream(f) : UserController.defaultPic());

		return os -> FileCopyUtils.copy(in, os);
	}

	@GetMapping("universidad/{id}/pic")
	public StreamingResponseBody getPicUniversidad(@PathVariable long id) throws IOException {
		File f = localData.getFile("universidad", "" + id + ".jpg");
		InputStream in = new BufferedInputStream(f.exists() ? new FileInputStream(f) : UserController.defaultPicUniversidad());

		return os -> FileCopyUtils.copy(in, os);
	}

	/**
	 * Uploads a profile pic for a user id
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@PostMapping("{id}/pic")
	@ResponseBody
	public String setPic(@RequestParam("photo") MultipartFile photo, @PathVariable long id,
			HttpServletResponse response, HttpSession session, Model model) throws IOException {

		User target = entityManager.find(User.class, id);
		model.addAttribute("user", target);

		// check permissions
		User requester = (User) session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				!requester.hasRole(Role.ADMIN)) {
			throw new NoEsTuPerfilException();
		}

		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", "" + id + ".jpg");
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
				log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				log.warn("Error uploading " + id + " ", e);
			}
		}
		return "{\"status\":\"photo uploaded correctly\"}";
	}

	/**
	 * Returns JSON with all received messages
	 */
	@GetMapping(path = "received", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Notificacion.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((User) session.getAttribute("u")).getId();
		User u = entityManager.find(User.class, userId);
		log.info("Generating message list for user {} ({} messages)",
				u.getUsername(), u.getReceived().size());

		List<Notificacion> notifications = entityManager.createNamedQuery("Notificacion.getNonRead", Notificacion.class)
				.setParameter("userId", userId).getResultList();

		return notifications.stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}

	/**
	 * Returns JSON with count of unread messages
	 */
	@GetMapping(path = "unread", produces = "application/json")
	@ResponseBody
	public String checkUnread(HttpSession session) {
		long userId = ((User) session.getAttribute("u")).getId();
		long unread = entityManager.createNamedQuery("Notificacion.countUnread", Long.class)
				.setParameter("userId", userId)
				.getSingleResult();
		session.setAttribute("unread", unread);
		return "{\"unread\": " + unread + "}";
	}

	/**
	 * Posts a message to a user.
	 * 
	 * @param id of target user (source user is from ID)
	 * @param o  JSON-ized message, similar to {"message": "text goes here"}
	 * @throws JsonProcessingException
	 */
	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id,
			@RequestBody JsonNode o, Model model, HttpSession session)
			throws JsonProcessingException {

		String text = o.get("message").asText();
		User u = entityManager.find(User.class, id);
		User sender = entityManager.find(
				User.class, ((User) session.getAttribute("u")).getId());
		model.addAttribute("user", u);

		// construye mensaje, lo guarda en BD
		Notificacion m = new Notificacion();
		m.setRecipient(u);
		// m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		// m.setText(text);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(m.toTransfer());

		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/" + u.getUsername() + "/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}

	@PostMapping("{id}/notificacion/{notiId}")
	@Transactional
	@ResponseBody
	public String setNotificationRead(@PathVariable long id, @PathVariable long notiId,
			HttpServletResponse response, HttpSession session, Model model) throws IOException {

		Notificacion n = entityManager.find(Notificacion.class, notiId);

		long userId = ((User) session.getAttribute("u")).getId();

		if (n.getRecipient().getId() == id && userId == id) {

			n.setDateRead(LocalDateTime.now());
			n.setRead(true);
			entityManager.persist(n);

			return "{\"status\":\"notification read correctly\"}";
		}

		return "{\"status\":\"something bad happened\"}";
	}

	@GetMapping("{id}/notificacion/{notiId}")
	@ResponseBody
	public String getNotification(@PathVariable long id, @PathVariable long notiId,
			HttpServletResponse response, HttpSession session, Model model) throws IOException {

		Notificacion n = entityManager.find(Notificacion.class, notiId);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(n.toTransfer());

		return json;
	}

	@PostMapping("/editarPerfil")
	@Transactional
	public String editarPerfil(
			@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "grado", required = false) Long grado,
			@RequestParam(name = "curso", required = false) Integer curso,
			@RequestParam(name = "foto") MultipartFile foto,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttrs) {

		log.info("GRADO", grado == null);

		long userId = ((User) session.getAttribute("u")).getId();
		User user = entityManager.find(User.class, userId);

		Titulacion titulacion = null;

		if (grado != null) {
			titulacion = entityManager.find(Titulacion.class, grado);
		}

		List<User> existente = entityManager.createNamedQuery("User.byUsername", User.class)
				.setParameter("username", username).getResultList();

		if (!username.isEmpty() && existente.isEmpty()) {
			user.setUsername(username);
		} else if (!existente.isEmpty() && !existente.contains(user)) {
			redirectAttrs.addFlashAttribute("errorMessage",
					"El nombre de usuario ingresado ya existe. Por favor, ingrese otro.");
			return "redirect:/perfil";
		}

		if (!foto.isEmpty()) {
			try {
				File f = localData.getFile("user", user.getId() + ".jpg");
				foto.transferTo(new File(f.getAbsolutePath()));
			} catch (Exception e) {
				log.info("error al guardar foto");
			}
		}

		if (curso != null) {
			user.setGrado(curso);
		}

		if (titulacion != null) {
			user.setTitulacion(titulacion);
		}

		redirectAttrs.addFlashAttribute("successMessage", "Los datos fueron modificados correctamente");
		return "redirect:/perfil/" + user.getId();
	}

	@PostMapping("/cambiarPW")
	@Transactional
	public String changePassword(
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			Model model, HttpSession session, RedirectAttributes redirectAttrs) {
		// Obtener el usuario actual a partir de la sesión
		User actual = ((User) session.getAttribute("u"));
		if (!passwordEncoder.matches(currentPassword, actual.getPassword())) {
			redirectAttrs.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
			return "redirect:/perfil/" + actual.getId();
		}

		// Validar la confirmación de la nueva contraseña
		if (!newPassword.equals(confirmPassword)) {
			redirectAttrs.addFlashAttribute("errorMessage", "Las contraseñas no coinciden.");
			return "redirect:/perfil/" + actual.getId();
		}
		// Actualizar la contraseña del usuario

		actual.setPassword(passwordEncoder.encode(newPassword));
		entityManager.merge(actual);
		entityManager.flush();
		redirectAttrs.addFlashAttribute("successMessage", "La contraseña se ha actualizado correctamente.");
		return "redirect:/perfil/" + actual.getId();
	}

	@PostMapping("/solicitudUni")
	@Transactional
	@ResponseBody
	public void solicitudUni(@RequestBody JsonNode o, HttpSession session) throws JsonProcessingException {

		String universidad = o.get("titulo").asText();
		String detalles = o.get("cuerpo").asText();
		User sender = entityManager.find(
				User.class, ((User) session.getAttribute("u")).getId());

		Notificacion m = new Notificacion();
		m = NotificationGenerator.solicitud(m, universidad, detalles, "universidad", sender);

		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(m.toTransfer());
		messagingTemplate.convertAndSend("/topic/admin", json);

	}

	@PostMapping("/solicitudTitulacion")
	@Transactional
	@ResponseBody
	public void solicitudTitulacion(@RequestBody JsonNode o, HttpSession session) throws JsonProcessingException {

		String titulacion = o.get("titulo").asText();
		User sender = entityManager.find(
				User.class, ((User) session.getAttribute("u")).getId());
		String detalles = o.get("cuerpo").asText();
		// construye mensaje, lo guarda en BD

		Notificacion m = new Notificacion();
		m = NotificationGenerator.solicitud(m, titulacion, detalles, "Titulacion", sender);

		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(m.toTransfer());
		messagingTemplate.convertAndSend("/topic/admin", json);

	}

	@PostMapping("/completarTutorial")
	@Transactional
	@ResponseBody
	public String completarTutorial(@RequestBody JsonNode datos, HttpSession session) {
		long userId = ((User) session.getAttribute("u")).getId();
		User user = entityManager.find(User.class, userId);
		Long iduni = datos.get("datos").get("iduni").asLong();
		String grado = datos.get("datos").get("grado").asText();
		Integer curso = datos.get("datos").get("curso").asInt();
		Titulacion titulacion = null;

		if (grado != null) {
			titulacion = entityManager.createNamedQuery("Titulacion.titulo", Titulacion.class)
					.setParameter("universidad", iduni).setParameter("grado", grado).getSingleResult();
		}
		if (curso != null) {
			user.setGrado(curso);
		}

		if (titulacion != null) {
			user.setTitulacion(titulacion);
		}
		user.setFirst(false);
		return "";
	}

	@PostMapping("/reporte")
	@Transactional
	public String reporte(@RequestParam String cuerpo, long reportado, HttpSession session)
			throws JsonProcessingException {

		// Crear notificaion de Profesor para admin
		long userId = ((User) session.getAttribute("u")).getId();
		User u = entityManager.find(User.class, userId);

		User reported = entityManager.find(User.class, reportado);

		// Crear notificaion de Profesor para admin
		Notificacion m = new Notificacion();
		m = NotificationGenerator.reportUser(m, u, reported.getUsername(), cuerpo);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(m.toTransfer());
		messagingTemplate.convertAndSend("/topic/admin", json);

		return "redirect:/";
	}

	@PostMapping("/solicitarProfesor")
	@Transactional
	public String solicitarProfesor(@RequestParam String cuerpo, HttpSession session) throws JsonProcessingException {
		long userId = ((User) session.getAttribute("u")).getId();
		User u = entityManager.find(User.class, userId);

		// Crear notificaion de Profesor para admin
		Notificacion m = new Notificacion();
		m = NotificationGenerator.requestProfesor(m, u, cuerpo);
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(m.toTransfer());
		messagingTemplate.convertAndSend("/topic/admin", json);
		messagingTemplate.convertAndSend("/user/" + u.getUsername() + "/queue/updates", json);

		return "redirect:/";
	}

}