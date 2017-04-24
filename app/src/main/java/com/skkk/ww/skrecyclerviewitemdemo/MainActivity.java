package com.skkk.ww.skrecyclerviewitemdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewFragment recyclerViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setDefaultFragment();
    }

    /**
     * 设置默认fragment
     */
    private void setDefaultFragment() {
        recyclerViewFragment = new RecyclerViewFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, recyclerViewFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerViewFragment.onActivityResult(requestCode,resultCode,data);
    }
}
