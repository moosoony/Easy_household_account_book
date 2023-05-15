package com.es.household_account_book.expenses_element;

import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.es.household_account_book.LoginActivity;
import com.es.household_account_book.MainMenu;
import com.es.household_account_book.R;
import com.es.household_account_book.Expenses;
import com.es.household_account_book.Stt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class expenses_voice_main extends AppCompatActivity {
    DatePicker datePicker;
    CheckBox cb_food;
    CheckBox cb_shopping;
    CheckBox cb_medical;
    CheckBox cb_traffic;
    CheckBox cb_etc;
    EditText et_amount;
    Button btn_esubmit;
    ImageButton btnstart;

    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;

    String date_voice;
    String category_voice;
    String account_voice;

    String year;
    String day;
    String month;


    String Expen_UserID = ((LoginActivity)LoginActivity.context_login).userID;

    public static String[] date(String str){
        String year = null;
        String month= null;
        String day= null;
        int count;

        if(str.contains("년 ") &&str.contains("월 ") &&str.contains("일")) {
            count = str.indexOf("년 ");
            year = str.substring(0,count);
            count = str.indexOf("월 ");
            month = str.substring(str.indexOf("년 ")+2, count);
            day = str.substring(count+2,str.indexOf("일"));
        }

        String[] date_stt = {year,month,day};
        return date_stt;
    }

    // stt로 가져온 캘린더 값
    public String cal_set(String result){
        String[] date_spilt; // 문자열 자르기 위한 변수

        date_spilt = date(result); // 문자열 자르기

        // DB에 넣을 날짜 데이터
        year = date_spilt[0];
        month = date_spilt[1];
        day = date_spilt[2];

        // DB에 넣을 "월" "일" 전처리
        if(month.length()<2){
            month = "0"+month;
        }
        if(day.length()<2){
            day = "0"+day;
        }
        String rresult = year+"/"+month+"/"+day;
        return rresult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_write);

        datePicker = findViewById(R.id.dp_edate);
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                Toast.makeText(expenses_voice_main.this, date, Toast.LENGTH_LONG).show();
            }
        });
        cb_food = findViewById(R.id.cb_food);
        cb_shopping = findViewById(R.id.cb_shopping);
        cb_medical = findViewById(R.id.cb_medical);
        cb_traffic = findViewById(R.id.cb_traffic);
        cb_etc = findViewById(R.id.cb_etc);
        et_amount = findViewById(R.id.et_amount);
        btn_esubmit = findViewById(R.id.btn_esubmit);
        btnstart = (ImageButton) findViewById(R.id.sttStart);

        //음성 디비 연동

        Intent intent_voice = getIntent(); //전달할 데이터를 받을 Intent
        String Expen_date = ((com.es.household_account_book.expenses_element.date) com.es.household_account_book.expenses_element.date.context_expendate).date_db;
        String Expen_category = ((category) com.es.household_account_book.expenses_element.category.context_expencategory).category_db;
        String Expen_account = ((account) account.context_expenaccount).account_db;

        if (intent_voice.hasExtra("date")) {
            date_voice = intent_voice.getStringExtra("date");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Expen_date!=null){
            date_voice = Expen_date;
        } else if(Expen_date==null) {
            Toast.makeText(this, "전달된 날짜가 없습니다", Toast.LENGTH_SHORT).show();
        }

        if (intent_voice.hasExtra("category")) {
            category_voice = intent_voice.getStringExtra("category");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Expen_category!=null){
            date_voice = Expen_category;
        } else if(Expen_category==null) {
            Toast.makeText(this, "전달된 카테고리가 없습니다", Toast.LENGTH_SHORT).show();
        }

        if (intent_voice.hasExtra("account")) {
            account_voice = intent_voice.getStringExtra("account");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Expen_account!=null){
            account_voice = Expen_account;
        } else if(Expen_account==null) {
            Toast.makeText(this, "전달된 카테고리가 없습니다", Toast.LENGTH_SHORT).show();
        }

        et_amount.setText(account_voice);

        //String date = String.format("%d년 %d월 %d일", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());

        // Expen_Date에 값 저장
        String Expen_Date = cal_set(date_voice);



        // Expen_Category에 값 저장
        String Expen_Category=category_voice;

        if(Expen_Category.equals("식비")){

            cb_food.setChecked(true);
        }

        else if (Expen_Category.equals("쇼핑")){

            cb_shopping.setChecked(true);
        }
        else if (Expen_Category.equals("의료")){

            cb_medical.setChecked(true);
        }
        else if (Expen_Category.equals("교통")){

            cb_traffic.setChecked(true);
        }
        else if (Expen_Category.equals("기타")){

            cb_etc.setChecked(true);
        }
        //String Date_DB = String.format("%d/%d/%d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        // Expen_Sum에 값 저장
        String Expen_Sum = account_voice;


        // DB연동
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    // 지출 입력에 성공한 경우
                    if (success) {
                        Toast.makeText(getApplicationContext(),"지출 입력에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(expenses_voice_main.this, MainMenu.class);
                        startActivity(intent);
                    } else { // 수입 입력에 실패한 경우
                        Toast.makeText(getApplicationContext(),"지출 입력에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함.
        expenses_voice_request ExpenRequest = new expenses_voice_request(Expen_Date,Expen_Category,Expen_Sum, Expen_UserID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(expenses_voice_main.this);
        queue.add(ExpenRequest);

        Intent intent = new Intent(expenses_voice_main.this, MainMenu.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(expenses_voice_main.this, Expenses.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }


}






