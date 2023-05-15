package com.es.household_account_book;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class SearchResult extends AppCompatActivity {
    ArrayAdapter<String> Income_adpater;
    static ArrayAdapter<String> Expen_adpater;

    TextView textView;
    static TextToSpeech tts;
    static ListView listView;
    EditText editSearch;
    static String Search_input;

    static Handler handler;
    static Runnable r ;

    static String UserID = ((LoginActivity)LoginActivity.context_login).userID;

    static ArrayList<String> Income_Sum_AR = new ArrayList<String>();
    static ArrayList<String> Expen_Sum_AR = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        listView = findViewById(R.id.search_item);
        textView = findViewById(R.id.No_Result);
        editSearch = findViewById(R.id.editSearch);
        Search_input= ((SearchStore)SearchStore.context_SearchData).Search_Data;
        editSearch.setText(Search_input);

        if(Search_input.equals("용돈") || Search_input.equals("월급")){
            get_Search_Income();
            Income_adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Income_Sum_AR);
        }

        else{
            Expen_Sum_AR = get_Search_Expen();
            Expen_adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Expen_Sum_AR);
        }

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                    onstart(Search_input);
                }
            }
        });
    }

    public static void onstart(String str){
        tts.speak(str,TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchResult.this, MainMenu.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        if(editSearch != null){
            Search_input=null;
        }
        Expen_Sum_AR.clear();
        Income_Sum_AR.clear();
        if(handler != null){
            handler.removeCallbacks(r);
            handler = null;
        }
    }

    private void get_Search_Income()
    {
        String Income_UserID = UserID;
        String Income_Category = Search_input;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Income_Search = jsonObject.getString("Income_Search");
                    JSONArray jsonArray = new JSONArray(Income_Search);
                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);
                        String Income_Sum = subJsonObject.getString("Income_Sum");
                        Income_Sum_AR.add(Income_Sum);
                    }

                    listView.setAdapter(Income_adpater);
                    if(!Income_Sum_AR.isEmpty()) {
                        voice(Income_Sum_AR,Income_Sum_AR.size());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SearchIncomeRequest searchIncomeRequest = new SearchIncomeRequest(Income_UserID, Income_Category, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SearchResult.this);
        queue.add(searchIncomeRequest);
    }

    ArrayList<String> get_Search_Expen()
    {
        String Expen_UserID = UserID;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response); //php불러오기
                    String Expen_Search = jsonObject.getString("Expen_Search"); // json에서 Expen_Search 타이틀 찾기
                    JSONArray jsonArray = new JSONArray(Expen_Search); // 위에 타이틀을 JSONArray에 넣기
                    for (int i=0; i < jsonArray.length(); i++) {
                        JSONObject subJsonObject = jsonArray.getJSONObject(i);

                        String Expen_Sum = subJsonObject.getString("Expen_Sum")+"원";
                        Expen_Sum_AR.add(Expen_Sum);
                    }

                    listView.setAdapter(Expen_adpater);

                    Expen_Sum_AR.add(0," ");
                    if(!Expen_Sum_AR.isEmpty()) {
                        voice(Expen_Sum_AR,Expen_Sum_AR.size());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        SearchExpenRequest searchExpenRequest = new SearchExpenRequest(Expen_UserID, Search_input, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SearchResult.this);
        queue.add(searchExpenRequest);

        return Expen_Sum_AR;
    }

    private void voice(ArrayList str, int length) {
        final Handler handler = new Handler();

        r = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                if (i < length) {
                    handler.postDelayed(r, 3500);
                    onstart((String) str.get(i));
                    i++;
                }
            }
        };
        handler.post(r);
    }
}