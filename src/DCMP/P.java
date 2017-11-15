package DCMP;

import java.util.HashMap;
import java.util.List;

public class P {
    public int id;
    //mpref[i] = woman id of rank i
    public int[] mpref;
    //K = STEP V = List of ConflictPairs at this point
    public HashMap<Integer, List<ConflictPair>> prerequisite;
    public int curIdx;

    public P(int id, int[] mpref, HashMap<Integer, List<ConflictPair>> prerequisite){
        this.id = id;
        this.mpref = mpref;
        this.prerequisite = prerequisite;
        curIdx = -1;
    }

    public void propose(){

    }
}
