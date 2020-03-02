/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;

/**
 *
 * @author Dragan Markovic
 */
public class EntityRepository {

	public EntityRepository() {

	}

	public  <T> int Obrisi(T obj) {

		int obrisan = -1;
		try {
			String tabela = obj.getClass().getSimpleName().toLowerCase();
			String sqlWhereByKey = this.getSqlForSelectByKey(obj, tabela);
			Connection baza = DbUtil.getInstance().getConn();
			String upit = "DELETE FROM " + tabela + " WHERE " + sqlWhereByKey + " ;";
			PreparedStatement st = baza.prepareStatement(upit);
			obrisan = st.executeUpdate();

		} catch (SQLException ex) {
			Logger.getLogger(EntityRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return obrisan;
	}

	/*
	 * za objekat sa parametrima koje posedujepretraziodgovarajucu tabelu koju nadje
	 * po objektu Ako nadje prvi takav primerak setuje ostala polja i vraca ga .
	 * Posle metode original ima setovana polja . Ako ne nadje vraca null/
	 * Originalni objekat je isti Metod moze da se poziva sa a=GUcitaj(Objekat) ili
	 * samo sa GUcitaj(Objekat)
	 */
	public  <T> Optional<T> Ucitaj(Object obj) throws RuntimeException, ReflectiveOperationException {

		// Vector<String> pki =
		// Constants.setTabelaKljuceva.get(obj.getClass().getSimpleName());
		int imanesto = 0;
		ArrayList<Field> polja = getFields(obj);
		String tabela = obj.getClass().getSimpleName().toLowerCase();
		String dodAnd = " ";
		String dodupit = "";
		String zarez = "";
		String fieldsForSelect = "";
		for (Field polje : polja) {

			boolean accessible = polje.isAccessible();

			fieldsForSelect = fieldsForSelect + zarez + polje.getName();
			zarez = ",";
			polje.setAccessible(true);

			if (polje.get(obj) == null) {

			} else {
				String yy = "1" + polje.get(obj);
				if (yy.equals("10")) {
					//polje je vrednosti  nula pa ga preskacem.Kako otkriti da li je nula ili difoltnanula??
				} else {

					dodupit = dodupit + (dodAnd + polje.getName() + "='" + polje.get(obj) + "'");
					dodAnd = " AND ";
				}

			}
			polje.setAccessible(accessible);
		}
		if (fieldsForSelect.isEmpty()) { //  nemoguce ??

			fieldsForSelect = "*";
		}
		String upit = "select " + fieldsForSelect + " from " + tabela;
		/* prazan where uslov */
		if (dodupit == "") {
			upit = upit + " ;";
		} else {
			upit = upit + " where " + dodupit + " ;";

		}

		Connection baza = DbUtil.getInstance().getConn();

		try (Statement st = baza.createStatement(); ResultSet rs = st.executeQuery(upit)) {

			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();

			Field declaredField;

			while (rs.next()) {
				imanesto = 1;

				for (int i = 1; i <= columns; i++) {

					String type = md.getColumnTypeName(i);
					declaredField = polja.get(i - 1);

					// declaredField = polja[i - 1];
					boolean accessible = declaredField.isAccessible();
					declaredField.setAccessible(true);

					switch (type) {
					case "FLOAT":
						declaredField.set(obj, rs.getFloat(i));

						break;
					case "COUNTER":
						declaredField.set(obj, rs.getInt(i));

						break;
					case "VARCHAR":
						declaredField.set(obj, rs.getString(i));

						break;
					case "INT":
						declaredField.set(obj, rs.getInt(i));

						break;
					case "INTEGER":
						declaredField.set(obj, rs.getInt(i));

						break;
					case "DATETIME":
						declaredField.set(obj, rs.getString(i));

						break;
					case "MEDIUMINT":
						declaredField.set(obj, rs.getInt(i));

						break;
					case "LONGBLOB":
						declaredField.set(obj, rs.getTime(i));

						break;
					default:
						declaredField.set(obj, rs.getString(i));

					}
					declaredField.setAccessible(accessible);

				}

			}

		} catch (SQLException ex) {
			Logger.getLogger(EntityRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		// System.out.println("sada je " + obj.toString());
		if (imanesto == 0) {
			return Optional.empty();
		} else {
			return  Optional.of((T)obj);

		}
	}

	/*
	 ** Gsnimaj po objektu pronalazi tabelu,metod za dohvatanje svihpolja klase
	 ** proverava ima li ID nekuvrednost. Ako nema generise upit INSERT i izvrsava.
	 ** Dobija nazad ID pod kojim je ubacen zapis. Setuje ID objekta na tu vrednost
	 ** Vraca id generisanod strane MySQL Ako ima definisan ID moramo proveriti da li
	 * ima zapisa satimID-UPDATE Ako nema onda je to i dalje INSERT sa timID.-
	 */

	public  int Snimaj(Object obj)
			throws NoSuchFieldException, SecurityException, RuntimeException, IllegalAccessException {
		int newId = -1;
		String tabela = obj.getClass().getSimpleName().toLowerCase();
		try {
			String upitdeo = getSqlForSelectByKey(obj, tabela);
			System.out.println(upitdeo);
			boolean imaga = false;

			if (upitdeo != null) {// ako nema kljuca upitdeo="no key ???"
				System.out.println("id zaovaj objekat" + upitdeo);
				/* proveri da li ima zapisa sa tim ID */

				String up = "SELECT 1 FROM " + tabela + " WHERE " + upitdeo + " ;";

				System.out.println(upitdeo + "---getSqlForSelect------" + up);

				Connection baza = DbUtil.getInstance().getConn();
				PreparedStatement ps = baza.prepareStatement(up);
				// String up1 = "SELECT 1 FROM " + tabela + " WHERE ID =" + upitdeo + " ;";
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					System.out.println("----" + rs.getString(1));

					imaga = true; /* znaci ima ga ID -radi se UPDATE tog zapisa */
					String sqlUpdate = getSqlForUpdate(obj, tabela, upitdeo);
					newId = doPrepStatemnet(sqlUpdate);

				} else {
					/*
					 * nema zapisa sa tim ID - obican INSERT u nastavku zato je imaga postavljen na
					 * false
					 */
				}
			}
			if (imaga == false) {
				String upit = sqlForInsert(obj, tabela);
				System.out.println("sqlForInsert " + upit);
				int z = doPrepStatemnet(upit);

				// String upitzatab = "INSERT INTO "+tabela+"(" + dodupit + ")" + " SELECT *
				// FROM (SELECT " + dodupit1 + ") AS tmp WHERE NOT EXISTS ( SELECT tabela FROM
				// "+tabela+" WHERE tabela = '" + tabela + "') LIMIT 1;";
			} else {
			}
		} catch (SQLException ex) {
			Logger.getLogger(EntityRepository.class.getName()).log(Level.SEVERE, null, ex);
		}
		return newId;
	}

	public  String sqlForInsert(Object obj, String tabela) {
		String zarez = " ";
		String dodupit = "";
		String dodupit1 = "";
		ArrayList<Field> polja = getFields(obj);
		for (Field polje : polja) {
			try {
				boolean accessible = polje.isAccessible();
				polje.setAccessible(true);
				if (polje.get(obj) == null) {
				} else {
					dodupit = dodupit + zarez + polje.getName();
					dodupit1 = dodupit1 + zarez + "'" + polje.get(obj) + "'";
					zarez = " , ";
				}
				polje.setAccessible(accessible);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				Logger.getLogger(EntityRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		String upit = "INSERT into " + tabela + "(" + dodupit + ")" + " VALUES ( " + dodupit1 + ");";
		System.out.println(dodupit + " / " + dodupit1 + " upitzaGINSERT " + upit);
		return upit;
	}

	public  String getSqlForUpdate(Object obj, String tabela, String upitdeo) {
		String zarez = " ";
		String dodupit = "";
		ArrayList<Field> polja = getFields(obj);
		for (Field polje : polja) {

			try {
				boolean accessible = polje.isAccessible();
				polje.setAccessible(true);
				if (polje.getName() == "id" || polje.get(obj) == null) {
					// kljuc preskacem - oni su u prvom delu sql izjave
					// null preskacem mada treba razmotriti opcijei probleme

				} else {
					dodupit = dodupit + zarez + polje.getName() + "='" + polje.get(obj) + "'";
					zarez = " , ";
				}

				polje.setAccessible(accessible);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				Logger.getLogger(EntityRepository.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		String upit = "UPDATE " + tabela + " SET " + dodupit + " WHERE " + upitdeo + ";";
		System.out.println(dodupit + "/" + "upitzaUpdate " + upit);

		// String upitzatab = "INSERT INTO "+tabela+"(" + dodupit + ")" + " SELECT *
		// FROM (SELECT " + dodupit1 + ") AS tmp WHERE NOT EXISTS ( SELECT tabela FROM
		// "+tabela+" WHERE tabela = '" + tabela + "') LIMIT 1;";

		return upit;
	}

	public  int doPrepStatemnet(String upit) {
		ResultSet rs;
		int noviid = -1;
		Connection baza = DbUtil.getInstance().getConn();
		PreparedStatement st;
		try {
			st = baza.prepareStatement(upit, PreparedStatement.RETURN_GENERATED_KEYS);
			int affectedRows = st.executeUpdate();
			if (affectedRows == 0) {
				System.out.println("Opet?  Nesto samzabrljao...");
			}
			rs = st.getGeneratedKeys();
			if (rs.next()) {
				noviid = rs.getInt(1);
				System.out.println("Izvrsen nalog za zapis pod ID brojem " + noviid + " ");
				System.out.println(noviid);
				rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return noviid;
	}

	public  String getSqlForSelectByKey(Object obj, String tabela) {
		Field uu;
		Object oo = null;
		/*
		 * za sve primarne kljuceve sastavi upit radi provere da li vec postoji u tabeli
		 * takav obj Ako postoji to je Update a ne Insert
		 */
		Vector<String> nizkluc = Constants.SETTABKEY.get(tabela);

		String upitdeo = "";
		String dodAnd = "";
		Iterator<String> ij = nizkluc.iterator();
		try {
			while (ij.hasNext()) {
				Object er = ij.next();
				uu = obj.getClass().getDeclaredField((String) er);
				uu.setAccessible(true);
				oo = uu.get(obj);
				// treba doraditida moze kljuc da bude i neki drugi tip osim int
				upitdeo = upitdeo + " " + er + " = " + (int) oo + dodAnd;
				dodAnd = " AND ";
			}
			return upitdeo;

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "no key";
	}

	public  <T> ArrayList<Field> getFields(T t) {
		ArrayList<Field> fields = new ArrayList<>();
		Class<?> clazz = t.getClass();
		while (clazz != Object.class) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}

		return fields;
	}
	public boolean isClassEntity(Object obj) {
	Class<?> c = obj.getClass();
			
	for (Annotation ann : c.getAnnotations()) {
	  if (ann instanceof Entity) {   // make sure it's your annotation
	    System.out.println("klasa "+obj.getClass().getSimpleName()+"  je entity");
	    return true;
	  }
	  }System.out.println("klasa "+obj.getClass().getSimpleName()+"  nije entity");
	return false;
	}
}
