import java.util.Random;

public class PercolationCode {
    public static class Percolation {
        private final int[][] grid;
        private final int[] parent;
        private final int[] rank;

        private final int n;

        private int openCells;

        private final Random randomObj;

        private final int FULL = 2;
        private final int OPEN = 1;
        private final int BLOCKED = 0;

        private final int TOP;
        private final int BOTTOM;

        private boolean ableToPercolate;

        /**
         * Constructor for Percolation object
         * @param n width or height of the grid's object
         */
        public Percolation(int n) {
            randomObj = new Random();

            ableToPercolate = false;

            this.n = n;

            grid = new int[n][n];

            // n * n for n^2 cells, + 2 for TOP and BOTTOM values
            parent = new int[(n * n) + 2];
            TOP = n * n;
            BOTTOM = n * n + 1;
            parent[n * n] = TOP;
            parent[n * n + 1] = BOTTOM;

            // Make the rank of TOP and BOTTOM massive to always have them be connected to

            rank = new int[n * n + 2];

            rank[n * n] = TOP;
            rank[n * n + 1] = BOTTOM;

            for (int i = 0; i < n * n; i++) {
                parent[i] = i;
            }

            // Block out the grid initially
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    grid[x][y] = BLOCKED;
                }
            }

            openCells = 0;
        }

        // Getter methods
        public boolean getAbleToPercolate() { return ableToPercolate; }

        public int getOpenCells() { return openCells; }

        /**
         * Printing method to display the full grid
         * @return a string to print in the console
         */
        public String printGrid() {
            StringBuilder sb = new StringBuilder();

            // Unicode declarations for grid cells
            String RED = "\u001B[31m\u2588\u2588\u001B[0m";
            String WHITE = "\u2588\u2588";
            String BLUE = "\u001B[34m\u2588\u2588\u001B[0m";

            // Parses each grid cell and adds it to the string to return
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    if (grid[y][x] == BLOCKED) { sb.append(RED).append(" "); }
                    else if (grid[y][x] == OPEN) { sb.append(WHITE).append(" "); }
                    else { sb.append(BLUE).append(" "); }
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        /**
         * Main functioning method of the object
         * Opens a random cell, calls functions for creating unions
         * and filling neighboring cells
         */
        public void openRandomCell() {
            boolean foundBlockedCell = false;

            while (!foundBlockedCell) {
                int randomX = randomObj.nextInt(n);
                int randomY = randomObj.nextInt(n);

                if (grid[randomY][randomX] == BLOCKED) {
                    grid[randomY][randomX] = OPEN;
                    int currentCell = toCellId(randomX, randomY);
                    openCells++;
                    // Connect cell with adjoining cells (even to TOP or BOTTOM)
                    if (randomY == 0) {
                        union(TOP, currentCell);
                    } else if (randomY == n - 1) {
                        union(BOTTOM, currentCell);
                    }

                    unionWithNeighbors(randomX, randomY);
                    // If this new cell connects to TOP, check the full array. ONLY if it connects to TOP
                    if (unionFind(currentCell) == unionFind(TOP)) {
                        fillConnected();
                    }
                    // The previous step will fill all of BOTTOM if TOP and BOTTOM are unionised.
                    // However, must still check if Percolation is possible.
                    if (unionFind(BOTTOM) == unionFind(TOP)) { ableToPercolate = true; }

                    // Break while loop
                    foundBlockedCell = true;
                }
            }
        }

        /**
         * Method to union all cells with neighbors
         * @param xCoord column of current cell
         * @param yCoord row of current cell
         */
        private void unionWithNeighbors (int xCoord, int yCoord) {
            int currentCell = toCellId(xCoord, yCoord);

            // Check upper & lower neighbor cells
            if (yCoord - 1 >= 0 && grid[yCoord - 1][xCoord] != BLOCKED) {
                int upperCell = toCellId(xCoord, yCoord - 1);
                if (unionFind(upperCell) == unionFind(TOP)) {
                    union(TOP, currentCell);
                } else {
                    union(currentCell, upperCell);
                }
            }
            if (yCoord + 1 < n && grid[yCoord + 1][xCoord] != BLOCKED) {
                int lowerCell = toCellId(xCoord, yCoord + 1);
                if (unionFind(lowerCell) == unionFind(BOTTOM)) {
                    union(BOTTOM, currentCell);
                } else {
                    union(currentCell, lowerCell);
                }
            }

            // Check left & right neighbor cells
            if (xCoord + 1 < n && grid[yCoord][xCoord + 1] != BLOCKED) {
                int rightCell = toCellId(xCoord + 1,yCoord);
                union(currentCell, rightCell);
            }
            if (xCoord - 1 >= 0 && grid[yCoord][xCoord - 1] != BLOCKED) {
                int leftCell = toCellId(xCoord - 1,yCoord);
                union(currentCell, leftCell);
            }
        }

        /**
         * Method for converting grid-useable coords into union-useable coords
         * @param x cell's current column
         * @param y cell's current row
         * @return cell's parent-union-array id index
         */
        private int toCellId(int x, int y) {
            return n * y + x;
        }

        /**
         * Method to fill all TOP-joined cells
         * This function is ONLY called if a new group is joined to TOP. NOT for each new opened cell.
         * This saves on computing power and efficiency while making sure ALL connected cells are filled.
         */
        public void fillConnected() {
            for (int y = 0; y < n; y++){
                for (int x = 0; x < n; x++) {
                    int currentCell = toCellId(x, y);
                    if (unionFind(currentCell) == unionFind(TOP)) { grid[y][x] = FULL; }
                }
            }
        }

        // These 2 methods are taken from UnionFindCode.java and
        // modified to fit the needs of this percolation
        public int unionFind(int value) {
            // Path compression of union through halving
            while (value != parent[value]) {
                parent[value] = parent[parent[value]];
                value = parent[value];
            }
            return value;
        }

        private void union(int x, int y) {
            // Find the roots of the 2 items
            int rootX = unionFind(x);
            int rootY = unionFind(y);

            if (rank[rootX] < rank[rootY]) {
                rank[rootY] += rank[rootX];
                parent[rootX] = rootY;
            } else {
                // Else, rootY's parent is now rootX
                rank[rootX] += rank[rootY];
                parent[rootY] = rootX;
            }
        }
    }


    public static void main(String[] args) {

        int n = 20;

        // Pass n to make an nxn grid
        Percolation percolationObj = new Percolation(n);

        while (!percolationObj.getAbleToPercolate()) {
            percolationObj.openRandomCell();

            if (percolationObj.getOpenCells() % 50 == 0 || percolationObj.getAbleToPercolate()) {
                if (percolationObj.getAbleToPercolate()) {
                    System.out.printf("Able to percolate at %d open cells.\n", percolationObj.getOpenCells());
                }
                else { System.out.printf("Grid at %d cells opened \n", percolationObj.getOpenCells()); }

                System.out.println(percolationObj.printGrid());

            }
        }
        int totalCellsOpened = percolationObj.getOpenCells();
        int testTotal = 1000;

        Percolation[] percObjList = new Percolation[testTotal];

        // Testing loop for n^3 total tests
        for (int i = 0; i < testTotal; i++) {
            percObjList[i] = new Percolation(n);
            while (!percObjList[i].getAbleToPercolate()) {
                percObjList[i].openRandomCell();
            }
            totalCellsOpened += percObjList[i].getOpenCells();
        }

        // Percolation threshold calculation
        float avgPercRate = ((float)totalCellsOpened / (float)testTotal) / (float)(n * n);

        //Results printout
        System.out.println("This was repeated " + testTotal + " times and then averaged by total cells in a grid to get" +
                "\nthe most accurate percolation threshold possible.");
        System.out.printf("\n(%d [Total Cells Opened] / %d [Total Tests]) / (%d * %d)[Total Cells in a Grid] = %.5f\n",
                totalCellsOpened, testTotal, n, n, avgPercRate);
        System.out.printf("\nThe percolation threshold of %dx%d grids is about %.3f%%.\n",n,n,avgPercRate * 100);
    }
}
