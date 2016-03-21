package com.example.client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ChatActivity extends AppCompatActivity {
    static SocketTask socketTask = null;
    RelativeLayout chatLayout;
    TextView tvTo,tvMsg,tvChat;
    EditText etDestNick,etMsg;
    Button btnSend;
    static  boolean authFlag=false,listFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatLayout = (RelativeLayout) findViewById(R.id.chatLayout);
        chatLayout.setVisibility(View.GONE);
        tvTo = (TextView)findViewById(R.id.tvDest);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvChat = (TextView) findViewById(R.id.tvChatFrame);
        etDestNick = (EditText) findViewById(R.id.etDestNickname);
        etMsg = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFlag=true;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMsg.getText().toString().equals("")){
                    Toast.makeText(ChatActivity.this,"Введите сообщение",Toast.LENGTH_LONG).show();
                    return;
                }
                if(etDestNick.getText().toString().equals("")){
                    Toast.makeText(ChatActivity.this,"Введите имя пользователя",Toast.LENGTH_LONG).show();
                    return;
                }
                socketTask.msg = etMsg.getText().toString();
                socketTask.destNick = etDestNick.getText().toString();
            }
        });
        Intent intent = getIntent();
        socketTask = new SocketTask();
        socketTask.execute(intent.getStringExtra("name"));
    }

    @Override
    public void onBackPressed() {

        openDcDialog();
    }
    private void openDcDialog() {
        AlertDialog.Builder dcDialog = new AlertDialog.Builder(this);
        dcDialog.setTitle("Выйти из сеанса?");
        dcDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dcDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dcDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (authFlag) {
            socketTask.flag = true;
        }
        super.onDestroy();

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String nick = data.getStringExtra("user");
        etDestNick.setText(nick);
    }


     class SocketTask extends AsyncTask<String,String,Void> {
         ProgressDialog pdConnect;
         private String nickname, SERV_ADDR ="192.168.1.68",text ="";
         public String msg ="",destNick="";
         private int PORT =8000;
         public boolean flag=false;


        @Override
        protected void onPreExecute() {
            pdConnect = new ProgressDialog(ChatActivity.this);
            pdConnect.setTitle("Клиент");
            pdConnect.setMessage("Соединение");
            pdConnect.show();
            authFlag=false;
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);
            if(values[0].equals("auth")){
                tvChat.setText("Добро пожаловать, " + nickname);
                chatLayout.setVisibility(View.VISIBLE);
                pdConnect.dismiss();
                return;
            }
            if(values[0].equals("error")){
                Toast.makeText(ChatActivity.this,values[1].toString(),Toast.LENGTH_LONG).show();
                flag=true;
                return;
            }
            if(Utility.parse(values[0]).equals("nickname_is_already_used")){
                Toast.makeText(ChatActivity.this,"Данное имя  уже используется",Toast.LENGTH_LONG).show();
                flag=true;
                return;
            }
            if(Utility.parse(values[0]).equals("nickname_is_system_reserved")){
                Toast.makeText(ChatActivity.this,"Данное имя зарезервировано системой",Toast.LENGTH_LONG).show();
                flag=true;
                return;
            }
            if(Utility.parse(values[0]).equals("completed")){
                Toast.makeText(ChatActivity.this,"Отправлено",Toast.LENGTH_LONG).show();
                return;
            }
            if(Utility.parse(values[0]).equals("user_not_found")){
                Toast.makeText(ChatActivity.this,"Пользователь не найден",Toast.LENGTH_LONG).show();
                return;
            }
            if(Utility.parse(values[0]).equals("users_not_found")){
                Toast.makeText(ChatActivity.this,"В данный момент нет активных пользователей",Toast.LENGTH_LONG).show();
                return;
            }
            if(Utility.parse(values[0]).equals("send_msg")){
                String nickname = Utility.getParams(values[0],0);
                String msg = Utility.getParams(values[0],1);;
                tvChat.setText(tvChat.getText() + "\n" + nickname + ": " + msg);
            }
            if(Utility.parse(values[0]).equals("list")){
                String user[]=Utility.parseList(values[0]);
                Intent intent = new Intent(ChatActivity.this,UserList.class);
                intent.putExtra("values",user);
                startActivityForResult(intent, 0);
            }
        }


        @Override
        protected Void doInBackground(String... values) {
            this.nickname=values[0];
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(SERV_ADDR, PORT), 2000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                Utility.sendMsg(dataOutputStream, Utility.createConnectJson(nickname).toString());
                String result= Utility.getMsg(dataInputStream);
                if(Utility.parse(result).equals("ok")){
                        publishProgress("auth");
                        authFlag=true;
                }
                else publishProgress(result);
                while (!flag) {
                    text = "";
                    if (dataInputStream.available() > 0) {
                        text = Utility.getMsg(dataInputStream);
                        publishProgress(text);
                    }

                    if(listFlag){
                        String msgResult = Utility.sendMsg(dataOutputStream,Utility.createGetJson().toString());
                        if(!msgResult.equals(null)){
                            if(msgResult.equals("java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)"))
                                publishProgress("error","Соединение с сервером потеряно");
                        }
                        listFlag=false;
                    }

                    if (!msg.equals("")) {
                        String msgResult=Utility.sendMsg(dataOutputStream, Utility.createMessageJson(msg, destNick).toString());
                                if(!msgResult.equals(null)){
                                    if(msgResult.equals("java.net.SocketException: sendto failed: EPIPE (Broken pipe)"))
                                        publishProgress("error","Ошибка, данный сокет закрыт");
                        }
                        msg = destNick = "";
                    }
                }
            } catch (UnknownHostException e) {
                publishProgress("error",e.toString());
                return null;
            }
            catch (SocketTimeoutException e){
                publishProgress("error","Ошибка соединения с сервером");
                return null;
            }
            catch (SocketException e){
                publishProgress("error","Ошибка соединения");
                return null;
            }

            finally {
                if(!authFlag)return null;
                if (socket != null) {
                    try {
                        Utility.sendMsg(dataOutputStream,Utility.createDisconnectJson().toString());
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            if(authFlag){
            tvChat.setText("");
            etDestNick.setText("");
            etMsg.setText("");
            authFlag=false;}
            else pdConnect.dismiss();
            finish();
            super.onPostExecute(result);
        }


    }


}
