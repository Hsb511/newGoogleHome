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

    public AppliDevice(InputStream inputStream) throws InvalidDescriptionException {
        super(inputStream);
    }

    public void startDevice() {
        Action getStateAction = getAction("GetAskingState");
        getStateAction.setActionListener(this);

        Action setAvailableShowersAction = getAction("SetAvailableShowers");
        setAvailableShowersAction.setActionListener(this);

        ServiceList serviceList = getServiceList();
        Service service = serviceList.getService(0);
        service.setQueryListener(this);

        Log.d("DEVICE","Device lancé!");
        this.start();
    }

    private String nbAvailableShowers;

    private String nameBuilding = "i1";
    private String nameFloor = "1";

    ////////////////////////////////////////////////
    //	Asking number of shower available
    ////////////////////////////////////////////////

    private boolean askFlag = false;

    public void startAsking() {
        askFlag = true;
        stateVar = getStateVariable("AskingState");
        stateVar.setValue("Start");
        System.out.println("Demande lancée!");
    }

    public void stopAsking() {
        askFlag = false;
        stateVar = getStateVariable("AskingState");
        stateVar.setValue("Stop");
        System.out.println("Demande arrêtée!");
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
        System.out.println("ActionName : " + actionName);

        boolean ret = false;

        if (actionName.equals("GetAskingState") == true) {
            String state = getAskingState();
            Argument stateArg = action.getArgument("AskingState");
            stateArg.setValue(state);
            ret = true;
        }

        if (actionName.equals("SetAvailableShowers") == true) {
            Argument nbOfShower = action.getArgument("NbAvailableShowers");
            nbAvailableShowers = nbOfShower.getValue();
            //stopAsking();
            System.out.println("Nombre de douches : " + nbAvailableShowers);
            ret = true;
        }

        if (actionName.equals("GetBuildingFloor") == true) {
            Argument buildingArg = action.getArgument("nameBuilding");
            buildingArg.setValue(nameBuilding);
            Argument floorArg = action.getArgument("nameFloor");
            floorArg.setValue(nameFloor);
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
}

