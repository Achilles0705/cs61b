package byow.Core;

public class Position {

    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position add(Position other) {
        return new Position(this.x + other.x, this.y + other.y);
    }

    public boolean equal(Position other) {
        return this.x == other.x && this.y == other.y;
    }


}
