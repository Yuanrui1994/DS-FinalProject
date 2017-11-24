package SCSMP_GS;

import java.util.HashMap;
import java.util.LinkedList;

public class Man{
    int id;
    HashMap<Integer, Integer> rankmap;
    int partnerIndex;
    HashMap<Integer, LinkedList<ConflictPair>> conflictMap;
    //boolean inQueue;
    public Man(int id, HashMap<Integer, Integer> rankmap, HashMap<Integer, LinkedList<ConflictPair>> conflictMap){
        this.id = id;
        this.rankmap = rankmap;
        this.partnerIndex = -1;
        this.conflictMap = conflictMap;
       // this.inQueue = false;
    }
}