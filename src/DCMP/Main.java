package DCMP;

public class Main {
    public static void main(String[] args){

    }
    P[] ps = null;
    Q[] qs = null;
    public void initPQ(int nsize){
        String host = "127.0.0.1";
        String[] peers = new String[nsize];
        int[] ports = new int[nsize];
        ps = new P[nsize/2];
        qs = new Q[nsize/2];
        for(int i = 0 ; i < nsize; i++){
            ports[i] = 1100+i;
            peers[i] = host;
        }
        for(int i = 0; i < nsize/2; i++){
            ps[i] = new P(i,null, null, peers, ports);
        }
        for(int i = 0; i < nsize/2; i++){
            qs[i] = new Q(i, null, peers, ports);
        }
    }

}
