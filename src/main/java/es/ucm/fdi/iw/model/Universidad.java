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
@Table(name="Universidad")
@NamedQueries({
    @NamedQuery(name="allUniversidades", query="SELECT u FROM Universidad u"),
    @NamedQuery(name="buscarUniversidadesPorTitulo", query="SELECT u FROM Universidad u WHERE UPPER(u.nombre) LIKE CONCAT('%',UPPER(:query),'%')"),
})

public class Universidad implements Transferable<Universidad.Transfer>{
    private String nombre;
    private String localizacion;
    private String descripcion;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @OneToMany()
    @JoinColumn(name="universidad_id")
    private List<Titulacion> titulaciones;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String nombre;
        private String localizacion;
        private String descripcion;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	nombre, localizacion, descripcion);
	}
	
	@Override
	public String toString() {
		return toTransfer().toString();
	}
    

    
}
