package com.es.household_account_book.expenses_element;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class expenses_voice_request extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://moomoo.dothome.co.kr/Expen.php";
    private Map<String, String> map;

    public expenses_voice_request(String Expen_Date, String Expen_Category, String Expen_Sum, String Expen_UserID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("Expen_Date", Expen_Date);
        map.put("Expen_Category", Expen_Category);
        map.put("Expen_Sum", Expen_Sum);
        map.put("Expen_UserID", Expen_UserID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
