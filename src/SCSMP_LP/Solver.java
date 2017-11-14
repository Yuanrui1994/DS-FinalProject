package SCSMP_LP;

import java.util.HashMap;
import java.util.HashSet;
// #forbidden = n^3
// complexity of forbidden = O(n)
// overall complexity = O(n^4)
public class Solver {
    private int n;
    private int[][] mpref;
    private int[][] wpref;
    //m[i][j] = the rank of woman j of man i
    private int[][] mrank;
    private int[][] wrank;
    private HashMap<String, HashSet<String>> externalEdges;
    public Solver(){

    }
    public Solver(int[][] mpref, int[][] wpref, HashMap<String, HashSet<String>> externalEdges) {
        this.mpref = mpref;
        this.wpref = wpref;
        n = this.mpref.length;
        mrank = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mrank[i][mpref[i][j]] = j;
            }
        }
        wrank = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                wrank[i][wpref[i][j]] = j;
            }
        }
        this.externalEdges = externalEdges;
    }
    // F[i] = the # of proposes of man_i by now.
    public int[] getMenOptimal(int[] F) {
        //best[i] = the best proposal get by woman i
        int[] best = new int[n];
        for (int i = 0; i < n; i++) {
            best[i] = -1;
        }
        int[] G = new int[n];
        for (int i = 0; i < n; i++) {
            G[i] = F[i];
            for (int k = 0; k <= G[i]; k++) {
                int w = mpref[i][k];
                if (best[w] == -1 || wrank[w][i] < wrank[w][best[w]]) {
                    best[w] = i;
                }
            }
        }
        while(true){
            boolean valid = true;
            for (int i = 0; i < n; i++) {
                if (G[i] == n) return null;
                if (forbidden(G, i, best)) {
                    valid = false;
                    G[i]++;
                    if (G[i] == n) return null;
                    //update best
                    else {
                        int w = mpref[i][G[i]];
                        if (best[w] == -1 || wrank[w][i] < wrank[w][best[w]]) {
                            best[w] = i;
                        }
                    }
                }
            }
            if (valid) break;
        }
        return G;
    }
    public void printSMP(int[] G) {
        if (G == null) {
            System.out.println("Stable Matching Does Not Exit");
            return;
        }
        System.out.println("Stable Matching Found");
        for (int i = 0; i < n; i++) {
            System.out.println("Man: " + i + " -> " + "Woman " + mpref[i][G[i]]);
        }
    }

    boolean forbidden(int[] G, int i, int[] best) {
        if (G[i] == -1) return true;
        if (best[mpref[i][G[i]]] != i) return true;
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            // wi is the current match of man i
            int wi = mpref[i][G[i]];
            int wj = mpref[j][G[j]];
//            if (wi == wj && wrank[wi][j] < wrank[wi][i]) return true;
            if (wrank[wi][j] < wrank[wi][i] && mrank[j][wi] < mrank[j][wj]) return true;
            String from = i + "-" + (G[i]+1);
            String to = j + "-" + G[j];
            if (externalEdges.containsKey(from) && externalEdges.get(from).contains(to)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] mpref = new int[][]{{3,0,1,2}, {1,2,0,3}, {2,0,3,1}, {1,3,2,0}};
        int[][] wpref = new int[][]{{3,0,2,1}, {0,3,1,2}, {0,1,3,2}, {2,0,3,1}};
        HashMap<String, HashSet<String>> externalEdges = new HashMap<>();
        externalEdges.put("0-0", new HashSet<String>());
        externalEdges.put("0-1", new HashSet<String>());
        externalEdges.put("0-2", new HashSet<String>());
        externalEdges.put("0-3", new HashSet<String>());
        externalEdges.get("0-0").add("1-0");
        externalEdges.get("0-1").add("1-1");
        externalEdges.get("0-2").add("1-2");
        externalEdges.get("0-3").add("1-3");
        Solver solver = new Solver(mpref, wpref, externalEdges);
        int[] F = new int[]{0,0,0,0};
        int[] G = solver.getMenOptimal(F);
        solver.printSMP(G);
    }
}
