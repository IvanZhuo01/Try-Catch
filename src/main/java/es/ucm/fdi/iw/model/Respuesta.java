package es.ucm.fdi.iw.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity 
@Table(name="Respuesta")
@Data
@EqualsAndHashCode(callSuper=true) // es para decirle que use el equals y hashcode de la clase padre, asi quitamos un warning
@NoArgsConstructor
public class Respuesta extends Aportacion implements Transferable<Respuesta.Transfer>{

    @ManyToOne
    Pregunta pregunta;
  

    @Getter
    @AllArgsConstructor
    public static class Transfer {
	   private long pregunta_id;
       private String cuerpo;
    }

    @Override
    public Transfer toTransfer() {
		return new Transfer(pregunta.getId(), super.getCuerpo());
	}
    @Override
	public String toString() {
		return toTransfer().toString();
	}

    
}
