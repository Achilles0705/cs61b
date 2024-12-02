package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class Room {

    public Position topLeft;
    public Position bottomRight;

    public Room(Position topLeft, Position bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Room(Position topLeft, int width, int height) {
        this.topLeft = topLeft;
        this.bottomRight = new Position(0,0);
        this.bottomRight.x = topLeft.x + width;
        this.bottomRight.y = topLeft.y - height;
    }

    public static boolean isOverlapped(List<Room> rooms, Room currentRoom) {
        List<Room> roomsCopy = new LinkedList<>(rooms);
        while (!roomsCopy.isEmpty()) {
            Room tmpRoom = roomsCopy.removeFirst();
            if (isOverlappedInTwo(tmpRoom, currentRoom)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOverlappedInTwo(Room room1, Room room2) {
        return !(room2.topLeft.x - 1 > room1.bottomRight.x // room2 在 room1 的右侧
                || room2.bottomRight.x + 1 < room1.topLeft.x // room2 在 room1 的左侧
                || room2.topLeft.y + 1 < room1.bottomRight.y // room2 在 room1 的下方
                || room2.bottomRight.y - 1 > room1.topLeft.y); // room2 在 room1 的上方
    }


    public static void fillInRooms(TETile[][] world, List<Room> rooms) {
        List<Room> roomsCopy = new LinkedList<>(rooms);
        while (!roomsCopy.isEmpty()) {
            Room currentRoom = roomsCopy.removeFirst();
            fillInOneRoom(world, currentRoom);
        }
    }

    private static void fillInOneRoom(TETile[][] world, Room currentRoom) {
        for (int i = currentRoom.topLeft.x; i <= currentRoom.bottomRight.x; i++) {
            for (int j = currentRoom.bottomRight.y; j <= currentRoom.topLeft.y; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = currentRoom.topLeft.x - 1; i <= currentRoom.bottomRight.x + 1; i++) {
            world[i][currentRoom.bottomRight.y - 1] = Tileset.WALL;
            world[i][currentRoom.topLeft.y + 1] = Tileset.WALL;
        }
        for (int j = currentRoom.bottomRight.y - 1; j <= currentRoom.topLeft.y + 1; j++) {
            world[currentRoom.topLeft.x - 1][j] = Tileset.WALL;
            world[currentRoom.bottomRight.x + 1][j] = Tileset.WALL;
        }
    }

    public static void connectRoom(int index, Room currentRoom, List<Room> rooms) {
        List<Room> copyRoomList = rooms;
        while (!copyRoomList.isEmpty() && index != 0) {
            Room tmpRoom = copyRoomList.removeFirst();
            index--;
        }
    }

}
