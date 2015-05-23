/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.*;
import java.util.Random;

public class BeesAlgo {

    private int beesSentToBetterPlaces;      // liczba pszczol wysylanych do lepszych miejsc
    private int beesSentToOtherPlaces;       // liczba pszczol wyslanych do pozostalych wybranych miejsc
    private int betterPlaces;                // liczba lepszych miejsc
    private int chosenPlaces;                // liczba wybranych miejsc
    private int iterations;                   // maksymalna liczba iteracji
    private int scoutBees;                   // liczba pszczol zwiadowcow
    private int var;
    private int citiesCounter;

    private double worstResult, averageResult, bestResult;

    private int cities [][];

    private double neighborhoodSize;         // rozmiar sasiedztwa(%) [0,1]
    private double distances [][];

    private String pathToFile;

    public BeesAlgo(double neighborhoodSize, int beesSentToBetterPlaces, int beesSentToOtherPlaces,
                    int betterPlaces, int chosenPlaces, int iterations, int scoutBees, String pathToFile){
        this.neighborhoodSize = neighborhoodSize;
        this.beesSentToBetterPlaces = beesSentToBetterPlaces;
        this.beesSentToOtherPlaces = beesSentToOtherPlaces;
        this.betterPlaces = betterPlaces;
        this.chosenPlaces = chosenPlaces;
        this.iterations = iterations;
        this.scoutBees = scoutBees;
        this.pathToFile = pathToFile;
    }

    public void startAlgo() throws FileNotFoundException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        LineNumberReader lnr = null;

        try {
            fis = new FileInputStream(pathToFile);
            lnr = new LineNumberReader(new FileReader(pathToFile));
            lnr.skip(Long.MAX_VALUE);
            reader = new BufferedReader(new InputStreamReader(fis));
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
                fis.close();
                lnr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDistances () {
        for (int i = 0; i < citiesCounter; i++)
            for (int j = 0; j < citiesCounter; j++)
                distances[i][j] = distances[j][i] = Math.sqrt((cities[0][i] - cities[0][j]) * (cities[0][i] - cities[0][j])
                        + (cities[1][i] - cities[1][j]) * (cities[1][i] - cities[1][j]));
    }

    public double generateFirstSolution () {
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
                //kontrola - czy nie porownujemy miasta z samym soba; czy dystans lepszy od lokalnego;
                //czy nie bylismy juz w tym miescie
                for (int k = 0; k < citiesCounter; k++)
                    if ((distances[index][k] > 0) && (distances[index][k] < localBestSolution) && (!visitedCities[k])) {
                        localBestSolution = distances[index][k];
                        index = k;
                        break;
                    }

                visitedCities[index] = true;
                currentBestSolution += localBestSolution;

                //kontrola - czy przeszlismy juz po wszystich miastach
                for (int j = 0; j < citiesCounter; j++) if (!visitedCities[j]) flag = true;
            }

            //najlepsze rozwiazanie z danego miasta poczatkowego
            //dodajemy odleglosc od ostatniego miasta do poczatkowego
            currentBestSolution += distances[i][index];

            //najlepsze rozwiazanie z dowolnego miasta
            if (currentBestSolution < bestSolution) bestSolution = currentBestSolution;
        }
        System.out.println("Nearest neighbor heuristics best result: " + bestSolution + "\n");
        return round(bestSolution, 2);
    }

    public int[] fullRandom(){                                           //pszczoly zwiadowcy
        var = citiesCounter;
        boolean visitedCities[] = new boolean[citiesCounter];
        int citiesOrder[] = new int[citiesCounter];
        for(int i = 0; i < citiesCounter; i++) visitedCities[i] = false;
        Random gen = new Random();
        for(int i = 0; i < citiesCounter; i++){
            int tmp = gen.nextInt(citiesCounter);
            while(visitedCities[tmp]) tmp = gen.nextInt(citiesCounter);
            visitedCities[tmp] = true;
            citiesOrder[i] = tmp;
        }
        return citiesOrder;
    }

    public void run() {
        int[][] searchPoints = new int[scoutBees][var + 1];

        //tablica wynikow funkcji celu z randomowych pszczol zwiadowcow
        double [] beeScoutResults = new double[scoutBees];
        for (int i = 0; i < scoutBees; i++) {
            int[] tmpX = this.fullRandom();
            System.arraycopy(tmpX, 0, searchPoints[i], 0, var);
            double tmp = targetFunction(tmpX);
            beeScoutResults[i] = tmp;   //zapisanie wyniku
        }

        sort2tables(beeScoutResults, searchPoints);

        System.out.println("Random, sorted results:");
        for(double print: beeScoutResults) System.out.println(print);

        //przeliczenie procentowego sasiedztwa na sasiedztwo w formie liczby miast
        int neigh = (int) (neighborhoodSize * citiesCounter);

        for(int iter = 0; iter < iterations; iter++) {                      //liczba iteracji
            sort2tables(beeScoutResults, searchPoints);                     //sort po dzialaniu
            for(int btrplcs = 0; btrplcs < betterPlaces; btrplcs++)
                for (int j = 0; j < beesSentToBetterPlaces; j++) {
                    int[] operativeVector = new int[citiesCounter];         //vector do operowania swapami
                    int[] beforeSwapVector = new int[citiesCounter];        //vector do zapamietania stanu przed swapem
                    System.arraycopy(searchPoints[btrplcs], 0, operativeVector, 0, citiesCounter);
                    System.arraycopy(operativeVector, 0, beforeSwapVector, 0, citiesCounter);
                    int randIndex = new Random().nextInt(citiesCounter - 2);  //wylosowane miasto, centrum sasiedztwa

                    //losowanie odleglosci pomiedzy  miastami do swapa (mniejszy niz rozmiar sasiedztwa)
                    int neighJump = (new Random().nextInt(2 * neigh)) - neigh;
                    int swapIndex = randIndex + neighJump;                  //obliczenie odleglosci pomiedzy miastami do swapa

                    //zabezpieczenie przed wykroczeniem poza indeks tablicy
                    if (swapIndex < 0)
                        swapIndex = 0;
                    else if (swapIndex > citiesCounter - 1)
                        swapIndex = citiesCounter - 1;
                    swapIntTable(randIndex, swapIndex, operativeVector);    //swap pomiedzy miastami

                    double newValue = targetFunction(operativeVector);      //nowa funkcja celu
                    double oldValue = targetFunction(beforeSwapVector);     //stara funckcja celu

                    //sprawdzanie czy sie poprawilo
                    if (newValue < oldValue)
                        for (int i = 0; i < citiesCounter; i++) {           //jak tak to zapisujemy nowy wynik
                            searchPoints[btrplcs][i] = operativeVector[i];
                            beeScoutResults[btrplcs] = newValue;            //zapis do beeScoutResults nowego wyniku
                        }
                }

            for(int chsplcs = betterPlaces; chsplcs < chosenPlaces + betterPlaces; chsplcs++)
                for (int j = 0; j < beesSentToOtherPlaces; j++) {
                    int[] operativeVector = new int[citiesCounter];         //vector do operowania swapami
                    int[] beforeSwapVector = new int[citiesCounter];        //vector do zapamietania stanu przed swapem
                    System.arraycopy(searchPoints[chsplcs], 0, operativeVector, 0, citiesCounter);
                    System.arraycopy(operativeVector, 0, beforeSwapVector, 0, citiesCounter);
                    int randIndex = new Random().nextInt(citiesCounter - 2);  //wylosowane miasto, centrum sasiedztwa

                    //losowanie odleglosci pomiedzy  miastami do swapa (mniejszy niz rozmiar sasiedztwa)
                    int neighJump = (new Random().nextInt(2 * neigh)) - neigh;
                    int swapIndex = randIndex + neighJump;                  //obliczenie odleglosci pomiedzy miastami do swapa

                    //zabezpieczenie przed wykroczeniem poza indeks tablicy
                    if (swapIndex < 0)
                        swapIndex = 0;
                    else if (swapIndex > citiesCounter - 1)
                        swapIndex = citiesCounter - 1;
                    swapIntTable(randIndex, swapIndex, operativeVector);    //swap pomiedzy miastami

                    double newValue = targetFunction(operativeVector);      //nowa funkcja celu
                    double oldValue = targetFunction(beforeSwapVector);     //stara funckcja celu

                    //sprawdzanie czy sie poprawilo
                    if (newValue < oldValue)
                        for (int i = 0; i < citiesCounter; i++) {           //jak tak to zapisujemy nowy wynik
                            searchPoints[chsplcs][i] = operativeVector[i];
                            beeScoutResults[chsplcs] = newValue;            //zapis do beeScoutResults nowego wyniku
                        }
                }
        }

        System.out.println("\nFinal, sorted results:");
        for (double print: beeScoutResults) System.out.println(print);

        worstResult = beeScoutResults[scoutBees-1];
        System.out.println("\nWorst result: " + worstResult);
        averageResult = 0;
        for (int i = 0; i < scoutBees; i++) averageResult += beeScoutResults[i];
        averageResult /= scoutBees;
        System.out.println("Average result: " + averageResult);
        bestResult = beeScoutResults[0];
        System.out.println("Best result: " + bestResult);
    }

    public double targetFunction(int[] x) {
        double distanceCovered = 0;
        for(int i = 0; i < citiesCounter - 1 ; i++) distanceCovered += distances[x[i]][x[i+1]];
        distanceCovered += distances[x[citiesCounter-1]][x[0]];
        return distanceCovered;
    }

    //sort obu tablic (wartosci funkcji celu i kolejnosci miast)
    public void sort2tables(double[] sortingData, int[][]additionalTable){
        for(int i = 0; i < scoutBees-1; i++)
            for (int j = 0; j < scoutBees - 1; j++)
                if (sortingData[j] > sortingData[j + 1]) {          //warunek == wartosc funkcji celu
                    swapDoubleTable(j, j + 1, sortingData);         //swap jednowymiarowej z wartosciami funkcji celu
                    complicatedSwap(j, j + 1, additionalTable);     //swap dwuwymiarowej z kolejnoscia miast
                }
    }

    public void swapIntTable(int a, int b, int[] tab){
        int tmp;
        tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;
    }

    public void swapDoubleTable(int a, int b, double[] tab){
        double tmp;
        tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;
    }

    public void complicatedSwap(int a, int b, int[][]additionalTable){
        int[]tmp = new int[citiesCounter];
        System.arraycopy(additionalTable[a], 0, tmp, 0, citiesCounter);
        System.arraycopy(additionalTable[b], 0, additionalTable[b], 0, citiesCounter);
        System.arraycopy(tmp, 0, additionalTable[b], 0, citiesCounter);
    }

    public double getWorstResult() {
        return round(worstResult, 2);
    }

    public double getAverageResult() {
        return round(averageResult, 2);
    }

    public double getBestResult() {
        return round(bestResult, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}