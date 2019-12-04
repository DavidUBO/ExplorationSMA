package sma3.comportement.election;

import java.util.Set;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sma3.ExplorationAgent;

public class DecouverteAgentsBehaviour extends OneShotBehaviour {

	private ExplorationAgent agent;
	
	public DecouverteAgentsBehaviour(ExplorationAgent agent) {
		this.agent = agent;
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.agent.getAID());
		ServiceDescription service = new ServiceDescription();
		service.setType("exploration");
		service.setName("agent");
		dfd.addServices(service);
		
		try {
			DFService.register(this.agent, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		Set<AID> listeAgents = this.agent.getListeAgents();
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription service = new ServiceDescription();
		service.setType("exploration");
		service.setName("agent");
		dfd.addServices(service);
		
		try {
			DFAgentDescription[] agents = DFService.search(this.agent, dfd);
			for (DFAgentDescription agent : agents) {
				listeAgents.add(agent.getName());
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
