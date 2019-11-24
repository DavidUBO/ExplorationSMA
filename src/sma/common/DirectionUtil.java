package sma.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import exploration.Case;
import exploration.Direction;

public final class DirectionUtil {
	
	public static List<Case> cases(List<Case> casesEnvironnantes, Direction direction) {
		switch (direction) {
			case Nord:
				return casesEnvironnantes.stream() 
						.filter(maCase -> {
							int x = maCase.getX_relative();
							return maCase.getY_relative() <= -1 && x <= 1 && x >= -1;
						})
						.collect(Collectors.toList());
			case Sud:
				return casesEnvironnantes.stream() 
						.filter(maCase -> {
							int x = maCase.getX_relative();
							return maCase.getY_relative() >= 1 && x <= 1 && x >= -1;
						})
						.collect(Collectors.toList());
			case Ouest:
				return casesEnvironnantes.stream() 
						.filter(maCase -> {
							int y = maCase.getY_relative();
							return maCase.getX_relative() <= -1 && y <= 1 && y >= -1;
						})
						.collect(Collectors.toList());
			case Est:
				return casesEnvironnantes.stream() 
						.filter(maCase -> {
							int y = maCase.getY_relative();
							return maCase.getX_relative() >= 1 && y <= 1 && y >= -1;
						})
						.collect(Collectors.toList());
			default:
				return new ArrayList<Case>();
		}
	}
	
	public static Direction directionGauche(Direction direction) {
		switch (direction) {
			case Nord:
				return Direction.Ouest;
			case Ouest:
				return Direction.Sud;
			case Sud:
				return Direction.Est;
			case Est:
				return Direction.Nord;
			default:
				return Direction.Ouest;
		}
	}
	
	public static Coordonnees getXYfromDirection(Direction direction) {
		switch (direction) {
		case NO:
			return new Coordonnees(-1, -1);
		case Nord:
			return new Coordonnees(0, -1);
		case NE:
			return new Coordonnees(1, -1);
		case Est:
			return new Coordonnees(1, 0);
		case SE:
			return new Coordonnees(1, 1);
		case Sud:
			return new Coordonnees(0,  1);
		case SO:
			return new Coordonnees(-1, 1);
		default:
			return new Coordonnees(-1, 0);
		}
	}
	
	public static Direction getOpposite(Direction dir) {
		switch (dir) {
	        case Nord:
	            return Direction.Sud;
	        case NE:
	            return Direction.SO;
	        case Est:
	            return Direction.Ouest;
	        case SE:
	            return Direction.NO;
	        case Sud:
	            return Direction.Nord;
	        case SO:
	            return Direction.NE;
	        case Ouest:
	            return Direction.Est;
	        default:
	            return Direction.SE;
		}
	}
	
	public static String directionToString(Direction dir) {
		if (dir == null)
			return "null";
		switch (dir) {
	        case Nord:
	            return "N";
	        case NE:
	            return "NE";
	        case Est:
	            return "E";
	        case SE:
	            return "SE";
	        case Sud:
	            return "S";
	        case SO:
	            return "SO";
	        case Ouest:
	            return "O";
	        default:
	            return "NO";
		}
	}
	
	public static Direction stringToDirection(String s) {
		switch (s) {
	        case "N":
	            return Direction.Nord;
	        case "NE":
	            return Direction.NE;
	        case "E":
	            return Direction.Est;
	        case "SE":
	            return Direction.SE;
	        case "S":
	            return Direction.Sud;
	        case "SO":
	            return Direction.SO;
	        case "O":
	            return Direction.Ouest;
	        case "NO":
	        	return Direction.NO;
	        default:
	            return null;
		}
	}
}
