package sma3.boss;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.common.Coordonnees;
import sma3.ExplorationAgent;

public class SuiteInitialisationBossBehaviour extends CyclicBehaviour {

	private ExplorationAgent agent;
	
	public SuiteInitialisationBossBehaviour(ExplorationAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		
//		if (this.agent.getPlacementAgents().size() == this.agent.getIdsEtAgents().size() - 1) {
//			this.agent.addBehaviour(new BossExplorationBehaviour(agent));
//		}
//			
//		
//		MessageTemplate mt = MessageTemplate.MatchOntology("Info place");
//		ACLMessage msg = this.agent.receive(mt);
//		if (msg != null) {
//			String[] valeurRecue = msg.getContent().split(";");
//			for (int i = 0; i < valeurRecue.length - 1; i++) {
//				String [] infos = valeurRecue[i].split(",");
//				int idAgent = Integer.parseInt(infos[0]);
//				int xAgent = Integer.parseInt(infos[1]);
//				int yAgent = Integer.parseInt(infos[2]);
//				this.agent.getPlacementAgents().put(idAgent, new Coordonnees(xAgent, yAgent));
//				
//				ACLMessage message = new ACLMessage(ACLMessage.CFP);
//				message.addReceiver(this.agent.getIdsEtAgents().get(idAgent));
//				message.setOntology("Info place");
//				message.setContent(xAgent + "," + yAgent);
//				message.setSender(this.agent.getAID());
//				this.agent.send(message);
//			}
//			
//		}
//		else {
//			block();
//		}
	}

}
