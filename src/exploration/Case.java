/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

import java.util.ArrayList;
import java.util.List;

/**
 * Composant de la {@link Carte}, a des coordonnées (x,y) dans une matrice.
 *
 * @author riviere
 */
public class Case {

    // Peut être occupée par un véhicule
    private boolean occupee = false;
    private int idVehicule;
    private List<Pheromone> pheromones;

    // Découverte ou encore à explorer
    protected boolean decouverte = false;

    // Le nombre de fois où la case a été occupée par un véhicule
    private int nbOccupee = 0;

    // Ses coordonnées (cachées aux véhicules) dans la matrice
    private final int x;
    private final int y;

    // Ses coordonnées relatives à la position du véhicule
    private int x_relative = 0;
    private int y_relative = 0;

    /**
     * Le constructeur prend en paramètres les coordonnées de la case dans la
     * matrice de la classe {@link Carte}
     *
     * @param i abscisse de la case dans la matrice
     * @param j ordonnée de la case dans la matrice
     */
    public Case(int i, int j) {
        idVehicule = -1;
        x = i;
        y = j;
        pheromones = new ArrayList<>();
    }

    public void addPheromone(Pheromone p) {
        pheromones.add(p);
    }

    public Case copyCase(int i, int j) {
        Case c = new Case(i, j);
        if (isOccupee()) {
            c.occuper(getVehicule());
        }
        if (isDecouverte()) {
            c.decouvrir();
        }
        c.setOccupee(nbOccupee);
        c.setPheromones(pheromones);
        return c;
    }

    protected void setPheromones(List<Pheromone> p) {
        pheromones = p;
    }

    protected void setOccupee(int nbOcc) {
        this.nbOccupee = nbOcc;
    }

    public synchronized boolean isOccupee() {
        return occupee;
    }

    public synchronized boolean isDecouverte() {
        return decouverte;
    }

    public synchronized void decouvrir() {
        decouverte = true;
    }

    public synchronized void occuper(int id) {
        occupee = true;
        idVehicule = id;
        nbOccupee++;
    }

    public synchronized void quitter() {
        idVehicule = -1;
        occupee = false;
    }

    public synchronized int getVehicule() {
        return idVehicule;
    }
    
    public boolean estObstacle(){
        return false;
    }

    public int getX_relative() {
        return x_relative;
    }

    public int getY_relative() {
        return y_relative;
    }

    public int getNbOccupee() {
        return nbOccupee;
    }

    public List<Pheromone> getPheromones() {
        return pheromones;
    }

    public boolean hasPheromones() {
        boolean full = getPheromones().size() > 0;
        return full;
    }

    public boolean hasPheromones(int idAgent) {
        List<Pheromone> all = getPheromones();
        for (Pheromone p : all) {
            if (p.getAgentID() != idAgent) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ") : " + decouverte + "-" + occupee + ":" + idVehicule;
    }

    public void set_relatives(int i, int j) {
        x_relative = i;
        y_relative = j;
    }

    public void updatePheromones() {
        List<Pheromone> ids = new ArrayList<>();
        for (int i = 0; i < pheromones.size(); i++) {
            Pheromone p = pheromones.get(i);
            if (p == null || p.evaporer()) {
                ids.add(p);
            }
        }

        for (int j = ids.size() - 1; j >= 0; j--) {
            pheromones.remove(ids.get(j));
        }
    }

}
