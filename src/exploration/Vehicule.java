/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

import java.util.ArrayList;

/**
 * Véhicule qui explore la {@link Carte} en se déplaçant et en découvrant les
 * {@link Case}
 *
 * @author riviere
 */
public class Vehicule {

    private final int id;
    private final Carte carte;
    private int dist_parcourue = 0;

    // Détermine l'ordre du voisinage
    private final int vision = 2;   

    /**
     *
     * @param carte la {@link Carte} sur laquelle est positionné le véhicule
     * @param id son identifiant
     */
    public Vehicule(Carte carte, int id) {
        this.id = id;
        this.carte = carte;
    }

    public int getID() {
        return id;
    }

    /**
     * Méthode à appeler pour faire avancer le véhicule vers une {@link Case}
     * voisine (à 1) dans une des {@link Direction} suivantes
     * (-1,-1),(-1,0),(-1,1),(0,-1),(0,1),(1,-1),(1,0),(1,1). Fait appel à la
     * méthode {@link Carte#avancer(int, int, int)}.
     *
     * @param direction la Direction choisie selon l'axe des x (-1,0,1) et des y
     * (-1,0,1)
     */
    public void avancer(Direction direction) {
        carte.avancer(id, direction.getX(), direction.getY());
        dist_parcourue++;
    }

    /**
     * Méthode à appeler pour déposer une {@link Pheromone} (classe à modifier
     * selon les besoins) là où se trouve le véhicule
     */
    public void dropPheromone() {
        Pheromone p = new Pheromone(id);
        carte.dropPheromone(p, id);
    }

    /**
     * Retourne la liste des {@link Case} dans le voisinage du véhicule. Le
     * voisinage du véhicule est défini par l'attribut {@link #vision}. Fait
     * appel à la méthode {@link Carte#getVoisinage(int, int)}.
     *
     * @return la liste des {@link Case} dans le voisinage du véhicule
     */
    public ArrayList<Case> getVoisinage() {
        return carte.getVoisinage(id, vision);
    }

    /**
     * Retourne la liste des {@link Case} dans le voisinage du véhicule qui sont
     * occupées. Le voisinage du véhicule est défini par l'attribut
     * {@link #vision}. Fait appel à la méthode
     * {@link Carte#getVoisinage(int, int)}.
     *
     * @return la liste des {@link Case} dans le voisinage du véhicule
     */
    public ArrayList<Case> getVoisins() {
        return carte.getVoisins(id, vision);
    }

    public int getDistanceParcourue() {
        return dist_parcourue;
    }

}
