package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<>();
        AList<Double> T = new AList<>();
        AList<Integer> Ns = new AList<>();
        AList<Integer> oop = new AList<>();
        /*for (int i = 1000; i <= 128000; i *= 2) {
            N.addLast(i);
        }
        for (int i = 1000; i <= 128000; i *= 2) {
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j <= i; j++) {
                L.addLast(1);
            }
                T.addLast(sw.elapsedTime());
        }
        printTimingTable(N, T, N);*/
        int cnt = 0;
        Stopwatch sw = new Stopwatch();
        for (int i = 1; i <= 128000; i++) {
            N.addLast(i);
            if (N.size() == Math.pow(2, cnt) * 1000) {
                T.addLast(sw.elapsedTime());
                cnt++;
                Ns.addLast(N.size());
                oop.addLast(N.size());
            }
        }
        printTimingTable(Ns, T, oop);
    }
}