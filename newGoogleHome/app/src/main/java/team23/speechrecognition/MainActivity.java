package team23.speechrecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.device.*;

public class MainActivity extends AppCompatActivity {
    private TextView textTV;
    private final int REQ_CODE_SPEECH_INPUT = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);

        textTV = findViewById(R.id.text_said);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        try {
            InputStream inputStream = this.getResources().openRawResource(R.description.description);
            BlindDevice blindDev = new BlindDevice(inputStream);
            Log.d("DEVICE", "CREATED DEVICE...........");
            blindDev.start();
            Log.d("DEVICE", "STARTED DEVICE...........");

        }
        catch (InvalidDescriptionException e){
            String errMsg = e.getMessage();
            Log.d("DEVICE","InvalidDescriptionException = " + errMsg);
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez !");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Reconnaissance non supportée",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textTV.setText(result.get(0));
                }
                break;
            }

        }
    }
}
