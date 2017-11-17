package DCMP;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PClient implements Runnable{
    public String rmi = "";
    public Request request = null;
    public int toId = -1;
    public int[] ports;
    public int nsize;
    public PClient(String rmi, Request request, int toId, int[] ports) {
        this.rmi = rmi;
        this.request = request;
        this.toId = toId;
        this.ports = ports;
        this.nsize = ports.length / 2;
    }
    public Response Call(String rmi, Request req, int id){
        Response callReply = null;
        Registry registry = null;
        try{
            //Registry registry=LocateRegistry.getRegistry(this.ports[id]);
            //stub=(PRMI) registry.lookup("DCMP");
            if(rmi.equals("Advance")) {
                registry= LocateRegistry.getRegistry(this.ports[id]);
                PRMI stub = (PRMI) registry.lookup("DCMP");
                System.out.println("man "+req.myId+" call Advance to man "+id);
                callReply = stub.AdvanceHandler(req);
            }
            else if(rmi.equals("Init")) {
//                registry=LocateRegistry.getRegistry(this.ports[id]);
//                PRMI stub = (PRMI) registry.lookup("DCMP");
//                callReply = stub.InitHandler(req);
                // System.out.println("Wrong parameters!");
            }
            else if(rmi.equals("Propose")) {
                registry=LocateRegistry.getRegistry(this.ports[nsize+id]);
                QRMI stub = (QRMI) registry.lookup("DCMP");
                System.out.println("man "+req.myId+" call Propose to woman "+id);
                callReply = stub.ProposalHandler(req);
            }
            else{
                System.out.println("Wrong parameter.");
            }
        } catch(Exception e){
            return null;
        }
        return callReply;
    }

    @Override
    public void run() {
        try {
            Call(rmi, request, toId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }
}
