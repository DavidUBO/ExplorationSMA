package sma3.comportement.election;

import java.util.concurrent.atomic.AtomicInteger;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma3.ExplorationAgent;
import sma3.comportement.InitialisationBehaviour;
import sma3.comportement.InitialisationBossBehaviour;

public class ReceptionValeurElectionBehaviour extends Behaviour {

	private ExplorationAgent agent;
	private boolean messageRecuEtTraite = false;
	private int totalReponses;
	private AtomicInteger actualReponses;
	
	public ReceptionValeurElectionBehaviour(ExplorationAgent agent, int totalReponses, AtomicInteger actualReponses) {
		this.agent = agent;
		this.totalReponses = totalReponses;
		this.actualReponses = actualReponses;
	}
	
	@Override
	public void action() {
		
		int idMaxActuel = this.agent.getIdDuBigBoss();
			
		MessageTemplate mt = MessageTemplate.MatchOntology("Élection");
		ACLMessage msg = this.agent.receive(mt);
		if (msg != null) {
			int valeurRecue = Integer.parseInt(msg.getContent());
			this.agent.getIdsEtAgents().put(valeurRecue, msg.getSender());
			if (valeurRecue > idMaxActuel) {
				this.agent.setIdDuBigBoss(valeurRecue);
			}
			actualReponses.set(actualReponses.get() + 1);
			messageRecuEtTraite = true;
			
			if (actualReponses.get() == totalReponses) {
				if (this.agent.isBigBoss()) {
					this.agent.addBehaviour(new InitialisationBossBehaviour(agent));
				}
				else {
					this.agent.addBehaviour(new InitialisationBehaviour(agent));
				}
			}
		}
		else {
			block();
		}
	}

	@Override
	public boolean done() {
		return messageRecuEtTraite;
	}

}
