package com.example.client;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Utility {
    static String parse (String json) {
        String result;

        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            result = object.getString("result");
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    static String[] parseList (String json) {
        String user[]=null;

        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray param =object.getJSONArray("params");
            user = new String[param.length()];
            for (int i=0;i<user.length;i++)user[i]=param.getString(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    static String getParams(String message,int flag){
        String param="";
        try{
            JSONObject object = (JSONObject) new JSONTokener(message).nextValue();
            JSONArray params = object.getJSONArray("params");
            switch (flag){
                case 0: param = params.getString(0);
                    break;
                case 1: param = params.getString(1);
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    static JSONObject createConnectJson(String nickname){
        JSONObject obj = null;
        try {
            obj= new JSONObject();
            obj.put("method", "connect");
            JSONArray param = new JSONArray();
            param.put(nickname);
            obj.put("params", param);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSON Exception: ", e.toString());
        }

        return obj;
    }

    static JSONObject createGetJson(){
        JSONObject obj = null;
        try {
            obj= new JSONObject();
            obj.put("method", "get_list");
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSON Exception: ", e.toString());
        }

        return obj;
    }

    static JSONObject createDisconnectJson(){
        JSONObject obj = null;
        try {
            obj= new JSONObject();
            obj.put("method", "disconnect");
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSON Exception: ", e.toString());
        }

        return obj;
    }

    static JSONObject createMessageJson(String message,String nickname){
        JSONObject obj = null;
        try {
            obj= new JSONObject();
            obj.put("method", "send_msg");
            JSONArray param = new JSONArray();
            if(!nickname.equals("Всем"))param.put(nickname);
            else param.put("all");
            param.put(message);
            obj.put("params", param);
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("JSON Exception: ", e.toString());
        }

        return obj;
    }


    static String getMsg(DataInputStream in) {
        String text ="";
        try {
            int len = in.readInt();
            char buf[] = new char[len];
            for (int i = 0; i < len; i++) {
                buf[i] = in.readChar();
                text += buf[i];
            }
            return text;
        } catch (IOException e) {
            Log.e("Socket", e.toString());
        }
        return null;
    }
    static String sendMsg(DataOutputStream out,String msg){
        try{out.writeInt(msg.length());
        char buf[] = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            buf[i] = msg.charAt(i);
            out.writeChar(buf[i]);
         }
        out.flush();
        }
        catch (IOException e){
            return e.toString();
        }
        return null;
    }
}
