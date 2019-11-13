package sma3;

import java.util.List;
import java.util.StringJoiner;

import exploration.Case;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import sma.common.Coordonnees;

public class InitialisationBehaviour extends Behaviour {

	private ExplorationAgent agent;
	private int etape = 0;
	private boolean messageRecuEtTraite = false;
	
	public InitialisationBehaviour(ExplorationAgent agent) {
		this.agent = agent;
	}
	
	@Override
	public void action() {
		if (etape == 0) {		
			List<Case> environnement = this.agent.getVehicule().getVoisinage();
			StringJoiner infoAEnvoyer = new StringJoiner(";");
			
			for(Case c : environnement)
				infoAEnvoyer.add(c.toString());
			
			ACLMessage message = new ACLMessage(ACLMessage.CFP);
			message.addReceiver(this.agent.getIdsEtAgents().get(this.agent.getIdDuBigBoss()));
			message.setOntology("Info init");
			message.setContent(infoAEnvoyer.toString());
			message.setSender(this.agent.getAID());
			this.agent.send(message);
			etape++;
		}
		else {
			ACLMessage msg = agent.receive();
			if(msg != null) {
				String[] coordsString = msg.getContent().split(",");
				this.agent.setPlaceAbsolue(new Coordonnees(Integer.parseInt(coordsString[0]), Integer.parseInt(coordsString[1])));
				this.agent.addBehaviour(new ExplorationBehaviour(agent));
			}				
			else
				block();
		}
		
		
		
//		MessageTemplate mt = MessageTemplate.MatchOntology("Info place");
//		ACLMessage msg = this.agent.receive(mt);
//		if (msg != null) {
//			String[] valeursRecues = msg.getContent().split(",");
//			int monX = Integer.parseInt(valeursRecues[0]);
//			int monY = Integer.parseInt(valeursRecues[1]);
//			this.agent.setxAbsolu(monX);
//			this.agent.setyAbsolu(monY);
//			
//			StringBuffer s = new StringBuffer();
//			List<Case> voisins = this.agent.getVehicule().getVoisins();
//			for (Case voisin : voisins) {
//				int idVoisin = voisin.getVehicule();
//				int xVoisin = voisin.getX_relative();
//				int yVoisin = voisin.getY_relative();				
//				s.append(idVoisin + "," + (monX + xVoisin) + "," + (monY + yVoisin) + ";");
//			}
//			
//			String reponse = s.toString();
//			ACLMessage message = new ACLMessage(ACLMessage.CFP);
//			message.addReceiver(this.agent.getIdsEtAgents().get(this.agent.getIdDuBigBoss()));
//			message.setOntology("Info place");
//			message.setContent(reponse);
//			message.setSender(this.agent.getAID());
//			this.agent.send(message);
//			
//			messageRecuEtTraite = true;
//			
//			this.agent.addBehaviour(new ExplorationBehaviour(this.agent));
//		}
//		else {
//			block();
//		}
	}

	@Override
	public boolean done() {
		return messageRecuEtTraite;
	}

}
