package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * A message that users can send each other.
 *
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "Notificacion.countUnread", query = "SELECT COUNT(m) FROM Notificacion m "
				+ "WHERE m.recipient.id = :userId AND m.read = false"),

		@NamedQuery(name = "Notificacion.getNonRead", query = "SELECT m FROM Notificacion m "
				+ "WHERE m.recipient.id = :userId AND m.read = false"),

		@NamedQuery(name = "Notificacion.getNotificionesAdmin", query = "SELECT m FROM Notificacion m "
				+ "WHERE m.recipient.id IS NULL")
})
@Data
public class Notificacion implements Transferable<Notificacion.Transfer> {

	private static Logger log = LogManager.getLogger(Notificacion.class);

	public enum Tipo {
		INFO, // informacion
		GESTION, // admin users
		AVISO
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
	@SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

	@ManyToOne
	private User recipient; // destinatario del mensaje

	@ManyToOne
	private User sender; // emisor del mensaje (caso admin)

	private String titulo;
	private String cuerpo;

	private LocalDateTime dateSent;
	private LocalDateTime dateRead;

	private Tipo tipo;
	private String topic; // para asuntos de gestion
	private boolean read = false;

	/**
	 * Objeto para persistir a/de JSON
	 * 
	 * @author mfreire
	 */
	@Getter
	@AllArgsConstructor
	public static class Transfer {

		private String topic;
		private Tipo tipo;
		private String to;
		private String from;
		private long receiverId;
		private String sent;
		private String received;
		private String titulo;
		private String cuerpo;
		private boolean read;
		long id;

		public Transfer(Notificacion m) {
			this.topic = m.topic;
			this.tipo = m.tipo;
			this.to = m.getRecipient().getUsername();
			this.from = m.getSender().getUsername();
			this.receiverId = m.getRecipient().getId();
			this.sent = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateSent());
			this.received = m.getDateRead() == null ? null
					: DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateRead());
			this.titulo = m.getTitulo();
			this.cuerpo = cuerpo == null ? null : cuerpo;
			this.read = m.read;
			this.id = m.getId();
		}
	}

	@Override
	public Transfer toTransfer() {
		return new Transfer(topic == null ? "" : topic, tipo,
				recipient == null ? "sin receptor" : recipient.getUsername(),
				sender == null ? "sin emisor" : sender.getUsername(),
				recipient == null ? -1 : recipient.getId(),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateSent),
				dateRead == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateRead),
				titulo, cuerpo, read, id);
	}

}
