package com.es.household_account_book;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalendarExpenRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://moomoo.dothome.co.kr/Cal_Expen.php";
    private Map<String, String> map;

    public CalendarExpenRequest(String Expen_UserID, String Expen_Date, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("Expen_UserID",Expen_UserID);
        map.put("Expen_Date",Expen_Date);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}

