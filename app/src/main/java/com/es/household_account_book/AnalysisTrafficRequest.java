package com.es.household_account_book;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AnalysisTrafficRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://moomoo.dothome.co.kr/Analysis_Traffic.php";
    private Map<String, String> map;

    public AnalysisTrafficRequest(String Now_ID, String formatedNow, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("Now_ID", Now_ID);
        map.put("formatedNow", formatedNow);


    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
