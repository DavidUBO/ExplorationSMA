package sma2;

import java.util.List;

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
			
			Direction directionRegardee = agent.getDirectionRegardee();
			Direction gauche = DirectionUtil.directionGauche(directionRegardee);
			List<Case> casesDeGauche = DirectionUtil.cases(casesEnvironnantes, gauche);
			
			if (casesEnvironnantes.stream().allMatch(x -> x.isDecouverte())) {
				Coordonnees coord = DirectionUtil.getXYfromDirection(directionRegardee);
				Case caseDevant = casesEnvironnantes.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
				if (caseDevant.isOccupee() || caseDevant.estObstacle())
					directionRegardee = Direction.getRandom();
				agent.setDirectionRegardee(directionRegardee);
				vehiculeAgent.avancer(directionRegardee);					
			}				
			else if (casesDeGauche.stream().anyMatch(x -> !x.isDecouverte())) {
				agent.setDirectionRegardee(gauche);
				vehiculeAgent.avancer(gauche);
			}
			else {				
					Direction versCase = gauche.next().next();
					
					int tests = 0;
					while (tests <= 3) {
						List<Case> casesParLa = DirectionUtil.cases(casesEnvironnantes, versCase);
						Coordonnees coord = DirectionUtil.getXYfromDirection(versCase);
						Case cheminInteressant = casesEnvironnantes.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
						
						if (casesParLa.stream().allMatch(x -> x.isDecouverte()) || cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
							versCase = versCase.next().next();
							tests++;
						}
						else {
							agent.setDirectionRegardee(versCase);
							vehiculeAgent.avancer(versCase);
							return;
						}						
					}
				Direction dir = Direction.getRandom();
				agent.setDirectionRegardee(dir);
				vehiculeAgent.avancer(dir);
			}
	}
}
