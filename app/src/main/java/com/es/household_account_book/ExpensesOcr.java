package com.es.household_account_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExpensesOcr extends AppCompatActivity {
    static final int REQUEST_CODE = 2;

    ImageView imageView;    // 갤러리에서 가져온 이미지를 보여줄 뷰
    Uri uri;                // 갤러리에서 가져온 이미지에 대한 Uri
    Bitmap bitmap;          // 갤러리에서 가져온 이미지를 담을 비트맵
    InputImage image = null;       // ML 모델이 인식할 인풋 이미지
    TextView text_info;     // ML 모델이 인식한 텍스트를 보여줄 뷰
    Button btn_get_image, btn_detection_image;  // 이미지 가져오기 버튼, 이미지 인식 버튼
    TextRecognizer recognizer;    //텍스트 인식에 사용될 모델
    Button btn_picture; //사진 찍는 버튼

    static final int REQUEST_IMAGE_CAPTURE = 672;
    private String resultText1 = null, resultText2 = null;

    String Expen_UserID = ((LoginActivity) LoginActivity.context_login).userID;

    String won="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_ocr);

        imageView = findViewById(R.id.imageView);
        text_info = findViewById(R.id.text_info);
        recognizer = TextRecognition.getClient();    //텍스트 인식에 사용될 모델
        btn_picture = (Button) findViewById(R.id.takePicture); // 카메라 촬영하기 버튼

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        // 카메라 촬영하기 버튼 클릭 시
        btn_picture = findViewById(R.id.takePicture);
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ExpensesOcr.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        // 이미지 가져오기 버튼 클릭 시
        btn_get_image = findViewById(R.id.btn_get_image);
        btn_get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // 텍스트 인식 버튼 클릭 시
        btn_detection_image = findViewById(R.id.btn_detection_image);
        btn_detection_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognition(recognizer);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE) {
            // 갤러리에서 선택한 사진에 대한 uri를 가져온다.
            uri = data.getData();

            setImage(uri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            image = InputImage.fromBitmap(imageBitmap, 0);
        }
    }

    // uri를 비트맵으로 변환시킨후 이미지뷰에 띄워주고 InputImage를 생성하는 메서드
    private void setImage(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bitmap);

            image = InputImage.fromBitmap(bitmap, 0);
            Log.e("setImage", "이미지 to 비트맵");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void TextRecognition(TextRecognizer recognizer) {

        if (image==null) {
            Toast.makeText(getApplicationContext(), "사진이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Task<Text> result = recognizer.process(image)

                    // 이미지 인식에 성공하면 실행되는 리스너
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            Log.e("텍스트 인식", "성공");
                            // Task completed successfully
                            String resultText = visionText.getText();

                            // 날짜 전처리
                            String regEx1 = "(\\s|20)\\d{2}(\\.|\\/)((11|12)|(0?(\\d)))(\\.|\\/)(30|31|((0|1|2)?\\d))"; // 날짜 정규식
                            Matcher date = Pattern.compile(regEx1).matcher(resultText);
                            while (date.find()) {
                                resultText1 = date.group();
                            }
                            if (resultText1 == null) {
                                Date currentTime = Calendar.getInstance().getTime();
                                resultText1 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentTime);

                            }
                            // 가격 전처리
                            String regEx2 = "\\d{1,}(\\,|\\.)(\\d{3}|(\\s\\d{3}))"; // 가격 전처리
                            Matcher price = Pattern.compile(regEx2).matcher(resultText);
                            while (price.find()) {
                                resultText2 = price.group();
                                won = regEx2;
                                won = won.replace('.', ',');
                            }

                            if (resultText2 == null) {
                                resultText2 = "0";
                                won="0";
                            }
                            resultText = "📅 " + resultText1 + "\n" + "💵 " + resultText2 + "원";

                            text_info.setText(resultText);  // 인식한 텍스트를 TextView에 세팅

                            // Expen_Date에 값 저장
                            String[] edate = resultText1.split("" + "/|\\.");

                            String year = edate[0];
                            String month = edate[1];
                            String day = edate[2];

                            String Expen_Date = year + "/" + month + "/" + day;

                            String Expen_Sum = won;

                            // Expen_Category에 값 저장
                            String Expen_Category = "기타";

                            // DB연동
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success");

                                        // 지출 입력에 성공한 경우
                                        if (success) {
                                            Toast.makeText(getApplicationContext(), "지출 입력에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                        } else { // 지출 입력에 실패한 경우
                                            Toast.makeText(getApplicationContext(), "지출 입력에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            // 서버로 Volley를 이용해서 요청을 함.
                            ExpenRequest ExpenRequest = new ExpenRequest(Expen_Date, Expen_Category, Expen_Sum, Expen_UserID, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ExpensesOcr.this);
                            queue.add(ExpenRequest);

                        }
                    })
                    // 이미지 인식에 실패하면 실행되는 리스너
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("텍스트 인식", "실패: " + e.getMessage());
                                }
                            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExpensesOcr.this, Expenses.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }
}