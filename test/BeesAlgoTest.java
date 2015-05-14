/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.FileNotFoundException;

public class BeesAlgoTest {
    public static void main(String[] args) throws FileNotFoundException {
        BeesAlgo beesAlgo = new BeesAlgo(false, 0.2, 100000, 10000, 4, 4, 200, 8);

        beesAlgo.startAlgo();
        beesAlgo.setDistances();
        beesAlgo.generateFirstSolution();
        beesAlgo.init();
        beesAlgo.fullRandom();
        beesAlgo.run();

        /*
        double[] optimalPoint=beesAlgo.optimalPoint();
        System.out.print("Point (");
        for (int j = 0; j < beesAlgo.var; j++) {
            System.out.print(optimalPoint[j]);
            if(j!=beesAlgo.var-1) System.out.print(",");
        }
        System.out.println(")");
        System.out.println("Value " + beesAlgo.optimalValue());
        */
    }
}