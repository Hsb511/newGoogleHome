package team23.speechrecognition;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowerActivity extends AppCompatActivity {
    protected TextView infoTV;
    protected TextView showerTV;
    protected String myBuilding;
    protected String myFloor;

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

        //TODO RECUPERE L'INFOS DU NOMBRE DE DOUCHES A MON BATIMENT ET A MON ETAGE
        showerTV.setText("3");
    }
}
