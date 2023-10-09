package es.ucm.fdi.iw.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name="Aportacion")
public class Aportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @Column(length = 2000)
    private String cuerpo;

    private int numVotaciones;

    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    private User usuario;

    @OneToMany(mappedBy = "aportacion")
	private List<Votacion> votaciones;
    
}
