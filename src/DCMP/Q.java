package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Q implements QRMI {
    ReentrantLock mutex;
    int id;
    // <K, V> = <MAN_ID, RANK OF THE MAN>
    public HashMap<Integer, Integer> rank;
    int partner;
    String[] peers;
    int[] ports;
    int nsize;
    Registry registry;
    QRMI stub;

    public Q(int id, HashMap<Integer, Integer> rank, String[] peers, int[] ports){
        this.id = id;
        this.rank = rank;
        this.partner = -1;
        this.peers = peers;
        this.ports = ports;
        this.nsize = peers.length/2;
        this.mutex = new ReentrantLock();
        try{
            System.setProperty("java.rmi.server.hostname", this.peers[nsize+this.id]);
            registry = LocateRegistry.createRegistry(this.ports[nsize+this.id]);
            stub = (QRMI) UnicastRemoteObject.exportObject(this, this.ports[nsize+this.id]);
            registry.rebind("DCMP", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public Response Call(String rmi, Request req, int id){
        Response callReply = null;
        PRMI stub;
        try{
            Registry registry=LocateRegistry.getRegistry(this.ports[id]);
            stub=(PRMI) registry.lookup("DCMP");
            if(rmi.equals("Reject")) {
                System.out.println("woman "+req.myId+" call Reject to man "+id);
                callReply = stub.RejectHandler(req);
            }
            else
                System.out.println("Wrong parameters!");
        } catch(Exception e){
            return null;
        }
        return callReply;
    }

    @Override
    public synchronized Response ProposalHandler(Request req) throws RemoteException {
        System.out.println("    woman "+this.id + " is in Propose handler requested from man "+req.myId);
//      System.out.println("        partner:"+this.partner+ "(rank"+rank.get(this.partner)+") thisman:"+req.myId+ "(rank"+ rank.get(req.myId)+")");


        int thisMan = req.myId;
            if (this.partner == -1) {
                this.partner = thisMan;
                return new Response(true);
            }
            if (rank.get(this.partner) < rank.get(thisMan)) {
                Call("Reject", new Request(this.id,-1),thisMan);
                return new Response(false);
            } else {
                Call("Reject", new Request(this.id, -1),this.partner);//???
                this.partner = thisMan;
                return new Response(true);
            }
    }

    @Override
    public synchronized Response SignalHandler(Request req) throws RemoteException {
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
