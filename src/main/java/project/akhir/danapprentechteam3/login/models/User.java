package project.akhir.danapprentechteam3.login.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.akhir.danapprentechteam3.transaksi.model.CustomerChoice;
import project.akhir.danapprentechteam3.transaksi.model.Transaksi;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Entity
@Table(	name = "users")

//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = User.class)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;
	@NotBlank
	@Size(max = 15)
	@Column
	private String noTelepon;

	@NotBlank
	@Size(min = 3, max = 20)
	@Column
	private String namaUser;

	@NotBlank
	@Size(max = 50)
	@Column
	private String email;

	@NotBlank
	@Column
	private String password;

	@NotBlank
	@Column
	private String virtualAccount ;

	@JsonIgnore
	@Transient
	private Set<Role> roles = new HashSet<>();

	@Transient
	private String message ;

	@Transient
	private String status ;

	private String tokenAkses;

	private Long pinTransaksi;

	private Long saldo = 1000000L ;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate = new Date();

	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	private Set<CustomerChoice> transaksis;

	public User() {
	}

}
