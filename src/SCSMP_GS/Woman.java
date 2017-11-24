package SCSMP_GS;

import java.util.HashMap;

public class Woman{
    int id;
    HashMap<Integer, Integer> rankmap;
    int partnerIndex;
    public Woman(int id, HashMap<Integer, Integer> rankmap){
        this.id = id;
        this.rankmap = rankmap;
        this.partnerIndex = -1;
    }
}