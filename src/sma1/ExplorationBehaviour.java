package sma1;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import exploration.Case;
import exploration.Direction;
import exploration.Vehicule;
import jade.core.behaviours.TickerBehaviour;
import sma.common.Coordonnees;
import sma.common.DirectionUtil;

public class ExplorationBehaviour extends TickerBehaviour {

	private ExplorationAgent agent;
	private Vehicule vehiculeAgent;
	
	public ExplorationBehaviour(ExplorationAgent agent) {
		super(agent, 1);
		this.agent = agent;
		this.vehiculeAgent = this.agent.getVehicule();
	}

	@Override
	protected void onTick() {
			
			List<Case> casesEnvironnantes = vehiculeAgent.getVoisinage();
			
			List<Case> casesInconnues = casesEnvironnantes.stream().filter(x -> !x.isDecouverte()).collect(Collectors.toList());
			
			Set<Direction> directionsTestees = new HashSet<>();
			
			if (!casesInconnues.isEmpty()) {
				Case uneCaseInconnue = casesInconnues.get(new Random().nextInt(casesInconnues.size()));
				Direction versCase = Direction.getDirection(uneCaseInconnue.getX_relative(), uneCaseInconnue.getY_relative());
				
				int tests = 0;
				while (tests <= 8) {
					Coordonnees coord = DirectionUtil.getXYfromDirection(versCase);
					Case cheminInteressant = casesEnvironnantes.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
					
					if (cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
						directionsTestees.add(versCase);
						do {
							versCase = Direction.getRandom();
						} while (directionsTestees.contains(versCase));
						tests++;
					}
					else {
						vehiculeAgent.avancer(versCase);
						return;
					}						
				}
			}
			vehiculeAgent.avancer(Direction.getRandom());
	}
	
}
