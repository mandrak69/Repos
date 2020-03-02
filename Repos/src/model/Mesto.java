package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mesto database table.
 * 
 */
@Entity
@NamedQuery(name="Mesto.findAll", query="SELECT m FROM Mesto m")
public class Mesto implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String pttbroj;

	
	public Mesto() {
	}

	

	public Mesto(String naziv, String pttbroj) {
		super();
		this.name = naziv;
		this.pttbroj = pttbroj;
	}



	public Mesto(int i) {
		this.id=id;
	}



	public Mesto(String string) {
		this.name=name;
	}



	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNaziv() {
		return this.name;
	}

	public void setNaziv(String naziv) {
		this.name = naziv;
	}

	public String getPttbroj() {
		return this.pttbroj;
	}

	public void setPttbroj(String pttbroj) {
		this.pttbroj = pttbroj;
	}



	@Override
	public String toString() {
		return "Mesto [id=" + id + ", " + (name != null ? "name=" + name + ", " : "")
				+ (pttbroj != null ? "pttbroj=" + pttbroj : "") + "]";
	}

	


}