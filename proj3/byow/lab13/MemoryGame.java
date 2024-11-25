package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
        //String tmp = generateRandomString(10);
        //drawFrame(tmp);
        //System.out.println(tmp);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = RandomUtils.uniform(rand, n) % 26;
            randomString.append(CHARACTERS[index]);
        }
        return randomString.toString();
        //return null;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) width / 2 , (double) height / 2, s);

        Font font2 = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(font2);
        Random random = new Random();
        int randomNum = random.nextInt(7);
        String encouragement = ENCOURAGEMENT[randomNum];
        double currentHeight = 38.5;
        if (playerTurn) {
            StdDraw.text((double) width / 2, currentHeight, "Type!");
        } else {
            StdDraw.text((double) width / 2, currentHeight, "Watch!");
        }
        StdDraw.textLeft(0, currentHeight, "Round:" + round);
        StdDraw.textRight(width, currentHeight, encouragement);

        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(String.valueOf(letters.charAt(i)));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
        playerTurn = true;
        drawFrame("");
    }

    private void clearTypedKey() {
        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
        }
    }

    private void checkEnterKey() {
        while (true) {
            if (StdDraw.isKeyPressed(10)) {
                break;
            }
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        clearTypedKey();
        String tmpString = "";
        for (int i = 0; i < n; ) {
            if (StdDraw.hasNextKeyTyped()) {
                i++;
                tmpString += StdDraw.nextKeyTyped();
                drawFrame(tmpString);
            }
        }
        checkEnterKey();
        playerTurn = false;
        return tmpString;
        //return null;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        //TODO: Establish Engine loop

        while (!gameOver) {
            round ++;
            String currentString = generateRandomString(round);
            drawFrame("");
            flashSequence(currentString);
            String typeString = solicitNCharsInput(round);
            System.out.println(typeString);
            if (!Objects.equals(typeString, currentString)) {
                gameOver = true;
                gameOver();
            }
        }

    }

    public void gameOver() {
        //Font font = new Font("Monaco", Font.BOLD, 30);
        //StdDraw.setFont(font);
        //StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text((double) width / 2 , (double) height / 2, "Game Over! You made it to round: " + round);
        StdDraw.show();
    }

}
