package com.example.client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserList extends AppCompatActivity {
    ListView lvUser;
    String[] user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        String[] data =intent.getStringArrayExtra("values");
        user = new String[data.length+1];
        user[0]="Всем";
        for (int i=1;i<data.length+1;i++)user[i]=data[i-1];
        lvUser = (ListView) findViewById(R.id.lvUsers);
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,user);
        lvUser.setAdapter(adapter);
        lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent answerIntent = new Intent();
                answerIntent.putExtra("user",user[(int)id]);
                setResult(RESULT_OK, answerIntent);
                finish();
            }
        });
    }
}
