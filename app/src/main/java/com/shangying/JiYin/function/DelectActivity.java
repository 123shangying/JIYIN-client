package com.shangying.JiYin.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shangying.JiYin.R;

/**
 * @author shangying
 */
public class DelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delect);
        findViewById(R.id.delectyx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DelectActivity.this, Deweb.class); startActivity(intent);
//                activity生命周期相关的--调转后关闭前一个activiy
                //finish();
            }
        });
    }
}