package sma1;

import exploration.Vehicule;
import jade.core.Agent;

public class ExplorationAgent extends Agent {
	
	private Vehicule vehicule;
	
	@Override
	public void setup() {
		vehicule = (Vehicule) getArguments()[0];
		addBehaviour(new ExplorationBehaviour(this));
	}
	
	public Vehicule getVehicule() {
		return vehicule;
	}
	
}
