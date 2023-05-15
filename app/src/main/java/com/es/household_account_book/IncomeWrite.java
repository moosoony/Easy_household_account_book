package com.es.household_account_book;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

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

public class IncomeWrite extends AppCompatActivity {
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

    int year;
    int month;
    int day;

    String Income_UserID = ((LoginActivity) LoginActivity.context_login).userID;

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
                Toast.makeText(IncomeWrite.this, date, Toast.LENGTH_LONG).show();
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


        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = createSpeechRecognizer(IncomeWrite.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sttBtn.setImageResource(R.drawable.clickedinputptn);
                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출
                onstart("음성 입력을 원하시면 네, 라고 말씀해주세요");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecognizer.startListening(intent); //듣기 시작
                    }
                }, 3000);// 1.8초 정도 딜레이를 준 후 시작
            }
        });


        // 저장 버튼 클릭시 수행
        btn_isubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String year = String.valueOf(datePicker.getYear());
                String month = String.valueOf(datePicker.getMonth() + 1);
                String day = String.valueOf(datePicker.getDayOfMonth());

                String Income_Date = year+"/"+month+"/"+day;


                // Income_Category에 값 저장
                String Income_Category = null;

                // Income_Sum에 값 저장
                String Income_Sum = amount.getText().toString();

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
                IncomeRequest incomeRequest = new IncomeRequest(Income_Date, Income_Category, Income_Sum, Income_UserID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(IncomeWrite.this);
                queue.add(incomeRequest);
            }

        });

    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 말하기 시작할 준비가되면 호출

        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
            Toast sttToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

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

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌

            ArrayList<String> matches =
                    results.getStringArrayList(RESULTS_RECOGNITION);
            String result = "";

            for (int i = 0; i < matches.size(); i++) {
                result = matches.get(i);
            }

            if (result.equals("네") || result.equals("예")) {
                onstart(result);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(IncomeWrite.this, com.es.household_account_book.income_element.date.class);
                        startActivity(intent);
                        finish();
                    }
                }, 600);// 0.6초 정도 딜레이를 준 후 시작

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

    //해당되는 stt가 없을 때
    public void no_stt() {
        String retext = "다시 입력해주세요.";
        onstart(retext);
        Toast.makeText(getApplicationContext(), retext, Toast.LENGTH_SHORT).show();
    }

    // tts
    public void onstart(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(3000, TextToSpeech.QUEUE_ADD, null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(IncomeWrite.this, MainMenu.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    public void doToastMsg(String str) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

//        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

}
