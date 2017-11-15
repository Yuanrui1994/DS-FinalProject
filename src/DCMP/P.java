package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class P implements PRMI, Runnable{
    ReentrantLock mutex;
    public int id;
    //mpref[i] = woman id of rank i
    public int[] mpref;
    //K = STEP V = List of ConflictPairs at this point
    public HashMap<Integer, List<ConflictPair>> prerequisite;
    public int curIdx;
    private int n;

    // for thread
    //type = true send message to man
    //type = false send message to woman
    String rmi = "";
    Request request = null;
    int toId = -1;
    //


    public P(int id, int[] mpref, HashMap<Integer, List<ConflictPair>> prerequisite){
        this.id = id;
        this.mpref = mpref;
        this.prerequisite = prerequisite;
        curIdx = -1;
        this.n = mpref.length;
        this.mutex = new ReentrantLock();
    }

    //assumption
    public Response Call(String rmi, Request req, int id) {
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
                        mutex.lock();
                        try{
                            rmi = "advance";
                            request = new Request(id, cp.regret);
                            toId = cp.pId;
                            new Thread(this).start();
                        }finally {
                            mutex.unlock();
                        }

//                        CallMan("advance", new Request(id, cp.regret), cp.pId);
                    }
                }
                mutex.lock();
                try{
                    rmi = "proposal";
                    request = new Request(id, curIdx);
                    toId = mpref[curIdx];
                    new Thread(this).start();
                }finally {
                    mutex.unlock();
                }
//                CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
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
                    mutex.lock();
                    try{
                        rmi = "advance";
                        request = new Request(id, cp.regret);
                        toId = cp.pId;
                        new Thread(this).start();
                    }finally {
                        mutex.unlock();
                    }
//                    CallMan("advance", new Request(id, cp.regret), cp.pId);
                }
            }
        }
        mutex.lock();
        try{
            rmi = "proposal";
            request = new Request(id, curIdx);
            toId = mpref[curIdx];
            new Thread(this).start();
        }finally {
            mutex.unlock();
        }
//                CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
        return new Response(true);
    }

    @Override
    public Response SignalHandler(Request req) throws RemoteException {
        return null;
    }

    @Override
    public void run() {
        Call(rmi, request, toId);
    }
}
