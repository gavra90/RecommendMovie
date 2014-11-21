/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package film;

/**
 *
 * @author Gavra
 */
public class Film implements Comparable<Film> {

    private int rb;
    private int ID;
    private String naziv;
    private String[] plotLema;

    private double similarity;

    public Film(int rb, int ID, String naziv, String s) {
        this.rb = rb;
        this.ID = ID;
        this.naziv = naziv;
        this.plotLema = s.split(",");
    }

    public int getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String[] getPlotLema() {
        return plotLema;
    }

    public void setPlotLema(String[] plotLema) {
        this.plotLema = plotLema;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public int compareTo(Film o) {
        if (this.similarity > o.similarity) {
            return -1;
        } else if (this.similarity == o.similarity) {
            return 0;
        } else {
            return 1;
        }
    }
}
