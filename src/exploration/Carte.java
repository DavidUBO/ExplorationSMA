/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

import ihm.CarteListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

/**
 * Environnement dans lequel seront placés les {@link Vehicule}. La Carte
 * contient une matrice de {@link Case} de taille X*Y, au départ inconnues, que
 * les véhicules peuvent découvrir ({@link #decouvrir(int, int)}) dans leur
 * voisinage et occuper ({@link #occuper(int, int, int)}) en se déplaçant.
 *
 * @author riviere
 */
public class Carte extends Thread {

    private Case cases[][];
    //une hashMap contenant la liste des véhicules classée par identifiants (e.g. pour 4 véhicules : 0 1 2 3)
    private final HashMap<Integer, Vehicule> vehicules;
    private final HashMap<Integer, Point2D> positions;
    private final int X;
    private final int Y;
    private int cptDecouverte = 0;
    // Nombre d'obstacles
    private final int nbObstacles;

    // Liste des classes implémentant l'interface {@link CarteListener} à l'écoute de la Carte
    private final EventListenerList listeners;
    private boolean end = false;

    /**
     * Initialise la liste des véhicules et construit la matrice de {@link Case}
     *
     * @param x la largeur réelle de la Carte
     * @param y la hauteur réelle de la Carte
     * @param nb_obstacles
     */
    public Carte(int x, int y, int nb_obstacles) {
        this.X = x+1;
        this.Y = y+1;
        vehicules = new HashMap<>();
        positions = new HashMap<>();
        initCases();
        listeners = new EventListenerList();
        nbObstacles = nb_obstacles;
    }

    /**
     * Construction de la matrice de {@link Case}
     */
    private void initCases() {
        cases = new Case[X][Y];
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                if((i==0)||(j==0)||(i==X-1)||(j==Y-1)){
                    cases[i][j] = new Obstacle(i, j);
                }
                else cases[i][j] = new Case(i, j);
            }
        }
    }

    /**
     * Méthode qui place aléatoirement les {@link Obstacle} sur la Carte, en
     * prenant soin de ne pas en placer 2 au même endroit ou à l'endroit où on a
     * placé un véhicule (voir {@link #initVehicules(int)}).
     *
     * @param positionsInit les positions des véhicules à l'initialisation
     */
    private void initObstacles() {
        for (int i = 0; i < nbObstacles; i++) {
            Point2D p;
            int x;
            int y;
            do {
                x = Double.valueOf(Math.random() * X).intValue();
                y = Double.valueOf(Math.random() * Y).intValue();
                p = new Point2D.Double(x, y);
            } while ((cases[x][y] instanceof Obstacle) || (positions.containsValue(p)));
            cases[x][y] = new Obstacle(x, y);
        }
    }   

    /**
     * Initialise et place nbr {@link Vehicule} sur les {@link Case} au centre
     * de la Carte
     *
     * @param nbr le nombre de véhicules à créer
     */
    public void initVehicules(int nbr) {
        int xDep;
        int yDep;
        if ((X - 1) % 2 == 0) {
            xDep = X / 2;
        } else {
            xDep = (X - 1) / 2;
        }
        if ((Y - 1) % 2 == 0) {
            yDep = Y / 2;
        } else {
            yDep = (Y - 1) / 2;
        }

        int xv = xDep;
        int yv = yDep;

        for (int i = 0; i < nbr; i++) {
            Vehicule v = new Vehicule(this, i);
            placer(i, xv, yv);
            vehicules.put(i, v);

            if (i == (nbr - 1) / 2) {
                xv = xDep;
                yv = yv + 1;
            } else {
                xv = xv + 1;
            }
        }
        
        initObstacles();
        updateAll();
    }
    
     /**
     * Méthode appelée automatiquement dans le Thread, permettant la mise à jour
     * des {@link Pheromone}, si il y en a, dans chacune des {@link Case}
     */
    @Override
    public void run() {
        while (!end) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Carte.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < X; i++) {
                for (int j = 0; j < Y; j++) {
                    cases[i][j].updatePheromones();
                }
            }
            updateAll();
        }
    }

    /**
     * place le {@link Vehicule} v sur la {@link Case} aux coordonnées (xv,yv)
     *
     * @param v le Véhicule à placer
     * @param xv abscisse de la Case
     * @param yv ordonnée de la Case
     */
    private synchronized void placer(Integer idV, int xv, int yv) {
        positions.put(idV, new Point2D.Double(xv, yv));
        occuper(idV, xv, yv);

        ArrayList<Case> voisins = getCasesVoisines(xv, yv);
        for (Case c : voisins) {
            if (!c.isDecouverte()) {
                decouvrir(c);
            }
        }
    }

    /**
     * Action d'occuper une {@link Case} par un {@link Vehicule}. Découvre la
     * Case si elle n'a pas été découverte avant.
     *
     * @param id identifiant du véhicule
     * @param xc abscisse de la Case
     * @param yc ordonnée de la Case
     */
    private synchronized void occuper(int id, int xc, int yc) {
        Case c = cases[xc][yc];
        c.occuper(id);
        if (!c.isDecouverte()) {
            decouvrir(c);
        }
    }

    /**
     * Renvoit la liste des {@link Case} qui font partie du voisinage du point
     * (xc,yc), à ordre (1,2 ...) de distance. Par exemple, retourne toutes les
     * cases de coordonnées (xc +- ordre, yc +- ordre).
     *
     * @param idV l'idée du véhicule courant
     * @param ordre le rayon du voisinage
     * @return la liste des {@link Case} du voisinage de (xc,yc)
     */
    protected synchronized ArrayList<Case> getVoisinage(int idV, int ordre) {
        int xc = Double.valueOf(positions.get(idV).getX()).intValue();
        int yc = Double.valueOf(positions.get(idV).getY()).intValue();

        ArrayList<Case> voisins = new ArrayList<>();
        for (int i = -ordre; i <= ordre; i++) {
            for (int j = -ordre; j <= ordre; j++) {
                if (((i != 0) || (j != 0)) && dansLesLimites(xc + i, yc + j)) {
                    Case c = cases[xc + i][yc + j].copyCase(i, j);
                    c.set_relatives(i, j);
                    voisins.add(c);
                }
            }
        }
        return voisins;
    }

    /**
     * Renvoit la liste des {@link Case} qui font partie du voisinage du point
     * (xc,yc), à ordre (1,2 ...) de distance, et qui contiennent d'autres
     * agents.
     *
     * @param idV l'idée du véhicule courant
     * @param ordre le rayon du voisinage
     * @return la liste des {@link Case} du voisinage de (xc,yc) occupées par
     * d'autres agents
     */
    protected ArrayList<Case> getVoisins(int idV, int ordre) {
        ArrayList<Case> voisins = getVoisinage(idV, ordre);
        for (int i = voisins.size() - 1; i >= 0; i--) {
            if (!voisins.get(i).isOccupee()) {
                voisins.remove(i);
            }
        }
        return voisins;
    }

    private ArrayList<Case> getCasesVoisines(int xc, int yc) {
        int ordre = 1;
        ArrayList<Case> voisins = new ArrayList<>();
        for (int i = -ordre; i <= ordre; i++) {
            for (int j = -ordre; j <= ordre; j++) {
                if (((i != 0) || (j != 0)) && dansLesLimites(xc + i, yc + j)) {
                    voisins.add(cases[xc + i][yc + j]);
                }
            }
        }
        return voisins;
    }

    /**
     * Fonctionne de la même manière que la méthode {@link #getVoisinage(int, int, int)
     * }, mais retourne une matrice de {@link Case}, rendant ainsi l'accès aux
     * cases plus rapide.
     *
     * @param xc abscisse du centre
     * @param yc ordonnée du centre
     * @param ordre le rayon du voisinage
     * @return la matrice des {@link Case} du voisinage de (xc,yc)
     */
    private synchronized Case[][] getQuickVoisinage(int xc, int yc, int ordre) {
        Case[][] voisins = new Case[2 * ordre + 1][2 * ordre + 1];

        for (int i = -ordre; i <= ordre; i++) {
            for (int j = -ordre; j <= ordre; j++) {
                if (dansLesLimites(xc + i, yc + j)) {
                    voisins[i + 1][j + 1] = cases[xc + i][yc + j];
                }
            }
        }
        return voisins;
    }

    /**
     * Teste si le point de coordonnées (a,b) est dans les limites de la carte,
     * soit >0 et infèrieure à sa taille (X,Y)
     *
     * @param a
     * @param b
     * @return vrai si le point de coordonnées (a,b) est dans les limites de la
     * carte
     */
    private boolean dansLesLimites(int a, int b) {
        return ((a >= 0) && (b >= 0) && (a < X) && (b < Y));
    }

    /**
     * Passe l'attribut {@link Case#decouverte} à vrai
     *
     * @param xc la position en x de la {@link Case} dans la matrice
     * @param yc la position en y de la {@link Case} dans la matrice
     */
    private synchronized void decouvrir(Case c) {
        c.decouvrir();
        cptDecouverte++;
        if (cptDecouverte >= X * Y) {
            end = true;
            int cout = calculCout();
            for (final CarteListener listen : listeners.getListeners(CarteListener.class)) {
                listen.success(cout, calculEfficacite(cout));
            }
        }
    }

    /**
     * Méthode utilisée par un {@link Vehicule} pour avancer d'une {@link Case}
     * sur la Carte
     *
     * @param idVehicule identifiant du véhicule
     * @param xDirection direction selon l'axe des x (-1,0,1)
     * @param yDirection direction selon l'axe des y (-1,0,1)
     */
    public synchronized void avancer(int idVehicule, int xDirection, int yDirection) {
        Vehicule v = vehicules.get(idVehicule);
        int xv = Double.valueOf(positions.get(idVehicule).getX()).intValue();
        int yv = Double.valueOf(positions.get(idVehicule).getY()).intValue();

        if (v != null) {

            int newX = xv + xDirection;
            int newY = yv + yDirection;
            if (dansLesLimites(newX, newY)) {
                if ((!(cases[newX][newY] instanceof Obstacle)) && (!cases[newX][newY].isOccupee())) {
                    cases[xv][yv].quitter();
                    placer(idVehicule, newX, newY);
                    updateVoisinage(getQuickVoisinage(newX, newY, 1), newX - 1, newY - 1, 3);
                }
            }
        }
//        System.out.println(toString());
    }

    public void avancerIHM(int vehiculeControlledByUser, int xDirection, int yDirection, boolean pheromone) {
        Vehicule v = vehicules.get(vehiculeControlledByUser);
        if (v != null) {
            v.avancer(Direction.getDirection(xDirection, yDirection));
            if (pheromone) {
                v.dropPheromone();
            }
        }
    }

    /**
     * Renvoit le cout total de l'exploration, c'est-à-dire la distance totale
     * parcourue par les véhicules (en nombre de cases)
     *
     * @return
     */
    private int calculCout() {
        int dist = 0;
        for (int id : vehicules.keySet()) {
            dist = dist + vehicules.get(id).getDistanceParcourue();
        }
        return dist;
    }

    /**
     * Renvoit l'efficacité, c'est-à-dire la zone totale découverte (en nombre
     * de cases) / par le cout de l'exploration
     *
     *
     * @return l'efficacité en %
     */
    private Double calculEfficacite(int cout) {
        Double eff = (X * Y * 1.) / cout;
        return eff;
    }

    public HashMap<Integer, Vehicule> getVehicules() {
        return vehicules;
    }

    /**
     * Ajoute une classe implémentant l'interface {@link CarteListener} pour la
     * mettre à l'écoute des changements de la Carte
     *
     * @param l une classe implémentant l'interface {@link CarteListener}
     */
    public void addListener(CarteListener l) {
        listeners.add(CarteListener.class, l);
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    /**
     * Prévient les classes à l'écoute d'un changement dans l'ensemble des
     * {@link Case} de la carte
     */
    private void updateAll() {
        for (final CarteListener listen : listeners.getListeners(CarteListener.class)) {
            listen.update(cases, X, Y, 0, 0);
        }
    }

    /**
     * Prévient les classes à l'écoute d'un changement dans la matrice de
     * {@link Case} voisines, de dimension taille * taille
     *
     * @param voisines les cases dont l'état a changé
     * @param taille la dimension de la matrice
     */
    private void updateVoisinage(Case[][] voisines, int xstart, int ystart, int taille) {
        for (final CarteListener listen : listeners.getListeners(CarteListener.class)) {
            listen.update(voisines, taille, taille, xstart, ystart);
        }
    }

    /**
     * Méthode appelée par {@link Vehicule}, permettant de déposer une
     * {@link Pheromone} à la {@link Case} occupée par le véhicule.
     *
     * @param p - la Phéromine créée
     * @param idV - l'id du Véhicule
     */
    public void dropPheromone(Pheromone p, int idV) {
        Case c = cases[Double.valueOf(positions.get(idV).getX()).intValue()][Double.valueOf(positions.get(idV).getY()).intValue()];
        c.addPheromone(p);
    }
    
    @Override
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	for (int i = 0; i < cases[0].length; i++) {
    		for (int j = 0; j < cases.length; j++) {
    			Case c = cases[j][i];
    			buffer.append(c.estObstacle() ? "X" : c.getVehicule() != -1 ? c.getVehicule() : c.isDecouverte() ? " " : "-");
			}
			buffer.append('\n');
    	}
    	return buffer.toString();
    }
}
