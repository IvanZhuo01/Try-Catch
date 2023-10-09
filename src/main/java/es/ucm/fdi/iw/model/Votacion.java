package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


import javax.persistence.*;

@Data
@Entity
public class Votacion implements Transferable<Votacion.Transfer>{
    @EmbeddedId
	private VotacionID id = new VotacionID();
    private boolean tipo_voto;

    @ManyToOne
	@MapsId("usuario_id") private User usuario;
	
	@ManyToOne
	@MapsId("aportacion_id") private Aportacion aportacion;

	@Getter
    @AllArgsConstructor
    public static class Transfer {
		private VotacionID id;
        private User usuario;
        private Aportacion aportacion;
		private boolean tipo_voto;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	usuario, aportacion, tipo_voto);
	}
	
	@Override
	public String toString() {
		return toTransfer().toString();
	}
}
