package com.tongji.tantairs.olderandroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyService2 extends Service {
    private File root1 = Environment.getExternalStorageDirectory();
    @Override
    public void onCreate() {
        registerScreenActionReceiver();
    }

    public void registerScreenActionReceiver() {
        Receiver1 receiver = new Receiver1();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    public class Receiver1 extends BroadcastReceiver {
        FileOutputStream out;
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ArrayList<String> tmp = new ArrayList<String>();
        File file1;
        String ton, toff;
//        String filename1="/TrackingData2/"+ new  java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+".csv";
        String filename1="/TrackingDataScreen/"+ new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+".csv";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                ton = DateFormat.format(new Date());
                System.out.println("亮屏++"  + ton);
            }
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                toff = DateFormat.format(new Date());
                System.out.println("灭屏++"  + toff);
            }
            tmp.add(ton);
            tmp.add(toff);
            System.out.println("the tmp size is: " + tmp.size());
            if(tmp.size() > 12){
                saveScreenData(tmp);
                tmp.clear();
            }
        }

        public void saveScreenData(ArrayList<String> list){
            System.out.println("进入亮屏灭屏保存模块++");
            try {
                if(root1.canWrite()){
                    file1=new File(Environment.getExternalStorageDirectory(),filename1);
                    System.out.println("亮灭屏文件生成成功++");
                }
                out=new FileOutputStream(file1,true);
                System.out.println("the value of out is: " +  out );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for(int i = 0; i< list.size(); i+=2){
                try {
                    out.write(list.get(i).getBytes());
                    out.write("on".getBytes());
                    out.write('\n');
                    out.write(list.get(i+1).getBytes());
                    out.write("off".getBytes());
                    out.write('\n');
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            System.out.println("the value has insert sucefully ");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
