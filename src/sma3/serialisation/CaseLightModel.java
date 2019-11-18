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

		obstacle = items[0].equals("Obstacle");		
		x = Integer.parseInt(items[1]);
		y = Integer.parseInt(items[2]);
		String[] booleens = items[3].split("-");
		decouverte = Boolean.parseBoolean(booleens[0]);
		occupee = Boolean.parseBoolean(booleens[1]);
		vehicule = Integer.parseInt(items[4]);
	}

	@Override
	public String toString() {
		return "CaseLightModel [x=" + x + ", y=" + y + ", decouverte=" + decouverte + ", occupee=" + occupee
				+ ", vehicule=" + vehicule + ", obstacle=" + obstacle + "]";
	}
}
