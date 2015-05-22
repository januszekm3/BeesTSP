/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeesAlgoTest {
    public static void main(String[] args) throws IOException {
        BeesGUI.run();

        /*  OLD VERSION
        String pathToDir = "C:\\\\Users\\\\Janusz\\\\IdeaProjects\\\\BeesTSP\\\\TSPLIB\\\\";

        List<String> files = new ArrayList<>();
        files.add("a280");
        files.add("att532");
        files.add("berlin52");
        files.add("eil51");
        files.add("eil76");
        files.add("kroA100");
        files.add("kroB200");
        files.add("kroE100");
        files.add("lin105");
        files.add("pr76");
        files.add("pr264");
        files.add("rat195");
        files.add("st70");
        files.add("ts225");

        BeesAlgo beesAlgo;

        for(String file : files){
            long start = System.currentTimeMillis();
            beesAlgo = new BeesAlgo(
                    0.5,        //neighborhoodSize
                    10000,      //beesSentToBetterPlaces
                    1000,       //beesSentToOtherPlaces
                    5,          //betterPlaces
                    5,          //chosenPlaces
                    3000,       //iteration
                    10,          //scoutBees
                    pathToDir + file + ".tsp");

            System.out.println("\nInstance: " + file);
            beesAlgo.startAlgo();
            beesAlgo.setDistances();
            beesAlgo.generateFirstSolution();
            beesAlgo.init();
            beesAlgo.fullRandom();
            beesAlgo.run();
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (double) (end - start) / 1000);
        }*/
    }
}