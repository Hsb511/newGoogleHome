package team23.speechrecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Spinner buildingsSpinner = (Spinner) findViewById(R.id.spinner_building);
        ArrayAdapter<CharSequence> adapterBuilding = ArrayAdapter.createFromResource(this,
                R.array.building_names, android.R.layout.simple_spinner_item);
        adapterBuilding.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingsSpinner.setAdapter(adapterBuilding);

        Spinner floorSpinner = (Spinner) findViewById(R.id.spinner_floor);
        ArrayAdapter<CharSequence> adapterFloor = ArrayAdapter.createFromResource(this,
                R.array.floor_names, android.R.layout.simple_spinner_item);
        adapterFloor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(adapterFloor);
    }

}
