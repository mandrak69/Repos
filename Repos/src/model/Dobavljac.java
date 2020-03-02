package model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the dobavljac database table.
 * 
 */
@Entity
@NamedQuery(name="Dobavljac.findAll", query="SELECT d FROM Dobavljac d")
public class Dobavljac implements Serializable {

	@Id
	private int id;

	private String naziv;

	//bi-directional many-to-one association to Mesto
	@ManyToOne
	@JoinColumn(name="mestoId")
	private Mesto mesto;

	public Dobavljac() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNaziv() {
		return this.naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public Mesto getMesto() {
		return this.mesto;
	}

	public void setMesto(Mesto mesto) {
		this.mesto = mesto;
	}

}