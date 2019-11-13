package sma3.carte;

public class Case {
	
	private boolean isObstacle;
	private int idOccupee;
	public boolean isDecouverte;
	
	public Case() {
		isObstacle = false;
		idOccupee = -1;
		isDecouverte = false;
	}

	public boolean isObstacle() {
		return isObstacle;
	}

	public void declareAsObstacle() {
		this.isObstacle = true;
		this.isDecouverte = true;
	}

	public int getIdOccupee() {
		return idOccupee;
	}
	
	public void setInoccupee() {
		this.idOccupee = -1;
	}

	public void setIdOccupee(int idOccupee) {
		this.idOccupee = idOccupee;
	}
	
}
