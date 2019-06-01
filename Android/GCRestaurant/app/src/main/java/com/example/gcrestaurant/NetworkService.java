package com.example.gcrestaurant;

import android.os.Debug;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NetworkService extends Service implements NetworkReceiveInterface{
    private static List<NetworkReceiveInterface> Listener = new LinkedList<>(); // 메인 쓰레드에서만 작업
    public static NetworkService instance = null;

    // 서버와 연결, 이때 인증 모듈도 다시 실행된다.
    public static void Connect()
    {
        // 소켓 쓰레드 실행
        new ESocket().start();
    }
    public static void setListener(NetworkReceiveInterface receiveInterface)
    {
        Listener.add(receiveInterface);
    }
    public static void removeListener(NetworkReceiveInterface receiveInterface)
    {
        Listener.remove(receiveInterface);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "서비스의 onCreate");

        // 어플리케이션에 해당 서비스 실행을 알림
        instance = this;

        Connect();
        NetworkService.SendDebugMessage("키 : " + MainActivity.getKeyHash(getApplicationContext()));

        // GPS 서비스를 실행시켜 백그라운드에서도 실시간으로 위치 정보 전송
        if (GPSService.instance == null) {
            GPSService service = new GPSService(this);
            service.Start();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    Handler ReceiveHandler = new Handler() {
        public void handleMessage(Message msg) {
            String message = msg.getData().getString("data");
            JSONObject json = null;

            // 메세지 디버그가 필요하면 주석 해제
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            try
            {
                json = new JSONObject(message);
            }
            catch ( Exception e)
            {

            }
            instance.ReceivePacket(json);
            // 최종 리스너(액티비티)에게 패킷 전달
            for (NetworkReceiveInterface context : Listener) {
                context.ReceivePacket(json);
            }

        }
    };

    public void ReceivePacket(JSONObject json)
    {
        try
        {
            switch (json.getInt("type"))
            {
                case PacketType.Login:
                    if (json.getBoolean("result"))
                    {
                        GlobalApplication.user_id = json.getInt("id");
                        GlobalApplication.user_name = json.getString("name");
                        if (!json.isNull("icon"))
                            GlobalApplication.user_icon = json.getString("icon");
                        SendDebugMessage(GlobalApplication.user_name + "으로 로그인 성공" + " 이미지 : " + GlobalApplication.user_icon);
                    }
                    break;
                case PacketType.Message:
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (Exception e)
        {

        }

        SendDebugMessage("서비스가 패킷을 수신함");
    }

    public static void SendMessage(JSONObject json)
    {
        if (ESocket.instance != null)
            ESocket.instance.SendMessage(json);
    }

    public static void SendMessage(int type)
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("type", type);
        }
        catch ( Exception e)
        {
        }
        SendMessage(json);
    }
    public static void SendMessage(int type, String key, String message)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put("type", type);
            json.put(key, message);
        }
        catch ( Exception e)
        {

        }
        SendMessage(json);
    }

    public static void SendDebugMessage(String data)
    {
        JSONObject json = new JSONObject();

        try
        {
            json.put("type",PacketType.Debug);
            json.put("message",data);
        }
        catch ( Exception e)
        {

        }
        SendMessage(json);
    }
}
