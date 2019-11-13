package sma3.boss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import exploration.Case;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.common.Coordonnees;
import sma3.ExplorationAgent;
import sma3.carte.CarteDynamique;
import sma3.serialisation.CaseLightModel;
import sma3.serialisation.EnvironmentInformation;

public class InitialisationBossBehaviour extends Behaviour {

	private ExplorationAgent agent;
	private int messagesRecus = 0;
	private boolean fini = false;
	private Set<Integer> agentsPretsATraiter;
	private Set<Integer> agentsTraites;
	private Map<Integer, EnvironmentInformation> infosRecues;
	
	public InitialisationBossBehaviour(ExplorationAgent agent) {
		this.agent = agent;
		agentsPretsATraiter = new HashSet<>();
		agentsTraites = new HashSet<>();
		infosRecues = new HashMap<>();
	}
	
	@Override
	public void action() {
		//Initialisation avec l'environnement du boss
		if (agentsTraites.size() == 0) {
			this.agent.carte = new CarteDynamique(this.agent.getVehicule().getID());
			this.agent.setPlaceAbsolue(new Coordonnees(0, 0));
			List<Case> casesEnviron = this.agent.getVehicule().getVoisinage();
			Coordonnees maPlace = this.agent.carte.miseAJourCarte(new Coordonnees(0, 0), casesEnviron);
			this.agent.setPlaceAbsolue(maPlace);
			
			List<Case> voisins = this.agent.getVehicule().getVoisins();
			for(Case caseAvecVoisin : voisins) {
				agentsPretsATraiter.add(caseAvecVoisin.getVehicule());
				this.agent.carte.declarePosition(caseAvecVoisin.getVehicule(), new Coordonnees(caseAvecVoisin.getX_relative(), caseAvecVoisin.getY_relative()).translation(maPlace));
			}
			
			agentsTraites.add(this.agent.getVehicule().getID());
		}
		else {
			//Réception des infos reçues par les autres agents
			if (messagesRecus < this.agent.getNombreAgentsSysteme() - 1) {
				MessageTemplate mt = MessageTemplate.MatchOntology("Info init");
				ACLMessage msg = this.agent.receive(mt);
				if (msg != null) {
					EnvironmentInformation cases = new EnvironmentInformation(msg.getContent());
					
					//On récupère l'id (int) du véhicule qui sert d'identifiant à partir de l'AID de l'agent
					int idAgent = this.agent.getIdsEtAgents().entrySet().stream().filter(x -> x.getValue().equals(msg.getSender())).findFirst().get().getKey();
					infosRecues.put(idAgent, cases);
					messagesRecus++;
				}
				else
					block();
			}
			//Traitement de toutes les infos
			else {
				traiteTousLesAgents();
				this.agent.setPlaceAbsolue(agent.carte.getPlacementAgents().get(agent.getIdDuBigBoss()));
				System.out.println(this.agent.carte.toString());
				repondreAuxAgents();
				agent.addBehaviour(new BossExplorationBehaviour(agent));
				fini = true;
			}
		}
	}

	@Override
	public boolean done() {
		return fini;
	}
	
	private void traiteTousLesAgents() {
		while (!agentsPretsATraiter.isEmpty()) {
			Iterator<Integer> it = agentsPretsATraiter.iterator();
			int idAgentATraiter = it.next();
			it.remove();
			
			EnvironmentInformation cases = infosRecues.get(idAgentATraiter);
			Coordonnees placeActuelle = this.agent.carte.getPlacementAgents().get(idAgentATraiter);
			Coordonnees nvellePlace = this.agent.carte.miseAJourCarte2(placeActuelle, cases.casesAlentour);
			//Coordonnees vecteurDecalage = Coordonnees.getVecteur(placeActuelle, nvellePlace);
			for (CaseLightModel c : cases.casesAlentour) {
				if (!c.occupee)
					continue;
				int idVehicule = c.vehicule;
				if (!agentsTraites.contains(idVehicule)) {
					this.agent.carte.declarePosition(idVehicule, nvellePlace);
					agentsPretsATraiter.add(idVehicule);
				}
			}
			
			agentsTraites.add(idAgentATraiter);
			agentsPretsATraiter.remove(idAgentATraiter);
		}
	}
	
	private void repondreAuxAgents() {
		int monId = this.agent.getIdDuBigBoss();
		for (Entry<Integer, AID> paire : this.agent.getIdsEtAgents().entrySet()) {
			if (paire.getKey() != monId) {
				Coordonnees c = this.agent.carte.getPlacementAgents().get(paire.getKey());
				ACLMessage message = new ACLMessage(ACLMessage.CFP);
				message.addReceiver(this.agent.getIdsEtAgents().get(paire.getKey()));
				message.setContent(c.X + "," + c.Y);
				message.setSender(this.agent.getAID());
				this.agent.send(message);
			}
		}
	}

}
