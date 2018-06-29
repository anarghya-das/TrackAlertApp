package com.example.anarg.trackalertsystem;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

public class BackEnd {

    public ArrayList<Train> jsonGov(String s){
        ArrayList<Train> allInformation=new ArrayList<>();
        JsonValue jsonValue = Json.parse(s);
        JsonArray jsonArray = jsonValue.asArray();
        for(int i=0;i<jsonArray.size();i++){
            JsonObject o=jsonArray.get(i).asObject();
            String direction=o.get("direction").asString();
//            String locoNo=o.get("locoNo").asString();
//            long trainID=o.get("trainId").asLong();
            String trackName=o.get("trackName").asString();
            String trainName= o.get("trainName").asString();
            String trainNum=Integer.toString(o.get("trainNo").asInt());
            Train t;
            if (!trainName.isEmpty()&&!trainNum.equals("0")) {
                t = new Train(Integer.parseInt(trainNum), trainName,trackName);
            }
            else {
                t = new Train(0,null,null);
            }
            t.setDirection(direction);
            JsonArray signals=o.get("zSignals").asArray();
            int index;
            String station="No name";
            JsonArray relays=null;
            if(signals.size()!=0) {
                for (int j = 0; j < signals.size(); j++) {
                    JsonObject o1 = signals.get(j).asObject();
                    trackName = o1.get("trackName").asString();
                    station = o1.get("station").asString();
                    index= o1.get("index").asInt();
//                    System.out.println("Signal "+(j+1)+" ahead (Stn Code): "+station);
//                    System.out.println("Track Name: "+trackName);
                    JsonObject aspectSignal = o1.get("ztoAspectSignal").asObject();
                    String signalName=aspectSignal.get("objectName").asString();
//                    System.out.println("Signal "+(j+1)+" ahead Signal Name: "+signalName);
                    if (aspectSignal.get("relays").isArray()) {
                        relays = aspectSignal.get("relays").asArray();
                        ArrayList<String> channelDescriptions=new ArrayList<>();

                        for(int k=0;k<relays.size();k++){
                            JsonObject relaysObject= relays.get(k).asObject();
                            if(relaysObject.get("currentStatus").asString().equalsIgnoreCase("Up")){
                                channelDescriptions.add(relaysObject.get("channelDescription").asString());
                            }
                        }
//                        System.out.println("Signal "+(j+1)+" Aspect: "+signalColor(channelDescriptions));
                        String aspect=signalColor(channelDescriptions);
                        Signal sig=new Signal(station,signalName,aspect,index);
                        if(sig!=null) {
                            t.addSignals(sig);
                        }
                    }
                }
            }
            if(t.getTrainName()!=null) {
                allInformation.add(t);
            }
        }
        return allInformation;
    }

    private String signalColor(ArrayList<String> a) {
        String s="Yellow";
        for (String channelDescription: a) {
            if (channelDescription.contains("RGKE")) {
                s = "Red";
            } else if (channelDescription.contains("HGKE")&&channelDescription.contains("HHGKE")) {
                s = "YellowYellow";
            } else if (channelDescription.contains("DGKE")) {
                s = "Green";
            }
        }
        return  s;
    }

    public ArrayList<String> trackNames(ArrayList<Train> t){
        ArrayList<String> track=new ArrayList<>();
        for (Train to: t){
            track.add(to.getTrackName());
        }
        return track;
    }
}
