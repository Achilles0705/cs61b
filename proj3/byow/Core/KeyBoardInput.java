package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

public class KeyBoardInput {

    private static final boolean PRINT_TYPED_KEYS = false;
    public KeyBoardInput() {
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2, "New Game (N)");
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 - 2, "Load Game (L)");
        StdDraw.text((double) Engine.WIDTH / 2, (double) Engine.HEIGHT / 2 - 4, "Quit (Q)");
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }

}
