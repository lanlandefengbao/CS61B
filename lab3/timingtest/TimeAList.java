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
        AList<Integer> Ns = new AList<Integer>();
        Integer[] n = new Integer[] {100, 1000, 3000, 10000, 30000, 60000, 120000};
        Ns.fillAList(n);

        AList<Double> times = new AList<Double>();
        double[] t = new double[Ns.size()];
        for (int i = 0; i < Ns.size(); i++) {
            AList<Integer> x = new AList<Integer>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < Ns.get(i); j++) {
                x.addLast(1);
            }
            double timeSpent = sw.elapsedTime();
            times.addLast(timeSpent);
        }

        AList<Integer> opCounts = Ns;

        printTimingTable(Ns,times, opCounts);
    }
}
