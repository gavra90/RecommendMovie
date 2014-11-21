/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecommend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import film.Film;
import java.sql.CallableStatement;
import java.util.Collections;
import lsafunctions.LSA;
import java.util.HashMap;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 *
 * @author Gavra
 */
public class MovieRecommend {

    /**
     * @param args the command line arguments
     */
    public static HashMap dicry;
    public static RealMatrix M;
    public static int i = 0;
    public static int brojDimenzija = 25;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:sqlserver://localhost;databaseName=MovieDB;integratedSecurity=true";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection conn = DriverManager.getConnection(url);

        Statement stm = conn.createStatement();
        ResultSet rsRecnik = stm.executeQuery("SELECT Recnik FROM Recnik WHERE (ID_Zanra = 1)"); //citam recnik iz baze za odredjeni zanr
        String recnik[] = null;

        while (rsRecnik.next()) {
            recnik = rsRecnik.getString("Recnik").split(",");   //delim recnik na reci

        }

        ResultSet rsFilmovi = stm.executeQuery("SELECT TOP (200) Naziv_Filma, LemmaPlots, "
                + "ID_Filma FROM Film WHERE (ID_Zanra = 1)");
        List<Film> listaFilmova = new ArrayList<>();
        Film f = null;
        int rb = 0;
        while (rsFilmovi.next()) {
            f = new Film(rb, Integer.parseInt(rsFilmovi.getString("ID_Filma")), rsFilmovi.getString("Naziv_Filma"), rsFilmovi.getString("LemmaPlots"));
            listaFilmova.add(f);
            rb++;

        }
        //kreiranje vektorskog modela
        M = MatrixUtils.createRealMatrix(recnik.length, listaFilmova.size());
        System.out.println("Prva tezinska matrica");

        for (int i = 0; i < recnik.length; i++) {
            String recBaza = recnik[i];
            for (Film film : listaFilmova) {
                for (String lemmaRec : film.getPlotLema()) {
                    if (recBaza.equals(lemmaRec)) {
                        M.setEntry(i, film.getRb(), M.getEntry(i, film.getRb()) + 1);
                    }
                }
            }
        }
//racunanje tf-idf
        System.out.println("td-idf");
        M = LSA.calculateTfIdf(M);
        System.out.println("SVD");
//SVD
        SingularValueDecomposition svd = new SingularValueDecomposition(M);
        RealMatrix V = svd.getV();
        RealMatrix Vk = V.getSubMatrix(0, V.getRowDimension() - 1, 0, brojDimenzija - 1); //dimenzija je poslednji argument
//kosinusna slicnost
        System.out.println("Cosin simmilarity");
        CallableStatement stmTop = conn.prepareCall("{call Dodaj_TopList(?,?,?)}");
        
        for (int j = 0; j < listaFilmova.size(); j++) {
            Film fl = listaFilmova.get(j);
            List<Film> lFilmova1 = new ArrayList<>();
            lFilmova1.add(listaFilmova.get(j));
            double sim = 0.0;
            for (int k = 0; k < listaFilmova.size(); k++) {
                // System.out.println(listaFilmova.size());                
                sim = LSA.cosinSim(j, k, Vk.transpose());
                listaFilmova.get(k).setSimilarity(sim);
                lFilmova1.add(listaFilmova.get(k));
            }
            Collections.sort(lFilmova1);
            for (int k = 2; k < 13; k++) {
                stmTop.setString(1, fl.getID() + "");
                stmTop.setString(2, lFilmova1.get(k).getID() + "");
                stmTop.setString(3, lFilmova1.get(k).getSimilarity() + "");
                stmTop.execute();
            }

        }

        stm.close();
        rsRecnik.close();
        rsFilmovi.close();
        conn.close();

    }

}
