package com.es.household_account_book.expenses_element;

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
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.es.household_account_book.ExpensesWrite;
import com.es.household_account_book.LoginActivity;
import com.es.household_account_book.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class date extends AppCompatActivity {
    DatePicker datepicker;
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageButton stt_date;
    public static Context context_expendate; // context 변수 선언
    String date_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);
        context_expendate = this;

        datepicker = findViewById(R.id.dp_edate);
        stt_date = (ImageButton) findViewById(R.id.stt_date);

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer= createSpeechRecognizer(date.this);
        mRecognizer.setRecognitionListener(listener); // 리스너 설정
//stt코드 끝

        stt_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stt_date.setImageResource(R.drawable.clickedinputptn);
                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출
                onstart("날짜를 말씀해주세요");

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mRecognizer.startListening(intent); //듣기 시작
                    }
                }, 2000);// 1.8초 정도 딜레이를 준 후 시작

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(date.this, ExpensesWrite.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
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
            Toast sttToast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);

            View toastLayout = getLayoutInflater().inflate(R.layout.stt_toast_layout, null);

            sttToast.setView(toastLayout);
            sttToast.show();
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

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌

            ArrayList<String> matches =
                    results.getStringArrayList(RESULTS_RECOGNITION);
            String result = "";

            for(int i = 0; i < matches.size() ; i++){
                result = matches.get(i);
            }

            if(result.substring(0,3).contains("년")){
                onstart("다시 말씀해주세요");

                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mRecognizer.startListening(intent); //듣기 시작
                    }
                }, 1500);// 1.8초 정도 딜레이를 준 후 시작
            }

            else {
                cal_set(result);
                date_db = result;

                //인텐트 선언 및 정의
                Intent intent_voice_post = new Intent(date.this, category.class);
                //입력한 input값을 intent로 전달한다.
                intent_voice_post.putExtra("date", result.toString());
                //setResult(RESULT_OK,intent_voice_post);
                //액티비티 이동
                startActivity(intent_voice_post);
            }


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

    // stt로 가져온 캘린더 값
    public void cal_set(String result){
        String[] date_spilt; // 문자열 자르기 위한 변수

        date_spilt = date(result); // 문자열 자르기

        int int_year = Integer.parseInt(date_spilt[0]);
        int int_month = Integer.parseInt(date_spilt[1]);
        int int_day = Integer.parseInt(date_spilt[2]);
//데이트피커

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, int_year);
        cal.set(Calendar.MONTH, (int_month - 1));
        cal.set(Calendar.DAY_OF_MONTH, int_day);

        int xxday = cal.get(Calendar.DATE);
        int xxmonth = cal.get(Calendar.MONTH);
        int xxyear = cal.get(Calendar.YEAR);

        datepicker.init(xxyear, xxmonth, xxday, null);
    }

    // tts 출력
    public void onstart(String str){
        tts.speak(str,TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null); //5초 딜레이
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
    }
}
