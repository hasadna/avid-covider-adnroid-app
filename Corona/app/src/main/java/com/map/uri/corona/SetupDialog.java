package com.map.uri.corona;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetupDialog extends Dialog implements View.OnClickListener {
    Context context;

    public SetupDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupdialoglayout);

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
                    context.startActivity(Intent.createChooser(intent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        final Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        switch (v.getId()) {
            case R.id.addBtn:
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                // notification time
                                long when = calendar.getTimeInMillis();
                                long interval = AlarmManager.INTERVAL_DAY;

                                alarmManager.setRepeating(AlarmManager.RTC, when, interval, pendingIntent);

                                String s = context.getResources().getString(R.string.toast_notification_created);
                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = context.getSharedPreferences("corona", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("hourOfDay", hourOfDay);
                                editor.putInt("minute", minute);
                                editor.putLong("interval",interval);
                                editor.commit();
                            }
                        }, hour, minute, true);
                timePickerDialog.setTitle("daily reminder at:");
                timePickerDialog.show();
                break;
            case R.id.deleteBtn:
                alarmManager.cancel(pendingIntent);
                String s = context.getResources().getString(R.string.toast_notification_canceled);
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                break;
        }
        dismiss();
    }
}
