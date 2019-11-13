/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

/**
 * Classe représentant une trace chimique laissée par les robots, contenant leur
 * ID, et qu'ils peuvent également détecter. A modifier selon les besoins.
 *
 * @author Jérémy Rivière
 */
public class Pheromone {

    private Double valeur = 75.;
    private final Double tauxEvaporation = 5.;
    private final int agentID;

    public Pheromone(int agentID) {
        this.agentID = agentID;
    }

    public boolean evaporer() {
        valeur = valeur - tauxEvaporation;
        return valeur.compareTo(0.) <= 0;
    }

    public Double getValeur() {
        return valeur;
    }

    public int getAgentID() {
        return agentID;
    }

}
