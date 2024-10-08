package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class StopwatchDemo {
    /** Computes the nth Fibonacci number using a slow naive recursive strategy.*/
    private static int fib(int n) {
        if (n < 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        Stopwatch sw = new Stopwatch();
        int fib30 = fib(30);
        double timeInSeconds = sw.elapsedTime();
        System.out.println("Time taken to compute 30st fibonacci number: " + timeInSeconds + " seconds.");
        int fib10 = fib(10);
        double timeInSeconds2 = sw.elapsedTime();
        System.out.println("Time taken to compute 10st fibonacci number: " + timeInSeconds2 + " seconds.");
    }
}
