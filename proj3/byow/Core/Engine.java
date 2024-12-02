package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    static Random rand;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        int seed = 0;
        String regex = "n(\\d+)s"; // 正则表达式，括号定义了捕获组
        Pattern pattern = Pattern.compile(regex); // 1. 创建 Pattern 对象
        Matcher matcher = pattern.matcher(input); // 2. 创建 Matcher 对象
        if (matcher.find()) {  // find() 方法尝试查找第一个匹配项
            String seedString = matcher.group(1);
            seed = Integer.parseInt(seedString);
        } else {

        }

        rand = new Random(seed);
        List<Room> rooms = new ArrayList<>();
        int roomNum = RandomUtils.uniform(rand, 2, 20);
        for (int i = 0; i < roomNum; i++) {
            Position topLeft = new Position(0, 0);
            topLeft.x = RandomUtils.uniform(rand, 1, WIDTH - 1);
            topLeft.y = RandomUtils.uniform(rand, 1, HEIGHT - 1);
            int width = RandomUtils.uniform(rand, 3, WIDTH / 4);
            int height = RandomUtils.uniform(rand, 3, HEIGHT / 4);

            int maxX = topLeft.x + width;
            int minY = topLeft.y - height;
            if (maxX >= WIDTH - 1 || minY <= 0) {
                continue;
            }
            Room newRoom = new Room(topLeft, width,height);
            if (!Room.isOverlapped(rooms, newRoom)) {
                rooms.add(newRoom);
            }
        }

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillTheWorldWithNothing(finalWorldFrame);
        Room.fillInRooms(finalWorldFrame, rooms);

        List<Corridor> corridors = generateCorridors(rooms);
        drawCorridors(finalWorldFrame, corridors);

        return finalWorldFrame;
    }

    private static void fillTheWorldWithNothing(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    private static void drawCorridors(TETile[][] world, List<Corridor> corridors) {
        for (Corridor tmpCorridor : corridors) {
            drawCorridorWithWalls(world, tmpCorridor);
        }
    }

    private static void drawCorridorWithWalls(TETile[][] world, Corridor corridor) {
        int minX = Math.min(corridor.start.x, corridor.end.x);
        int maxX = Math.max(corridor.start.x, corridor.end.x);
        int minY = Math.min(corridor.start.y, corridor.end.y);
        int maxY = Math.max(corridor.start.y, corridor.end.y);
        if (corridor.isHorizontal) {
            // 绘制水平走廊及墙壁
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY - 1; y <= minY + corridor.width; y++) {
                    if (illegal(x, y)) {
                        return;
                    }
                    if (y == minY || y == minY + corridor.width - 1) {
                        //world[x][y] = Tileset.FLOWER; // 中间部分是地板
                        world[x][y] = Tileset.FLOOR;
                    } else if (world[x][y] == null || world[x][y] == Tileset.NOTHING) {
                        world[x][y] = Tileset.WALL; // 周围是墙
                    }
                }
            }
            for (int y = minY - 1; y <= minY + corridor.width; y++) {
                if (world[minX - 1][y] == null || world[minX - 1][y] == Tileset.NOTHING) {
                    world[minX - 1][y] = Tileset.WALL;
                }
                if (world[maxX + 1][y] == null || world[maxX + 1][y] == Tileset.NOTHING) {
                    world[maxX + 1][y] = Tileset.WALL;
                }
            }
        } else {
            // 绘制垂直走廊及墙壁
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX - 1; x <= minX + corridor.width; x++) {
                    if (illegal(x, y)) {
                        return;
                    }
                    if (x == minX || x == minX + corridor.width - 1) {
                        //world[x][y] = Tileset.WATER; // 中间部分是地板
                        world[x][y] = Tileset.FLOOR;
                    } else if (world[x][y] == null || world[x][y] == Tileset.NOTHING) {
                        world[x][y] = Tileset.WALL; // 周围是墙
                    }
                }
            }
            for (int x = minX - 1; x <= minX + corridor.width; x++) {
                if (world[x][minY - 1] == null || world[x][minY - 1] == Tileset.NOTHING) {
                    world[x][minY - 1] = Tileset.WALL;
                }
                if (world[x][maxY + 1] == null || world[x][maxY + 1] == Tileset.NOTHING) {
                    world[x][maxY + 1] = Tileset.WALL;
                }
            }
        }
        //world[corridor.start.x][corridor.start.y] = Tileset.GRASS;
        //world[corridor.end.x][corridor.end.y] = Tileset.GRASS;
    }

    private static boolean illegal(int x, int y) {
        return x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT;
    }

    public static List<Corridor> generateCorridors(List<Room> rooms) {
        List<Room> unconnectedRooms = new ArrayList<>(rooms);
        List<Corridor> corridors = new ArrayList<>();
        Room currentRoom = unconnectedRooms.remove(0); // 选择第一个房间作为起始点

        while (!unconnectedRooms.isEmpty()) {
            Room nextRoom = unconnectedRooms.get(rand.nextInt(unconnectedRooms.size())); //在剩下房间里随机选一个

            Position start = getRandomPointInRoom(currentRoom);
            Position end = getRandomPointInRoom(nextRoom);
            int width = rand.nextInt(2) + 1; // 随机宽度 1-2

            Corridor horizontal = new Corridor(start, new Position(end.x, start.y), width);
            Corridor vertical = new Corridor(new Position(end.x, start.y), end, width);
            corridors.add(horizontal);
            corridors.add(vertical);

            currentRoom = nextRoom;
            unconnectedRooms.remove(currentRoom);
        }

        return corridors;
    }

    private static Position getRandomPointInRoom(Room room) {
        int x = rand.nextInt(room.bottomRight.x - room.topLeft.x) + room.topLeft.x;
        int y = rand.nextInt(room.topLeft.y - room.bottomRight.y) + room.bottomRight.y;
        return new Position(x, y);
    }

    public static void main(String[] args) {
        TETile[][] world;
        ter.initialize(WIDTH, HEIGHT);
        world = interactWithInputString("n1218s");
        ter.renderFrame(world);
    }

}
