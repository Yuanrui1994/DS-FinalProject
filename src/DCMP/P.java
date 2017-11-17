package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class P implements PRMI{
    ReentrantLock mutex;
    public int id;
    //mpref[i] = woman id of rank i
    public int[] mpref;
    //K = STEP V = List of ConflictPairs at this point
    public HashMap<Integer, LinkedList<ConflictPair>> prerequisite;
    public int curIdx;
    public String[] peers;
    public int[] ports;
    public int nsize;
    Registry registry;
    PRMI stub;
    private int n;


    public P(int id, int[] mpref, HashMap<Integer, LinkedList<ConflictPair>> prerequisite, String[] peers, int[] ports){
        this.id = id;
        this.mpref = mpref;
        this.prerequisite = prerequisite;
        curIdx = -1;
        this.peers = peers;
        this.ports = ports;
        this.nsize = peers.length/2;
        try{
            System.setProperty("java.rmi.server.hostname", this.peers[this.id]);
//            Registry reg = LocateRegistry.getRegistry (this.ports[this.id]);
//            if(reg==null){
//                System.out.println(this.id+"  port is closing");
//                try {
//                    UnicastRemoteObject.unexportObject(this.registry, true);
//                } catch(Exception e){
//                    System.out.println("None reference");
//                }
//            }
            registry = LocateRegistry.createRegistry(this.ports[this.id]);
            stub = (PRMI) UnicastRemoteObject.exportObject(this, this.ports[this.id]);
            registry.rebind("DCMP", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
        this.n = mpref.length;
        this.mutex = new ReentrantLock();
    }

    public synchronized Response InitHandler( ) {
        Runnable r = new PClient("Propose", new Request(this.id, curIdx++), this.mpref[0], this.ports);
        new Thread(r).start();
        return null;
    }

    @Override
    public synchronized Response RejectHandler(Request req) throws RemoteException {
        System.out.println("    man "+this.id + " is in Rejestion handler requested from woman "+req.myId);
        if (curIdx != -1 && mpref[curIdx] == req.myId) {
            if (curIdx == n - 1) {
                // do i need to broadcast this message?
                System.out.println("no constrained stable marriage possible");
            }else{
                curIdx++;
                if(prerequisite!=null && this.prerequisite.containsKey(curIdx)) {
                    LinkedList<ConflictPair> conflictPairs = this.prerequisite.get(curIdx);
                    if (conflictPairs != null) {
                        for (ConflictPair cp : conflictPairs) {
                            Runnable r = new PClient("Advance", new Request(id, cp.regret), cp.pId, this.ports);
                            new Thread(r).start();
                        }
                    }
                }
                    Runnable r = new PClient("Propose", new Request(id, curIdx), mpref[curIdx], this.ports);
                    new Thread(r).start();

            }
        }
        return new Response(true);
    }

    @Override
    public synchronized Response AdvanceHandler(Request req) throws RemoteException {
//        System.out.println("    man "+this.id + " is in Advance handler requested from man "+req.myId);
//        System.out.println("        my curIdx= "+curIdx+"  req.regret= "+req.regret);
        if(curIdx>req.regret){
            return new Response(true);
        }
        while(curIdx < req.regret) {
            curIdx++;
//            System.out.println("            curIdx= "+curIdx);
//            System.out.println("                heyheyheyheyhey1:");
            if(prerequisite!=null && prerequisite.containsKey(curIdx)) {
                LinkedList<ConflictPair> conflictPairs = prerequisite.get(curIdx);
//                System.out.println("                heyheyheyheyhey2:");
//                System.out.println("                conflictPair:" + conflictPairs);
                if (conflictPairs != null) {
                    for (ConflictPair cp : conflictPairs) {
                        Runnable r = new PClient("Advance", new Request(id, cp.regret), cp.pId, this.ports);
                        new Thread(r).start();
                    }
                }
            }
        }
        Runnable r = new PClient("Propose", new Request(id, curIdx), mpref[curIdx], this.ports);
        new Thread(r).start();
        return new Response(true);
    }

    @Override
    public Response SignalHandler(Request req) throws RemoteException {
        return null;
    }

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
