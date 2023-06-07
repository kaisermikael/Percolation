import com.sun.jdi.event.StepEvent;

import java.util.ArrayList;

public class UnionFindCode {

    public static class UnionFind {
        private int[] parent;
        private int[] rank;
        private int total;

        public UnionFind(int x) {
            total = x;
            rank = new int[x];
            parent = new int[x];
            // Initialization array for testing
            for (int i = 0; i < x; i++){
                parent[i] = i;
            }
        }

        public int getTotal() {
            return total;
        }

        public int unionFind(int value) {
            // Path compression of union through halving
            while (value != parent[value]) {
                parent[value] = parent[parent[value]];
                value = parent[value];
            }
            return value;
        }

        public void union(int x, int y) {
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

            // There is now 1 less object in the pool because of the join
            total--;
        }

        // toString override for the parent[] array
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();

            for (Integer ints : parent) {
                sb.append(ints).append(" ");
            }
            return sb.toString();
        }

    }

    public static void main(String[] args) {
        // Pass UnionFind number 30 to test an array of 30
        UnionFind testUnion = new UnionFind(30);

        // Testing calls

        // Find what the total # of objects in the array is initially
        System.out.println("Total Objects in Array: " + testUnion.getTotal());

        // Display all objects in the array currently
        System.out.println("Current Objects in Array: " + testUnion.toString());

        // Some union test creations. 29 into union under 0.
        System.out.println("Testing union of 29 under 0: ");
        testUnion.union(0, 29);

        // Display all objects in the array currently
        System.out.println("Current Objects in Array: " + testUnion.toString());

        // 29 is now no longer in the parent array itself. Now check if it is still IN the structure under parent 0
        if (testUnion.unionFind(29) == 0) {
            System.out.println("29 is in the Union. Its parent is 0. This is good!");
        } else {
            System.out.println("29 is no longer in the Union. It has no parent. This bad");
        }

        // Now perform several unions and check the results
        testUnion.union(0, 28);
        testUnion.union(27, 0);
        testUnion.union(23, 27);
        testUnion.union(6, 23);
        testUnion.union(4, 6);

        // Display all objects in the array currently
        System.out.println("Current Objects in Array: " + testUnion.toString());
        System.out.println();

        // We can now see all the replaced parents in the array.
        // Now we want to verify that a string of unions actually updates the value TRUE root parent, not just their
        // DIRECT parent.
        System.out.println("Test if path compression causes item to point to the set's true root parent, not just direct parent.");
        System.out.println("Also test if multiple unions causes re-traceable path of data");
        System.out.println("unionFind(28) returns: " + testUnion.unionFind(28));
        if (testUnion.unionFind(28) == 0) {
            System.out.println("28's parent is 0. This is bad");
        } else {
            System.out.println("28's parent is no longer 0. This is good!");
        }

        // Is 0's parent still 27?
        System.out.println("unionFind(0) returns: " + testUnion.unionFind(0));
        if (testUnion.unionFind(0) == 27) {
            System.out.println("0's parent is 27. This is bad");
        } else {
            System.out.println("0's parent is no longer 27. This is good!");
        }

        // Is 27's parent still 23?
        System.out.println("unionFind(27) returns: " + testUnion.unionFind(27));
        if (testUnion.unionFind(27) == 23) {
            System.out.println("27's parent is 23. This is bad");
        } else {
            System.out.println("27's parent is no longer 23. This is good!");
        }

        // Is 23's parents still 6?
        System.out.println("unionFind(23) returns: " + testUnion.unionFind(23));
        if (testUnion.unionFind(23) == 6) {
            System.out.println("23's parent is 6. This is bad");
        } else {
            System.out.println("23's parent is no longer 6. This is good!");
        }

        // Is 6's parent still 4?
        System.out.println("unionFind(6) returns: " + testUnion.unionFind(6));
        if (testUnion.unionFind(6) == 4) {
            System.out.println("6's parent is 4. This is good!");
        } else {
            System.out.println("6's parent is no longer 4. This is bad");
        }

        // How many "total" roots are there? There should be 24; this proves path compression is working
        // Path compression means each unionized element points to its over parent, NOT just the direct parent
        System.out.println();
        System.out.printf("There are currently %d total independent root parents. \n", testUnion.getTotal());
        System.out.println("If there are 24 roots, then path compression is functioning.");
        System.out.println();

        System.out.println("If all tests return good, Smart Union is currently working as intended");
    }
}
