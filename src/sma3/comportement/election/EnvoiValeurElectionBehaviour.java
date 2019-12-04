package sma3.comportement.election;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import sma3.ExplorationAgent;

public class EnvoiValeurElectionBehaviour extends OneShotBehaviour {

	private ExplorationAgent agent;
	
	public EnvoiValeurElectionBehaviour(ExplorationAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		ACLMessage message = new ACLMessage(ACLMessage.CFP);
		for (AID aid : this.agent.getListeAgents()) {
			if (!aid.equals(this.agent.getAID())) {
				message.addReceiver(aid);
			}
		}
		message.setOntology("Élection");
		Integer monId = this.agent.getVehicule().getID();
		message.setContent(monId.toString());
		message.setSender(this.agent.getAID());
		this.agent.send(message);
	}
	
}
