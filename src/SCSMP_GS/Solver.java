package SCSMP_GS;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;

public class Solver {
   // public class Restrictions
    public LinkedList<Woman> womenList;
    public LinkedList<Man> menList;

    public HashMap<Integer,Integer> womenBest;
    public ArrayDeque<Integer> bachelorQueue;
    public int length;
    public Solver(LinkedList<Woman> womenList, LinkedList<Man> menList){
        this.womenList = womenList;
        this.menList = menList;
        this.womenBest = new HashMap<>();
        this.bachelorQueue = new ArrayDeque<>();
        this.length = womenList.size();
        for(Man m: menList){
            this.bachelorQueue.add(m.id);
           // m.inQueue = true;
        }
    }
    public boolean findStableMarriage(){
         while(!this.bachelorQueue.isEmpty()){
             Man thisMan = menList.get(this.bachelorQueue.poll());
//             System.out.println("New Heyheyhyey this man:"+thisMan.id);
//             System.out.println("bq size: "+bachelorQueue.size());
             boolean ret = isAvailable(thisMan);
             System.out.println("finished one round:");
             System.out.println("   man "+thisMan.id + "  curIndex: "+thisMan.partnerIndex);
             if(ret==false)
                 return false;
         }
         return true;
    }

    public boolean isAvailable(Man thisMan){
        thisMan.partnerIndex++;
        //thisMan.inQueue = false;
        for(int tempSpouseIndex = thisMan.partnerIndex;tempSpouseIndex<length;tempSpouseIndex++){
            int tempSpouse = thisMan.rankmap.get(tempSpouseIndex);
            if(thisMan.conflictMap!=null && thisMan.conflictMap.containsKey(tempSpouseIndex)){
                //To resolve external conflict
                LinkedList<ConflictPair> conflictList = thisMan.conflictMap.get(tempSpouseIndex);
                Boolean isBlocked = false;
                for(ConflictPair pair : conflictList){
                    Man thatMan = menList.get(pair.manID);
                    if(thatMan.partnerIndex<pair.womanIndex){
                        //That man needs to advance, or this man will still get blocked.
                        isBlocked = true;
                        System.out.println("that man "+ thatMan.id + " blockes me");
                        if(thatMan.partnerIndex>-1 && womenBest.containsKey(thatMan.rankmap.get(thatMan.partnerIndex))) {
                            //womenBest.remove(thatMan.rankmap.get(thatMan.partnerIndex));
                            bachelorQueue.add(thatMan.id);
                        }
                        break;
                    }
                }
                if(isBlocked==true){
                    System.out.println(thisMan.id+ " being blocked.");
                    thisMan.partnerIndex--;
                    bachelorQueue.add(thisMan.id);
                    return true;  //beingBlocked
                }
            }
            if(womenBest.containsKey(tempSpouse)){ //The woman has been matched before.
                int thatMan = womenBest.get(tempSpouse);
                HashMap<Integer,Integer> herRankList = womenList.get(tempSpouse).rankmap;
                if(herRankList.get(thatMan)< herRankList.get(thisMan.id)){
                    System.out.println("rejected");
                    System.out.println("  this man: " + thisMan.id + " advanced.");
                    System.out.println("  my rank:"+herRankList.get(thisMan.id));
                    System.out.println("  that man" + thatMan +" rank:"+herRankList.get(thatMan));
                    System.out.println("  woman: "+ tempSpouse);
                    //There is an unstable pair found, this man continues advancing.
                       continue;
                }
                else{
                    //Remove the woman's current partner.
                    System.out.println("accepted");
                    System.out.println("  that man: " + thatMan + " kicked out.");
                    System.out.println("  my rank:"+herRankList.get(thisMan.id));
                    System.out.println("  that man" + thatMan +" rank:"+herRankList.get(thatMan));
                    System.out.println("  woman: "+ tempSpouse);
                   // womenBest.remove(tempSpouse);
                    bachelorQueue.add(thatMan);
                }
            }


            thisMan.partnerIndex = tempSpouseIndex;
            womenBest.put(tempSpouse,thisMan.id);
            System.out.println("matched:");
            System.out.println("  man: "+thisMan.id);
            System.out.println("  woman: "+ tempSpouse);

            return true;
        }


        return false;
    }

}
