/**
 * Created by Janusz on 21-04-2015.
 */

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BeesAlgo {

    public int beesSentToBetterPlaces;      // liczba pszczol wysylanych do lepszych miejsc
    public int beesSentToOtherPlaces;       // liczba pszczol wyslanych do pozostalych wybranych miejsc
    public int betterPlaces;                // liczba lepszych miejsc
    public int chosenPlaces;                // liczba wybranych miejsc
    public int iteration;                   // maksymalna liczba iteracji
    public int scoutBees;                   // liczba pszczol zwiadowcow
    public int var;
    public int citiesCounter;

    private int cities [][];

    public double neighborhoodSize;         // rozmiar sasiedztwa(%) [0,1]

    public double optimalPoint[];
    private double distances [][];

    public boolean integerize = false;

    public BeesAlgo(){
    }

    public BeesAlgo(boolean integerize, double neighborhoodSize, int beesSentToBetterPlaces, int beesSentToOtherPlaces,
                    int betterPlaces, int chosenPlaces, int iteration, int scoutBees){
        this.integerize = integerize;
        this.neighborhoodSize = neighborhoodSize;
        this.beesSentToBetterPlaces = beesSentToBetterPlaces;
        this.beesSentToOtherPlaces = beesSentToOtherPlaces;
        this.betterPlaces = betterPlaces;
        this.chosenPlaces = chosenPlaces;
        this.iteration = iteration;
        this.scoutBees = scoutBees;
        init();
    }

    public void startAlgo() throws FileNotFoundException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        LineNumberReader lnr = null;

        try {
            fis = new FileInputStream("C:\\Users\\Vanquis\\IdeaProjects\\BeesTSP\\TSPLIB\\berlin52.tsp");
            lnr = new LineNumberReader(new FileReader("C:\\Users\\Vanquis\\IdeaProjects\\BeesTSP\\TSPLIB\\berlin52.tsp"));
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
                    if ((distances[index][k] > 0) && (distances[index][k] < localBestSolution) && (!visitedCities[k])) {
                        localBestSolution = distances[index][k];
                        index = k;
                        break;
                    }
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
        System.out.println("Nearest neighbor heuristics best result: " + bestSolution);
    }

    public void init(){
        var = citiesCounter;
        this.optimalPoint = new double[var + 1];
        for (int j = 0; j < var + 1; j++) optimalPoint[j] = 1;
    }


    public int[] fullRandom(){      //pszczoly zwiadowcy
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

    public double function(int[] x) {
        double distanceCovered = 0;
        for(int i = 0; i < citiesCounter - 1 ; i++) distanceCovered += distances[x[i]][x[i+1]];
        distanceCovered += distances[x[citiesCounter-1]][x[0]];
        return distanceCovered;
    }
    /*
        public int random(double high, double low) {
            Random generator = new Random();
            if(integerize) return (generator.nextInt((int) high - (int) low) + (int) low);
            double range = high-low+1;
            double fraction = range*generator.nextDouble();
            return(fraction+low);
        }
    */
    public void sort(int[][] searchPoints[][]){
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

    public void sort2tables(double[] sortingData, int[][]additionalTable){  //sort obu tablic (wartosci funkcji celu i kolejnosci miast)
        for(int i = 0; i < scoutBees-1; i++){
            for(int j = 0; j < scoutBees-1; j++){
                if(sortingData[j] > sortingData[j+1]){  //warunek == wartosc funkcji celu
                    swap(j, j+1, sortingData);          //swap jednowymiarowej z wartosciami funkcji celu
                    complicatedSwap(j, j+1, additionalTable); //swap dwuwymiarowej z kolejnoscia miast
                }
            }
        }



    }

    public void swap(int a, int b, double[] tab){
        double tmp;
        tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;
    }

    public void swapIntTable(int a, int b, int[] tab){
        int tmp;
        tmp = tab[a];
        tab[a] = tab[b];
        tab[b] = tmp;
    }

    public void complicatedSwap(int a, int b, int[][]additionalTable){
        int[]tmp = new int[citiesCounter];
        for (int i = 0; i < citiesCounter; i++){
            tmp[i] = additionalTable[a][i];
        }
        for (int i = 0; i < citiesCounter; i++){
            additionalTable[a][i] = additionalTable[b][i];
        }
        for (int i = 0; i < citiesCounter; i++){
            additionalTable[b][i] = tmp[i];
        }
    }


    public void run() {
        int[][] searchPoints = new int[scoutBees][var + 1];
        double [] beeScoutResults = new double[scoutBees];//tablica wynikow funkcji celu z randomowych pszczol zwiadowcow
        for (int i = 0; i < scoutBees; i++) {
            int[] tmpX = this.fullRandom();
            for(int j = 0; j < var; j++) searchPoints[i][j] = tmpX[j];
            double tmp = function(tmpX);
            beeScoutResults[i] = tmp;   //zapisanie wyniku
            //searchPoints[i][var] = tmp;
        }

        sort2tables(beeScoutResults, searchPoints);
        for(double print: beeScoutResults)
            System.out.println(print);

        int neigh = (int) (neighborhoodSize * citiesCounter);  //przeliczenie procentowego sąsiedztwa na sąsiedztwo w formie liczby miast



        for(int iter = 0; iter < iteration; iter++) { //liczba iteracji

            sort2tables(beeScoutResults, searchPoints);
            for(int btrplcs = 0; btrplcs < betterPlaces; btrplcs++){
                for(int j = 0; j < beesSentToBetterPlaces; j++){

                    int[] operativeVector = new int[citiesCounter];   //vector do operowania swapami
                    int[] beforeSwapVector = new int[citiesCounter];  //vector do zapamietania stanu przed swapem
                    for(int i = 0; i < citiesCounter; i++){
                        operativeVector[i] = searchPoints[btrplcs][i];
                    }

                    for (int ind = 0; ind < citiesCounter; ind++) {
                        beforeSwapVector[ind] = operativeVector[ind];      //zapamietanie vectora przed swapem
                    }

                    int randIndex = new Random().nextInt(citiesCounter-2);   //wylosowane miasto, centrum sasiedztwa
                    int neighJump = (new Random().nextInt(2*neigh)) - neigh;    //losowanie odległości pomiedzy  miastami do swapa (mniejszy niż rozmiar sasiedztwa)
                    int swapIndex = randIndex + neighJump;          //obliczenie odległości pomiedzy miastami do swapa


                    if(swapIndex < 0){              //zabezpieczenie przed wykroczeniem poza indeks tablicy
                        swapIndex = 0;
                    }else if(swapIndex > citiesCounter-1){
                        swapIndex = citiesCounter-1;
                    }
                    swapIntTable(randIndex, swapIndex, operativeVector);        //swap pomiedzy miastami

                    double newValue = function(operativeVector);                //nowa funkcja celu
                    double oldValue = function(beforeSwapVector);               //stara funckcja celu

                    if(newValue < oldValue){
                                       //sprawdzanie czy sie poprawiło
                        for(int i = 0; i < citiesCounter; i++){                 //jak tak to zapisujemy nowy wynik
                            searchPoints[btrplcs][i] = operativeVector[i];
                            beeScoutResults[btrplcs] = newValue;                //zapis do beeScoutResults nowego wyniku
                        }
                    }
                }
            }







            for(int chsplcs = betterPlaces; chsplcs < chosenPlaces + betterPlaces; chsplcs++){
                for(int j = 0; j < beesSentToOtherPlaces; j++){

                    int[] operativeVector = new int[citiesCounter];   //vector do operowania swapami
                    int[] beforeSwapVector = new int[citiesCounter];  //vector do zapamietania stanu przed swapem
                    for(int i = 0; i < citiesCounter; i++){
                        operativeVector[i] = searchPoints[chsplcs][i];
                    }

                    for (int ind = 0; ind < citiesCounter; ind++) {
                        beforeSwapVector[ind] = operativeVector[ind];      //zapamietanie vectora przed swapem
                    }

                    int randIndex = new Random().nextInt(citiesCounter-2);   //wylosowane miasto, centrum sasiedztwa
                    int neighJump = (new Random().nextInt(2*neigh)) - neigh;    //losowanie odległości pomiedzy  miastami do swapa (mniejszy niż rozmiar sasiedztwa)
                    int swapIndex = randIndex + neighJump;          //obliczenie odległości pomiedzy miastami do swapa


                    if(swapIndex < 0){              //zabezpieczenie przed wykroczeniem poza indeks tablicy
                        swapIndex = 0;
                    }else if(swapIndex > citiesCounter-1){
                        swapIndex = citiesCounter-1;
                    }
                    swapIntTable(randIndex, swapIndex, operativeVector);        //swap pomiedzy miastami

                    double newValue = function(operativeVector);                //nowa funkcja celu
                    double oldValue = function(beforeSwapVector);               //stara funckcja celu

                    if(newValue < oldValue){
                        //sprawdzanie czy sie poprawiło
                        for(int i = 0; i < citiesCounter; i++){                 //jak tak to zapisujemy nowy wynik
                            searchPoints[chsplcs][i] = operativeVector[i];
                            beeScoutResults[chsplcs] = newValue;                //zapis do beeScoutResults nowego wyniku
                        }
                    }
                }
            }






        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        for(double print: beeScoutResults)
            System.out.println(print);




/*
        double minimum = Double.MAX_VALUE;
        for (int i = 0; i < scoutBees; i++){
            if(minimum > beeScoutResults[i]) {
                minimum = beeScoutResults[i];
            }
        }
        System.out.println("Random result: " + minimum);
*/





        //wysylanie nastepnych N pszczol do K miejsc, czyli wybieranie K podzbiorów rozwiazania
        // i losowe permutowanie ich na N sposobow

        /*
        for (int itr = 1; itr <= iteration; itr++) {
            sort(searchPoints);

            for (int i = 0; i < betterPlaces; i++) {
                int[] x = new int[var];
                int[] x_best = new int[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }
                int f = searchPoints[i][var];
                int f_best = f;

                for (int indx = 0; indx < beesSentToBetterPlaces; indx++) {
                    int[] x_elite = new int[var];
                    for (int j = 0; j < var; j++)
                        x_elite[j] = this.random(x[j] + neighborhoodSize, x[j] - neighborhoodSize);
                    int f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) x_best[j] = x_elite[j];
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) searchPoints[i][j] = x_best[j];
                searchPoints[i][var] = f_best;
            }

            for (int i = betterPlaces; i < chosenPlaces; i++) {
                int[] x = new int[var];
                int[] x_best = new int[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }

                int f = searchPoints[i][var];
                int f_best = f;

                for (int indx = 0; indx < beesSentToOtherPlaces; indx++) {
                    int[] x_elite = new int[var];
                    for (int j = 0; j < var; j++) x_elite[j] = this.random(x[j] + neighborhoodSize, x[j] - neighborhoodSize);
                    int f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) x_best[j] = x_elite[j];
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) searchPoints[i][j] = x_best[j];
                searchPoints[i][var] = f_best;
            }

            for (int i = chosenPlaces; i < scoutBees; i++) {
                int[] tmpX = new int[var];
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
        */
    }

    public double optimalValue(){
        return(optimalPoint[var]);
    }

    public double[] optimalPoint(){
        double[] result = new double[var];
        for (int j = 0; j < var; j++) result[j] = optimalPoint[j];
        return(result);
    }
}