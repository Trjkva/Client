package com.example.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity  {
    TextView tvHint;
    EditText etNickname;
    ImageView imageView;
    Button btnConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tvHint = (TextView)findViewById(R.id.tvHint);
        imageView = (ImageView)findViewById(R.id.imageView);
        etNickname = (EditText)findViewById(R.id.etNickname);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNickname.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this,"Введите имя",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                intent.putExtra("name",etNickname.getText().toString());
                startActivity(intent);

            }
        });

    }
    @Override
    protected void onDestroy() {
        if (ChatActivity.authFlag) {
            ChatActivity.socketTask.flag = true;
        }
        super.onDestroy();

    }








}


