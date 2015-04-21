/**
 * Created by Janusz on 21-04-2015.
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BeesAlgo {

    public int var = 3;

    public int n = 40;								// liczba pszczol zwiadowcow
    public int m = 5;								// liczba wybranych miejsc
    public int e = 4;								// liczba lepszych miejsc
    public double ngh = 1;							// rozmiar sasiedztwa(%) [0,1]
    public int nep = 4;								// liczba pszczol wysylanych do lepszych miejsc
    public int nsp = 2;								// liczba pszczol wyslanych do pozostalych wybranych miejsc

    public int iteration = 200;						// maksymalna liczba iteracji

    public int upperLimit = 10;
    public int lowerLimit = -10;
    public boolean integerize =false;

    public double optimalPoint[];

    public BeesAlgo(){
        init();
    }

    public BeesAlgo(int no_of_var) {
        this.var = no_of_var;
        init();
    }

    public BeesAlgo(int var, int n, int m, int e, double ngh, int nep, int nsp, int iteration, int upperLimit, int lowerLimit, boolean integerize){
        this.var=var;
        this.n=n;
        this.m=m;
        this.ngh=ngh;
        this.nep=nep;
        this.nsp=nsp;
        this.iteration=iteration;
        this.upperLimit=upperLimit;
        this.lowerLimit=lowerLimit;
        this.integerize=integerize;
        init();
    }

    private void init(){
        this.optimalPoint = new double[var + 1];
        for (int j = 0; j < var + 1; j++) {
            optimalPoint[j] = 1;
        }
    }

    public double function(double[] x) {
        double result = 0;
        for (int j = 0; j < var; j++) {
            result = result + (x[j]-1) * (x[j]-1);
        }
        return (-result);
    }

    public double random(double high, double low) {
        Random generator = new Random();
        if(integerize){
            return (generator.nextInt((int) high - (int) low) + (int) low);
        }
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
        double searchPoints[][] = new double[n][var + 1];

        for (int i = 0; i < n; i++) {
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

            for (int i = 0; i < e; i++) {
                double[] x = new double[var];
                double[] x_best = new double[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }
                double f = searchPoints[i][var];
                double f_best = f;

                for (int indx = 0; indx < nep; indx++) {
                    double[] x_elite = new double[var];
                    for (int j = 0; j < var; j++) {
                        x_elite[j] = this.random(x[j] + ngh, x[j] - ngh);
                    }
                    double f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) {
                            x_best[j] = x_elite[j];
                        }
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) {
                    searchPoints[i][j] = x_best[j];
                }
                searchPoints[i][var] = f_best;

            }

            for (int i = e; i < m; i++) {
                double[] x = new double[var];
                double[] x_best = new double[var];
                for (int j = 0; j < var; j++) {
                    x[j] = searchPoints[i][j];
                    x_best[j] = x[j];
                }
                double f = searchPoints[i][var];
                double f_best = f;

                for (int indx = 0; indx < nsp; indx++) {
                    double[] x_elite = new double[var];
                    for (int j = 0; j < var; j++) {
                        x_elite[j] = this.random(x[j] + ngh, x[j] - ngh);
                    }
                    double f_elite = function(x_elite);
                    if (f_elite > f_best) {
                        for (int j = 0; j < var; j++) {
                            x_best[j] = x_elite[j];
                        }
                        f_best = f_elite;
                    }
                }

                for (int j = 0; j < var; j++) {
                    searchPoints[i][j] = x_best[j];
                }
                searchPoints[i][var] = f_best;

            }

            for (int i = m; i < n; i++) {
                double[] tmpX = new double[var];
                for (int j = 0; j < var; j++) {
                    searchPoints[i][j] = this.random(upperLimit, lowerLimit);
                    tmpX[j] = searchPoints[i][j];
                }
                searchPoints[i][var] = function(tmpX);
            }
        }

        sort(searchPoints);

        for (int j = 0; j < var; j++) {
            optimalPoint[j] = searchPoints[0][j];
        }
        optimalPoint[var] = -searchPoints[0][var];
    }

    public double optimalValue(){
        return(optimalPoint[var]);
    }

    public double[] optimalPoint(){
        double[] result = new double[var];
        for (int j = 0; j < var; j++) {
            result[j]=optimalPoint[j];
        }
        return(result);
    }

}
