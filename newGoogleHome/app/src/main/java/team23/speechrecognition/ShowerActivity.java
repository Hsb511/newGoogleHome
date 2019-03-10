package team23.speechrecognition;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.InvalidDescriptionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShowerActivity extends AppCompatActivity {
    // UI
    protected TextView infoTV;
    protected TextView showerTV;
    protected LinearLayout stateLayout;
    protected  ImageView douche1;
    protected  ImageView douche2;
    protected  ImageView douche3;
    protected TextView consumptionTV;
    protected TextView ecoTipTV;
    protected Button refreshBtn;
    protected  ArrayList<ImageView> douches;

    private AppliDevice cyberlinkDevice;
    protected String myBuilding;
    protected String myFloor;
    protected static int screenWidth;
    protected  Drawable freeDrawable;
    protected  Drawable takenDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower);
        infoTV = findViewById(R.id.info_textView);
        stateLayout = findViewById(R.id.show_showers_state_layout);
        showerTV = findViewById(R.id.available_shower);
        consumptionTV = findViewById(R.id.consumption_text_view);
        refreshBtn = findViewById(R.id.resfresh_button);
        ecoTipTV = findViewById(R.id.eco_tip_text_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        SharedPreferences preferences =
                getSharedPreferences(InformationActivity.MY_PREFERENCES, MODE_PRIVATE);
        myBuilding = preferences.getString(InformationActivity.MY_BUILDING, "");
        myFloor = preferences.getString(InformationActivity.MY_FLOOR, "");

        String displayInfo = getResources().getString(R.string.building) + " : "
                + myBuilding + " - "
                + getResources().getString(R.string.floor) + " : "
                + myFloor;
        infoTV.setText(displayInfo);

        freeDrawable = getResources().getDrawable(R.drawable.free_shower);
        takenDrawable = getResources().getDrawable(R.drawable.using_shower);

        douche1 = new ImageView(getApplicationContext());
        douche2 = new ImageView(getApplicationContext());
        douche3 = new ImageView(getApplicationContext());
        douches = new ArrayList<>();
        douches.add(douche1);
        douches.add(douche2);
        douches.add(douche3);

        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
        new StartDeviceTask().execute();

        refreshBtn.setOnClickListener(handleClickRefresh);

    }

    @Override
    public void onStop() {
        super.onStop();
        //cyberlinkDevice.stop();
    }

    @SuppressLint("SetTextI18n")
    public void showShowerInfo(String showersState) {
        String[] showersInformation = showersState.split("/");
        if (showersInformation.length < 4) {
            Log.d("DEVICE", "error nb arguments");
        } else {
            int showersTotalAmount = Integer.parseInt(showersInformation[1]);
            int showersTotalAvailable = Integer.parseInt(showersInformation[0]);
            int showersWidth = Math.round((screenWidth - 100)/showersTotalAmount);
            int showersHeight = Math.round(showersWidth * 13/6.5f);
            showerTV.setText(showersInformation[0]+"/"+showersInformation[1]);
            String consumption =
                    "Consommation des 3 dernières semaines de l'étage en eau: "+showersInformation[2]+"L";
            consumptionTV.setText(consumption);
            ecoTipTV.setText("Eco tip: " + showersInformation[3]);

            for (int i = 0; i < showersTotalAvailable; i++) {
                douches.get(i).setImageBitmap(convertToBitmap(freeDrawable, showersWidth, showersHeight));
            }
            for (int i = showersTotalAvailable; i < showersTotalAmount - showersTotalAvailable +1; i++ ) {
                douches.get(i).setImageBitmap(convertToBitmap(takenDrawable, showersWidth, showersHeight));
            }
            for (int i = 0; i < showersTotalAmount; i++) {
                stateLayout.addView(douches.get(i));
            }
        }
    }

    private View.OnClickListener handleClickRefresh = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
        if (cyberlinkDevice != null) {
            new StartAskingTask().execute();
            Log.d("DEVICE", "Asking again");
        } else {
            Log.d("DEVICE","Trying to ask when device not initialised");
            //new StartDeviceTask().execute();
        }
        }
    };


    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    private class StartDeviceTask extends AsyncTask<Void, Void, String> {
        private String showersState = "///";
        @Override
        protected String doInBackground(Void... params) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.description_device);
                cyberlinkDevice = new AppliDevice(inputStream);
                SharedPreferences preferences =
                        getSharedPreferences(InformationActivity.MY_PREFERENCES, MODE_PRIVATE);
                myBuilding = preferences.getString(InformationActivity.MY_BUILDING, "i1");
                myFloor = preferences.getString(InformationActivity.MY_FLOOR, "0");
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
                Log.d("DEVICE", "CREATED DEVICE");
                cyberlinkDevice.startDevice();
                Log.d("DEVICE", "STARTED DEVICE");
                cyberlinkDevice.startAsking();
                while (cyberlinkDevice.getFloorConsumption().equals("")
                        && cyberlinkDevice.getEcoTip().equals("")
                        &&  cyberlinkDevice.getNbAvailableShowers().equals("")
                        && cyberlinkDevice.getNbTotalShowers().equals("")  ) {
                    continue;
                }
                showersState = cyberlinkDevice.getNbAvailableShowers()+
                        "/"+cyberlinkDevice.getNbTotalShowers()+
                        "/"+cyberlinkDevice.getFloorConsumption()+
                        "/"+cyberlinkDevice.getEcoTip();
                Log.d("DEVICE", "info gotten by activity: "+showersState);
            } catch (InvalidDescriptionException e) {
                String errMsg = e.getMessage();
                Log.d("DEVICE", "InvalidDescriptionException = " + errMsg);
            }
            return showersState;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("DEVICE", "onpostexecute: "+result);
            showShowerInfo(result);
        }
    }

    private class StartAskingTask extends AsyncTask<Void, Void, String> {
        private String showersState = "///";
        @Override
        protected String doInBackground(Void... params) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            cyberlinkDevice.startAsking();
            while (cyberlinkDevice.getFloorConsumption().equals("")
                    && cyberlinkDevice.getEcoTip().equals("")
                    &&  cyberlinkDevice.getNbAvailableShowers().equals("")
                    && cyberlinkDevice.getNbTotalShowers().equals("")  ) {
                continue;
            }
            showersState = cyberlinkDevice.getNbAvailableShowers()+
                    "/"+cyberlinkDevice.getNbTotalShowers()+
                    "/"+cyberlinkDevice.getFloorConsumption()+
                    "/"+cyberlinkDevice.getEcoTip();
            Log.d("DEVICE", "Info gotten by activity: "+showersState);
            return showersState;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("DEVICE", "on Post execute START ASKING : "+result);
            showShowerInfo(result);
        }
    }
}
