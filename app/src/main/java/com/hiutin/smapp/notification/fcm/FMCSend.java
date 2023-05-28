package com.hiutin.smapp.notification.fcm;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FMCSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SEVER_KEY ="key=AAAAWm6zYGE:APA91bFw_eHb5CYKpGag4dC5TVXy0qh1qcmwkJTEfb_BROs8TwPg1b9TbQcuwd7zEPjmFa84XgDoHwY92j399OgNzJDcaRMfA76JiENHYgPoxLgrpV7BrNBbX5hPhtV9ukYbX8tljdgo";

    public static void pushNotification(Context context,String token,String title,String messsage) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", messsage);
            jsonObject.put("notification", notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SEVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
