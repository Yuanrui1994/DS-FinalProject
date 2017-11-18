package DCMP;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    P[] ps = null;
    Q[] qs = null;

    public static void main(String[] args) {
        Main m = new Main();
        try {
            int matrixMan[][] = {{3, 0, 1, 2},
                    {1, 2, 0, 3},
                    {2, 0, 3, 1},
                    {1, 3, 2, 0}};
            int matrixWoman[][] = {{3, 0, 2, 1},
                    {0, 3, 1, 2},
                    {0, 1, 3, 2},
                    {2, 0, 3, 1}};
//                    { 0,2, 3, 1}};
            HashMap<Integer, LinkedList<ConflictPair>> conflictPair = new HashMap<>();
            for (int i = 0; i < matrixMan.length; i++) {
                LinkedList<ConflictPair> list = new LinkedList<>();
                list.add(new ConflictPair(0, i));
                conflictPair.put(i, list);
            }
            LinkedList<HashMap<Integer, Integer>> womanrankList = new LinkedList<>();
            for (int i = 0; i < matrixWoman.length; i++) {
                HashMap<Integer, Integer> tempMap = new HashMap<>();
                for (int j = 0; j < matrixWoman[0].length; j++) {
                    tempMap.put(matrixWoman[i][j], j);
                }
                womanrankList.add(tempMap);
            }
            m.initPQ(8, matrixMan, womanrankList, conflictPair);
//            m.cleanup();
//            m.initPQ(8, matrixMan, womanrankList, conflictPair);
            //P(int id, int[] mpref, HashMap<Integer, LinkedList<ConflictPair>> prerequisite, String[] peers, int[] ports)
            for (int i = 0; i < m.ps.length; i++) {
                System.out.println("Initialize man " + i);
                m.ps[i].InitHandler();
            }
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < m.ps.length; i++)
                System.out.println("man " + i + "'s partnerIndex is: " + m.ps[i].curIdx);
        } finally {
            m.cleanup();
        }
    }
    // P(int id, int[] mpref, HashMap<Integer, List<ConflictPair>> prerequisite, String[] peers, int[] ports)
    // Q(int id, HashMap<Integer, Integer> rank, String[] peers, int[] ports)

    public void initPQ(int nsize, int[][] matrixMan, LinkedList<HashMap<Integer, Integer>> womanRankList, HashMap<Integer, LinkedList<ConflictPair>> conflictPair) {
        String host = "127.0.0.1";
        String[] peers = new String[nsize];
        int[] ports = new int[nsize];
        ps = new P[nsize / 2];
        qs = new Q[nsize / 2];
        for (int i = 0; i < nsize; i++) {
            ports[i] = 1100 + i;
            peers[i] = host;
        }

        for (int i = 0; i < nsize / 2; i++) {
            if (i != 1)
                ps[i] = new P(i, matrixMan[i], null, peers, ports);
            else
                ps[i] = new P(i, matrixMan[i], conflictPair, peers, ports);

        }
        for (int i = 0; i < nsize / 2; i++) {
            qs[i] = new Q(i, womanRankList.get(i), peers, ports);
        }
    }

    private void cleanup() {
        for (int i = 0; i < ps.length; i++) {
            if (ps[i] != null) {
                ps[i].Kill();
            }
        }
        for (int i = 0; i < qs.length; i++) {
            if (qs[i] != null) {
                qs[i].Kill();
            }
        }
        System.out.println("cleaned.");
    }

}
