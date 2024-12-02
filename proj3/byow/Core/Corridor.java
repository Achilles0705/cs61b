package byow.Core;

public class Corridor {

    public Position start;
    public Position end;
    public int width;
    boolean isHorizontal; // 新增字段，标识方向

    public Corridor(Position start, Position end, int width) {
        this.start = start;
        this.end = end;
        this.width = width;
        this.isHorizontal = start.y == end.y; // 判断方向
    }

}
