package sma.common;

import java.util.HashMap;
import java.util.Map.Entry;

import exploration.Vehicule;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Launcher {
	public ContainerController container;
	
	public Launcher(HashMap<Integer, Vehicule> vehicules) {		
		//Récupérer l'environnement d'exécution
		jade.core.Runtime rt = jade.core.Runtime.instance();
		//Créer un profil par défaut
		Profile p = new ProfileImpl();
		// est équivalent à :
		//  Profile p = new ProfileImpl(adresse_ip, 1099, adresse_ip:1099/JADE", true);
		//Créer un conteneur principal par défaut (i.e. sur cet hôte, port 1099)
		container = rt.createMainContainer(p);
		
		for (Vehicule vehicule : vehicules.values()) {
			initAndRun(new sma3.ExplorationAgent(), "agent" + vehicule.getID(), new Object[] { vehicule });
		}
	}
	
	private void initAndRun(Agent a, String nomAgent, Object[] args) {
		try {
			AgentController agentController = container.acceptNewAgent(nomAgent, a);
			a.setArguments(args);
			agentController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}
