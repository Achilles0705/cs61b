package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */

public class HexWorld {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 40;
    private static final long SEED = 1218;
    private static final Random RANDOM = new Random(SEED);

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.WATER;
            case 4: return Tileset.SAND;
            default: return Tileset.FLOOR;
        }
    }

    public static void fillTheWorldWithNothing(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void addHexagon(TETile[][] tiles, int row, int col, int size) {

        if (size < 1) {
            return;
        }

        TETile texture = randomTile();
        int radius = size; // 半圆的半径
        int width = 2 * size; // 心形整体的宽度
        int height = size * 2; // 心形的高度

        // 绘制上半部分的六边形
        for (int i = 0; i < size; i++) {  // i 表示从顶部到中部的行索引
            int startX = col - i;         // 当前行的起始列
            int endX = col + size + i;    // 当前行的结束列（不包括）
            for (int x = startX; x < endX; x++) {
                tiles[x][row + i] = texture; // 填充当前行的纹理
            }
        }

        // 绘制下半部分的六边形
        for (int i = 0; i < size; i++) {  // i 表示从中部到底部的行索引
            int startX = col - (size - 1) + i;  // 当前行的起始列
            int endX = col + size + (size - 1) - i; // 当前行的结束列（不包括）
            for (int x = startX; x < endX; x++) {
                tiles[x][row + size + i] = texture; // 填充当前行的纹理
            }
        }

    }

    public static void paintInLine(TETile[][] tiles, int size, int num, int startX, int startY) {
        for (int i = 0; i < num; i++) {
            int currentY = startY + 2 * i * size;
            addHexagon(tiles, currentY, startX, size);
        }
    }

    public static void paint(TETile[][] tiles, int size) {

        int X = (WIDTH - size) / 2;
        int delta = 2 * size - 1;
        int leftX1 = X - delta;
        int leftX2 = X - 2 * delta;
        int rightX1 = X + delta;
        int rightX2 = X + 2 * delta;
        int startY1 = size;
        int startY2 = 2 * size;

        paintInLine(tiles, size, 5, X, 0);
        paintInLine(tiles, size, 4, leftX1, startY1);
        paintInLine(tiles, size, 4, rightX1, startY1);
        paintInLine(tiles, size, 3, leftX2, startY2);
        paintInLine(tiles, size, 3, rightX2, startY2);

    }

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        fillTheWorldWithNothing(hexTiles);
        paint(hexTiles, 3);

        ter.renderFrame(hexTiles);

    }

}
