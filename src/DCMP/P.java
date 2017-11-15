package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class P implements PRMI{
    public int id;
    //mpref[i] = woman id of rank i
    public int[] mpref;
    //K = STEP V = List of ConflictPairs at this point
    public HashMap<Integer, List<ConflictPair>> prerequisite;
    public int curIdx;
    private int n;

    //


    //
    public P(int id, int[] mpref, HashMap<Integer, List<ConflictPair>> prerequisite){
        this.id = id;
        this.mpref = mpref;
        this.prerequisite = prerequisite;
        curIdx = -1;
        this.n = mpref.length;
    }

    //assumption
    public Response CallMan(String rmi, Request req, int id) {
        return null;
    }
    public Response CallWoman(String rmi, Request req, int id) {
        return null;
    }
    @Override
    public Response InitHandler(Request req) throws RemoteException {
        return null;
    }

    @Override
    public Response RejectHandler(Request req) throws RemoteException {
        if (curIdx != -1 && mpref[curIdx] == req.myId) {
            if (curIdx == n - 1) {
                // do i need to broadcast this message?
                System.out.println("no constrained stable marriage possible");
            }else{
                curIdx++;
                List<ConflictPair> conflictPairs = prerequisite.get(curIdx);
                if (conflictPairs != null) {
                    for (ConflictPair cp : conflictPairs) {
                        CallMan("advance", new Request(id, cp.regret), cp.pId);
                    }
                }
                CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
            }
        }
        return new Response(true);
    }

    @Override
    public Response AdvanceHandler(Request req) throws RemoteException {
        while(curIdx < req.regret) {
            curIdx++;
            List<ConflictPair> conflictPairs = prerequisite.get(curIdx);
            if (conflictPairs != null) {
                for (ConflictPair cp : conflictPairs) {
                    CallMan("advance", new Request(id, cp.regret), cp.pId);
                }
            }
        }
        CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
        return new Response(true);
    }

    @Override
    public Response SignalHandler(Request req) throws RemoteException {
        return null;
    }

}
