package main;

import java.util.Optional;

import model.Mesto;

public class Main {

	public static void main(String[] args) {

		EntityRepository entityMan = new EntityRepository();

		try {
			// dohvatam sve metapodatke o svim tabelama u bazi
			Class.forName("main.Constants");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Mesto obj = new Mesto();
		obj.setNaziv("45444");
		obj.setPttbroj("2323");
		obj.setId(9);
		System.out.println(entityMan.isClassEntity(entityMan));
		try {
			int e = entityMan.Snimaj(obj);
			System.out.println("umetnuo na id= " + e);

			// moguce je ucitati objekat po bilo kom setovanom atributu ili kombinaciji
			// atributa

			Optional<Mesto> poId = entityMan.Ucitaj(new Mesto(9));
			if (poId.isPresent()) {
				System.out.println(poId.get().toString());
			}

			Mesto poNazivu = new Mesto();
			poNazivu.setNaziv("London");
			Optional<Mesto> poNazivuO = entityMan.Ucitaj(poNazivu);
			
			if (poNazivuO.isPresent()) {
				System.out.println(poNazivuO.get().toString());
			}
			
			Mesto poPTTbroju = new Mesto();
			poPTTbroju.setPttbroj("2323");
			Optional<Mesto> poPTTbrojuO = entityMan.Ucitaj(poPTTbroju);
			
			if (poPTTbrojuO.isPresent()) {
				System.out.println(poPTTbrojuO.get().toString());
			}
			
			
		} catch (RuntimeException | ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
