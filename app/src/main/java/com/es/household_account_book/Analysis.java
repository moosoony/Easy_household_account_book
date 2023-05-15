package com.es.household_account_book;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Analysis extends AppCompatActivity {
    // 현재 유저 아이디
    String Now_ID = ((LoginActivity) LoginActivity.context_login).userID;

    private RequestQueue requestQueue;

    // 현재 날짜 구하기
    LocalDate now = LocalDate.now();

    // 포맷 정의
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM");

    // 포맷 적용
    String formatedNow = now.format(formatter);

    String Expen_s; // 차트에 넣을 쇼핑 합
    String Expen_f; // 차트에 넣을 식비 합
    String Expen_m; // 차트에 넣을 의료 합
    String Expen_t; // 차트에 넣을 교통 합
    String Expen_e; // 차트에 넣을 기타 합

    String Expen_st; // DB에서 가져온 쇼핑 합
    String Expen_ft; // DB에서 가져온 식비 합
    String Expen_mt; // DB에서 가져온 의료 합
    String Expen_tt; // DB에서 가져온 교통 합
    String Expen_et; // DB에서 가져온 기타 합

    float fpercentage;
    float spercentage;
    float mpercentage;
    float tpercentage;
    float epercentage;

    int sum;
    float total;

    PieData data;
    PieChart pieChart;
    TextToSpeech tts;
    ArrayList yValues = new ArrayList();

    EditText shop;
    EditText food;
    EditText traffic;
    EditText medical;
    EditText besides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        shop = findViewById(R.id.shop);
        food = findViewById(R.id.food);
        traffic = findViewById(R.id.traffic);
        medical = findViewById(R.id.medical);
        besides = findViewById(R.id.besides);

        // 원그래프 출력
        pieChart = (PieChart) findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(61f);

        yValues.add(new PieEntry(0, "식비"));
        yValues.add(new PieEntry(0, "쇼핑"));
        yValues.add(new PieEntry(0, "의료"));
        yValues.add(new PieEntry(0, "교통"));
        yValues.add(new PieEntry(0, "기타"));

        Description description = new Description();
        description.setText("지출카테고리별 분석"); //라벨
        description.setTextSize(20);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues, "Category");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        // 파이 차트 출력 끝

        get_Expen_s();

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);

                }
            }
        }
        );
    }

    private void get_Expen_s()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기
                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기
                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);

                        Expen_st = subJsonObject.getString("쇼핑");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(Expen_st==null ||Expen_st== "null"){
                    Expen_st = "0";
                }
                get_Expen_f();
                shop.setText(Expen_st);
            }
        };
        AnalysisShoppingRequest AnalysisShoppingRequest = new AnalysisShoppingRequest(Now_ID, formatedNow, responseListener);
        RequestQueue squeue = Volley.newRequestQueue(Analysis.this);
        squeue.add(AnalysisShoppingRequest);
    }

    private void get_Expen_f()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기
                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기
                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);
                        Expen_ft = subJsonObject.getString("식비");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }if(Expen_ft==null ||Expen_ft== "null"){
                    Expen_ft = "0";
                }
                food.setText(Expen_ft);
                get_Expen_m();
            }
        };
        AnalysisFoodRequest AnalysisFoodRequest = new AnalysisFoodRequest(Now_ID, formatedNow, responseListener);
        RequestQueue fqueue = Volley.newRequestQueue(Analysis.this);
        fqueue.add(AnalysisFoodRequest);
    }

    // 디비에서 의료 합 가져오기
    private void get_Expen_m() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기

                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);
                        Expen_mt = subJsonObject.getString("의료");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }if(Expen_mt==null ||Expen_mt== "null"){
                    Expen_mt = "0";
                }

                medical.setText(Expen_mt);
                get_Expen_t();
            }
        };
        AnalysisMedlcalRequest AnalysisMedlcalRequest = new AnalysisMedlcalRequest(Now_ID, formatedNow, responseListener);
        RequestQueue mqueue = Volley.newRequestQueue(Analysis.this);
        mqueue.add(AnalysisMedlcalRequest);
    }

    // 디비에서 교통 합 가져오기
    private void get_Expen_t() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기
                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);

                        Expen_tt = subJsonObject.getString("교통");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }if(Expen_tt==null ||Expen_tt== "null"){
                    Expen_tt = "0";
                }
                traffic.setText(Expen_tt);
                get_Expen_e();
            }
        };
        AnalysisTrafficRequest AnalysisTrafficRequest = new AnalysisTrafficRequest(Now_ID, formatedNow, responseListener);
        RequestQueue tqueue = Volley.newRequestQueue(Analysis.this);
        tqueue.add(AnalysisTrafficRequest);
    }

    // 디비에서 기타 합 가져오기
    public void get_Expen_e() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(), response ,Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기
                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);

                        Expen_et = subJsonObject.getString("기타");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(Expen_et==null ||Expen_et== "null"){
                    Expen_et = "0";
                }
                besides.setText(Expen_et);
                besides.setFocusable(false);

                //차트 데이터 변경
                Expen_s = String.valueOf(shop.getText());
                Expen_f = String.valueOf(food.getText());
                Expen_m = String.valueOf(medical.getText());
                Expen_t = String.valueOf(traffic.getText());
                Expen_e = String.valueOf(besides.getText());

                yValues.clear();

                sum = Integer.parseInt(shop.getText().toString()) + Integer.parseInt(food.getText().toString()) + Integer.parseInt(medical.getText().toString()) +Integer.parseInt(traffic.getText().toString()) + Integer.parseInt(besides.getText().toString());
                total = sum;
                String value= String.valueOf((int)total);

                fpercentage =Math.round(Integer.parseInt(Expen_f.toString()) / total * 100);
                spercentage = Math.round(Integer.parseInt(Expen_s.toString()) / total * 100);
                mpercentage = Math.round(Integer.parseInt(Expen_m.toString()) / total * 100);
                tpercentage = Math.round(Integer.parseInt(Expen_t.toString()) / total * 100);
                epercentage = Math.round(Integer.parseInt(Expen_e.toString()) / total * 100);

                yValues.add(new PieEntry(fpercentage, "식비"));
                yValues.add(new PieEntry(spercentage, "쇼핑"));
                yValues.add(new PieEntry(mpercentage, "의료"));
                yValues.add(new PieEntry(tpercentage, "교통"));
                yValues.add(new PieEntry(epercentage, "기타"));

                pieChart.setData(data);

                // 금액으로 출력하기
//                tts.speak(food.getText().toString()+"원"+shop.getText().toString()+"원"+medical.getText().toString()+"원"+traffic.getText().toString()+"원"+besides.getText().toString()+"원 사용하셨습니다.",TextToSpeech.QUEUE_FLUSH, null);

                // 퍼센트로 출력하기
                tts.speak("전체 지출"+value+"원 중 "+"식비"+fpercentage+"% 쇼핑"+spercentage+"% 의료"+mpercentage+"% 교통"+tpercentage+"% 기타"+epercentage+"% 사용하셨습니다.",TextToSpeech.QUEUE_FLUSH, null);
                
                // 토스트 메시지 출력
                Toast.makeText(getApplicationContext(),"전체 지출"+value+"원\n"+"식비"+fpercentage+"% 쇼핑"+spercentage+"% 의료"+mpercentage+"% 교통"+tpercentage+"% 기타"+epercentage+"%",Toast.LENGTH_SHORT).show();
            }
        };
        AnalysisEtcRequest AnalysisEtcRequest = new AnalysisEtcRequest(Now_ID, formatedNow, responseListener);
        RequestQueue equeue = Volley.newRequestQueue(Analysis.this);
        equeue.add(AnalysisEtcRequest);
    }

    // tts
    public void onstart(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Analysis.this, MainMenu.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}