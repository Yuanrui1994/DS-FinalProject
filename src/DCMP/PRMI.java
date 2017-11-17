package DCMP;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PRMI extends Remote {
  //  Response InitHandler(Request req) throws RemoteException;
    Response RejectHandler(Request req) throws RemoteException;
    Response AdvanceHandler(Request req) throws RemoteException;
    Response SignalHandler(Request req) throws RemoteException;
}