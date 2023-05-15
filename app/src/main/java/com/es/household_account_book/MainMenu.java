package com.es.household_account_book;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenu extends AppCompatActivity {

    RelativeLayout home_ly;
    BottomNavigationView bottomNavigationView;

    final int PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        init(); //객체 정의
        SettingListener(); //리스너 등록

        //맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);
    }

    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new Frag1())
                            .commit();
                    return true;
                }
                case R.id.tab_calendar: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new Frag2())
                            .commit();
                    return true;
                }
                case R.id.tab_money: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new Frag3())
                            .commit();
                    return true;
                }
                case R.id.tab_search: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new Frag4())
                            .commit();
                    return true;
                }
                case R.id.tab_analysis: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new Frag5())
                            .commit();
                    return true;
                }
            }

            return false;
        }
            }
        }



