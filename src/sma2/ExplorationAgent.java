package sma2;

import java.util.List;

import exploration.Case;
import exploration.Direction;
import exploration.Vehicule;
import jade.core.Agent;

public class ExplorationAgent extends Agent {
	
	private Vehicule vehicule;
	private Direction directionRegardee;
	
	@Override
	public void setup() {
		vehicule = (Vehicule) getArguments()[0];
		
		do 
			directionRegardee = Direction.getRandom();
		while (directionRegardee == Direction.NE || directionRegardee == Direction.NO || directionRegardee == Direction.SE || directionRegardee == Direction.SO);
		
		addBehaviour(new ExplorationBehaviour(this));
	}
	
	public Vehicule getVehicule() {
		return vehicule;
	}
	
	public Direction getDirectionRegardee() {
		return directionRegardee;
	}
	
	public void setDirectionRegardee(Direction direction) {
		this.directionRegardee = direction;
	}
	
	public Case caseDevantMoi(List<Case> casesEnvironnantes) {
		switch (directionRegardee) {
			case Nord:
				return casesEnvironnantes.stream()
						.filter(maCase -> maCase.getX_relative() == 0 && maCase.getY_relative() == -1)
						.findFirst().get();
			case Sud:
				return casesEnvironnantes.stream()
						.filter(maCase -> maCase.getX_relative() == 0 && maCase.getY_relative() == 1)
						.findFirst().get();
			case Ouest:
				return casesEnvironnantes.stream()
						.filter(maCase -> maCase.getX_relative() == -1 && maCase.getY_relative() == 0)
						.findFirst().get();
			case Est:
				return casesEnvironnantes.stream()
						.filter(maCase -> maCase.getX_relative() == 1 && maCase.getY_relative() == 0)
						.findFirst().get();
			default:
				return null;
		}
	}
	
}
