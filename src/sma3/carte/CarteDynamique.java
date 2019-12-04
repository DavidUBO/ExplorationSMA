package sma3.carte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import exploration.Direction;
import javafx.util.Pair;
import sma.common.Coordonnees;
import sma.common.DirectionUtil;
import sma3.serialisation.CaseLightModel;

public class CarteDynamique {

	private List<List<Case>> carte;
	private int largeur;
	private int hauteur;
	
	/***
	 * Pour retrouver aisément le placement des véhicules
	 */
	private Map<Integer, Coordonnees> placementAgents;
	
	public CarteDynamique(int idVehiculeCreateur) {
		placementAgents = new HashMap<>();
		this.carte = new LinkedList<>();
		this.carte.add(new LinkedList<>());
		Case maCase = new Case();
		this.carte.get(0).add(maCase);
		declarePosition(idVehiculeCreateur, new Coordonnees(0, 0));
		
		this.largeur = 1;
		this.hauteur = 1;
	}
	
	public Case getCase(int x, int y) {
		return carte.get(x).get(y);
	}
	
	private void growNord(int nombreNouvellesLignes) {
		for (int i = 0; i < largeur; i++) {
			List<Case> currentColonne = carte.get(i);
			for (int j = 0; j < nombreNouvellesLignes; j++)
				currentColonne.add(0, new Case());
		}
		hauteur += nombreNouvellesLignes;
		
		for (Entry<Integer,Coordonnees> paire : placementAgents.entrySet())
			paire.getValue().Y += nombreNouvellesLignes;
	}
	
	private void growSud(int nombreNouvellesLignes) {		
		for (int i = 0; i < largeur; i++) {
			List<Case> currentColonne = carte.get(i);  
			for (int j = 0; j < nombreNouvellesLignes; j++)
				currentColonne.add(new Case());
		}
		hauteur += nombreNouvellesLignes;
	}
	
	private void growEst(int nombreNouvellesColonnes) {
		for (int i = 0; i < nombreNouvellesColonnes; i++) {
			carte.add(createNouvelleColonne());
		}
		largeur += nombreNouvellesColonnes;
	}
	
	private void growOuest(int nombreNouvellesColonnes) {
		for (int i = 0; i < nombreNouvellesColonnes; i++) {
			carte.add(0, createNouvelleColonne());
		}
		largeur += nombreNouvellesColonnes;
		
		for (Entry<Integer,Coordonnees> paire : placementAgents.entrySet())
			paire.getValue().X += nombreNouvellesColonnes;
	}
	
	private List<Case> createNouvelleColonne() {
		List<Case> col = new LinkedList<Case>();
		for (int i = 0; i < hauteur; i++)
			col.add(new Case());
		return col;
	}
	
	public Coordonnees miseAJourCarte(int idAgent, List<exploration.Case> nouvellesCases) {
		Coordonnees placeAgent = placementAgents.get(idAgent);
		for (exploration.Case maCase : nouvellesCases) {
			int x = maCase.getX_relative();
			int y = maCase.getY_relative();
			int xAbsolu = placeAgent.X + x;
			int yAbsolu = placeAgent.Y + y;
			
			boolean ajustement = false;
			//Ajustement si nécessaire de la taille de la carte
			if (xAbsolu < 0) {
				int sizeToGrow = -xAbsolu; //valeur absolue
				this.growOuest(sizeToGrow);
				ajustement = true;
			} else if (xAbsolu >= largeur) {
				int sizeToGrow = xAbsolu - largeur + 1;
				this.growEst(sizeToGrow);
				ajustement = true;
			}
			
			if (yAbsolu < 0) {
				int sizeToGrow = -yAbsolu; //valeur absolue
				this.growNord(sizeToGrow);
				ajustement = true;
			} else if (yAbsolu >= hauteur) {
				int sizeToGrow = yAbsolu - hauteur + 1;
				this.growSud(sizeToGrow);
				ajustement = true;
			}
			
			if (ajustement) {
				placeAgent = placementAgents.get(idAgent);
				xAbsolu = placeAgent.X + x;
				yAbsolu = placeAgent.Y + y;
			}
			
			//Véritable mise à jour de la carte
			Case c = this.getCase(xAbsolu, yAbsolu);
			if (maCase.estObstacle())
				c.declareAsObstacle();
			if (maCase.getVehicule() != -1)
				declarePosition(maCase.getVehicule(), new Coordonnees(xAbsolu, yAbsolu));
			else
				c.setInoccupee();
			//c.setIdOccupee(maCase.getVehicule());
			c.isDecouverte = maCase.isDecouverte();
		}
		
		return placeAgent;
	}
	
	public Coordonnees miseAJourCarte2(int idAgent, List<CaseLightModel> nouvellesCases) {
		Coordonnees placeAgent = placementAgents.get(idAgent);
		for (CaseLightModel maCase : nouvellesCases) {
			int x = maCase.x;
			int y = maCase.y;
			int xAbsolu = placeAgent.X + x;
			int yAbsolu = placeAgent.Y + y;
			
			boolean ajustement = false;
			//Ajustement si nécessaire de la taille de la carte
			if (xAbsolu < 0) {
				int sizeToGrow = -xAbsolu; //valeur absolue
				this.growOuest(sizeToGrow);
				ajustement = true;
			} else if (xAbsolu >= largeur) {
				int sizeToGrow = xAbsolu - largeur + 1;
				this.growEst(sizeToGrow);
				ajustement = true;
			}
			
			if (yAbsolu < 0) {
				int sizeToGrow = -yAbsolu; //valeur absolue
				this.growNord(sizeToGrow);
				ajustement = true;
			} else if (yAbsolu >= hauteur) {
				int sizeToGrow = yAbsolu - hauteur + 1;
				this.growSud(sizeToGrow);
				ajustement = true;
			}
			
			if (ajustement) {
				placeAgent = placementAgents.get(idAgent);
				xAbsolu = placeAgent.X + x;
				yAbsolu = placeAgent.Y + y;
			}
			
			//Véritable mise à jour de la carte
			Case c = this.getCase(xAbsolu, yAbsolu);
			if (maCase.obstacle)
				c.declareAsObstacle();
			if (maCase.vehicule != -1)
				declarePosition(maCase.vehicule, new Coordonnees(xAbsolu, yAbsolu));
			else
				c.setInoccupee();
			c.isDecouverte = maCase.decouverte;
		}
		
		return placeAgent;
	}
	
	public void declarePosition(int numero, Coordonnees place) {
		Coordonnees eventuelleAnciennePlace = placementAgents.get(numero);
		if (eventuelleAnciennePlace != null) {
			Case c = getCase(eventuelleAnciennePlace.X, eventuelleAnciennePlace.Y);
			c.setInoccupee();
		}
		placementAgents.put(numero, place);
		getCase(place.X, place.Y).setIdOccupee(numero);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < hauteur; i ++) {
			for (int j = 0; j < largeur; j++) {
				Case c = this.getCase(j, i);
				buffer.append(c.isObstacle() ? "X" : c.getIdOccupee() != -1 ? c.getIdOccupee() : c.isDecouverte ? " " : "-");
			}
			buffer.append('\n');
		}
		
		buffer.append(placementAgents.toString());
		
		return buffer.toString();
	}

	public Map<Integer, Coordonnees> getPlacementAgents() {
		return placementAgents;
	}
	
	public synchronized Coordonnees moveVehicule(int id, Direction directionPrise) {
		Coordonnees placeActuelle = placementAgents.get(id);
		Case ancienneCase = getCase(placeActuelle.X, placeActuelle.Y);
		Coordonnees nouvellePlace = placeActuelle.translation(DirectionUtil.getXYfromDirection(directionPrise));
		Case nouvelleCase = getCase(nouvellePlace.X, nouvellePlace.Y);
		if (!nouvelleCase.isObstacle() && nouvelleCase.getIdOccupee() == -1) {
			ancienneCase.setInoccupee();
			nouvelleCase.setIdOccupee(id);
			placementAgents.put(id, nouvellePlace);
			return nouvellePlace;
		}
		else
			return placeActuelle;
	}
	
	public Coordonnees getClosestUndiscoveredCase(Coordonnees depart) {
		int distance = 1;
		while (true) {
			System.out.println("recherche distance " + distance);
			int borneMinX = depart.X - distance;
			int borneMaxX = depart.X + distance;
			int borneMinY = depart.Y - distance;
			int borneMaxY = depart.Y + distance;
			Map<Coordonnees,Case> casesPotentielles = new HashMap<>();
			for (int i = borneMinX < 0 ? 0 : borneMinX; i <= (borneMaxX > largeur - 1 ? largeur - 1 : borneMaxX) ; i++) {
				if (i != borneMinX && i != borneMaxX) {
					if (borneMinY >= 0)
						casesPotentielles.put(new Coordonnees(i, borneMinY), getCase(i, borneMinY));
					if (borneMaxY < hauteur)
						casesPotentielles.put(new Coordonnees(i, borneMaxY), getCase(i, borneMaxY));
				}
				else {
					for (int j = borneMinY < 0 ? 0 : borneMinY; j <= (borneMaxY > hauteur - 1 ? hauteur - 1 : borneMaxY); j++)
						casesPotentielles.put(new Coordonnees(i, j), getCase(i, j));
				}
			}
			//On a trop écarté le rayon
			if (casesPotentielles.isEmpty())
				break;
			//On cherche s'il y a une case non découverte (la première, afin de sortir toujours la même et ne pas balader l'agent)
			Optional<Entry<Coordonnees,Case>> caseNonDecouverte = casesPotentielles.entrySet().stream().filter(x -> !x.getValue().isDecouverte).findFirst();
			if (caseNonDecouverte.isPresent())
				return getClosestCaseFromShortestPath(depart, caseNonDecouverte.get().getKey());
			
			distance++;
		}
		//On a exploré toute la carte, il n'y a que des cases découvertes
		//pour ne pas rien renvoyer, on propose la limite externe la plus proche
		//sauf si l'on est déjà sur le bord, auquel cas on propose une bordure différente de la case occupée
		return getClosestCaseFromShortestPath(depart, getClosestLimite(depart));
	}
	
	private Coordonnees getClosestLimite(Coordonnees depart) {
		int x = depart.X;
		int y = depart.Y;
		
		//Cas si on est déjà sur le bord
		// angle
		if ((x == 0 || x == largeur - 1) && (y == 0 || y == hauteur - 1)) {
			Random r = new Random();
			boolean variationDeX = r.nextBoolean();
			int variation = 
					variationDeX && x == 0 ? 1 :
					variationDeX && x == largeur - 1 ? -1 :
					!variationDeX && y == 0 ? 1 :
					!variationDeX && y == hauteur - 1 ? -1 :
					r.nextBoolean() ? -1 : 1;
			return new Coordonnees(variationDeX ? x + variation : x, !variationDeX ? y + variation : y);
		}
		// simple bord horizontal
		if (x == 0 || x == largeur - 1) {
			Random r = new Random();
			int variation =
					y == 0 ? 1 :
					y == hauteur - 1 ? -1 :
					r.nextBoolean() ? -1 : 1;
			return new Coordonnees(x, y + variation);
		}
		// simple bord vertical
		if  (y == 0 || y == hauteur - 1){
			Random r = new Random();
			int variation =
					x == 0 ? 1 :
					x == largeur - 1 ? -1 :
					r.nextBoolean() ? -1 : 1;
			return new Coordonnees(x + variation, y);
		}
		
		//Cas normal (on n'est pas sur le bord)
		int distMin = y;
		Coordonnees coordProche = new Coordonnees(x, 0);
		if (hauteur - y - 1 < distMin) {
			distMin = hauteur - y - 1;
			coordProche = new Coordonnees(x, hauteur - 1);
		}
		if (x < distMin) {
			distMin = x;
			coordProche = new Coordonnees(0, y);
		}
		if (largeur - x - 1 < distMin) {
			distMin = largeur - x - 1;
			coordProche = new Coordonnees(largeur - 1, y);
		}
		return coordProche;
	}
	
	//Adapted from https://en.wikipedia.org/wiki/Pathfinding
	private Coordonnees getClosestCaseFromShortestPath(Coordonnees depart, Coordonnees arrivee) {
		if (getCoordonneesAdjacentes(depart).stream().anyMatch(x -> x.equals(arrivee)))
			return arrivee;
		
		List<Pair<Coordonnees, Integer>> file = new LinkedList<>();
		int compteur = 0;
		file.add(new Pair<>(arrivee, compteur++));
		
		int minIndice = 0;
		while (true) {
			final Integer compteurInteger = new Integer(compteur); // thanks : https://stackoverflow.com/a/33799995
			for (int i = minIndice; i < file.size(); i++) {
				Pair<Coordonnees, Integer> paire = file.get(i);
				List<Coordonnees> casesAdjacentes = getCoordonneesAdjacentes(paire.getKey());
				for (Iterator<Coordonnees> it = casesAdjacentes.iterator(); it.hasNext(); ) {
					Coordonnees c = it.next();
					Case maCase = getCase(c.X, c.Y);
					if (maCase.isObstacle()) //On ne considère pas les places occupées
						it.remove();
					else if (file.parallelStream().anyMatch(x -> x.getKey().equals(c) && x.getValue() <= compteurInteger))
						it.remove();
					else {
						if (c.equals(depart)) {
							System.out.println("on renvoie " + paire.getKey().toString());
							return paire.getKey();
						}
						file.add(new Pair<>(c, compteur));
					}
				}
				minIndice++;
			}
			compteur++;
		}
	}
	
	private List<Coordonnees> getCoordonneesAdjacentes(Coordonnees point) {
		int borneMinX = point.X - 1;
		int borneMaxX = point.X + 1;
		int borneMinY = point.Y - 1;
		int borneMaxY = point.Y + 1;
		
		List<Coordonnees> res = new ArrayList<>();
		for (int i = borneMinX < 0 ? 0 : borneMinX; i <= (borneMaxX > largeur - 1 ? largeur - 1 : borneMaxX) ; i++) {
			if (i != borneMinX && i != borneMaxX) {
				if (borneMinY >= 0)
					res.add(new Coordonnees(i, borneMinY));
				if (borneMaxY < hauteur)
					res.add(new Coordonnees(i, borneMaxY));
			}
			else {
				for (int j = borneMinY < 0 ? 0 : borneMinY; j <= (borneMaxY > hauteur - 1 ? hauteur - 1 : borneMaxY); j++)
					res.add(new Coordonnees(i, j));
			}
		}
		return res;
	}
		
}
