package sma3.boss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exploration.Case;
import exploration.Direction;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.common.Coordonnees;
import sma3.ExplorationAgent;
import sma3.serialisation.EnvironmentInformation;

public class BossExplorationBehaviour extends Behaviour {

	private ExplorationAgent agent;
	private ETAPE_EXPLORATION stade;
	
	private List<Case> mesCasesAlentour;
	
	private int nbMessagesInfoEnvironnementRecus;
	private int nbMessagesDirectionRecus;
	private int nbMessagesDirectionAttendus;
	
	private Map<Integer, Direction> directionsEmpruntees;
	
	public BossExplorationBehaviour(ExplorationAgent agent) {
		this.agent = agent;
		stade = ETAPE_EXPLORATION.MON_EXPLORATION;		
	}
	
	@Override
	public void action() {
		//Traitement de ce que voit le boss
		if (stade == ETAPE_EXPLORATION.MON_EXPLORATION) {
			System.out.println("Je suis le boss en " + agent.getPlaceAbsolue().toString());
			try { Thread.sleep(500); } catch (InterruptedException e) { }
			
			//Initialisaton de ce tour d'itération
			nbMessagesInfoEnvironnementRecus = 0;
			nbMessagesDirectionRecus = 0;
			nbMessagesDirectionAttendus = 0;
			
			directionsEmpruntees = new HashMap<>();
			
			//Début du traitement
			mesCasesAlentour = this.agent.getVehicule().getVoisinage();
			Coordonnees maPlace = this.agent.carte.miseAJourCarte(agent.getPlaceAbsolue(), mesCasesAlentour);
			this.agent.setPlaceAbsolue(maPlace);
			
			stade = ETAPE_EXPLORATION.RECEPTION_INFOS;			
		}
		//Le boss reçoit toutes les infos et les traite
		else if (stade == ETAPE_EXPLORATION.RECEPTION_INFOS) {
			if (nbMessagesInfoEnvironnementRecus < this.agent.getNombreAgentsSysteme() - 1) {
				MessageTemplate mt = MessageTemplate.MatchOntology("Info cases");
				ACLMessage msg = this.agent.receive(mt);
				if (msg != null) {
					EnvironmentInformation cases = new EnvironmentInformation(msg.getContent());
					
					//On récupère l'id (int) du véhicule qui sert d'identifiant à partir de l'AID de l'agent
					int idAgent = this.agent.getIdAgentFromAID(msg.getSender());
					nbMessagesInfoEnvironnementRecus++;
					//On traite l'info
					Coordonnees placeActuelle = this.agent.carte.getPlacementAgents().get(idAgent);
					this.agent.carte.miseAJourCarte2(placeActuelle, cases.casesAlentour);
					
					if (cases.direction == null) {
						nbMessagesDirectionAttendus++;
					}
					else {
						directionsEmpruntees.put(idAgent, cases.direction);
					}
				}
				else
					block();
			}
			else
				stade = ETAPE_EXPLORATION.AIDE_AUTRES_AGENTS;
		}
		//Le boss donne des objectifs aux agents qui ne savent pas où aller
		else if (stade == ETAPE_EXPLORATION.AIDE_AUTRES_AGENTS) {
			if (nbMessagesDirectionRecus < nbMessagesDirectionAttendus) {
				
			}
			else
				stade = ETAPE_EXPLORATION.RECEPTION_DIRECTION;
		}
		//Le boss se déplace
		else if (stade == ETAPE_EXPLORATION.MA_DECISION) {
			
		}
		//Le boss applique (virtuellement) tous les déplacements et informe les agents de leur nouvelle place
		else if (stade == ETAPE_EXPLORATION.ENVOI_PLACEMENT_AUTRES) {
			
		}
		//Le boss reçoit les directions empruntées par les agents qui essaient d'atteindre leur objectif
		else {
			if (nbMessagesDirectionAttendus == 0) {
				
			}
		}
	}

	@Override
	public boolean done() {
		return false;
	}
	
	private enum ETAPE_EXPLORATION {
		MON_EXPLORATION,
		RECEPTION_INFOS,
		AIDE_AUTRES_AGENTS,
		MA_DECISION,
		ENVOI_PLACEMENT_AUTRES,
		RECEPTION_DIRECTION
	}
	
}
