package sma3.boss;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import exploration.Case;
import exploration.Direction;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.common.Coordonnees;
import sma.common.DirectionUtil;
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
	private Set<Integer> demandeursObjectif;
	
	private Direction directionPrise;
	
	public BossExplorationBehaviour(ExplorationAgent agent) {
		this.agent = agent;
		stade = ETAPE_EXPLORATION.MON_EXPLORATION;		
	}
	
	@Override
	public void action() {
		//Traitement de ce que voit le boss
		if (stade == ETAPE_EXPLORATION.MON_EXPLORATION) {
			System.out.println("Je suis le boss en " + agent.getPlaceAbsolue().toString());
			try { Thread.sleep(ExplorationAgent.TEMPS_PAUSE); } catch (InterruptedException e) { }
			
			//Initialisaton de ce tour d'itération
			nbMessagesInfoEnvironnementRecus = 0;
			nbMessagesDirectionRecus = 0;
			nbMessagesDirectionAttendus = 0;
			
			directionsEmpruntees = new HashMap<>();
			demandeursObjectif = new HashSet<>();
			
			//Début du traitement
			mesCasesAlentour = this.agent.getVehicule().getVoisinage();
			Coordonnees maPlace = this.agent.carte.miseAJourCarte(agent.getIdDuBigBoss(), mesCasesAlentour);
			this.agent.setPlaceAbsolue(maPlace);
			
			stade = ETAPE_EXPLORATION.RECEPTION_INFOS;			
		}
		//Le boss reçoit toutes les infos et les traite
		else if (stade == ETAPE_EXPLORATION.RECEPTION_INFOS) {
			if (nbMessagesInfoEnvironnementRecus < this.agent.getNombreAgentsSysteme() - 1) {
				ACLMessage msg = this.agent.receive();
				if (msg != null) {
					EnvironmentInformation cases = new EnvironmentInformation(msg.getContent());
					
					//On récupère l'id (int) du véhicule qui sert d'identifiant à partir de l'AID de l'agent
					int idAgent = this.agent.getIdAgentFromAID(msg.getSender());
					nbMessagesInfoEnvironnementRecus++;
					//On traite l'info
					this.agent.carte.miseAJourCarte2(idAgent, cases.casesAlentour);
					
					//Si l'agent a besoin d'un objectif
					if (cases.direction == null) {
						nbMessagesDirectionAttendus++;
						demandeursObjectif.add(idAgent);
					}
					//Sinon il indique où il est allé
					else {
						directionsEmpruntees.put(idAgent, cases.direction);
					}
				}
				else
					block();
			}
			else {
				System.out.println(agent.carte.toString());
				stade = ETAPE_EXPLORATION.AIDE_AUTRES_AGENTS;
			}
		}
		//Le boss donne des objectifs aux agents qui ne savent pas où aller
		else if (stade == ETAPE_EXPLORATION.AIDE_AUTRES_AGENTS) {
			if (!demandeursObjectif.isEmpty()) {
				for (int idAgent : demandeursObjectif) {
					Coordonnees placeAgent = this.agent.carte.getPlacementAgents().get(idAgent);
					Coordonnees objectifConseille = this.agent.carte.getClosestUndiscoveredCase(placeAgent);
					ACLMessage message = new ACLMessage(ACLMessage.CFP);
					message.addReceiver(this.agent.getIdsEtAgents().get(idAgent));
					message.setOntology("Info objectif");
					message.setContent(placeAgent.X + "," + placeAgent.Y + ";" + objectifConseille.X + "," + objectifConseille.Y);
					message.setSender(this.agent.getAID());
					this.agent.send(message);
				}
				stade = ETAPE_EXPLORATION.RECEPTION_DIRECTION;
			}
			else
				stade = ETAPE_EXPLORATION.MA_DECISION;
		}
		//Le boss se déplace
		else if (stade == ETAPE_EXPLORATION.MA_DECISION) {
			boolean decision = false;
			
			Direction directionRegardee = agent.getDirectionRegardee();
			Direction gauche = DirectionUtil.directionGauche(directionRegardee);
			List<Case> casesDeGauche = DirectionUtil.cases(mesCasesAlentour, gauche);
			
			if (mesCasesAlentour.stream().allMatch(x -> x.isDecouverte())) {
				findAndGoObjectif();
			}				
			else if (casesDeGauche.stream().anyMatch(x -> !x.isDecouverte())) {
				setDirection(gauche);
			}
			else {				
				Direction versCase = gauche.next().next();
				
				int tests = 0;
				while (tests <= 3) {
					List<Case> casesParLa = DirectionUtil.cases(mesCasesAlentour, versCase);
					Coordonnees coord = DirectionUtil.getXYfromDirection(versCase);
					Case cheminInteressant = mesCasesAlentour.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
					
					if (casesParLa.stream().allMatch(x -> x.isDecouverte()) || cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
						versCase = versCase.next().next();
						tests++;
					}
					else {
						setDirection(versCase);
						decision = true;
						break;
					}						
				}
				if (!decision) {
					findAndGoObjectif();
				}
			}
			stade = ETAPE_EXPLORATION.ENVOI_PLACEMENT_AUTRES;
		}
		//Le boss applique (virtuellement) tous les déplacements et informe les agents de leur nouvelle place
		else if (stade == ETAPE_EXPLORATION.ENVOI_PLACEMENT_AUTRES) {
			Coordonnees maPlace = agent.carte.moveVehicule(agent.getIdDuBigBoss(), directionPrise);
			agent.setPlaceAbsolue(maPlace);
			for (Entry<Integer, Direction> paire : directionsEmpruntees.entrySet()) {
				Direction directionEmpruntee = paire.getValue();
				int idAgent = paire.getKey();
				Coordonnees nouvellePlace = agent.carte.moveVehicule(idAgent, directionEmpruntee);
				ACLMessage message = new ACLMessage(ACLMessage.CFP);
				message.addReceiver(this.agent.getIdsEtAgents().get(idAgent));
				message.setOntology("Info place");
				message.setContent(nouvellePlace.X + "," + nouvellePlace.Y);
				message.setSender(this.agent.getAID());
				this.agent.send(message);
			}
			stade = ETAPE_EXPLORATION.MON_EXPLORATION;
		}
		//Le boss reçoit les directions empruntées par les agents qui essaient d'atteindre leur objectif
		else {
			if (nbMessagesDirectionRecus < nbMessagesDirectionAttendus) {
				MessageTemplate mt = MessageTemplate.MatchOntology("Info direction");
				ACLMessage msg = this.agent.receive(mt);
				if (msg != null) {
					Direction dir = DirectionUtil.stringToDirection(msg.getContent());
					
					//On récupère l'id (int) du véhicule qui sert d'identifiant à partir de l'AID de l'agent
					int idAgent = this.agent.getIdAgentFromAID(msg.getSender());
					nbMessagesDirectionRecus++;
					directionsEmpruntees.put(idAgent, dir);
				}
				else
					block();
			}
			else
				stade = ETAPE_EXPLORATION.MA_DECISION;
		}
	}

	@Override
	public boolean done() {
		return false;
	}
	
	private void setDirection(Direction dir) {
		agent.setDirectionRegardee(dir);
		agent.getVehicule().avancer(dir);
		directionPrise = dir;
	}
	
	private void findAndGoObjectif() {
		Coordonnees monObjectif = agent.carte.getClosestUndiscoveredCase(agent.carte.getPlacementAgents().get(agent.getVehicule().getID()));
		
		Coordonnees vecteurASuivre = Coordonnees.getVecteur(agent.getPlaceAbsolue(), monObjectif);
		Direction maDir = Direction.getDirection(vecteurASuivre.X, vecteurASuivre.Y);
		int testsFaits = 0;
		while (testsFaits <= 8) {
			Coordonnees coord = DirectionUtil.getXYfromDirection(maDir);
			Case cheminInteressant = mesCasesAlentour.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
			
			if (cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
				maDir = maDir.next();
				testsFaits++;
			}
			else {
				setDirection(maDir);
				break;
			}						
		}
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
