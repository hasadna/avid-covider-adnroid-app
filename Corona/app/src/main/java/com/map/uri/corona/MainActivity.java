package com.map.uri.corona;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    WebView wv;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setupdialoglayout);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            openUrl();
        } else {
            getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            Button addbtn = findViewById(R.id.addBtn);
            addbtn.setOnClickListener(this);
            Button deletebtn = findViewById(R.id.deleteBtn);
            deletebtn.setOnClickListener(this);

            Button sharebtn = findViewById(R.id.sharebtn);
            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "corona");
                        String s ="https://play.google.com/store/apps/details?id=com.map.uri.corona \n\n";
                        intent.putExtra(Intent.EXTRA_TEXT, s);
                        MainActivity.this.startActivity(Intent.createChooser(intent, "choose one"));
                    } catch(Exception e) {
                        //e.toString();
                    }
                    openUrl();
                }
            });
        }
    }

    private void openUrl(){
        String url = "https://avid-covider.phonaris.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

        finish();
        System.exit(0);
    }

    @Override
    public void onClick(View v) {
        final Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        switch (v.getId()) {
            case R.id.addBtn:
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                // notification time
                                long when = calendar.getTimeInMillis();
                                long interval =AlarmManager.INTERVAL_DAY;

                                alarmManager.setRepeating(AlarmManager.RTC, when, interval, pendingIntent);

                                String s = getResources().getString(R.string.toast_notification_created);
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getSharedPreferences("corona", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("hourOfDay", hourOfDay);
                                editor.putInt("minute", minute);
                                editor.putLong("interval",interval);
                                editor.commit();
                                openUrl();
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle("daily reminder at:");
                timePickerDialog.show();
                break;
            case R.id.deleteBtn:
                alarmManager.cancel(pendingIntent);
                String s = getResources().getString(R.string.toast_notification_canceled);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                openUrl();

                break;
        }
    }
}
