package com.es.household_account_book;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.SpeechRecognizer.RESULTS_RECOGNITION;
import static android.speech.SpeechRecognizer.createSpeechRecognizer;
import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Frag2 extends Fragment {
    public static Context mContext; // 다른 액티비티에서 함수 호출하기 위해 선언

    CalendarView cal;
    TextView tv_text;
    TextView income_text;
    TextView expense_text;

    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageButton sttBtn;

    String UserID = ((LoginActivity)LoginActivity.context_login).userID;
    String Income_DB;
    String Date_DB; // DB로 보낼 날짜
    String Expen_DB; // DB에서 가져온 지출 값

    String year;
    String month;
    String day;

    private View view;

    Context ct;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_calendar, container, false);
        ct = container.getContext();

        mContext = ct; // 다른 액티비티에서 함수 호출하기 위해 선언

        cal = view.findViewById(R.id.cal);
        tv_text = view.findViewById(R.id.tv_text);
        income_text = view.findViewById(R.id.income_text);
        expense_text=view.findViewById(R.id.expense_text);
        sttBtn = view.findViewById(R.id.sttStart);

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(ct, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

//stt코드 시작
        // RecognizerIntent 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer= createSpeechRecognizer(ct);
        mRecognizer.setRecognitionListener(listener); // 리스너 설정
//stt코드 끝

        sttBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sttBtn.setImageResource(R.drawable.clickedinputptn);
                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출
                mRecognizer.startListening(intent); //듣기 시작
                onstart("말씀해주세요");
            }
        });

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = String.format("%d/%02d/%02d", year,(month + 1),day);

                tv_text.setText(date); // 날짜 텍스트 출력

                Date_DB = date;
                // 날짜 음성 출력

                get_Expen(Date_DB);
            }

        });
        return view;
    }

    //stt로 가져온 날짜 가져오기
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


    // Stt 리스너 생성 시작
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 말하기 시작할 준비가되면 호출

        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
//            Toast sttToast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);

            View toastLayout = getLayoutInflater().inflate(R.layout.stt_toast_layout, null);

//            sttToast.setView(toastLayout);
//            sttToast.show();
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            String message;

            switch (error) {
                case ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

//            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌

            ArrayList<String> matches =
                    results.getStringArrayList(RESULTS_RECOGNITION);
            String result = "";

            for(int i = 0; i < matches.size() ; i++){
                tv_text.setText(matches.get(i));
                result = matches.get(i);
            }

            result = result.trim();

            tv_text.setText(result);
            onstart(result); // 날짜 음성 출력

            cal_set(result);
            Date_DB = year+"/"+month+"/"+day;

            get_Expen(Date_DB);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    };
// Stt 리스너 생성 끝

    // stt로 가져온 캘린더 값
    public void cal_set(String result){
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

        int int_year = Integer.parseInt(date_spilt[0]);
        int int_month = Integer.parseInt(date_spilt[1]);
        int int_day = Integer.parseInt(date_spilt[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.YEAR, int_year);
        calendar.set(java.util.Calendar.MONTH, (int_month - 1));
        calendar.set(java.util.Calendar.DAY_OF_MONTH, int_day);

        long milliTime = calendar.getTimeInMillis();
        CalendarView calendarView = (CalendarView)view.findViewById(R.id.cal);
        calendarView.setDate(milliTime,true,true);
    }

    // tts 출력
    public void onstart(String str){
        tts.speak(str,TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null); //5초 딜레이
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private String get_Expen(String Expen_Date)
    {
        onstart(Date_DB);

        expense_text.setText("");
        income_text.setText("");

        String Expen_UserID = UserID;
        Expen_Date = Date_DB;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("hongchul" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 데이터 가져오기 성공
                        Expen_DB = jsonObject.optString("Expen","실패");

                    } else { // 실패한 경우
                        Toast.makeText(ct,"지출 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        expense_text.setText("실패");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(Expen_DB==null || Expen_DB == "null"){
                    Expen_DB = "0";
                }

                expense_text.setText("지출 : "+ Expen_DB+"원");

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onstart("지출 : "+ Expen_DB +"원");
                    }
                }, 3000);// 1.8초 정도 딜레이를 준 후 시작
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        get_Income();
                    }
                }, 3000);// 1

            }
        };

        CalendarExpenRequest calendarExpenRequest = new CalendarExpenRequest(Expen_UserID, Expen_Date, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ct);
        queue.add(calendarExpenRequest);



        return Expen_DB;
    }

    private String get_Income()
    {
        String Income_UserID = UserID;
        String Income_Date = Date_DB;
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("hongchul" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 데이터 가져오기 성공
                        Income_DB = jsonObject.optString("Income","실패");

                    } else { // 실패한 경우
                        Toast.makeText(ct,"수입 연결에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        income_text.setText("수입 : ");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(Income_DB==null || Income_DB == "null"){
                    Income_DB = "0";
                }
                income_text.setText("수입 : "+ Income_DB+"원");
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onstart("수입 : "+ Income_DB+"원");;
                    }
                }, 3000);// 1.8초 정도 딜레이를 준 후 시작
            }
        };
        CalendarIncomeRequest calendarIncomeRequest = new CalendarIncomeRequest(Income_UserID, Income_Date, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ct);
        queue.add(calendarIncomeRequest);

        return Income_DB;
    }

    public void expense(String expen){
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onstart(expen); // 날짜 음성 출력
            }
        }, 2000);// 1초 정도 딜레이를 준 후 시작

    }

    public void income(String income){

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onstart( income); // 날짜 음성 출력
            }
        }, 2000);// 1초 정도 딜레이를 준 후 시작

    }

    }
