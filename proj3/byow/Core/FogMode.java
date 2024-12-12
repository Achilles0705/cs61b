package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Queue;

import static byow.Core.Engine.*;

public class FogMode {

    private static long fogDisabledTime = 0;
    private static final long FOG_DURATION = 300; // 1秒
    private static boolean fogEnabled = true;
    private static int cnt = 0;
    private static final int MAX_TIMES = 100;

    public static void toggleFogMode() {
        if (cnt == MAX_TIMES) {
            System.out.println("清空迷雾次数已用尽");
            return;
        }
        if (fogEnabled) {
            System.out.println("迷雾模式已解除");
            fogEnabled = false;
            fogDisabledTime = System.currentTimeMillis(); // 记录禁用迷雾的时间
            cnt++;
        }
    }

    public static boolean checkFogTimer() {
        if (!fogEnabled && System.currentTimeMillis() - fogDisabledTime >= FOG_DURATION) {
            System.out.println("迷雾模式已恢复" + '\n');
            fogEnabled = true; // 恢复迷雾模式
        }
        return !fogEnabled; // 当迷雾模式被禁用时返回true
    }

    public static void mistMode(TETile[][] world, int size) {
        boolean[][] isNotMist = new boolean[WIDTH][HEIGHT];
        int userX = user.x;
        int userY = user.y;

        Queue<Position> queue = new LinkedList<>();
        queue.offer(new Position(userX, userY));
        isNotMist[userX][userY] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        int steps = 0;
        while (steps < size && !queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Position current = queue.poll();
                int x = current.x;
                int y = current.y;

                for (int[] dir : directions) {
                    int newX = x + dir[0];
                    int newY = y + dir[1];

                    if (!illegal(newX, newY) && !isNotMist[newX][newY] && world[x][y] != Tileset.WALL) {
                        isNotMist[newX][newY] = true;
                        queue.offer(new Position(newX, newY));
                    }
                }
            }
            steps++;
        }

        // 遍历整个世界，将不在可见区域的方块设置为 Tileset.NOTHING
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!isNotMist[i][j]) {
                    world[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

}
