package sma3.carte;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import exploration.Direction;
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
		this.carte = new LinkedList<>();
		this.carte.add(new LinkedList<>());
		Case maCase = new Case();
		maCase.setIdOccupee(idVehiculeCreateur);
		placementAgents = new HashMap<>();
		placementAgents.put(idVehiculeCreateur, new Coordonnees(0, 0));
		this.carte.get(0).add(maCase);
		
		this.largeur = 1;
		this.hauteur = 1;
	}
	
	public Case getCase(int x, int y) {
		return carte.get(x).get(y);
	}
	
//	private void grow(Direction dir, int taille) {
//		switch (dir) {
//			case Nord:
//				growNord(taille);
//				break;
//			case Sud:
//				growSud(taille);
//				break;
//			case Est:
//				growEst(taille);
//				break;
//			case Ouest:
//				growOuest(taille);
//				break;
//			default:
//				break;
//		}
//	}
	
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
	
	public Coordonnees miseAJourCarte(Coordonnees placeActuelle, List<exploration.Case> nouvellesCases) {
		
		for (exploration.Case maCase : nouvellesCases) {
			int x = maCase.getX_relative();
			int y = maCase.getY_relative();
			int xAbsolu = placeActuelle.X + x;
			int yAbsolu = placeActuelle.Y + y;
			
			//Ajustement si nécessaire de la taille de la carte
			if (xAbsolu < 0) {
				int sizeToGrow = -xAbsolu; //valeur absolue
				this.growOuest(sizeToGrow);
				placeActuelle = new Coordonnees(placeActuelle.X + sizeToGrow, placeActuelle.Y);
				xAbsolu = placeActuelle.X + x;
			} else if (xAbsolu >= largeur) {
				int sizeToGrow = xAbsolu - largeur + 1;
				this.growEst(sizeToGrow);
			}
			
			if (yAbsolu < 0) {
				int sizeToGrow = -yAbsolu; //valeur absolue
				this.growNord(sizeToGrow);
				placeActuelle = new Coordonnees(placeActuelle.X, placeActuelle.Y + sizeToGrow);
				yAbsolu = placeActuelle.Y + y;
			} else if (yAbsolu >= hauteur) {
				int sizeToGrow = yAbsolu - hauteur + 1;
				this.growSud(sizeToGrow);
			}
			
			//Véritable mise à jour de la carte
			Case c = this.getCase(xAbsolu, yAbsolu);
			if (maCase.estObstacle())
				c.declareAsObstacle();
			c.setIdOccupee(maCase.getVehicule());
			c.isDecouverte = maCase.isDecouverte();
		}
		
		return placeActuelle;
	}
	
	public Coordonnees miseAJourCarte2(Coordonnees placeActuelle, List<CaseLightModel> nouvellesCases) {
		
		for (CaseLightModel maCase : nouvellesCases) {
			int x = maCase.x;
			int y = maCase.y;
			int xAbsolu = placeActuelle.X + x;
			int yAbsolu = placeActuelle.Y + y;
			
			//Ajustement si nécessaire de la taille de la carte
			if (xAbsolu < 0) {
				int sizeToGrow = -xAbsolu; //valeur abolue
				this.growOuest(sizeToGrow);
				placeActuelle = new Coordonnees(placeActuelle.X + sizeToGrow, placeActuelle.Y);
				xAbsolu = placeActuelle.X + x;
			} else if (xAbsolu > largeur) {
				int sizeToGrow = xAbsolu - largeur;
				this.growEst(sizeToGrow);
			}
			
			if (yAbsolu < 0) {
				int sizeToGrow = -yAbsolu; //valeur absolue
				this.growNord(sizeToGrow);
				placeActuelle = new Coordonnees(xAbsolu, placeActuelle.Y + sizeToGrow);
				yAbsolu = placeActuelle.Y + y;
			} else if (yAbsolu > hauteur) {
				int sizeToGrow = yAbsolu - hauteur;
				this.growSud(sizeToGrow);
			}
			
			//Véritable mise à jour de la carte
			Case c = this.getCase(xAbsolu, yAbsolu);
			if (maCase.obstacle)
				c.declareAsObstacle();
			c.setIdOccupee(maCase.vehicule);
			c.isDecouverte = maCase.decouverte;
		}
		
		return placeActuelle;
	}
	
	public void declarePosition(int numero, Coordonnees place) {
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
		ancienneCase.setInoccupee();
		Coordonnees nouvellePlace = placeActuelle.translation(DirectionUtil.getXYfromDirection(directionPrise));
		Case nouvelleCase = getCase(nouvellePlace.X, nouvellePlace.Y);
		nouvelleCase.setIdOccupee(id);
		placementAgents.put(id, nouvellePlace);
		return nouvellePlace;
	}
	
	public Coordonnees getClosestUndiscoveredCase(Coordonnees depart) {
		while (true) {
			int distance = 1;
			int borneMinX = depart.X - distance;
			int borneMaxX = depart.X + distance;
			int borneMinY = depart.Y - distance;
			int borneMaxY = depart.Y + distance;
			Map<Coordonnees,Case> casesPotentielles = new HashMap<>();
			for (int i = borneMinX < 0 ? 0 : borneMinX; i <= (borneMaxX > largeur ? largeur : borneMaxX) ; i++) {
				if (i != borneMinX && i != borneMaxX) {
					casesPotentielles.put(new Coordonnees(i, borneMinY), getCase(i, borneMinY));
					casesPotentielles.put(new Coordonnees(i, borneMaxY), getCase(i, borneMaxY));
				}
				else {
					for (int j = borneMinY < 0 ? 0 : borneMinY; j <= (borneMaxY > hauteur ? hauteur : borneMaxY); j++)
						casesPotentielles.put(new Coordonnees(i, j), getCase(i, j));
				}
			}
			//On a trop écarté le rayon
			if (casesPotentielles.isEmpty())
				break;
			//On cherche s'il y a une case non découverte (n'importe laquelle)
			Optional<Entry<Coordonnees,Case>> caseNonDecouverte = casesPotentielles.entrySet().stream().filter(x -> !x.getValue().isDecouverte).findAny();
			if (caseNonDecouverte.isPresent())
				return caseNonDecouverte.get().getKey();
		}
		//On a exploré toute la carte, il n'y a que des cases découvertes
		//pour ne pas rien renvoyer, on propose la  limite externe la plus proche
		return getClosestLimite(depart);
	}
	
	private Coordonnees getClosestLimite(Coordonnees depart) {
		return depart;
	}
		
}
