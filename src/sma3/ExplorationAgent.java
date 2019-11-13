package sma3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exploration.Direction;
import exploration.Vehicule;
import jade.core.AID;
import jade.core.Agent;
import sma.common.Coordonnees;
import sma3.boss.ElectionBigBossBehaviour;
import sma3.carte.CarteDynamique;

public class ExplorationAgent extends Agent {
	
	private Vehicule vehicule;
	private Direction directionRegardee;
	private Set<AID> tousLesAgents;
	
	private Map<Integer, AID> idsEtAgents;
	
	private int idDuBigBoss;
	
	private boolean JeSuisLeBigBoss = true;
	
	private Coordonnees placeAbsolue;
	public CarteDynamique carte;
	
	@Override
	public void setup() {
		vehicule = (Vehicule) getArguments()[0];
		
		do 
			directionRegardee = Direction.getRandom();
		while (directionRegardee == Direction.NE || directionRegardee == Direction.NO || directionRegardee == Direction.SE || directionRegardee == Direction.SO);
		
		tousLesAgents = new HashSet<AID>();
		tousLesAgents.add(this.getAID());
		
		idsEtAgents = new HashMap<Integer, AID>();
		idsEtAgents.put(vehicule.getID(), getAID());
		
		this.addBehaviour(new DecouverteAgentsBehaviour(this));
		this.addBehaviour(new ElectionBigBossBehaviour(this));
		
	}
	
	public Vehicule getVehicule() {
		return vehicule;
	}
	
	public Direction getDirectionRegardee() {
		return directionRegardee;
	}

	public void setDirectionRegardee(Direction directionRegardee) {
		this.directionRegardee = directionRegardee;
	}

	public Set<AID> getListeAgents() {
		return this.tousLesAgents;
	}
	
	public int getNombreAgentsSysteme() {
		return this.tousLesAgents.size();
	}

	public Map<Integer, AID> getIdsEtAgents() {
		return idsEtAgents;
	}

	public int getIdDuBigBoss() {
		return idDuBigBoss;
	}

	public void setIdDuBigBoss(int idDuBigBoss) {
		this.idDuBigBoss = idDuBigBoss;
		JeSuisLeBigBoss = idDuBigBoss == vehicule.getID();
	}
	
	public boolean isBigBoss() {
		return JeSuisLeBigBoss;
	}


	public Coordonnees getPlaceAbsolue() {
		return placeAbsolue;
	}

	public void setPlaceAbsolue(Coordonnees placeAbsolue) {
		this.placeAbsolue = placeAbsolue;
	}
	
	public int getIdAgentFromAID(AID aid) {
		return getIdsEtAgents().entrySet().stream().filter(x -> x.getValue().equals(aid)).findFirst().get().getKey();
	}
}
