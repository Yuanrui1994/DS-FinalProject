package SCSMP_GS;

import java.util.HashMap;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        LinkedList<Man> menList = new LinkedList<>();
        LinkedList<Woman> womenList = new LinkedList<>();

        int matrixMan[][]  = {{4,1,2,3},
                              {2,3,1,4},
                              {3,1,4,2},
                              {2,4,3,1}};
        int matrixWoman[][] = {{4, 1, 3, 2},
                {1, 4, 2, 3},
                {1, 2, 4, 3},
                {3, 1, 4, 2}};
                //{1, 3, 4, 2}};

        HashMap<Integer, LinkedList<ConflictPair>> conflictPair = new HashMap<>();
        for(int i=0;i<matrixMan.length;i++){
            LinkedList<ConflictPair> list = new LinkedList<>();
            list.add(new ConflictPair(0,i));
            conflictPair.put(i,list);
        }



        for(int i=0;i<matrixMan.length;i++){
            HashMap<Integer, Integer> rankmap = new HashMap<>();
            for(int j=0;j<matrixMan[0].length;j++){
                rankmap.put(j, matrixMan[i][j]-1);
            }
            Man newMan = new Man(i,rankmap,null);
            if(i==1)
                newMan.conflictMap = conflictPair;
            menList.add(newMan);
        }
        for(int i=0;i<matrixWoman.length;i++){
            HashMap<Integer, Integer> rankmap = new HashMap<>();
            for(int j=0;j<matrixWoman[0].length;j++){
                rankmap.put(matrixWoman[i][j]-1,j);
            }
            Woman newWoman = new Woman(i,rankmap);
            womenList.add(newWoman);
        }
        System.out.println("STARTTTTT"+menList.get(1).rankmap.get(1));
        for(int i = 0;i<menList.size();i++){
            for(int j=0;j<menList.size();j++)
             System.out.print(menList.get(i).rankmap.get(j)+" ");
            System.out.println(" ");
        }

        Solver sov = new Solver(womenList, menList);
        boolean ret =  sov.findStableMarriage();
        for(Man m: menList){
            System.out.print(m.id);
            System.out.println(" "+ m.partnerIndex);
        }
        System.out.println(ret);
    }
}
