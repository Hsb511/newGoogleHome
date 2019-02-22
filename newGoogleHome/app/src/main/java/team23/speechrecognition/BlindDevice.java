package team23.speechrecognition;

import android.util.Log;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import java.io.InputStream;

public class BlindDevice extends Device implements ActionListener, QueryListener {
	
	private StateVariable upState;
	private boolean upFlag;
	
	public BlindDevice(InputStream inputStream) throws InvalidDescriptionException {
		super(inputStream);
		/*
		Action upAction = getAction("Up");
        //Log.d("DEVICE", "UPACTION"+upAction.toString());
		upAction.setActionListener(this);
		
		Action downAction = getAction("Down");
		downAction.setActionListener(this);
		
		upState = getStateVariable("up");
        Log.d("DEVICE","Device created");*/
	}
		
	@Override
	public boolean actionControlReceived(Action action) {
		String actionName = action.getName();

		boolean ret = false;
		
		if (actionName.equals("GetStatus") == true) {
			String state = getUpState();
			Argument upArg = action.getArgument("Up");
			upArg.setValue(state);
			ret = true;
		}
		if (actionName.equals("Up") == true) {
			up();
			Argument resultArg = action.getArgument("Result");
			resultArg.setValue(upState.getValue());
			ret = true;
		}
		if (actionName.equals("Down") == true) {
			down();
			Argument resultArg = action.getArgument("Result");
			resultArg.setValue(upState.getValue());
			ret = true;
		}
		return ret;
	}
	
	public void up() {
		upFlag = true;
		upState.setValue("up");
	}
	
	public void down() {
		upFlag = false;
		upState.setValue("down");
	}
	
	public boolean isUp() {
		return upFlag;
	}
	
	public String getUpState() {
		if (upFlag) return "1";
		else return "0";
	}	
	
	public boolean queryControlReceived(StateVariable stateVar) {
		stateVar.setValue(getUpState());
		return true;
	}

}