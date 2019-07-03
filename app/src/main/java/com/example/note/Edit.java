package com.example.note;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Edit extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    LinearLayout myLayout;

    TextView date_text;
    TextView time_text;
    ImageButton alarm_button;
    EditText edt;
    TextView av;
    RadioGroup tagRadio;
    RadioButton rdButton;

    int tag;//颜色
    String textDate;//年
    String textTime;//月
    String mainText;//日

    //提醒闹钟
    int num=0; //for requestcode,是MainActivity中startActivityForResult(intent,position)的position，或者通俗说是该note列表位置
    String alarm="";
    int alarm_hour=0;
    int alarm_minute=0;
    int alarm_year=0;
    int alarm_month=0;
    int alarm_day=0;

    private DatePickerDialog dialogDate;
    private TimePickerDialog dialogTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//全屏显示
        setContentView(R.layout.activity_edit);

        //加载toolbar
        Toolbar edit_toolbar=(Toolbar)findViewById(R.id.edit_toolbar);
        setSupportActionBar(edit_toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //获取从MainActivity的intent
        Intent it=getIntent();
        getInformationFromMain(it);

        //设置背景
        myLayout = (LinearLayout) findViewById(R.id.whole);
        myLayout.setBackgroundResource(R.drawable.edit_bg_white);

        date_text=(TextView) findViewById(R.id.dateText);
        time_text=(TextView) findViewById(R.id.timeText);
        alarm_button=(ImageButton) findViewById((R.id.alarmButton));
        edt=(EditText) findViewById(R.id.editText);
        av=(TextView) findViewById(R.id.alarmView);

        //设置现在时间
        date_text.setText(textDate);
        time_text.setText(textTime);
        //设置初始化的编辑框
        edt.setText(mainText);
        //提醒时间显示框隐藏
        av.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(v.getId()==R.id.alarmView||v.getId()==R.id.alarmButton) {
                    //删除提醒信息
                    alarm="";
                    //隐藏textView
                    av.setVisibility(View.GONE);
                }
                return true;
            }
        });
        //设置提醒时间显示
        if(alarm.length()>1) {
            av.setText("提醒时间：" + alarm);
        } else{
            av.setVisibility(View.GONE);
        }

        //tagRadio中tagRadio改变背景
        tagRadio=(RadioGroup) findViewById(R.id.tagRadio);
        tagRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (tagRadio.getCheckedRadioButtonId()) {
                    case R.id.yellow:
                        tag=0;
                        myLayout.setBackgroundResource(R.drawable.edit_bg_yellow);
                        break;
                    case R.id.blue:
                        tag=1;
                        myLayout.setBackgroundResource(R.drawable.edit_bg_blue);
                        break;
                    case R.id.green:
                        tag=2;
                        myLayout.setBackgroundResource(R.drawable.edit_bg_green);
                        break;
                    case R.id.red:
                        tag=3;
                        myLayout.setBackgroundResource(R.drawable.edit_bg_red);
                        break;
                    case R.id.white:
                        tag=4;
                        myLayout.setBackgroundResource(R.drawable.edit_bg_white);
                        break;
                    default:
                        break;
                }
            }
        });

        //当点击该个tagRadio时,该tagRadio换图标
        setRadioButtonCheckedAccordingToTag(tag);
        rdButton.setChecked(true);
    }

    /**
     * 添加toolbar的menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar,menu);
        return true;
    }

    /**
     *toolbar的menu的事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //保存键
            case R.id.saveButton:
                //不为空就保存
                if ((edt.getText().toString()).equals("")){
                    finish();
                }else {
                    returnResult();
                    finish();
                }
                break;
            //返回键
            case android.R.id.home:
                if ((edt.getText().toString()).equals("")){
                    finish();
                }else {
                    returnResult();
                    finish();
                }
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按了手机返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((edt.getText().toString()).equals("")){
                finish();
            }else {
                returnResult();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //从MainActivity获取intent
    private void getInformationFromMain(Intent it) {
        num=it.getIntExtra("num",0);
        tag=it.getIntExtra("tag",0);
        textDate=it.getStringExtra("textDate");
        textTime=it.getStringExtra("textTime");
        alarm=it.getStringExtra("alarm");
        mainText=it.getStringExtra("mainText");
    }

    //返回MainActivity的intent
    private void returnResult() {
        Intent it=new Intent();
        it.putExtra("tag",tag);
        //不用返回现在时间，现在时间在MainActivity中在获取保存进数据库
        it.putExtra("alarm",alarm);
        it.putExtra("mainText",edt.getText().toString());
        setResult(RESULT_OK,it);//MainActivity中调用了startActivityForResult()
    }

    //点击该radion时，获取该tagRadio
    private void setRadioButtonCheckedAccordingToTag(int tag) {
        switch (tag) {
            case 0:
                rdButton=(RadioButton) findViewById(R.id.yellow);
                break;
            case 1:
                rdButton=(RadioButton) findViewById(R.id.blue);
                break;
            case 2:
                rdButton=(RadioButton) findViewById(R.id.green);
                break;
            case 3:
                rdButton=(RadioButton) findViewById(R.id.red);
                break;
            case 4:
                rdButton=(RadioButton) findViewById(R.id.white);
                break;
            default:
                break;
        }
    }

    //提醒闹钟事件
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarm(View v) {
        //如果之前没有设置闹钟
        if(alarm.length()<=1) {
            //显示当前时间
            Calendar c=Calendar.getInstance();
            alarm_hour=c.get(Calendar.HOUR_OF_DAY);
            alarm_minute=c.get(Calendar.MINUTE);

            alarm_year=c.get(Calendar.YEAR);
            alarm_month=c.get(Calendar.MONTH)+1;
            alarm_day=c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            //显示之前设置的闹钟时间
            int i=0, k=0;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_year=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_month=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
            alarm_day=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
            alarm_hour=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            alarm_minute=Integer.parseInt(alarm.substring(k));
        }

//        new TimePickerDialog(this,this,alarm_hour,alarm_minute,true).show();
//        new DatePickerDialog(this,this,alarm_year,alarm_month-1,alarm_day).show();

        //顺序不能调换
        dialogTime = new TimePickerDialog(this,
                android.app.AlertDialog.THEME_HOLO_LIGHT,this,
                alarm_hour,alarm_minute,true);
        dialogTime.setTitle("请选择时间");
        dialogTime.show();

        android.icu.util.Calendar calendar= android.icu.util.Calendar.getInstance();
        dialogDate = new DatePickerDialog(this,
                android.app.AlertDialog.THEME_HOLO_LIGHT,this,
                alarm_year,alarm_month-1,alarm_day);
        dialogDate.getDatePicker().setCalendarViewShown(false);
        dialogDate.getDatePicker().setMinDate(calendar.getTime().getTime());
        dialogDate.setTitle("请选择日期");
        dialogDate.show();


    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        alarm_year=year;
        alarm_month=monthOfYear+1;
        alarm_day=dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarm_hour=hourOfDay;
        alarm_minute=minute;
        alarm=alarm_year+"-"+alarm_month+"-"+alarm_day+" "+alarm_hour+":"+alarm_minute;
        av.setText("提醒时间："+alarm);
        av.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Alarm will be on at "+alarm+" !",Toast.LENGTH_LONG).show();
    }


}
