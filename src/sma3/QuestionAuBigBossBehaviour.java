package sma3;

import exploration.Direction;
import jade.core.behaviours.OneShotBehaviour;

public class QuestionAuBigBossBehaviour extends OneShotBehaviour {

	private ExplorationAgent agent;
	private Direction directionPrise;
	
	public QuestionAuBigBossBehaviour(ExplorationAgent agent, Direction directionPrise) {
		this.agent = agent;
		this.directionPrise = directionPrise;
	}
	
	@Override
	public void action() {
		if (directionPrise == null) {
			
		}
		else {
			
		}
	}

}
