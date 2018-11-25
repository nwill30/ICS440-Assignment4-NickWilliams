import java.util.Random;
import java.util.concurrent.*;

class ShortestPath implements Runnable {

    private final int i;
    private final int k;
    private final int[][] d;
    private final int dim;
    private static final int I = Integer.MAX_VALUE; // Infinity

    public ShortestPath(int i, int k, int[][] d, int dim) {
        this.i = i;
        this.k = k;
        this.d = d;
        this.dim = dim;
    }

    public void run() {
        for (int j = 0; j < dim; j++) {
            if (d[i][k] == I || d[k][j] == I) {
                continue;
            } else if (d[i][j] > d[i][k] + d[k][j]) {
                d[i][j] = d[i][k] + d[k][j];
            }
        }
    }
}


public class FloydWarshall extends Thread {
    private static final int I = Integer.MAX_VALUE; // Infinity
    private static final int dim = 50;
    private static double fill = 0.3;
    private static int maxDistance = 100;
    private static int adjacencyMatrix[][] = new int[dim][dim];
    private static int d[][] = new int[dim][dim];
    private static int threadCount = 1;

    /*
     * Generate a randomized matrix to use for the algorithm.
     */
    private static void generateMatrix() {
        Random random = new Random();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i != j)
                    adjacencyMatrix[i][j] = I;
            }
        }
        for (int i = 0; i < dim * dim * fill; i++) {
            adjacencyMatrix[random.nextInt(dim)][random.nextInt(dim)] =
                    random.nextInt(maxDistance + 1);
        }
    }


    /*
     * Execute Floyd Warshall on adjacencyMatrix.
     */
    private static void execute() {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                d[i][j] = adjacencyMatrix[i][j];
                if (i == j) {
                    d[i][j] = 0;
                }
            }
        }
        for (int k = 0; k < dim; k++) {
            //Iterate over all i solving for j through k
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            try{
//return i as a future but need to add it in order
                for (int i = 0; i < dim; i++) {
                    Runnable runnable= new ShortestPath(i,k,d,dim);
                    executorService.execute(runnable);

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            executorService.shutdown();
//            System.out.println("pass " + (k + 1) + "/" + dim);
            //threads wait
        }
    }

        /*
         * Print matrix[dim][dim]
         */
        private static void print ( int matrix[][]){
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (matrix[i][j] == I) {
                        System.out.print("I" + " ");
                    } else {
                        System.out.print(matrix[i][j] + " ");
                    }
                }
                System.out.println();
            }
        }

        /*
         * Compare two matrices, matrix1[dim][dim] and matrix2[dim][dim] and
         * print whether they are equivalent.
         */
        private static void compare ( int matrix1[][], int matrix2[][]){
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (matrix1[i][j] != matrix2[i][j]) {
                        System.out.println("Comparison failed");
                    }
                }
            }
            System.out.println("Comparison succeeded");
        }

        public static void main (String[]args){
            long start, end;
            generateMatrix();
            print(d);
            start = System.nanoTime();
            execute();
            end = System.nanoTime();
            System.out.println("time consumed: " + (double) (end - start) / 1000000000);
            compare(d, d);
            print(d);
        }
    }
