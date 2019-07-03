package com.example.note;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //list to store all the memo
    private List<OneNote> notelist=new ArrayList<>();

    //adapter
    NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar main_toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);

        //加载note
        loadHistoryData();

        //RecyclerView事件
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new NoteAdapter(notelist);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NoteAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent(MainActivity.this,Edit.class);
                Note record=getNoteWithNum(position);
                //添加信息进intent
                transportInformationToEdit(intent, record);
                startActivityForResult(intent,position);

            }
        });

        adapter.setOnItemLongClickListener(new NoteAdapter.ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final int position) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder (MainActivity.
                        this);
                dialog.setTitle("删除");
                dialog.setMessage("是否删除？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int n=notelist.size();
                        //如果这个备忘录有一个闹钟,取消它
                        if(notelist.get(position).getAlarm()) {
                            cancelAlarm(position);
                        }
                        notelist.remove(position);
                        adapter.notifyDataSetChanged();

                        String whereArgs = String.valueOf(position);
                        Uri uri = Uri.parse("content://com.example.note.provider/note" );
                        getContentResolver().delete(uri,"num = ?",new String[]{whereArgs});

                        for(int i=position+1; i<n; i++) {
                            ContentValues values = new ContentValues();
                            values.put("num", i-1);
                            String where = String.valueOf(i);
                            getContentResolver().update(uri, values, "num = ?", new String[]{where});
                            values.clear();
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.
                        OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();

                return true;
            }
        });


        FloatingActionButton addButton=(FloatingActionButton)findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdd();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent it) {
        if(resultCode==RESULT_OK) {
            updateDataBaseAndListView(requestCode, it);
        }
    }


    //加载已经存在的note
    private void loadHistoryData() {
        Uri uri = Uri.parse("content://com.example.note.provider/note");
        Cursor cursor = getContentResolver().query(uri,null, null, null, null);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                Log.d("MainActivity", "current num: " + cursor.getInt(cursor.getColumnIndex("num")));
                Log.d("MainActivity", "id: " + cursor.getInt(cursor.getColumnIndex("id")));
                Log.d("MainActivity", "getAlarm: " + cursor.getString(cursor.getColumnIndex("alarm")));

                int tag = cursor.getInt(cursor.getColumnIndex("tag"));
                String textDate = cursor.getString(cursor.getColumnIndex("textDate"));
                String textTime = cursor.getString(cursor.getColumnIndex("textTime"));
                boolean alarm = cursor.getString(cursor.getColumnIndex("alarm")).length() > 1 ? true : false;
                String mainText = cursor.getString(cursor.getColumnIndex("mainText"));
                OneNote temp = new OneNote(tag, textDate, textTime, alarm, mainText);
                notelist.add(temp);
            }
            cursor.close();
        }

    }


    //根据Edit.class返回的“num”,更新备忘录数据库和备忘录列表
    private void updateDataBaseAndListView(int requestCode, Intent it) {

        int num=requestCode;
        int tag=it.getIntExtra("tag",0);

        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);

        String alarm=it.getStringExtra("alarm");
        String mainText=it.getStringExtra("mainText");

        boolean gotAlarm = alarm.length() > 1 ? true : false;
        OneNote new_note = new OneNote(tag, current_date, current_time, gotAlarm, mainText);

        if((requestCode+1)>notelist.size()) {
            //将新的备忘录记录添加到数据库中
            addRecordToDataBase(num, tag, current_date, current_time, alarm, mainText);

            //将一个新的OneNote对象添加到notelist并显示
            notelist.add(new_note);
        }
        else {
            //如果之前一个有闹钟,先取消它
            if(notelist.get(num).getAlarm()) {
                cancelAlarm(num);
            }

            //更新以前备忘录的“num”位置(因为note在列表中的位置可能变化)
            Uri uri = Uri.parse("content://com.example.note.provider/note" );
            ContentValues values = new ContentValues();
            values.put("tag", tag);
            values.put("textDate", current_date);
            values.put("textTime", current_time);
            values.put("alarm", alarm);
            values.put("mainText", mainText);
            String where = String.valueOf(num);
            getContentResolver().update(uri, values, "num = ?", new String[]{where});

            notelist.set(num, new_note);
        }
        //如果用户已设置闹钟
        if(gotAlarm) {
            loadAlarm(alarm, requestCode, 0);
        }

        adapter.notifyDataSetChanged();
    }


    //以XX - XX格式获取当前日期
    private String getCurrentDate(Calendar c){
        return c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
    }

    //获取XX：XX格式的当前时间
    private String getCurrentTime(Calendar c){
        String current_time="";
        if(c.get(Calendar.HOUR_OF_DAY)<10) current_time=current_time+"0"+c.get(Calendar.HOUR_OF_DAY);
        else current_time=current_time+c.get(Calendar.HOUR_OF_DAY);

        current_time=current_time+":";

        if(c.get(Calendar.MINUTE)<10) current_time=current_time+"0"+c.get(Calendar.MINUTE);
        else current_time=current_time+c.get(Calendar.MINUTE);

        return current_time;
    }

    //添加到数据库
    private void addRecordToDataBase(int num, int tag, String textDate, String textTime, String alarm, String mainText) {
        Uri uri = Uri.parse("content://com.example.note.provider/note");
        ContentValues values = new ContentValues();
        values.put("num", num);
        values.put("tag", tag);
        values.put("textDate", textDate);
        values.put("textTime", textTime);
        values.put("alarm",alarm);
        values.put("mainText",mainText);
        getContentResolver().insert(uri, values);
    }

    //传递intent到EditActivity
    private void transportInformationToEdit(Intent it, Note record) {
        it.putExtra("num",record.getNum());
        it.putExtra("tag",record.getTag());
        it.putExtra("textDate",record.getTextDate());
        it.putExtra("textTime",record.getTextTime());
        it.putExtra("alarm",record.getAlarm());
        it.putExtra("mainText",record.getMainText());
    }

    //按下添加按钮事件
    public void onAdd() {
        Intent it=new Intent(this,Edit.class);
        //note列表有多少记录，position可以作为新note的位置(因为notelist是数组，下标比长度减一)
        int position = notelist.size();
        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);
        it.putExtra("num",position);
        it.putExtra("tag",4);
        it.putExtra("textDate",current_date);
        it.putExtra("textTime",current_time);
        it.putExtra("alarm","");
        it.putExtra("mainText","");
        startActivityForResult(it,position);
    }

    //根据num获取数据库中的note
    private Note getNoteWithNum(int num) {
        String whereArgs = String.valueOf(num);
        List<Note> list=new ArrayList<>();
        Uri uri = Uri.parse("content://com.example.note.provider/note");
        Cursor cursor = getContentResolver().query(uri,null, "num = ?",new String[]{whereArgs}, null);
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

    //根据"alarm"设置闹钟
    private void loadAlarm(String alarm, int num, int days) {
        /**
         * 当闹钟响起时，我们想向broadcast 一个intent给BroadcastReceiver
         *  在这里，我们使用一个显式类名创建一个Intent，已拥有我们自己的接收器（已发布于AndroidManifest.xml）实例化并调用
         * 然后创建一个IntentSender以将意图作为广播执行。
         */
        Note record=getNoteWithNum(num);
        //安排闹钟服务
        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        intent.putExtra("alarmId",record.getId());
        intent.putExtra("alarm",alarm);
        startService(intent);
    }

    //删除列表中note时，如果设置了闹钟就取消闹钟
    private void cancelAlarm(int num) {
        Intent intent = new Intent(MainActivity.this, AlarmService.class);
        stopService(intent);

    }

}
