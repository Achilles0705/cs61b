package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.LinkedList;
import java.util.Queue;

import static byow.Core.Engine.*;

public class FogMode {

    /*private static long fogDisabledTime = 0;
    private static final long FOG_DURATION = 300; // 1秒
    private static int cntFog = 0;
    private static final int FOG_MAX_TIMES = 100;*/
    private static boolean fogEnabled = true;

    private static long doorDisabledTime = 0;
    //private static final long DOOR_DURATION = 1000;
    private static boolean doorEnabled = true;
    private static int cntDoor = 0;
    private static final int DOOR_MAX_TIMES = 3;

    /*public static void toggleFogMode() {
        if (cntFog == FOG_MAX_TIMES) {
            System.out.println("清空迷雾次数已用尽");
            return;
        }
        if (fogEnabled) {
            System.out.println("迷雾模式已解除");
            fogEnabled = false;
            fogDisabledTime = System.currentTimeMillis(); // 记录禁用迷雾的时间
            cntFog++;
        }
    }*/

    public static void toggleFogMode() {
        if (fogEnabled) {
            System.out.println("调试模式已开启");
        } else {
            System.out.println("调试模式已关闭");
        }
        fogEnabled = !fogEnabled;
    }

    public static boolean getFogStatus() {
        return fogEnabled;
    }

    public static void peekDoor() {
        if (cntDoor == DOOR_MAX_TIMES) {
            System.out.println("看出口的次数已用尽");
            return;
        }
        if (doorEnabled) {
            System.out.println("出口已出现");
            doorEnabled = false;
            doorDisabledTime = System.currentTimeMillis(); // 记录禁用门的时间
            cntDoor++;
        }
    }

    public static boolean checkDoorTimer() {
        //if (!doorEnabled && System.currentTimeMillis() - doorDisabledTime >= DOOR_DURATION) {
        /*if (!doorEnabled && flashNTimes(3, 1000, 500)) {
            System.out.println("出口已消失于迷雾中" + '\n');
            doorEnabled = true; // 恢复门不可见
        }
        return !doorEnabled;*/
        //return flashNTimes(3, 500, 300);
        if (flashNTimes(3, 500, 300)) {
            doorEnabled = true;
            return true;
        }
        return false;
    }

    private static boolean flashNTimes(int times, long duration, long interval) {
        long time = System.currentTimeMillis() - doorDisabledTime;
        for (int i = 0; i < times; i++) {
            long startTime = i * (duration + interval);
            long endTime = duration + startTime;
            if (time >= startTime && time <= endTime) {
                return true;
            }
        }
        return false;
    }

    /*public static boolean checkFogTimer() {
        if (!fogEnabled && System.currentTimeMillis() - fogDisabledTime >= FOG_DURATION) {
            System.out.println("迷雾模式已恢复" + '\n');
            fogEnabled = true; // 恢复迷雾模式
        }
        return !fogEnabled; // 当迷雾模式被禁用时返回true
    }*/

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
