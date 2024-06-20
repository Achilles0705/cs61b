package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class DrumsHero {


    private static final int TOTAL_CONCERT = 37;
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static Drums[] strings = new Drums[TOTAL_CONCERT];

    public static void getStart() {
        double currentCONCERT;
        double baseFrequency = 110.0;
        for (int i = 0; i < TOTAL_CONCERT; i++) {
            currentCONCERT = baseFrequency * Math.pow(2, (i - 18.0) / 12.0);
            strings[i] = new Drums(currentCONCERT);
        }
    }

    public static void main(String[] args) {
        getStart();
        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int currentIndex = KEYBOARD.indexOf(key);
                if (currentIndex != -1) {
                    strings[currentIndex].pluck();
                }
            }

            double sample = 0.0;
            for (int i = 0; i < TOTAL_CONCERT; i++) {
                sample += strings[i].sample();
            }
            StdAudio.play(sample);

            for (int i = 0; i < TOTAL_CONCERT; i++) {
                strings[i].tic();
            }

        }

    }


}
