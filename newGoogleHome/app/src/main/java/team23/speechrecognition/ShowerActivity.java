package team23.speechrecognition;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import java.io.IOException;
import java.io.InputStream;

public class ShowerActivity extends AppCompatActivity {
    protected TextView infoTV;
    protected TextView showerTV;
    protected String myBuilding;
    protected String myFloor;
    private AppliDevice cyberlinkDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower);

        infoTV = findViewById(R.id.info_textView);
        SharedPreferences preferences = getSharedPreferences(InformationActivity.MY_PREFERENCES, MODE_PRIVATE);
        myBuilding = preferences.getString(InformationActivity.MY_BUILDING, "i1");
        myFloor = preferences.getString(InformationActivity.MY_FLOOR, "1");
        String displayInfo = getResources().getString(R.string.building) + " : "
                + myBuilding + " - "
                + getResources().getString(R.string.floor) + " : "
                + myFloor;
        infoTV.setText(displayInfo);

        showerTV = findViewById(R.id.available_shower);

        //TODO: RECUPERE L'INFOS DU NOMBRE DE DOUCHES A MON BATIMENT ET A MON ETAGE
        showerTV.setText("3");

        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
        Log.d("DEVICE","should start device");
        new StartDeviceTask().execute();
    }

    private class StartDeviceTask extends AsyncTask {
        public static final String TAG = "StartControlPointTask";

        @Override
        protected Object doInBackground(Object... params) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.description_device);
                cyberlinkDevice = new AppliDevice(inputStream);
                SharedPreferences preferences = getSharedPreferences(InformationActivity.MY_PREFERENCES, MODE_PRIVATE);
                myBuilding = preferences.getString(InformationActivity.MY_BUILDING, "i1");
                myFloor = preferences.getString(InformationActivity.MY_FLOOR, "1");
                cyberlinkDevice.setNameBuilding(myBuilding);
                cyberlinkDevice.setNameFloor(myFloor);
                Service upnpService = cyberlinkDevice.getService("urn:schemas-upnp-org:serviceId:state:1");
                InputStream stream = getResources().openRawResource(R.raw.description_services);
                String xmlServices = "";
                try {
                    byte[] buffer = new byte[stream.available()];
                    stream.read(buffer);
                    stream.close();
                    xmlServices = new String(buffer);
                } catch (IOException e) {
                    // Error handling
                }
                boolean scpdSuccess = upnpService.loadSCPD(xmlServices);
                Log.d("DEVICE", "CREATED DEVICE...........");
                cyberlinkDevice.startDevice();
                cyberlinkDevice.startAsking();

                Log.d("DEVICE", "STARTED DEVICE...........");

            } catch (InvalidDescriptionException e) {
                String errMsg = e.getMessage();
                Log.d("DEVICE", "InvalidDescriptionException = " + errMsg);
            }
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cyberlinkDevice.stop();
    }
}
