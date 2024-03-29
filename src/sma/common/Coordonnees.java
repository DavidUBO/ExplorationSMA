package sma.common;

public class Coordonnees {
	public int X;
	public int Y;
	
	public Coordonnees(int x, int y) {
		X = x;
		Y = y;
	}
	
	public Coordonnees translation(int x, int y) {
		return new Coordonnees(X + x, Y + y);
	}
	
	public Coordonnees translation(Coordonnees c) {
		return new Coordonnees(X + c.X, Y + c.Y);
	}
	
	public static Coordonnees getVecteur(Coordonnees depart, Coordonnees fin) {
		return new Coordonnees(fin.X - depart.X, fin.Y - depart.Y);
	}

	@Override
	public String toString() {
		return "Coordonnees [X=" + X + ", Y=" + Y + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof Coordonnees)) return false;
		Coordonnees c = (Coordonnees) o;
		return c.X == X && c.Y == Y;
	}
}
