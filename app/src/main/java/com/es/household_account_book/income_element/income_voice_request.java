package com.es.household_account_book.income_element;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class income_voice_request extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://moomoo.dothome.co.kr/Income.php";
    private Map<String, String> map;

    public income_voice_request(String Income_Date, String Income_Category, String Income_Sum, String Income_UserID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("Income_Date", Income_Date);
        map.put("Income_Category", Income_Category);
        map.put("Income_Sum", Income_Sum );
        map.put("Income_UserID", Income_UserID );
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}