package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {


    private static final int totalCONCERT = 37;
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static GuitarString[] strings = new GuitarString[totalCONCERT];

    public static void getStart() {
        double currentCONCERT;
        for(int i = 0; i < totalCONCERT; i++) {
            currentCONCERT = 440.0 * Math.pow(2, (i - 24.0) / 12.0);
            strings[i] = new GuitarString(currentCONCERT);
        }
    }

    public static void main(String[] args) {
        getStart();
        while(true) {

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int currentIndex = keyboard.indexOf(key);
                if(currentIndex != -1) {
                    strings[currentIndex].pluck();
                }
            }

            double sample = 0.0;
            for(int i = 0; i < totalCONCERT; i++) {
                sample += strings[i].sample();
            }
            StdAudio.play(sample);

            for(int i = 0; i < totalCONCERT; i++) {
                strings[i].tic();
            }

        }

    }


}