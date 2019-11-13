/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

/**
 * Un Obstacle est une {@link Case} particulière, qu'un {@link Vehicule} ne peut
 * pas occuper ni traverser
 *
 * @author riviere
 */
public class Obstacle extends Case {

    public Obstacle(int x, int y) {
        super(x, y);
    }
    
    @Override
     public Case copyCase(int i, int j) {
        Obstacle c = new Obstacle(i, j);
        if (isOccupee()) {
            c.occuper(getVehicule());
        }
        if (isDecouverte()) {
            c.decouvrir();
        }
        return c;
    }

    @Override
    public synchronized boolean isOccupee() {
        return false;
    }

    @Override
    public synchronized boolean isDecouverte() {
        return decouverte;
    }

    @Override
    public synchronized void decouvrir() {
        decouverte = true;
    }
    
    @Override
    public boolean estObstacle(){
        return isDecouverte();
    }

    @Override
    public synchronized void occuper(int id) {
    }

    @Override
    public synchronized void quitter() {
    }

    @Override
    public synchronized int getVehicule() {
        return -1;
    }

    @Override
    public String toString() {
        return "Obstacle " + super.toString();
    }

}