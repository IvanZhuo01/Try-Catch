package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import javax.persistence.*;


import lombok.Getter;

@Entity 
@Table(name="Pregunta")
@Data
@NamedQueries({
    @NamedQuery(name="allPreguntas", query="SELECT p FROM Pregunta p"),
    @NamedQuery(name="buscarPreguntaPorTitulo", query="SELECT p FROM Pregunta p WHERE UPPER(p.titulo) LIKE CONCAT('%',UPPER(:query),'%')"),
    @NamedQuery(name="preguntasRecientes", query="SELECT a FROM Pregunta a order by a.fecha desc"),
    @NamedQuery(name="preguntasConFicheroQuery", query="SELECT a FROM Pregunta a WHERE UPPER(a.titulo) LIKE CONCAT('%',UPPER(:query),'%') and fichero is not null order by a.fecha desc"),
    @NamedQuery(name="fichero", query="  SELECT COUNT(p) FROM Pregunta p WHERE fichero is not null")
  
})
public class Pregunta extends Aportacion implements Transferable<Pregunta.Transfer>{

    private String titulo;
    private String etiquetas;
    private String fichero;
    private boolean embebImg;

    @OneToMany
    @JoinColumn(name = "pregunta_id")
    private List<Respuesta> respuestas =new ArrayList<>();

    @ManyToOne
    private User usuario;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private String titulo;
        private String cuerpo;
        private String etiquetas;
        private int totalRes;
        private boolean embebImg;
        private String fichero;
    }
    
    @Override
    public Transfer toTransfer() {
		return new Transfer(titulo, super.getCuerpo(), etiquetas,respuestas.size(), embebImg, fichero);
	}
    @Override
	public String toString() {
		return toTransfer().toString();
	}

    public List<String> getEtiquetas(){

        String[] etiquetasArray = this.etiquetas.split(",");

        List<String> etiquetasList = Arrays.asList(etiquetasArray);

        return etiquetasList;
    }

    
}
