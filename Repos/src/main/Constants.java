/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import main.DbUtil;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author D
 */
public class Constants {
//public static final Object[] MENU_HEADER;

    public static Vector<String> ALLTABLES;
    public static HashMap<String, Vector<String>> SETTABKEY;
    /* set tabela sa stranim kljucevima*/
    public static HashMap<String, HashMap<String, String>> SETTABFKEY;
    public static HashMap<String, HashMap<String, String>> SETTYPESOFFIELDSTABELS;
    /* set tabela sa primarnim indeksima*/
    public static Vector<String> prkizatab;
    public static HashMap<String, Vector<String>> FIELDSFORTABLES;
    
    private static Constants instancaKonstanti;
   // public static Map<String, String> ImezaTabelu;
   // public static Map<String, String> TabelazaWebIme;
   // public static Map<String, String> WebImeZaIme;
    //public  static int SCRH;
    //public  static int SCRW;
    public static HashMap<String, HashMap<String, HashMap<String, String>>> TABFIELDTYPE;
    public static HashMap<String, HashMap<String, String>> FIELDTYPE;
public final static String APP_DIR;
public final static String IMG_DIR;
    private Constants() {

    }

    public static Constants getInstance() {
        return instancaKonstanti;
    }

    static {
         APP_DIR = "E:\\PROJEKTI SA PREDAVANJA\\WebGenPro\\web";
          IMG_DIR = "E:\\PROJEKTI SA PREDAVANJA\\WebGenPro\\web\\images";
        instancaKonstanti = new Constants();
     

       
        try {
            /*Uvozimo promenljive koje su const pri startuprograma i retko se menjaju a mnogo pozivajy
            Primer polja u tabelama i kljucevi
            SQL upiti
            getAllTables
             */

            ALLTABLES = new Vector<>();
            /*spisaktabela*/
            SETTYPESOFFIELDSTABELS = new HashMap<>();

            SETTABKEY = new HashMap<>();
            /* set tabela sa stranim kljucevima*/
            SETTABFKEY = new HashMap<>();
            /* set tabela sa primarnim indeksima*/
            Vector<String> prkizatab = new Vector<>();

            /*  set tabela sa imenima polja redosled iz tabele*/
            FIELDSFORTABLES = new HashMap<>();
 
            Vector<String> TABELE = new Vector<>();
            Vector<String> POLJA = new Vector<>();
            Vector<String> TIPOVI = new Vector<>();
            Vector<String> ColumnTIPOVI = new Vector<>();
            Vector<String> PRIMKLJUC = new Vector<>();
            
            //  String upit = "SELECT Table_name FROM INFORMATION_SCHEMA.TABLES where table_SCHEMA=\"roland_webshop\"";
            String upit = "SELECT TABLE_NAME,COLUMN_NAME,ORDINAL_POSITION,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,NUMERIC_PRECISION,COLUMN_TYPE,COLUMN_KEY,EXTRA FROM INFORMATION_SCHEMA.columns where table_SCHEMA='primer';";
           
            
            DbUtil instanca = DbUtil.getInstance();
            Connection baza = instanca.getConn();
            DatabaseMetaData metaData = baza.getMetaData();
            Statement st = baza.createStatement();
            ResultSet rs = st.executeQuery(upit);

        
            while (rs.next()) {

                TABELE.add(rs.getString("TABLE_NAME"));
                POLJA.add(rs.getString("COLUMN_NAME"));
                TIPOVI.add(rs.getString("DATA_TYPE"));
                ColumnTIPOVI.add(rs.getString("COLUMN_TYPE"));
                PRIMKLJUC.add(rs.getString("COLUMN_KEY"));

            }
         
            int du;
            String poctab = TABELE.get(0);
            TABFIELDTYPE = new HashMap<>();
            FIELDTYPE = new HashMap<>();

            for (du = 0; du < TABELE.size(); du++) {
                
                if (poctab.equals(TABELE.get(du))) {

                    HashMap<String, String> Karakteristike = new HashMap<>();
                    Karakteristike.put("DATA_TYPE", TIPOVI.get(du));
                    Karakteristike.put("COLUMN_TYPE", ColumnTIPOVI.get(du));
                    Karakteristike.put("COLUMN_KEY", PRIMKLJUC.get(du));
                    FIELDTYPE.put(POLJA.get(du), Karakteristike);

                } else {
                    TABFIELDTYPE.put(poctab, FIELDTYPE);
                    FIELDTYPE = new HashMap<>();

                    HashMap<String, String> Karakteristike = new HashMap<>();
                    Karakteristike.put("DATA_TYPE", TIPOVI.get(du));
                    Karakteristike.put("COLUMN_TYPE", ColumnTIPOVI.get(du));
                    Karakteristike.put("COLUMN_KEY", PRIMKLJUC.get(du));
                    FIELDTYPE.put(POLJA.get(du), Karakteristike);

                    poctab = TABELE.get(du);

                }

            }
            /*   posledni par u hashmapi ubaciti po kraju petlje*/

            TABFIELDTYPE.put(poctab, FIELDTYPE);

            /*   posledni par u hashmapi ubaciti po kraju petlje*/
            Vector<String> allFields;

            for (String key : TABFIELDTYPE.keySet()) {
                allFields = new Vector<>();
                for (String key1 : TABFIELDTYPE.get(key).keySet()) {
                    allFields.add(key1);
                   
                }

                FIELDSFORTABLES.put(key, allFields);
                ALLTABLES.add(key);
            }


            /*   proveravamo da li ima nove tabeleza ubaciti u glavnimeni tabelu  */
            /*   upit presekom trazi da li postoji vec tabela sa imenom.ako je nema dodaje je u glavnimeni i setuje parent na 0*/
            //  dohvata  primarne kljuceve za  tabele
            for (String tab : ALLTABLES) {
                //   Class<?> jj = Class.forName("model." + tab);
                //  Field[] polja = jj.getFields();

                ResultSet pkColumns = metaData.getPrimaryKeys(null, null, tab);
                Vector<String> pkColumnSet = new Vector<>();
                while (pkColumns.next()) {
                    String pkColumnName = pkColumns.getString("COLUMN_NAME");
                    Integer pkPosition = pkColumns.getInt("KEY_SEQ");
                       System.out.println("" + pkColumnName + " is the " + pkPosition + ". column of the primary key of the table " + tab);
                    pkColumnSet.add(pkColumnName);
                }

                SETTABKEY.put(tab, pkColumnSet);
            }

            for (String tab : ALLTABLES) {
                ResultSet foreignKeys = metaData.getImportedKeys(baza.getCatalog(), null, tab);
                HashMap<String, String> fkljucevi = new HashMap<>();
                fkljucevi.clear();
                while (foreignKeys.next()) {
                    String fkTableName = foreignKeys.getString("FKTABLE_NAME");
                    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                    fkljucevi.put(fkTableName + "." + fkColumnName, pkTableName + "." + pkColumnName);
                       System.out.println(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
                }
                SETTABFKEY.put(tab, fkljucevi);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Constants.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
