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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class Frag3 extends Fragment implements View.OnClickListener {
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ImageButton sttBtn;
    TextView textView;

    final int PERMISSION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Context ct;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stt, container, false);
        ct = container.getContext();
        textView = view.findViewById(R.id.sttResult);
        sttBtn = (ImageButton) view.findViewById(R.id.sttStart);

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(ct, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // 오디오, 카메라 권한설정
        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions((Activity) ct, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        // RecognizerIntent 객체 생성, 언어는 한국어로 설정
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer= createSpeechRecognizer(ct); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
        mRecognizer.setRecognitionListener(listener); // 리스너 설정

        // sttBtn 클릭 시
        sttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sttBtn.setImageResource(R.drawable.clickedinputptn);
                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출
                onstart("말씀해주세요");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecognizer.startListening(intent); //듣기 시작
                    }
                }, 1000);// 1.8초 정도 딜레이를 준 후 시작
            }
        });
        return view;
    }

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

            Toast.makeText(getActivity(),"에러가 발생하였습니다. : "+message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌

            ArrayList<String> matches =
                    results.getStringArrayList(RESULTS_RECOGNITION);
            String result = "";

            for (int i = 0; i < matches.size(); i++) {
                textView.setText(matches.get(i));
                result = matches.get(i);
            }
            if (result.equals("지출") || result.equals("지충")|| result.equals("제출")|| result.equals("17분")) {
                onstart(result);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), Expenses.class); //fragment라서 activity intent와는 다른 방식
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }, 600);// 0.6초 정도 딜레이를 준 후 시작
            } else if (result.equals("수입")) {
                onstart(result);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), Income.class); //fragment라서 activity intent와는 다른 방식
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }, 600);// 0.6초 정도 딜레이를 준 후 시작
            } else {
                String retext = "다시 입력해주세요.";
                onstart(retext);
                 Toast.makeText(getActivity(),retext,Toast.LENGTH_SHORT).show();

                listener.onBeginningOfSpeech(); //onBeginningOfSpeech()함수 호출

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
    public void onstart(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    @Override
    public void onClick(View view) {

    }
} // end of Frag3 extends Fragment