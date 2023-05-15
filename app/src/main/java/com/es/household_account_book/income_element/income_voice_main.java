package com.es.household_account_book.income_element;

import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.es.household_account_book.LoginActivity;
import com.es.household_account_book.MainMenu;
import com.es.household_account_book.R;
import com.es.household_account_book.Stt;
import com.es.household_account_book.expenses_element.account;
import com.es.household_account_book.expenses_element.category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class income_voice_main extends AppCompatActivity {
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    Button btn_isubmit;
    TextView category;
    TextView title;
    TextView money;
    TextView tts_txt;
    View v;
    DatePicker datePicker;
    CheckBox salary;
    CheckBox poket;
    CheckBox besides;
    EditText amount;
    ImageButton sttBtn;

    String year;
    String day;
    String month;

    String date_voice;
    String category_voice;
    String account_voice;

    String Income_UserID = ((LoginActivity)LoginActivity.context_login).userID;

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
        setContentView(R.layout.income_write);

        title = findViewById(R.id.title);
        btn_isubmit = findViewById(R.id.btn_isubmit);
        v = findViewById(R.id.horizontal);
        datePicker = findViewById(R.id.dp_edate);
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                Toast.makeText(income_voice_main.this, date, Toast.LENGTH_LONG).show();
            }
        });
        category = findViewById(R.id.category);
        salary = findViewById(R.id.cb_salary);
        poket = findViewById(R.id.cb_pocket);
        besides = findViewById(R.id.cb_besides);
        money = findViewById(R.id.money);
        amount = findViewById(R.id.et_amount);

        tts_txt = findViewById(R.id.ttstxt);
        sttBtn = findViewById(R.id.sttincome);


        //음성 디비 연동

        Intent intent_voice = getIntent(); //전달할 데이터를 받을 Intent
        String Income_Date = ((com.es.household_account_book.income_element.date) com.es.household_account_book.income_element.date.context_incomedate).incomedate_db;
        String Income_Category = ((com.es.household_account_book.income_element.category) com.es.household_account_book.income_element.category.context_incomecategory).incomecategory_db;
        String Income_account = ((com.es.household_account_book.income_element.account) com.es.household_account_book.income_element.account.context_incomeaccount).incomeaccount_db;

        if (intent_voice.hasExtra("date")) {
            date_voice = intent_voice.getStringExtra("date");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Income_Date!=null){
            date_voice = Income_Date;
        } else if(Income_Date==null) {
            Toast.makeText(this, "전달된 날짜가 없습니다", Toast.LENGTH_SHORT).show();
        }

        if (intent_voice.hasExtra("category")) {
            category_voice = intent_voice.getStringExtra("category");
        } else if(Income_Category!=null){
            date_voice = Income_Category;
        } else if(Income_Category==null) {
            Toast.makeText(this, "전달된 카테고리가 없습니다", Toast.LENGTH_SHORT).show();
        }

        if (intent_voice.hasExtra("account")) {
            account_voice = intent_voice.getStringExtra("account");
        } else if(Income_account!=null){
            account_voice = Income_account;
        } else if(Income_account==null) {
            Toast.makeText(this, "전달된 카테고리가 없습니다", Toast.LENGTH_SHORT).show();
        }

        amount.setText(account_voice);

        // Income_Date에 값 저장
        Income_Date = cal_set(date_voice);

        Income_Category=category_voice;

        String Income_Sum = account_voice;

        // 체크박스
        if (salary.isChecked()) {
            Income_Category = "월급";

        } else if (poket.isChecked()) {
            Income_Category = "용돈";
        } else if (besides.isChecked()) {
            Income_Category = "기타";
        }


        // DB연동
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    // 수입 입력에 성공한 경우
                    if (success) {
                        Toast.makeText(getApplicationContext(), "수입 입력에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(income_voice_main.this, MainMenu.class);
                        startActivity(intent);
                    } else { // 수입 입력에 실패한 경우
                        Toast.makeText(getApplicationContext(), "수입 입력에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함.
        income_voice_request incomeRequest = new income_voice_request(Income_Date, Income_Category, Income_Sum, Income_UserID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(income_voice_main.this);
        queue.add(incomeRequest);

        Intent intent = new Intent(income_voice_main.this, MainMenu.class);
        startActivity(intent);
        finish();
    }


}
