package es.ucm.fdi.iw.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class VotacionID implements Serializable{

    public VotacionID(long aportacion_id, long usuario_id){
        this.aportacion_id = aportacion_id;
        this.usuario_id = usuario_id;
    }

    public VotacionID() {
	}

    private long aportacion_id;
	private long usuario_id;
}
