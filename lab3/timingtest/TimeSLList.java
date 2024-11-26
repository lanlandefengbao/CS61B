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
        AList<Integer> Ns = new AList<>();
        Integer[] n = new Integer[] {100, 1000, 3000, 10000, 60000, 120000, 1000000};
        Ns.fillAList(n);

        AList<Integer> opCounts = new AList<Integer>();
        for (int i = 0; i < Ns.size(); i++) {
            opCounts.addLast(100);
        }

        AList<Double> times = new AList<>();
        for (int i = 0; i < Ns.size(); i++) {
            Integer[] items = new Integer[Ns.get(i)];
            SLList<Integer> x = new SLList<>(items);
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < opCounts.get(i); j++) {
                x.getLast();
            }
            double timeSpent = sw.elapsedTime();
            times.addLast(timeSpent);
        }

        printTimingTable(Ns,times,opCounts);
    }

}
