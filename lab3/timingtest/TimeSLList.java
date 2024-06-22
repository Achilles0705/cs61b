package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        SLList<Integer> N = new SLList<>();
        AList<Double> T = new AList<>();
        AList<Integer> oop = new AList<>();
        AList<Integer> Ns = new AList<>();
        /*for (int i = 1000; i <= 128000; i *= 2) {
            N.addLast(i);
            oop.addLast(10000);
        }
        int j = 0;
        for (int i = 1000; i <= 128000; i *= 2) {
            for (; j < i; j++) {
                L.addFirst(1);
            }
            Stopwatch sw = new Stopwatch();
            L.getLast();
            T.addLast(sw.elapsedTime());
        }
        printTimingTable(N, T, oop);*/
        int cnt = 0;
        //Stopwatch sw = new Stopwatch();
        for (int i = 1; i <= 128000; i++) {
            N.addLast(i);
            if (N.size() == Math.pow(2, cnt) * 1000) {
                Stopwatch sw = new Stopwatch();
                for(int j = 0; j < 10000; j++){
                    N.getLast();
                }
                T.addLast(sw.elapsedTime());
                cnt++;
                Ns.addLast(i);
                oop.addLast(10000);
            }
        }
        printTimingTable(Ns, T, oop);
    }
}
