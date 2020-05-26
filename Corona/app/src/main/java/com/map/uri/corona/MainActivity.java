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
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Button addbtn, deletebtn, openbtn, sharebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setupdialoglayout);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        boolean f = false;

        if (b != null) {
            f = b.getBoolean("from_notification", false);
        }
        if (f == true) {
            openUrl();
        } else {
            sharedPref = getSharedPreferences("corona", Context.MODE_PRIVATE);
            editor = sharedPref.edit();

            addbtn = findViewById(R.id.addBtn);

            int hourOfDay = sharedPref.getInt("hourOfDay", 0);
            int minute = sharedPref.getInt("minute", 0);
            long interval = sharedPref.getLong("interval", 0);

            if (interval > 0) {
                String s1 = getResources().getString(R.string.daily_alart_set_to);
                String s2 = hourOfDay + ":" + ((minute<10)?"0"+minute:minute);
                String s3 = getResources().getString(R.string.change);
                addbtn.setText(s1 + "\n" + s2 + "\n" + s3);

                setUpAlarm(MainActivity.this,hourOfDay,minute,interval);

            } else {
                String s = getResources().getString(R.string.daily_reminder_setup);
                addbtn.setText(s);
            }

            addbtn.setOnClickListener(this);

            deletebtn = findViewById(R.id.deleteBtn);
            deletebtn.setOnClickListener(this);

            openbtn = findViewById(R.id.openbtn);
            openbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUrl();
                }
            });

            sharebtn = findViewById(R.id.sharebtn);
            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "corona");
                        String s = "https://play.google.com/store/apps/details?id=com.map.uri.corona \n\n";
                        intent.putExtra(Intent.EXTRA_TEXT, s);
                        MainActivity.this.startActivity(Intent.createChooser(intent, "choose one"));
                    } catch (Exception e) {
                        //e.toString();
                    }
                }
            });
        }
    }

    private void openUrl() {
        String url = "https://avid-covider.phonaris.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

        finish();
        System.exit(0);
    }

    @Override
    public void onClick(View v) {
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
                                long interval =AlarmManager.INTERVAL_FIFTEEN_MINUTES; //AlarmManager.INTERVAL_DAY;

                                setUpAlarm(MainActivity.this,hourOfDay,minute,interval);

                                editor.putInt("hourOfDay", hourOfDay);
                                editor.putInt("minute", minute);
                                editor.putLong("interval", interval);
                                editor.commit();

                                if (interval > 0) {
                                    String s1 = getResources().getString(R.string.daily_alart_set_to);
                                    String s2 = hourOfDay + ":" + ((minute<10)?"0"+minute:minute);
                                    String s3 = getResources().getString(R.string.change);
                                    addbtn.setText(s1 + "\n" + s2 + "\n" + s3);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle("daily reminder at:");
                timePickerDialog.show();
                break;
            case R.id.deleteBtn:
                Intent intentAlarm = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);

                String s = getResources().getString(R.string.toast_notification_canceled);

                editor.putInt("hourOfDay", 0);
                editor.putInt("minute", 0);
                editor.putLong("interval", 0);
                editor.commit();

                String s1 = getResources().getString(R.string.daily_reminder_setup);
                addbtn.setText(s1);

                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static void setUpAlarm(Context context,int hour,int minute,long interval){
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // notification time
        long when = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC, when, interval, pendingIntent);

        String s =context.getResources().getString(R.string.toast_notification_created);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

    }
}
