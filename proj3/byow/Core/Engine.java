package byow.Core;

import byow.Networking.BYOWServer;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    static Position user;
    static Position door;
    static Random rand;
    static String inputString;
    private static Clip BGMClip;
    private static Clip MenuClip;
    static private final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public static void interactWithKeyboard() {

        drawFrame();
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        KeyBoardInput inputSource = new KeyBoardInput();
        MenuClip = SoundPlayer.loopMusic("menu.wav");
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'n' || c == 'N') {
                String seedString = inputSeed();
                inputString = 'n' + seedString;
                world = interactWithInputString(inputString + 's' + 'w');
                break;
            } else if (c == 'l' || c == 'L') {  //加载预存的世界
                File stagedInputString = join(CWD, "inputString.txt");
                inputString = readContentsAsString(stagedInputString);
                world = interactWithInputString(inputString);
                break;
            } else if (c == 'q' || c == 'Q') {
                System.exit(0);
            }
        }

        ter.initialize(WIDTH, HEIGHT);

        playGame(world, user, door);
        System.exit(0);

    }

    private static void playGame(TETile[][] world, Position user, Position door) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) { // 优先处理字符输入
                char c = StdDraw.nextKeyTyped();
                /*if (c != ':') {
                    inputString += c;
                }*/
                char lastInput = inputString.charAt(inputString.length() - 1);
                if (lastInput == ':' && (c == 'q' || c == 'Q')) {
                    String newString = inputString.substring(0, inputString.length() - 1);  //去掉最后的冒号
                    writeContents(join(CWD, "inputString.txt"), newString);
                    System.exit(0);
                }
                inputString += c;
                move(world, c);
            } else if (StdDraw.isKeyPressed(27)) { // 检查 ESC 键
                break; // 退出循环
            }
            TETile[][] copyWorld = new TETile[WIDTH][HEIGHT];
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    copyWorld[i][j] = world[i][j];
                }
            }
            FogMode.mistMode(copyWorld, 5);
            if (FogMode.getFogStatus()) {
                if (FogMode.checkDoorTimer()) {
                    copyWorld[door.x][door.y] = Tileset.LOCKED_DOOR;
                }
                ter.renderFrame(copyWorld);
            }
            else {
                ter.renderFrame(world);
            }

            checkWin();
        }
    }

    public static void getMouse(TETile[][] world) {
        int tileX = (int) StdDraw.mouseX();
        int tileY = (int) StdDraw.mouseY();
        String currentTileTexture = "";
        if (!illegal(tileX, tileY)) {
            currentTileTexture = world[tileX][tileY].description();
        }
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, "Material: " + currentTileTexture);
    }

    private static void checkWin() {
        if (user.equal(door)) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            Font font = new Font("Monaco", Font.BOLD, 40);
            StdDraw.setFont(font);
            StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2, "Congratulations! You win The Game!");
            StdDraw.show();
            SoundPlayer.stopMusic(BGMClip);
            SoundPlayer.playSound("win.wav");
            StdDraw.pause(3000);
            System.exit(0);
        }
    }

    private static void move(TETile[][] world, char c) {
        switch (c) {
            case 'e':
            case 'E':
                FogMode.peekDoor();
                break;
            case 'r':
            case 'R':
                FogMode.toggleFogMode();
                break;
            case 'w':
            case 'W':
                if (movable(world, user, 0, 1)) {
                    world[user.x][user.y] = Tileset.FLOOR;
                    user.y += 1;
                    world[user.x][user.y] = Tileset.AVATAR;
                }
                break;
            case 's':
            case 'S':
                if (movable(world, user, 0, -1)) {
                    world[user.x][user.y] = Tileset.FLOOR;
                    user.y -= 1;
                    world[user.x][user.y] = Tileset.AVATAR;
                }
                break;
            case 'd':
            case 'D':
                if (movable(world, user, 1, 0)) {
                    world[user.x][user.y] = Tileset.FLOOR;
                    user.x += 1;
                    world[user.x][user.y] = Tileset.AVATAR;
                }
                break;
            case 'a':
            case 'A':
                if (movable(world, user, -1, 0)) {
                    world[user.x][user.y] = Tileset.FLOOR;
                    user.x -= 1;
                    world[user.x][user.y] = Tileset.AVATAR;
                }
                break;
        }
    }

    /*private static void quitAndSave() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char next = StdDraw.nextKeyTyped();
                if (next == 'q' || next == 'Q') {
                    writeContents(join(CWD, "inputString.txt"), inputString);
                    System.exit(0);
                }
            }
        }
    }*/

    private static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    private static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    private static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    private static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    private static boolean movable(TETile[][] world, Position p, int dx, int dy) {
        int newX = p.x + dx;
        int newY = p.y + dy;
        if (illegal(newX, newY) || world[newX][newY] == Tileset.WALL) {
            return false;
        }
        return true;
    }

    private static Position randomObject(TETile[][] world, TETile replacedTexture,TETile texture) {

        Position newPosition;
        do {
            int randomX = RandomUtils.uniform(rand, 1, WIDTH - 1);
            int randomY = RandomUtils.uniform(rand, 1, HEIGHT - 1);
            newPosition = new Position(randomX, randomY);
            if (world[newPosition.x][newPosition.y] == replacedTexture && isReachable(world, newPosition)) {
                break;
            }
        //} while (world[newPosition.x][newPosition.y] != replacedTexture || !isReachable(world, newPosition));
        } while (true);

        world[newPosition.x][newPosition.y] = texture;
        return newPosition;
    }

    private static boolean isReachable(TETile[][] world, Position position) {
        int x = position.x;
        int y = position.y;
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        for (int i = 0; i < 4; i++) {
            if (world[x + dx[i]][y + dy[i]] == Tileset.FLOOR) {
                return true;
            }
        }
        return false;
    }

    private static String inputSeed() {
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        String tmpString = "";
        drawInTheCenter("");
        while (true) {
            if (StdDraw.isKeyPressed('s') || StdDraw.isKeyPressed('S')) { // 检查 s/S 键
                while (StdDraw.hasNextKeyTyped()) {
                    StdDraw.nextKeyTyped();
                }
                break; // 退出循环
            } else if (StdDraw.hasNextKeyTyped()) { // 优先处理字符输入
                char c = StdDraw.nextKeyTyped();
                tmpString += c;
                drawInTheCenter(tmpString);
            }
        }

        return tmpString;
    }

    private static void drawInTheCenter(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2, "Enter Seed:" + s);
    }

    private static void drawFrame() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        Font headFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(headFont);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 + 8, "CS61B: THE GAME");

        Font font = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(font);
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
        SoundPlayer.stopMusic(MenuClip);
        BGMClip = SoundPlayer.loopMusic("background.wav");
        long seed = 0;
        String regex = "(?i)n(\\d+)s(.*)"; //正则表达式,忽略大小写
        Pattern pattern = Pattern.compile(regex); // 1. 创建 Pattern 对象
        Matcher matcher = pattern.matcher(input); // 2. 创建 Matcher 对象
        String seedString = "";
        String movement = "";
        if (matcher.find()) {  // find() 方法尝试查找第一个匹配项
            seedString = matcher.group(1);
            seed = Long.parseLong(seedString);
            if (matcher.groupCount() >= 2) {
                movement = matcher.group(2);
            }
        }
        /*if (seedString.isEmpty()) {
            System.exit(0);
        }*/

        rand = new Random(seed);
        List<Room> rooms = new ArrayList<>();
        int roomNum = RandomUtils.uniform(rand, 5, 20);
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

        user = randomObject(finalWorldFrame, Tileset.FLOOR, Tileset.AVATAR);
        door = randomObject(finalWorldFrame, Tileset.WALL, Tileset.LOCKED_DOOR);
        for (int i = 0; i < movement.length(); i++) {
            char c = movement.charAt(i); // 使用 charAt 方法获取每个字符
            move(finalWorldFrame, c);
        }

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
        if (corridor.isHorizontal) {    // 绘制水平走廊及墙壁
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY - 1; y <= minY + corridor.width; y++) {
                    if (illegal(x, y)) {
                        return;
                    }
                    if (y == minY || y == minY + corridor.width - 1) {
                        world[x][y] = Tileset.FLOOR;    // 中间部分是地板
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
        } else {    // 绘制垂直走廊及墙壁
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX - 1; x <= minX + corridor.width; x++) {
                    if (illegal(x, y)) {
                        return;
                    }
                    if (x == minX || x == minX + corridor.width - 1) {
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
    }

    public static boolean illegal(int x, int y) {
        return x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT;
    }

    public static List<Corridor> generateCorridors(List<Room> rooms) {
        List<Room> unconnectedRooms = new ArrayList<>(rooms);
        List<Corridor> corridors = new ArrayList<>();
        Room currentRoom = null;
        if (!unconnectedRooms.isEmpty()) {
            currentRoom = unconnectedRooms.remove(0); // 选择第一个房间作为起始点
        }

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

    public void interactWithRemoteClient(String portNumber) throws IOException {

        BYOWServer server = new BYOWServer(Integer.parseInt(portNumber));
        drawFrameRemote(server);
        TETile[][] world;
        //KeyBoardInput inputSource = new KeyBoardInput();
        MenuClip = SoundPlayer.loopMusic("menu.wav");
        while (true) {
            //char c = inputSource.getNextKey();
            char c = getNextRemoteKey(server);
            if (c == 'n' || c == 'N') {
                String seedString = inputSeed();
                inputString = 'n' + seedString;
                world = interactWithInputString(inputString + 's' + 'w');
                break;
            } else if (c == 'l' || c == 'L') {  //加载预存的世界
                File stagedInputString = join(CWD, "inputString.txt");
                inputString = readContentsAsString(stagedInputString);
                world = interactWithInputString(inputString);
                break;
            } else if (c == 'q' || c == 'Q') {
                //System.exit(0);
                server.stopConnection();
            }
        }

        //ter.initialize(WIDTH, HEIGHT);

        playGameReote(world, user, door, server);
        System.exit(0);

    }

    private static void drawFrameRemote(BYOWServer server) {
        server.sendCanvasConfig(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        Font headFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(headFont);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 + 8, "CS61B: THE GAME");
        server.sendCanvas();

        Font font = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(font);
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2, "New Game (N)");
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 - 4, "Quit (Q)");
        server.sendCanvas();
    }

    private static void playGameReote(TETile[][] world, Position user, Position door, BYOWServer server) {
        while (true) {
            if (server.clientHasKeyTyped()) { // 优先处理字符输入
                char c = server.clientNextKeyTyped();
                char lastInput = inputString.charAt(inputString.length() - 1);
                if (lastInput == ':' && (c == 'q' || c == 'Q')) {
                    String newString = inputString.substring(0, inputString.length() - 1);  //去掉最后的冒号
                    writeContents(join(CWD, "inputString.txt"), newString);
                    //System.exit(0);
                    server.stopConnection();
                }
                inputString += c;
                move(world, c);
            }
            TETile[][] copyWorld = new TETile[WIDTH][HEIGHT];
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    copyWorld[i][j] = world[i][j];
                }
            }
            FogMode.mistMode(copyWorld, 5);
            if (FogMode.getFogStatus()) {
                if (FogMode.checkDoorTimer()) {
                    copyWorld[door.x][door.y] = Tileset.LOCKED_DOOR;
                }
                ter.renderFrame(copyWorld);
            }
            else {
                ter.renderFrame(world);
            }

            checkWin();
        }
    }

    private static void renderRemoteFrame(TETile[][] world, BYOWServer server) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x, y);
            }
        }
        Engine.getMouse(world);
        //StdDraw.show();
        server.sendCanvas();
    }

    private static char getNextRemoteKey(BYOWServer server) {
        while (true) {
            if (server.clientHasKeyTyped()) {
                return Character.toUpperCase(server.clientNextKeyTyped());
            }
        }
    }

    public static void main(String[] args) {
        /*TETile[][] world;
        ter.initialize(WIDTH, HEIGHT);
        world = interactWithInputString("n1218s");
        ter.renderFrame(world);*/
        interactWithKeyboard();
    }

}
