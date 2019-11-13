/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploration;

/**
 *
 * @author riviere
 */
public enum Direction {
    Nord(0, 0, -1),
    NE(1, 1, -1),
    Est(2, 1, 0),
    SE(3, 1, 1),
    Sud(4, 0, 1),
    SO(5, -1, 1),
    Ouest(6, -1, 0),
    NO(7, -1, -1);

    private final int x;
    private final int y;
    private final int pos;

    Direction(int pos, int x, int y) {
        this.x = x;
        this.y = y;
        this.pos = pos;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Prochaine direction dans le sens horaire
     * @return 
     */
    public Direction next() {
        int size = Direction.values().length;
        if (pos < size - 1) {
            return Direction.values()[pos + 1];
        } else {
            return Direction.values()[0];
        }
    }

    public static Direction getRandom() {
        Double rand = Math.random() * 8;
        if (rand < 1.) {
            return Sud;
        }
        if (rand < 2.) {
            return SO;
        }
        if (rand < 3.) {
            return Ouest;
        }
        if (rand < 4.) {
            return NO;
        }
        if (rand < 5.) {
            return Nord;
        }
        if (rand < 6.) {
            return NE;
        }
        if (rand < 7.) {
            return Est;
        } else {
            return SE;
        }
    }

    public boolean isOpposite(Direction dir) {
        switch (dir) {
            case Nord:
                return this == Sud;
            case NE:
                return this == SO;
            case Est:
                return this == Ouest;
            case SE:
                return this == NO;
            case Sud:
                return this == Nord;
            case SO:
                return this == NE;
            case Ouest:
                return this == Est;
            default:
                return this == SE;
        }
    }

    public static Direction getDirection(int x, int y) {
        if ((x == 0) && (y > 0 )) {
            return Sud;
        } else if ((x == 0) && (y < 0)) {
            return Nord;
        } else if ((x > 0) && (y == 0)) {
            return Est;
        } else if ((x < 0) && (y == 0)) {
            return Ouest;
        } else if ((x < 0) && (y < 0)) {
            return NO;
        } else if ((x < 0) && (y > 0)) {
            return SO;
        } else if ((x > 0) && (y < 0)) {
            return NE;
        } else if ((x > 0) && (y > 0)) {
            return SE;
        } else {
            return null;
        }
    }

}
