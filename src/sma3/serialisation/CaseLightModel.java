package sma3.serialisation;

public class CaseLightModel {
	public int x;
	public int y;
	public boolean decouverte;
	public boolean occupee;
	public int vehicule;
	public boolean obstacle;
	
	public CaseLightModel(String serializedInfo) {
		String[] items = serializedInfo.split("(\\(|,|\\)| |:)+");
		int indiceDepart = 1;
		
		obstacle = items[0].equals("Obstacle");		
		x = Integer.parseInt(items[indiceDepart + 0]);
		y = Integer.parseInt(items[indiceDepart + 1]);
		String[] booleens = items[indiceDepart + 2].split("-");
		decouverte = Boolean.parseBoolean(booleens[0]);
		occupee = Boolean.parseBoolean(booleens[1]);
		vehicule = Integer.parseInt(items[indiceDepart + 3]);
	}
}
