package com.es.household_account_book;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchIncomeRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://moomoo.dothome.co.kr/Search_Income.php";
    private Map<String, String> map;

    public SearchIncomeRequest(String Income_UserID, String Income_Category, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("Income_UserID",Income_UserID);
        map.put("Income_Category",Income_Category);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
