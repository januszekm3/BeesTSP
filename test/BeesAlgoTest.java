/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.FileNotFoundException;

public class BeesAlgoTest {
    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();

        BeesAlgo beesAlgo = new BeesAlgo(0.2, 100, 10, 4, 4, 2000000, 8, "C:\\Users\\Janusz\\IdeaProjects\\BeesTSP\\TSPLIB\\berlin52.tsp");

        beesAlgo.startAlgo();
        beesAlgo.setDistances();
        beesAlgo.generateFirstSolution();
        beesAlgo.init();
        beesAlgo.fullRandom();
        beesAlgo.run();
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (double)(end-start)/1000);
    }
}