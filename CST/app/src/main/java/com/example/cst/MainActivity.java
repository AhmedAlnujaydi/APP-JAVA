package com.example.cst;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;
    EditText editText;
    ImageView imageView;

    public static final Integer RecordAudioRequestCode=1;

    private SpeechRecognizer speechRecognizer;
    AlertDialog.Builder alertSpeechDialog;
    AlertDialog alertDialog;

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);

        EditText e1 = findViewById(R.id.editText);
        ImageView b1 = findViewById(R.id.speak);
        Objects.requireNonNull(getSupportActionBar()).hide();
        b1.setOnClickListener(v -> tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setSpeechRate(1.0f);
                    tts.speak(e1.getText().toString(),TextToSpeech.QUEUE_ADD, null);
                }
            }
        }));



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                !=PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getDisplayLanguage());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

                findViewById(android.R.id.content);


                alertSpeechDialog = new AlertDialog.Builder(MainActivity.this);
                alertSpeechDialog.setMessage("Listening...");
                alertDialog = alertSpeechDialog.create();
                alertDialog.show();
            }



            @Override
            public void onRmsChanged(float msdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                imageView.setImageResource(R.drawable.baseline_mic_24);
                ArrayList<String> arrayList=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(arrayList.get(0));
                alertDialog.dismiss();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        imageView.setOnTouchListener((view, motionEvent) -> {

            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                speechRecognizer.stopListening();
            }
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                imageView.setImageResource(R.drawable.baseline_mic_24);
                speechRecognizer.startListening(speechIntent);
            }
            return false;
        });
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);

        }
    }
        @Override
        protected void onDestroy() {
            super.onDestroy();
             speechRecognizer.destroy();
        }
        @Override
                public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode, permission, grantResults);
            if(requestCode==RecordAudioRequestCode && grantResults.length>0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }
}