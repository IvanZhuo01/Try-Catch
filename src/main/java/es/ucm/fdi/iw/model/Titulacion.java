package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import javax.persistence.*;
@Entity
@NoArgsConstructor
@Data
@Table(name="Titulacion")
@NamedQueries({
    @NamedQuery(name="allTitulaciones", query="SELECT t FROM Titulacion t WHERE NOT t.id=0"),
    @NamedQuery(name="Titulacion.titulo", query="SELECT t FROM Titulacion t WHERE t.universidad.id =:universidad AND t.Grado =:grado"),
    @NamedQuery(name="Titulacion.universidades", query="SELECT DISTINCT t.universidad FROM Titulacion t WHERE NOT t.id=0"),
    @NamedQuery(name="Titulacion.grados", query="SELECT t.Grado FROM Titulacion t WHERE t.universidad =:universidad "),
})

public class Titulacion implements Transferable<Titulacion.Transfer>{

    private String Grado;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    public Titulacion (Universidad universidad, String grado){
        this.universidad = universidad;
        this.Grado = grado;
    }
    
    @OneToMany()
    @JoinColumn(name="titulacion_id")
    private List<User> usuarios;

    @ManyToOne
    private Universidad universidad;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String uninombre;
        private long uniId;
        private String grado;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	universidad.getNombre(), universidad.getId(), Grado);
	}
	
	@Override
	public String toString() {
		return toTransfer().toString();
	}
    
}
