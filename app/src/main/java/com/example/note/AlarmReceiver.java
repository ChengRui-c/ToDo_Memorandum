package com.example.note;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private int alarmId;
    int BIG_NUM_FOR_ALARM=100;
    private Context mcontext;


    @Override
    public void onReceive(Context context, Intent intent) {
        alarmId=intent.getIntExtra("alarmId",0);
        Toast.makeText(context,"Time UP!",Toast.LENGTH_LONG).show();
        //振动权限
        Vibrator vb =(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(300);
        //通知
        mcontext=context.getApplicationContext();
        showNotice(mcontext);
    }

    //提醒闹钟广播
    private void showNotice(Context context) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            int num=alarmId-BIG_NUM_FOR_ALARM;
            Log.d("MainActivity","alarmNoticeId "+num);
            Intent intent=new Intent(context,Edit.class);//转到EditActivity
            Note record= getNoteWithId(num);
            deleteTheAlarm(num);//or num
            transportInformationToEdit(intent,record);
            PendingIntent pi=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager manager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationChannel channel=new NotificationChannel("default","alarm",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            Notification notification=new NotificationCompat.Builder(context,"default")
                    .setContentTitle(record.getTextDate()+" "+record.getTextTime())
                    .setContentText(record.getMainText())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.icon))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(Uri.fromFile(new File("/System/media/audio/ringtones/Luna.ogg")))
                    .setVibrate(new long[]{1000,500,1000})
                    .setLights(Color.GREEN,1000,1000)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            manager.notify(num,notification);
        }else {
            int num=alarmId-BIG_NUM_FOR_ALARM;
            Log.d("MainActivity","alarmNoticeId "+num);
            Intent intent=new Intent(context,Edit.class);
            Note record= getNoteWithId(num);
            deleteTheAlarm(num);//or num
            transportInformationToEdit(intent,record);
            PendingIntent pi=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager manager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notification=new NotificationCompat.Builder(context,"default")
                    .setContentTitle(record.getTextDate()+" "+record.getTextTime())
                    .setContentText(record.getMainText())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.icon))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(Uri.fromFile(new File("/System/media/audio/ringtones/Luna.ogg")))
                    .setVibrate(new long[]{1000,500,1000})
                    .setLights(Color.GREEN,1000,1000)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            manager.notify(num,notification);
        }
    }

    //提醒后，从数据库中删除提醒时间
    private void deleteTheAlarm(int num) {
        ContentValues temp = new ContentValues();
        temp.put("alarm", "");
        String where = String.valueOf(num);
        Uri uri = Uri.parse("content://com.example.note.provider/note");
        mcontext.getContentResolver().update(uri, temp, "id = ?", new String[]{where});
    }

    //点击通知后，转到EditActivity后显示内容
    private void transportInformationToEdit(Intent it, Note record) {
        it.putExtra("num",record.getNum());
        it.putExtra("tag",record.getTag());
        it.putExtra("textDate",record.getTextDate());
        it.putExtra("textTime",record.getTextTime());
        record.setAlarm("");
        it.putExtra("alarm","");
        it.putExtra("mainText",record.getMainText());
    }

    //根据id获取数据库中的note
    private Note getNoteWithId(int num) {
        String whereArgs = String.valueOf(num);
        List<Note> list=new ArrayList<>();
        Uri uri = Uri.parse("content://com.example.note.provider/note");
        Cursor cursor = mcontext.getContentResolver().query(uri,null, "id = ?", new String[]{whereArgs},null);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                int id =cursor.getInt(cursor.getColumnIndex("id"));
                int n = cursor.getInt(cursor.getColumnIndex("num"));
                int tag = cursor.getInt(cursor.getColumnIndex("tag"));
                String textDate = cursor.getString(cursor.getColumnIndex("textDate"));
                String textTime = cursor.getString(cursor.getColumnIndex("textTime"));
                String alarm = cursor.getString(cursor.getColumnIndex("alarm"));
                String mainText = cursor.getString(cursor.getColumnIndex("mainText"));
                Note temp=new Note(n,tag,textDate,textTime,alarm,mainText,id);
                list.add(temp);
            }
            cursor.close();
        }
        return list.get(0);
    }
}
