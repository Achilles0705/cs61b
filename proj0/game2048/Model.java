package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }
    /** Tilt the board toward SIDE. Return true iff this changes the board.
     * 将板子向侧面倾斜。如果这改变了板子，则返回 true。
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     *    如果两个 Tile 对象在运动方向上相邻且具有相同的值，则它们将合并为一个原值两倍的 Tile，并将新值添加到 score 实例变量中
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     *    合并后的图块不会在该倾斜处再次合并。因此，每次移动时，每个图块最多只能成为一次合并的一部分（可能为零）。
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     *    当运动方向上的三个相邻的图块具有相同的值时，则运动方向上的前两个图块会合并，而后一个图块则不会合并。
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        //向侧面倾斜。如果棋盘发生变化，则将改变的局部变量设置为 true。

        if(side == Side.EAST){
            board.setViewingPerspective(Side.EAST);
        }
        if(side == Side.SOUTH){
            board.setViewingPerspective(Side.SOUTH);
        }
        if(side == Side.WEST){
            board.setViewingPerspective(Side.WEST);
        }


        for(int i = 3; i >= 0; i--){   //不考虑行
            boolean []book=new boolean[4];
            for(int j = 3; j >= 0; j--){   //只考虑单列
                if(board.tile(i,j) != null && j != 3){
                    int j2 = j + 1;
                    Tile t = board.tile(i, j);
                    while(j2 < 3 && board.tile(i, j2) == null){
                        j2++;
                    }
                    if(j2 == 3 && board.tile(i, j2) == null) {   //上面全是0
                        board.move(i, 3, t);
                        changed = true;
                    }
                    else if(t.value() == board.tile(i, j2).value()){   //上面最近的一个相等
                        if(!book[j2]) {
                            board.move(i, j2, t);
                            book[j2] = true;
                            score += 2 * t.value();
                        }
                        else{
                            board.move(i,j2-1,t);
                        }
                        changed = true;
                    }
                    else if(j2 != j){ //上面最近的一个不等 + 不挨着，要是挨着直接没变化
                        board.move(i, j2 - 1, t);
                        changed = true;
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        boolean judge = false;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(b.tile(i,j) == null){
                    judge = true;
                    break;
                }
            }
        }
        return judge;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        /*if (emptySpaceExists(b)) {
            return false;
        }*/
        //int cnt = 0;
        boolean judge = false;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(b.tile(i,j) != null){
                    if(b.tile(i,j).value() == MAX_PIECE){
                        judge = true;
                        break;
                    }
                }
            }
        }
        return judge;
    }

    public static boolean borderJudge(int x){
        if(x < 0 || x > 3) return false;
        return true;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        boolean judge = false;
        if(emptySpaceExists(b)){
            return true;
        }

        int [][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                for(int t = 0; t < 4; t++){
                    int newRow = i + directions[t][0];
                    int newColumn = j + directions[t][1];
                    if(borderJudge(newRow) && borderJudge(newColumn)){
                        if(b.tile(i,j).value() == b.tile(newRow,newColumn).value())
                            return true;
                    }
                }
            }
        }
        return judge;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}