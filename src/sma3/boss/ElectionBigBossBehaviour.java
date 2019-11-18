package sma3.boss;

import java.util.concurrent.atomic.AtomicInteger;

import jade.core.behaviours.OneShotBehaviour;
import sma3.ExplorationAgent;
import sma3.ReceptionValeurElectionBehaviour;

public class ElectionBigBossBehaviour extends OneShotBehaviour {

	private ExplorationAgent agent;
	
	public ElectionBigBossBehaviour(ExplorationAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		
		int nbAutresAgents = this.agent.getListeAgents().size() - 1;
		if (nbAutresAgents == 0)
			this.agent.addBehaviour(new InitialisationBossBehaviour(agent));
		Integer monLancerDe = this.agent.getVehicule().getID();
		
		this.agent.setIdDuBigBoss(monLancerDe);
		
		this.agent.addBehaviour(new EnvoiValeurElectionBehaviour(agent));
		AtomicInteger nombreReponses = new AtomicInteger(0);
		for (int i = 0; i < nbAutresAgents; i++)
			this.agent.addBehaviour(new ReceptionValeurElectionBehaviour(agent, nbAutresAgents, nombreReponses));
	}
	
}
