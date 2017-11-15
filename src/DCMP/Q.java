package DCMP;

import java.util.HashMap;

public class Q {
    int id;
    // <K, V> = <MAN_ID, RANK OF THE MAN>
    public HashMap<Integer, Integer> rank;
    int partner = -1;
    public Q(int id, HashMap<Integer, Integer> rank){
        this.id = id;
        this.rank = rank;
        partner = -1;
    }
}
