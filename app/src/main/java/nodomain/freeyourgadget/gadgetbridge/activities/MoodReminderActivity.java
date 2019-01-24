package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.alertnotification.NewAlert;

public class MoodReminderActivity extends AppCompatActivity {

    private Context context = this;

    private Button submitButton;
    private CheckBox[] moodCheckBox;
    private SeekBar[] moodSeekBar;

    private float mood_x = 0;
    private float mood_y = 0;

    static int EXCITED = 0;
    static int HAPPY = 1;
    static int PLEASED = 2;
    static int RELAXED = 3;
    static int PEACEFUL = 4;
    static int CALM = 5;
    static int BORED = 6;
    static int DEPRESSED = 7;
    static int SAD = 8;
    static int NERVOUS = 9;
    static int ANXIOUS = 10;
    static int ANGRY = 11;
    static int MOOD_NUM = 12;

    private static float[] UNIT_X = new float[MOOD_NUM];
    private static float[] UNIT_Y = new float[MOOD_NUM];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //X FOR POSITIVE; Y FOR ENERGETIC
        UNIT_X[EXCITED] = 0.1f; UNIT_Y[EXCITED] = 0.9f;
        UNIT_X[HAPPY] = 0.5f; UNIT_Y[HAPPY] = 0.5f;
        UNIT_X[PLEASED] = 0.9f; UNIT_Y[PLEASED] = 0.1f;
        UNIT_X[RELAXED] = 0.9f; UNIT_Y[RELAXED] = -0.1f;
        UNIT_X[PEACEFUL] = 0.5f; UNIT_Y[PEACEFUL] = -0.5f;
        UNIT_X[CALM] = 0.1f; UNIT_Y[CALM] = -0.9f;
        UNIT_X[BORED] = -0.1f; UNIT_Y[BORED] = -0.9f;
        UNIT_X[DEPRESSED] = -0.5f; UNIT_Y[DEPRESSED] = -0.5f;
        UNIT_X[SAD] = -0.9f; UNIT_Y[SAD] = -0.1f;
        UNIT_X[NERVOUS] = -0.9f; UNIT_Y[NERVOUS] = 0.1f;
        UNIT_X[ANXIOUS] = -0.5f; UNIT_Y[ANXIOUS] = 0.5f;
        UNIT_X[ANGRY] = -0.1f; UNIT_Y[ANGRY] = 0.9f;

        submitButton = (Button)findViewById(R.id.submitButton);
        moodCheckBox = new CheckBox[MOOD_NUM];
        moodSeekBar = new SeekBar[MOOD_NUM];

        moodCheckBox[EXCITED] = (CheckBox)findViewById(R.id.excitedCheckBox);
        moodSeekBar[EXCITED] = (SeekBar)findViewById(R.id.excitedSeekBar);
        moodCheckBox[HAPPY] = (CheckBox)findViewById(R.id.happyCheckBox);
        moodSeekBar[HAPPY] = (SeekBar)findViewById(R.id.happySeekBar);
        moodCheckBox[PLEASED] = (CheckBox)findViewById(R.id.pleasedCheckBox);
        moodSeekBar[PLEASED] = (SeekBar)findViewById(R.id.pleasedSeekBar);
        moodCheckBox[RELAXED] = (CheckBox)findViewById(R.id.relaxedCheckBox);
        moodSeekBar[RELAXED] = (SeekBar)findViewById(R.id.relaxedSeekBar);
        moodCheckBox[PEACEFUL] = (CheckBox)findViewById(R.id.peacefulCheckBox);
        moodSeekBar[PEACEFUL] = (SeekBar)findViewById(R.id.peacefulSeekBar);
        moodCheckBox[CALM] = (CheckBox)findViewById(R.id.calmCheckBox);
        moodSeekBar[CALM] = (SeekBar)findViewById(R.id.calmSeekBar);
        moodCheckBox[BORED] = (CheckBox)findViewById(R.id.boredCheckBox);
        moodSeekBar[BORED] = (SeekBar)findViewById(R.id.boredSeekBar);
        moodCheckBox[DEPRESSED] = (CheckBox)findViewById(R.id.depressedCheckBox);
        moodSeekBar[DEPRESSED] = (SeekBar)findViewById(R.id.depressedSeekBar);
        moodCheckBox[SAD] = (CheckBox)findViewById(R.id.sadCheckBox);
        moodSeekBar[SAD] = (SeekBar)findViewById(R.id.sadSeekBar);
        moodCheckBox[NERVOUS] = (CheckBox)findViewById(R.id.nervousCheckBox);
        moodSeekBar[NERVOUS] = (SeekBar)findViewById(R.id.nervousSeekBar);
        moodCheckBox[ANXIOUS] = (CheckBox)findViewById(R.id.anxiousCheckBox);
        moodSeekBar[ANXIOUS] = (SeekBar)findViewById(R.id.anxiousSeekBar);
        moodCheckBox[ANGRY] = (CheckBox)findViewById(R.id.angryCheckBox);
        moodSeekBar[ANGRY] = (SeekBar)findViewById(R.id.angrySeekBar);

        for (int i = 0; i < MOOD_NUM; i ++) {
            if (moodCheckBox[i].isChecked()) {
                moodSeekBar[i].setEnabled(true);
                moodSeekBar[i].setProgress(80);
            } else {
                moodSeekBar[i].setEnabled(false);
                moodSeekBar[i].setProgress(0);
            }
        }

        for (int i = 0; i < MOOD_NUM; i ++) {
            final int j = i;
            moodCheckBox[i].setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (compoundButton.isChecked()) {
                                moodSeekBar[j].setEnabled(true);
                                moodSeekBar[j].setProgress(80);
                                for (int k = 0; k < MOOD_NUM; k ++) {
                                    if (k != j) {
                                        moodCheckBox[k].setChecked(false);
                                    }
                                }
                            } else {
                                moodSeekBar[j].setEnabled(false);
                                moodSeekBar[j].setProgress(0);
                            }
                        }
                    }
            );
        }

        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mood_x = 0; mood_y = 0;
                        int weightSum = 0;
                        for (int i = 0; i < MOOD_NUM; i ++) {
                            mood_x += moodSeekBar[i].getProgress() * UNIT_X[i];
                            mood_y += moodSeekBar[i].getProgress() * UNIT_Y[i];
                            weightSum += moodSeekBar[i].getProgress();
                        }
                        if (weightSum != 0) {
                            mood_x = mood_x / 100;
                            mood_y = mood_y / 100;
                        } else {
                            mood_x = 0;
                            mood_y = 0;
                        }
                        Log.i("MoodReminderActivity", mood_x+" "+mood_y+" ");

                        //Toast.makeText(context, mood_x+" "+mood_y+" ", Toast.LENGTH_LONG).show();
                        //TODO: PASS THE DATA

                        Intent intent = new Intent("nodomain.freeyourgadget.gadgetbridge.GBMoodResult");
                        intent.setComponent(new ComponentName("nodomain.freeyourgadget.gadgetbridge",
                                "nodomain.freeyourgadget.gadgetbridge.service.receivers.GBMoodResultReceiver"));
                        intent.putExtra("mood_x", mood_x);
                        intent.putExtra("mood_y", mood_y);
                        sendBroadcast(intent);
                        finish();
                    }
                }
        );
    }

}
