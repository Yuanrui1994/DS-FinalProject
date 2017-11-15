package DCMP;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Q implements QRMI {
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
            if(rmi.equals("Reject"))
                callReply = stub.RejectHandler(req);
            else
                System.out.println("Wrong parameters!");
        } catch(Exception e){
            return null;
        }
        return callReply;
    }

    @Override
    public Response ProposalHandler(Request req) throws RemoteException {
        int thisMan = req.myId;
        if(this.partner==-1){
            this.partner = thisMan;
            return new Response(true);
        }
        if(rank.get(this.partner)<rank.get(thisMan)){
            return new Response(false);
        }
        else{
            this.partner = thisMan;
            Response resp = Call("Reject", req, this.id);//???
            return new Response(true);
        }
    }

    @Override
    public Response SignalHandler(Request req) throws RemoteException {
        return null;
    }
}
