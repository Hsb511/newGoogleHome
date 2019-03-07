package team23.speechrecognition;

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
    protected static TextView infoTV;
    protected static TextView showerTV;
    protected static LinearLayout stateLayout;
    protected String myBuilding;
    protected String myFloor;
    private AppliDevice cyberlinkDevice;
    protected static int screenWidth;
    protected int showerImageHeight;
    protected String nbTotalShowers = "";
    protected String nbAvailableShowers = "";
    protected static Drawable freeDrawable;
    protected static Drawable takenDrawable;
    protected static ImageView douche1;
    protected static ImageView douche2;
    protected static ImageView douche3;
    protected static ArrayList<ImageView> douches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower);
        infoTV = findViewById(R.id.info_textView);
        stateLayout = findViewById(R.id.show_showers_state_layout);
        showerTV = findViewById(R.id.available_shower);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        SharedPreferences preferences = getSharedPreferences(InformationActivity.MY_PREFERENCES, MODE_PRIVATE);
        myBuilding = preferences.getString(InformationActivity.MY_BUILDING, "i1");
        myFloor = preferences.getString(InformationActivity.MY_FLOOR, "1");
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
        Log.d("DEVICE","should start device");
        new StartDeviceTask().execute();

        try {
            wait(10000);

        } catch (Exception e) {

        }
    }

    private class StartDeviceTask extends AsyncTask<Void, Void, String> {
        public static final String TAG = "StartControlPointTask";
        private String showersState = "/";
        @Override
        protected String doInBackground(Void... params) {
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
                while (showersState.equals("/")) {
                    showersState = cyberlinkDevice.getNbAvailableShowers()+
                            "/"+cyberlinkDevice.getNbTotalShowers();
                }
                Log.d("DEVICE", "STARTED DEVICE...........");
            } catch (InvalidDescriptionException e) {
                String errMsg = e.getMessage();
                Log.d("DEVICE", "InvalidDescriptionException = " + errMsg);
            }
            return showersState;
        }

    //       public AsyncResponse delegate = null;


        @Override
        protected void onPostExecute(String result) {
            Log.d("DEVICE", "onpostexecute: "+result);
            showShowerInfo(result);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cyberlinkDevice.stop();
    }

    public static void showShowerInfo(String showersState) {
        String[] showers = showersState.split("/");
        int showersTotalAmount = Integer.parseInt(showers[1]);
        int showersTotalAvailable = Integer.parseInt(showers[0]);
        int showersWidth = Math.round((screenWidth - 100)/showersTotalAmount);
        int showersHeight = Math.round(showersWidth * 13/6.5f);
        showerTV.setText(showersState);

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

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }
}
