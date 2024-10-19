package com.example.cloudmusic.http;

import android.util.Log;

import com.alibaba.fastjson2.JSONObject;
import com.example.cloudmusic.contant.ServerConstant;
import com.example.cloudmusic.tools.JsonHelper;
import com.example.cloudmusic.tools.StringHelper;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpExecutor {

    public static JSONObject httpGet(){

        HttpURLConnection connection;
        try {
            URL url = new URL(ServerConstant.URL_LIST_MUSICS.getDesc());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //设置请求方式 GET / POST 一定要大小
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code" + responseCode);
            }

            String httpResponse = getStringByStream(connection.getInputStream());

            return JsonHelper.getJsonObjectFromString(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JsonHelper.getEmptyJsonObject();
    }

    public static String httpGetString(String urlString){

        HttpURLConnection connection;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //设置请求方式 GET / POST 一定要大小
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code" + responseCode);
            }

            String httpResponse = getStringByStream(connection.getInputStream());

            return httpResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StringHelper.getEmptyString();
    }

    public static void httpPost(){
        HttpURLConnection connection=null;
        try {
            URL url = new URL("你要请求的网址，比如www.某度.com");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //设置请求方式 GET / POST 一定要大小
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code" + responseCode);
            }
            String result = getStringByStream(connection.getInputStream());
            if (result == null) {
                Log.d("Fail", "失败了");
            }else{
                Log.d("succuss", "成功了 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStringByStream(InputStream inputStream){
        Reader reader;
        try {
            reader=new InputStreamReader(inputStream,"UTF-8");
            char[] rawBuffer=new char[512];
            StringBuffer buffer=new StringBuffer();
            int length;
            while ((length=reader.read(rawBuffer))!=-1){
                buffer.append(rawBuffer,0,length);
            }
            return buffer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
