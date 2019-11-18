package sma3;

import java.util.List;
import java.util.StringJoiner;

import exploration.Case;
import exploration.Direction;
import exploration.Vehicule;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sma.common.Coordonnees;
import sma.common.DirectionUtil;

public class ExplorationBehaviour extends Behaviour {

	private ExplorationAgent agent;
	private Vehicule vehiculeAgent;
	private Direction directionChoisie;
	private Coordonnees monObjectif;
	private ETAPE_EXPLORATION stade;
	private List<Case> casesVues;
	
	public ExplorationBehaviour(ExplorationAgent agent) {
		this.agent = agent;
		this.vehiculeAgent = agent.getVehicule();
		stade = ETAPE_EXPLORATION.RECHERCHE;
		directionChoisie = null;
		monObjectif = null;
	}
	
	@Override
	public void action() {
		
		//Analyse de l'environnement
		if (stade == ETAPE_EXPLORATION.RECHERCHE) {
			System.out.println("Je suis agent " + vehiculeAgent.getID() + " en " + agent.getPlaceAbsolue().toString());
			try { Thread.sleep(ExplorationAgent.TEMPS_PAUSE); } catch (InterruptedException e) { }
			
			casesVues = vehiculeAgent.getVoisinage();
			boolean decision = false;
			
			Direction directionRegardee = agent.getDirectionRegardee();
			Direction gauche = DirectionUtil.directionGauche(directionRegardee);
			List<Case> casesDeGauche = DirectionUtil.cases(casesVues, gauche);
			
			if (casesVues.stream().allMatch(x -> x.isDecouverte())) {
				//À l'aide, boss
				stade = ETAPE_EXPLORATION.DEMANDE_OBJECTIF;
			}				
			else if (casesDeGauche.stream().anyMatch(x -> !x.isDecouverte())) {
				setDirection(gauche, ETAPE_EXPLORATION.ENVOI_ENV_DIR);
			}
			else {				
				Direction versCase = gauche.next().next();
				
				int tests = 0;
				while (tests <= 3) {
					List<Case> casesParLa = DirectionUtil.cases(casesVues, versCase);
					Coordonnees coord = DirectionUtil.getXYfromDirection(versCase);
					Case cheminInteressant = casesVues.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
					
					if (casesParLa.stream().allMatch(x -> x.isDecouverte()) || cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
						versCase = versCase.next().next();
						tests++;
					}
					else {
						setDirection(versCase, ETAPE_EXPLORATION.ENVOI_ENV_DIR);
						decision = true;
						break;
					}						
				}
				if (!decision) {
					if (monObjectif == null)
						//À l'aide, boss
						stade = ETAPE_EXPLORATION.DEMANDE_OBJECTIF;
					else {
						//Le robot a un objectif, il essaie d'y aller
						allerVersObjectif(ETAPE_EXPLORATION.ENVOI_ENV_DIR);
					}
				}
			}
		}
		//Demande d'objectif auprès du boss avec envoi de l'environnement
		else if (stade == ETAPE_EXPLORATION.DEMANDE_OBJECTIF) {
			StringJoiner infoAEnvoyer = new StringJoiner(";");
			
			for(Case c : casesVues)
				infoAEnvoyer.add(c.toString());
			
			ACLMessage message = new ACLMessage(ACLMessage.CFP);
			message.addReceiver(this.agent.getIdsEtAgents().get(this.agent.getIdDuBigBoss()));
			message.setOntology("Demande aide");
			message.setContent(infoAEnvoyer.toString());
			message.setSender(this.agent.getAID());
			this.agent.send(message);
			stade = ETAPE_EXPLORATION.RECEP_OBJECTIF;
		}
		//Envoi des informations (environnement et direction choisie)
		else if (stade == ETAPE_EXPLORATION.ENVOI_ENV_DIR) {
			//Cela signifie : voici ce que j'ai vu et voici ma décision (où je suis allé)
			StringJoiner infoAEnvoyer = new StringJoiner(";");
			
			for(Case c : casesVues)
				infoAEnvoyer.add(c.toString());
			
			ACLMessage message = new ACLMessage(ACLMessage.CFP);
			message.addReceiver(this.agent.getIdsEtAgents().get(this.agent.getIdDuBigBoss()));
			message.setOntology("Info cases");
			message.setContent(infoAEnvoyer.toString() + "_" + DirectionUtil.directionToString(directionChoisie));
			message.setSender(this.agent.getAID());
			this.agent.send(message);
			stade = ETAPE_EXPLORATION.RECEP_NVELLE_PLACE;
		}
		//Réception de la nouvelle localisation
		else if (stade == ETAPE_EXPLORATION.RECEP_NVELLE_PLACE) {
			MessageTemplate mt = MessageTemplate.MatchOntology("Info place");
			ACLMessage msg = agent.receive(mt);
			if(msg != null) {
				String[] coordsString = msg.getContent().split(",");
				this.agent.setPlaceAbsolue(new Coordonnees(Integer.parseInt(coordsString[0]), Integer.parseInt(coordsString[1])));
				stade = ETAPE_EXPLORATION.RECHERCHE;
			}				
			else
				block();
		}
		//Réception de mon nouvel objectif
		else if (stade == ETAPE_EXPLORATION.RECEP_OBJECTIF) {
			//Je sais où je suis et où je vais : j'y vais
			MessageTemplate mt = MessageTemplate.MatchOntology("Info objectif");
			ACLMessage msg = agent.receive(mt);
			if(msg != null) {
				System.out.println(msg.getContent());
				String[] coordsString = msg.getContent().split(";");
				String[] maPlaceS = coordsString[0].split(",");
				agent.setPlaceAbsolue(new Coordonnees(Integer.parseInt(maPlaceS[0]), Integer.parseInt(maPlaceS[1])));
				String[] objectifS = coordsString[1].split(",");
				monObjectif = new Coordonnees(Integer.parseInt(objectifS[0]), Integer.parseInt(objectifS[1]));
				allerVersObjectif(ETAPE_EXPLORATION.ENVOI_DIRECTION);
				monObjectif = null; //Pour qu'à l'itération suivante, je demande un nouvel objectif
									//au cas où l'endroit où j'allais a été découvert entretemps
			}				
			else
				block();
		}
		//Envoi de seulement ma direction
		else {
			ACLMessage message = new ACLMessage(ACLMessage.CFP);
			message.addReceiver(this.agent.getIdsEtAgents().get(this.agent.getIdDuBigBoss()));
			message.setOntology("Info direction");
			message.setContent(DirectionUtil.directionToString(directionChoisie));
			message.setSender(this.agent.getAID());
			this.agent.send(message);
			stade = ETAPE_EXPLORATION.RECEP_NVELLE_PLACE;
			System.out.println("agent " + agent.getVehicule().getID() + " dir envoyée");
		}
	}

	@Override
	public boolean done() {
		return false;
	}
	
	private void setDirection(Direction d, ETAPE_EXPLORATION stadeSuivant) {
		directionChoisie = d;
		agent.setDirectionRegardee(d);
		vehiculeAgent.avancer(d);
		stade = stadeSuivant;
		System.out.println("agent " + agent.getVehicule().getID()+ " vers " + d.toString());
	}
	
	private void allerVersObjectif(ETAPE_EXPLORATION stadeSuivant) {
		Coordonnees vecteurASuivre = Coordonnees.getVecteur(agent.getPlaceAbsolue(), monObjectif);
		Direction maDir = Direction.getDirection(vecteurASuivre.X, vecteurASuivre.Y);
		int testsFaits = 0;
		while (testsFaits <= 8) {
			Coordonnees coord = DirectionUtil.getXYfromDirection(maDir);
			Case cheminInteressant = casesVues.stream().filter(x -> x.getX_relative() == coord.X && x.getY_relative() == coord.Y).findFirst().get();
			
			if (cheminInteressant.estObstacle() || cheminInteressant.isOccupee()) {
				maDir = maDir.next();
				testsFaits++;
			}
			else {
				setDirection(maDir, stadeSuivant);
				break;
			}						
		}
	}
	
	private enum ETAPE_EXPLORATION {
		RECHERCHE,
		DEMANDE_OBJECTIF,
		ENVOI_ENV_DIR,
		RECEP_NVELLE_PLACE,
		RECEP_OBJECTIF,
		ENVOI_DIRECTION
	}
	
}
