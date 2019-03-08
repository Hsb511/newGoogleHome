package team23.speechrecognition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import static org.cybergarage.http.HTTP.HEAD;

public class InformationActivity extends AppCompatActivity {
    public static final String MY_PREFERENCES = "preferencesors";
    public static final String MY_BUILDING = "buildingdong";
    public static final String MY_FLOOR = "dance_floor";
    protected String buildingValue = "";
    protected String floorValue = "";
    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        preferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        // TODO: if building and floor already stored in phone, we go straight to shower activity
        // make launcher activity to decide which activity to show
        final SharedPreferences preferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        buildingValue = preferences.getString(InformationActivity.MY_BUILDING, "");
        floorValue = preferences.getString(InformationActivity.MY_FLOOR, "");
        if (buildingValue.equals("") && !floorValue.equals("")) {
            findViewById(R.id.info_layout).setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(InformationActivity.this, ShowerActivity.class);
            startActivity(intent);
        }
        // otherwise :
        final Spinner buildingsSpinner = (Spinner) findViewById(R.id.spinner_building);
        ArrayAdapter<CharSequence> adapterBuilding = ArrayAdapter.createFromResource(this,
                R.array.building_names, android.R.layout.simple_spinner_item);
        adapterBuilding.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingsSpinner.setAdapter(adapterBuilding);
        buildingsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                buildingValue = ((TextView) view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner floorSpinner = (Spinner) findViewById(R.id.spinner_floor);
        ArrayAdapter<CharSequence> adapterFloor = ArrayAdapter.createFromResource(this,
                R.array.floor_names, android.R.layout.simple_spinner_item);
        adapterFloor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(adapterFloor);


        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                floorValue = ((TextView) view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationActivity.this, ShowerActivity.class);
                editor.putString(MY_BUILDING, buildingValue);
                editor.putString(MY_FLOOR, floorValue);
                editor.commit();
                findViewById(R.id.info_layout).setVisibility(View.GONE);
                startActivity(intent);
            }
        });
    }

}

