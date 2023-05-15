package com.es.household_account_book.expenses_element;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.es.household_account_book.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

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

public class account extends AppCompatActivity {

    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageButton stt_account;
    TextView textView;
    EditText editText;
    boolean i = true;
    final int PERMISSION = 1;
    public static Context context_expenaccount; // context 변수 선언
    String date;
    String category;
    String account_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        stt_account = (ImageButton) findViewById(R.id.sttacc);
        editText = (EditText) findViewById(R.id.et_amount);
        context_expenaccount = this;

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


        // 오디오, 카메라 권한설정
        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        // RecognizerIntent 생성
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer= createSpeechRecognizer(account.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        stt_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onstart("금액을 말씀해주세요");
                stt_account.setImageResource(R.drawable.clickedinputptn);
                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mRecognizer.startListening(intent); //듣기 시작
                    }
                }, 3000);// 1.8초 정도 딜레이를 준 후 시작
            }
        });

        Intent intent_voice = getIntent(); //전달할 데이터를 받을 Intent
        String Expen_Date = ((com.es.household_account_book.expenses_element.date) com.es.household_account_book.expenses_element.date.context_expendate).date_db;
        String Expen_Category = ((category) com.es.household_account_book.expenses_element.category.context_expencategory).category_db;

        if (intent_voice.hasExtra("date")) {
            date = intent_voice.getStringExtra("date");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Expen_Date!=null){
            date = Expen_Date;
        } else if(Expen_Date==null) {
            Toast.makeText(this, "전달된 날짜가 없습니다", Toast.LENGTH_SHORT).show();
        }

        if (intent_voice.hasExtra("category")) {
            category = intent_voice.getStringExtra("category");
    /* "date"라는 이름의 key에 저장된 값이 있다면
       textView의 내용을 "nameKey" key에서 꺼내온 값으로 바꾼다 */
        } else if(Expen_Category!=null){
            date = Expen_Category;
        } else if(Expen_Category==null) {
            Toast.makeText(this, "전달된 카테고리가 없습니다", Toast.LENGTH_SHORT).show();
        }
    }


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
            result = result.replaceAll(" ","");
            result = result.replaceAll(",","");
            result = won(result);

            ArrayList<String> Non_Voice = new ArrayList<>(Arrays.asList("금","액","을","말","씀","해","주","세","요"));

            for (int i =0; i<Non_Voice.size(); i++) {
                if (result.contains(Non_Voice.get(i))) {
                    result = result.replace(Non_Voice.get(i),"");
                }
            }

            if( result.matches("^[0-9]*$")) { //숫자만 입력되었을 경우
                editText.setText(result);
                account_db = result;
                //인텐트 선언 및 정의
                Intent intent_voice = new Intent(com.es.household_account_book.expenses_element.account.this, expenses_voice_main.class);
                //입력한 input값을 intent로 전달한다.
                intent_voice.putExtra("account", result);
                intent_voice.putExtra("date", date);
                intent_voice.putExtra("category", category);
                //액티비티 이동
                startActivity(intent_voice);
            }

            else if( result.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) { // 숫자 포함 문자열이 들어왔을 경우
                if (result.matches(".*[0-9].*")) { // 문자열에 숫자가 포함되어있는지
                    if (result.contains("만")) { // 문자열에 "만"이 있는지
                        if (result.contains("천만")) {
                            result = result.replace("천만", "000");
                        }
                        else if (result.contains("천만") == false && result.contains("천")) {
                            int index_man = result.indexOf("만"); //355만6천 , 3
                            String man_result = result.substring(index_man+1); // "만" 뒤의 글자 저장, 만6천
                            int index = result.indexOf("천");
                            man_result = man_result.replace("천","");

                            if (man_result.length()<2){ //6천
                                String cheon = result.substring(index_man+1,result.length()-1);
                                man_result = result.substring(0,index_man);
                                result = man_result+cheon+"000";
                            }

                            else{
                                result = result.replace("천", "");
                                result = result.replace("만", "");
                                if (result.contains("백")) {
                                    result = result.replace("백", "00");
                                }
                            }
                        }

                        else {
                            int index = result.indexOf("만");
                            String rest = result.substring(index); // 만 뒤의 글자 rest에 저장
                            rest = rest.replace("만", ""); // "만"공백으로 치환

                            if (result.length()<5){
                                String man = result.substring(0, index);
                                result = man+"0000";
                            }
                            else{
                                if (rest.length() < 4) { // 만 뒤의 글자에 4자리 고정으로 빈자리에 0추가
                                    for (int i = 0; i < 4 - rest.length(); i++) {
                                        rest = "0" + rest;
                                    }
                                }

                                String man = result.substring(0, index); // 글자 처음부터 "만"까지 자르기

                                int man_length = man.length(); // 6
                                if (man.length() > 4) // 만약에 man이 35200+5 이면
                                {
                                    for (int i = 0; i < man.length() - 4; i++) { // 2번 돌아
                                        man = man.replace(man.substring(man_length - 2, man_length - 1), "");
                                    }
                                }
                                result = man + rest;
                            }
                        }
                    }
                } else {
                    result = String.valueOf(HangulToNum(result));
                }

                editText.setText(result);
                account_db = result;
                //인텐트 선언 및 정의
                Intent intent_voice = new Intent(com.es.household_account_book.expenses_element.account.this, expenses_voice_main.class);
                //입력한 input값을 intent로 전달한다.
                intent_voice.putExtra("account", result);
                intent_voice.putExtra("date", date);
                intent_voice.putExtra("category", category);
                //액티비티 이동
                startActivity(intent_voice);
            }
            else if(result.contains(",")){
                editText.setText(result);

                result = result.replaceAll(",","");
                account_db = result;
                //인텐트 선언 및 정의
                Intent intent_voice = new Intent(com.es.household_account_book.expenses_element.account.this, expenses_voice_main.class);
                //입력한 input값을 intent로 전달한다.
                intent_voice.putExtra("account", result);
                intent_voice.putExtra("date", date);
                intent_voice.putExtra("category", category);
                //액티비티 이동
                startActivity(intent_voice);
            }
            else {
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

    // tts
    public void onstart(String str){
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(account.this, category.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    //stt로 가져온 날짜 가져오기
    public static String won(String str){
        String money = null;

        int count;

        if(str.contains("원")) {
            count = str.indexOf("원");
            money = str.substring(0,count);
        }

        return money;
    }

    public static long HangulToNum(String str) {
        long result = 0;
        long tmpResult = 0;
        long num = 0;

        final String NUMBER ="영일이삼사오육칠팔구";
        final String UNIT = "십백천만억조";
        final long[] UNIT_NUM = { 10, 100, 1000, 10000, (long)Math.pow(10, 8), (long)Math.pow(10, 12)};

        StringTokenizer st = new StringTokenizer(str, UNIT, true); //input에 삼십만삼천백십오, UNIT에 십백천만억조 UNIT은 딜리미터

        while(st.hasMoreTokens()) {
            String token = st.nextToken(); // 문자열 읽어오기

            int check = NUMBER.indexOf(token); // NUMBER(영일이삼사오육칠팔구)중에 하나이면 인덱스 리턴

            if(check==-1) { //일이삼사오육칠팔구에 속하지 않고, 단위라면!!
                if("만억조".indexOf(token)==-1) {
                    tmpResult +=  (num!=0) ? num * UNIT_NUM[UNIT.indexOf(token)] : UNIT_NUM[UNIT.indexOf(token)];
                } else {
                    tmpResult += num;
                    result += (tmpResult!=0) ? tmpResult * UNIT_NUM[UNIT.indexOf(token)] : UNIT_NUM[UNIT.indexOf(token)];
                    tmpResult = 0;
                }
                num = 0;
            } else { //단위가 아니라면 check값을 num에 넣는다.
                num = check;
            }
        } // end of while

        return result + tmpResult + num;
    }
}