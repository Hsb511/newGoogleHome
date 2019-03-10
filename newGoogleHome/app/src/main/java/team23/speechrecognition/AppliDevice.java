package team23.speechrecognition;//1. Etat de l'appli : demande ou pas demande (douche libre) isAsking
//2. Controler notifié quand changement d'état (not asking --> isAsking)
//3. Utilise le service de l'appli "Get building and floor"
//4. Controler reçoit le builing et le floor qu'il attendait
//et envoie le nombre de douche dispo au batiment et étage correspondant

import android.util.Log;

import java.io.*;

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.device.*;
import org.cybergarage.upnp.control.*;

public class AppliDevice extends Device implements ActionListener, QueryListener {
    private final static String DESCRIPTION_FILE_NAME = "description/description.xml";

    private StateVariable stateVar;
    private StateVariable nbAvailableShowerState;
    private StateVariable nbTotalShowersState;
    private StateVariable floorConsumptionState;
    private String nbAvailableShowers = "";
    private String nbTotalShowers = "";
    private String nameBuilding = "";
    private String nameFloor = "";
    private String floorConsumption = "";
    private String ecoTip = "";


    public AppliDevice(InputStream inputStream) throws InvalidDescriptionException {
        super(inputStream);
    }

    public void startDevice() {
        Action getStateAction = getAction("GetAskingState");
        getStateAction.setActionListener(this);

        Action setShowersInformation = getAction("SetShowersInformation");
        setShowersInformation.setActionListener(this);

        Action getBuildingFloorAction = getAction("GetBuildingFloor");
        getBuildingFloorAction.setActionListener(this);

        ServiceList serviceList = getServiceList();
        Service service = serviceList.getService(0);
        service.setQueryListener(this);

        Log.d("DEVICE","Device lancé!");
        this.start();
    }

    public void setNameBuilding(String nameBuilding) {
        this.nameBuilding = nameBuilding;
    }

    public void setNameFloor(String nameFloor) {
        this.nameFloor = nameFloor;
    }


    ////////////////////////////////////////////////
    //	Asking number of shower available
    ////////////////////////////////////////////////

    private boolean askFlag = false;

    public void startAsking() {
        askFlag = true;
        stateVar = getStateVariable("AskingState");
        stateVar.setValue("Start");
        Log.d("DEVICE","Demande lancée!");
    }

    public void stopAsking() {
        askFlag = false;
        stateVar = getStateVariable("AskingState");
        stateVar.setValue("Stop");
        Log.d("DEVICE","Demande arrêtée!");
    }

    public String getAskingState() {
        if (askFlag == true)
            return "1";
        return "0";
    }

    ////////////////////////////////////////////////
    // ActionListener
    ////////////////////////////////////////////////

    public boolean actionControlReceived(Action action) {
        String actionName = action.getName();
        Log.d("DEVICE","ActionName : " + actionName);

        boolean ret = false;

        if (actionName.equals("GetAskingState")) {
            String state = getAskingState();
            Argument stateArg = action.getArgument("AskingState");
            stateArg.setValue(state);
            ret = true;
        }

        if (actionName.equals("SetShowersInformation")) {
            Argument nbOfShower = action.getArgument("NbAvailableShowers");
            nbAvailableShowers = nbOfShower.getValue();
            Argument nbTotalShowersArg = action.getArgument("NbTotalShowers");
            nbTotalShowers = nbTotalShowersArg.getValue();
            Argument floorConsumptionArg = action.getArgument("FloorConsumption");
            floorConsumption = floorConsumptionArg.getValue();
            Argument ecoTipArg = action.getArgument("EcoTip");
            ecoTip = ecoTipArg.getValue();
            stopAsking();
            Log.d("DEVICE", "Received shoers information");
            Log.d("DEVICE", "Nombre de douches dispo: " + nbAvailableShowers);
            Log.d("DEVICE", "Nombre de douches total: " + nbTotalShowers);
            Log.d("DEVICE", "Consommation: " + floorConsumption);
            Log.d("DEVICE", "Eco tip: " + ecoTip);
            ret = true;
        }

        if (actionName.equals("GetBuildingFloor")) {
            Argument buildingArg = action.getArgument("nameBuilding");
            buildingArg.setValue(this.nameBuilding);
            Argument floorArg = action.getArgument("nameFloor");
            floorArg.setValue(this.nameFloor);
            Log.d("DEVICE","giving building and floor");
            ret = true;
        }

        return ret;
    }

    ////////////////////////////////////////////////
    // QueryListener
    ////////////////////////////////////////////////

    public boolean queryControlReceived(StateVariable stateVar) {
        System.out.println("query control recieved");
        return false;
    }

    ////////////////////////////////////////////////
    // update
    ////////////////////////////////////////////////

    public void update() {
    }

    public String getNbAvailableShowers() {
        return nbAvailableShowers;
    }

    public String getNbTotalShowers(){
        return nbTotalShowers;
    }

    public String getFloorConsumption() { return floorConsumption; };

    public String getEcoTip() { return  ecoTip;}
}

