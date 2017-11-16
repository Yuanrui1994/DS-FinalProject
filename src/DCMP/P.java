package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class P implements PRMI{
    ReentrantLock mutex;
    public int id;
    //mpref[i] = woman id of rank i
    public int[] mpref;
    //K = STEP V = List of ConflictPairs at this point
    public HashMap<Integer, List<ConflictPair>> prerequisite;
    public int curIdx;
    public String[] peers;
    public int[] ports;
    public int nsize;
    Registry registry;
    PRMI stub;
    private int n;

//    // for thread
//    //type = true send message to man
//    //type = false send message to woman
//    String rmi = "";
//    Request request = null;
//    int toId = -1;
//    //


    public P(int id, int[] mpref, HashMap<Integer, List<ConflictPair>> prerequisite, String[] peers, int[] ports){
        this.id = id;
        this.mpref = mpref;
        this.prerequisite = prerequisite;
        curIdx = -1;
        this.peers = peers;
        this.ports = ports;
        this.nsize = peers.length/2;
        try{
            System.setProperty("java.rmi.server.hostname", this.peers[this.id]);
            registry = LocateRegistry.createRegistry(this.ports[this.id]);
            stub = (PRMI) UnicastRemoteObject.exportObject(this, this.ports[this.id]);
            registry.rebind("DCMP", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
        this.n = mpref.length;
        this.mutex = new ReentrantLock();
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
//                        mutex.lock();
//                        try{
//                            rmi = "advance";
//                            request = new Request(id, cp.regret);
//                            toId = cp.pId;
//                            new Thread(this).start();
//                        }finally {
//                            mutex.unlock();
//                        }
//
////                        CallMan("advance", new Request(id, cp.regret), cp.pId);
                        Runnable r = new PClient("advance", new Request(id, cp.regret), cp.pId, this.ports);
                        new Thread(r).start();
                    }
                }
//                mutex.lock();
//                try{
//                    rmi = "proposal";
//                    request = new Request(id, curIdx);
//                    toId = mpref[curIdx];
//                    new Thread(this).start();
//                }finally {
//                    mutex.unlock();
//                }
////                CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
                Runnable r = new PClient("proposal", new Request(id, curIdx), mpref[curIdx], this.ports);
                new Thread(r).start();
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
//                    mutex.lock();
//                    try{
//                        rmi = "advance";
//                        request = new Request(id, cp.regret);
//                        toId = cp.pId;
//                        new Thread(this).start();
//                    }finally {
//                        mutex.unlock();
//                    }
//                    CallMan("advance", new Request(id, cp.regret), cp.pId);
                    Runnable r = new PClient("advance", new Request(id, cp.regret), cp.pId, this.ports);
                    new Thread(r).start();
                }
            }
        }
//        mutex.lock();
//        try{
//            rmi = "proposal";
//            request = new Request(id, curIdx);
//            toId = mpref[curIdx];
//            new Thread(this).start();
//        }finally {
//            mutex.unlock();
//        }
//                CallWoman("proposal", new Request(id, curIdx), mpref[curIdx]);
        Runnable r = new PClient("proposal", new Request(id, curIdx), mpref[curIdx], this.ports);
        new Thread(r).start();
        return new Response(true);
    }

    @Override
    public Response SignalHandler(Request req) throws RemoteException {
        return null;
    }
//
//    @Override
//    public void run() {
//        Call(rmi, request, toId);
//    }
//
//    public Response Call(String rmi, Request req, int id){
//        Response callReply = null;
//        Registry registry = null;
//        //PRMI stub;
//        try{
//            //Registry registry=LocateRegistry.getRegistry(this.ports[id]);
//            //stub=(PRMI) registry.lookup("DCMP");
//            if(rmi.equals("Advance")) {
//                registry=LocateRegistry.getRegistry(this.ports[id]);
//                PRMI stub = (PRMI) registry.lookup("DCMP");
//                callReply = stub.AdvanceHandler(req);
//            }
//            else if(rmi.equals("Init")) {
//                registry=LocateRegistry.getRegistry(this.ports[id]);
//                PRMI stub = (PRMI) registry.lookup("DCMP");
//                callReply = stub.InitHandler(req);
//               // System.out.println("Wrong parameters!");
//            }
//            else if(rmi.equals("Propose")) {
//                registry=LocateRegistry.getRegistry(this.ports[nsize+id]);
//                QRMI stub = (QRMI) registry.lookup("DCMP");
//                callReply = stub.ProposalHandler(req);
//            }
//            else{
//                System.out.println("Wrong parmeter.");
//            }
//        } catch(Exception e){
//            return null;
//        }
//        return callReply;
//    }
    public void Kill(){
        if(this.registry != null){
            try {
                UnicastRemoteObject.unexportObject(this.registry, true);
            } catch(Exception e){
                System.out.println("None reference");
            }
        }
    }

}
