/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BeesAlgo {

    public int beesSentToBetterPlaces = 4;      // liczba pszczol wysylanych do lepszych miejsc
    public int beesSentToOtherPlaces = 2;       // liczba pszczol wyslanych do pozostalych wybranych miejsc
    public int betterPlaces = 4;                // liczba lepszych miejsc
    public int chosenPlaces = 5;                // liczba wybranych miejsc
    public int iteration = 200;                 // maksymalna liczba iteracji
    public int scoutBees = 40;                  // liczba pszczol zwiadowcow
    public int lowerLimit = -10;
    public int upperLimit = 10;
    public int var = 3;
    public int citiesCounter;

    public double neighborhoodSize = 1;         // rozmiar sasiedztwa(%) [0,1]
    public double optimalPoint[];

    public boolean integerize = false;

    private int cities [][];
    private double distances [][];

    public BeesAlgo(){
        init();
    }

    public BeesAlgo(int no_of_var) {
        this.var = no_of_var;
        init();
    }

    public BeesAlgo(int var, int scoutBees, int chosenPlaces, int betterPlaces, double neighborhoodSize,
                    int beesSentToBetterPlaces, int beesSentToOtherPlaces, int iteration, int upperLimit,
                    int lowerLimit, boolean integerize){
        this.var = var;
        this.scoutBees = scoutBees;
        this.chosenPlaces = chosenPlaces;
        this.neighborhoodSize = neighborhoodSize;
        this.beesSentToBetterPlaces = beesSentToBetterPlaces;
        this.beesSentToOtherPlaces = beesSentToOtherPlaces;
        this.iteration = iteration;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
        this.integerize = integerize;
        init();
    }

    public void startAlgo() throws FileNotFoundException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        LineNumberReader lnr = null;

        try {
            fis = new FileInputStream("E:\\Semestr VI\\Badania operacyjne\\Project beesAlgo\\TSPLIB\\berlin52.tsp");
            lnr = new LineNumberReader(new FileReader("E:\\Semestr VI\\Badania operacyjne\\Project beesAlgo\\TSPLIB\\berlin52.tsp"));
            lnr.skip(Long.MAX_VALUE);
            reader = new BufferedReader(new InputStreamReader(fis));
            //System.out.println(lnr.getLineNumber()+1);
            citiesCounter = lnr.getLineNumber()+1;
            cities = new int[2][citiesCounter];
            distances = new double[citiesCounter][citiesCounter];
            String line = reader.readLine();
            int i = 0;
            while(line != null){
                String[] parts = line.split(" ");
                cities[0][i] = Integer.parseInt(parts[1]);      //x
                cities[1][i++] = Integer.parseInt(parts[2]);    //y
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                reader.close();
                fis.close();
                lnr.close();
            } catch (IOException ex) {
            }
        }
    }

    public void setDistances () {
        for (int i = 0; i < citiesCounter; i++)
            for (int j = 0; j < citiesCounter; j++) {
                distances[i][j] = distances[j][i] = Math.sqrt((cities[0][i] - cities[0][j]) * (cities[0][i] - cities[0][j])
                        + (cities[1][i] - cities[1][j]) * (cities[1][i] - cities[1][j]));
            }
    }

    public void generateFirstSolution () {
        double bestSolution, currentBestSolution, localBestSolution;
        int index;
        boolean visitedCities[] = new boolean[citiesCounter];
        boolean flag;
        bestSolution = Double.MAX_VALUE;

        //kazde miasto moze byc miastem poczatkowym
        for (int i = 0; i < citiesCounter; i++) {
            index = i;
            flag = true;
            currentBestSolution = 0;
            for (int j = 0; j < citiesCounter; j++) visitedCities[j] = false;
            visitedCities[i] = true;

            //zaczynamy z poczatkowego miasta, wykonuj dopoki nie przejdziemy po wsztkich
            while (flag){
                localBestSolution = Double.MAX_VALUE;
                flag = false;   //zeby sie nie zapetlic

                //dla danego miasta przechodze po wszystkich sasiadach
                for (int k = 0; k < citiesCounter; k++) {

                    //kontrola - czy nie porownujemy miasta z samym soba; czy dystans lepszy od lokalnego;
                    //czy nie bylismy juz w tym miescie
                    if ((distances[index][k] > 0) && (distances[index][k] < localBestSolution) && (visitedCities[k] == false)) {
                        localBestSolution = distances[index][k];
                        index = k;
                    }
                }

                visitedCities[index] = true;
                currentBestSolution += localBestSolution;

                //kontrola - czy przeszlismy juz po wszystich miastach
                for (int j = 0; j < citiesCounter; j++) if (visitedCities[j] == false) flag = true;
            }

            //najlepsze rozwiazanie z danego miasta poczatkowego
            //dodajemy odleglosc od ostatniego miasta do poczatkowego
            currentBestSolution += distances[i][index];

            //najlepsze rozwiazanie z dowolnego miasta
            if (currentBestSolution < bestSolution) bestSolution = currentBestSolution;
        }
        System.out.println("best " + bestSolution);
    }

    private void init(){
        this.optimalPoint = new double[var + 1];
        for (int j = 0; j < var + 1; j++) optimalPoint[j] = 1;
    }

    public double function(double[] x) {
        double result = 0;
        for (int j = 0; j < var; j++) result = result + (x[j]-1) * (x[j]-1);
        return (-result);
    }

    public double random(double high, double low) {
        Random generator = new Random();
        if(integerize) return (generator.nextInt((int) high - (int) low) + (int) low);
        double range = high-low+1;
        double fraction = range*generator.nextDouble();
        return(fraction+low);
    }

    public void sort(double searchPoints[][]){
        Arrays.sort(searchPoints, new Comparator() {

            public int compare(double[] o1, double[] o2) {
                if (o1[var] < o2[var]) {
                    return 1; // -1 for descending order
                } else if (o1[var] > o2[var]) {
                    return -1; // 1 for descending order
                } else {
                    return 0;
                }
            }

            public int compare(Object o1, Object o2) {
                double[] O1 = (double[]) o1;
                double[] O2 = (double[]) o2;
                if (O1[var] < O2[var]) {
                    return 1; // -1 for descending order
                } else if (O1[var] > O2[var]) {
                    return -1; // 1 for descending order
                } else {
                    return 0;
                }
            }
        });
    }

    public void run() {
        double searchPoints[][] = new double[scoutBees][var + 1];

        for (int i = 0; i < scoutBees; i++) {
            double[] tmpX = new double[var];
            for (int j = 0; j < var; j++) {
                searchPoints[i][j] = this.random(upperLimit, lowerLimit);
                tmpX[j] = searchPoints[i][j];
            }
            double tmp = function(tmpX);
            searchPoints[i][var] = tmp;
        }

        for (int itr = 1; itr <= iteration; itr++) {
            sort(searchPoints);

            for (int i = 0; i < betterPlaces; i++) {
                double[] x = new double[var];
                double[] x_best = new double[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }
                double f = searchPoints[i][var];
                double f_best = f;

                for (int indx = 0; indx < beesSentToBetterPlaces; indx++) {
                    double[] x_elite = new double[var];
                    for (int j = 0; j < var; j++)
                        x_elite[j] = this.random(x[j] + neighborhoodSize, x[j] - neighborhoodSize);
                    double f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) x_best[j] = x_elite[j];
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) searchPoints[i][j] = x_best[j];
                searchPoints[i][var] = f_best;
            }

            for (int i = betterPlaces; i < chosenPlaces; i++) {
                double[] x = new double[var];
                double[] x_best = new double[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }

                double f = searchPoints[i][var];
                double f_best = f;

                for (int indx = 0; indx < beesSentToOtherPlaces; indx++) {
                    double[] x_elite = new double[var];
                    for (int j = 0; j < var; j++) x_elite[j] = this.random(x[j] + neighborhoodSize, x[j] - neighborhoodSize);
                    double f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) x_best[j] = x_elite[j];
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) searchPoints[i][j] = x_best[j];
                searchPoints[i][var] = f_best;
            }

            for (int i = chosenPlaces; i < scoutBees; i++) {
                double[] tmpX = new double[var];
                for (int j = 0; j < var; j++) {
                    searchPoints[i][j] = this.random(upperLimit, lowerLimit);
                    tmpX[j] = searchPoints[i][j];
                }
                searchPoints[i][var] = function(tmpX);
            }
        }

        sort(searchPoints);

        for (int j = 0; j < var; j++) optimalPoint[j] = searchPoints[0][j];
        optimalPoint[var] = -searchPoints[0][var];
    }

    public double optimalValue(){
        return(optimalPoint[var]);
    }

    public double[] optimalPoint(){
        double[] result = new double[var];
        for (int j = 0; j < var; j++) result[j]=optimalPoint[j];
        return(result);
    }
}