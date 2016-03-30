package com.tongji.tantairs.olderandroid;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;;
import java.net.MalformedURLException;

/**
 * Created by tantairs on 2015/9/7.
 */
public class WebUtil {

    String url = "http://180.168.144.190:35821/Ageing_service.asmx/insertCargoInfo";

    public void postToDataSource(String name, String eqid, String time, double longitude, double latitude, String connection, boolean screen_on, String battery) {

        try {
            HttpPost requst = new HttpPost(url);
            requst.addHeader("Accept", "application/json");
            requst.addHeader("Content-type", "application/json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("NAME", name);
            jsonObject.put("EQID", eqid);
            jsonObject.put("TIME", time);
            jsonObject.put("LOCATION_X", longitude);
            jsonObject.put("LOCATION_Y", latitude);
            jsonObject.put("CONNECTION", connection);
            jsonObject.put("SCREEN_ON", screen_on);
            jsonObject.put("BATTERY", battery);
            StringEntity se = new StringEntity(jsonObject.toString());
            requst.setEntity(se);
            new DefaultHttpClient().execute(requst);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
