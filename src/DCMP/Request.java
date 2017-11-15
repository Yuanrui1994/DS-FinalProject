package DCMP;

import java.io.Serializable;

public class Request implements Serializable {
    int myId;
    int regret;
    public Request(int myId, int regret){
        this.myId = myId;
        this.regret = regret;
    }
}
