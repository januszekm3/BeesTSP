/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.FileNotFoundException;

public class BeesAlgoTest {
    public static void main(String[] args) throws FileNotFoundException {
        BeesAlgo beesAlgo = new BeesAlgo(false, 1, 4, 2, 4, 5, 200, -10, 2000000, 10);

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