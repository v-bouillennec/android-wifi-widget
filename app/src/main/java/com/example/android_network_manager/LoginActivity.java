package com.example.android_network_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView responseTxt;
    private String cookie = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        responseTxt = (TextView) findViewById(R.id.responseTxt);
        usernameEditText = findViewById(R.id.activity_main_usernameEditText);
        passwordEditText = findViewById(R.id.activity_main_passwordEditText);
        loginButton = findViewById(R.id.activity_main_loginButton);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://preprod.citypassenger.com/ws/User/Login?login="+"vbouillennec@citypassenger.com"+"&password="+"dsdfsdf";
//        String url2 = "https://preprod.citypassenger.com/ws/User/Login?login="+"vbouillennec@citypassenger.com"+"&password="+"dsdfsdf";
        String url2 = "https://preprod.citypassenger.com/ws/User/Logged";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("swagger","Response is: "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("swagger","That didn't work!: "+error.toString());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> responseHeaders = response.headers;
                Log.i("swagger", "all set-cookie: "+ response.allHeaders.toString());

                List<Header> responseAllHeaders = response.allHeaders;
                for(Header header : responseAllHeaders){
                    if(header.getName().equals("Set-Cookie") && header.getValue().contains("VitrineAuthKey")){
                        cookie = header.getValue().split(";")[0];
//                        Log.i("swagger", "VitrineAuthKey1: "+header.getValue().split("[=;]")[1]);
                        Log.i("swagger", "VitrineAuthKey2: "+header.getValue());
                    }
                }

                Log.i("swagger", "Full cookie: "+responseAllHeaders.toString());

                JSONObject jsonObject = new JSONObject(response.headers);
                Log.i("swagger", "response: "+String.valueOf(jsonObject));
                return super.parseNetworkResponse(response);
            }
        };

        // Request a string response from the provided URL.
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("swagger","is Logged ? "+ response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("swagger","That didn't work!: "+error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                Log.i("swagger", "putHeaders: "+cookie);
                if(cookie.contains("="))
                    headers.put("Cookie: VitrineAuthKey", cookie.split("=")[1]);
                Log.i("swagger","headers?"+headers.toString());
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
              Map<String, String> responseHeaders = response.headers;
                Log.i("swagger", "all headers2: "+ response.allHeaders.toString());
                return super.parseNetworkResponse(response);
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.add(stringRequest2);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getText().length() > 0 && passwordEditText.getText().length() > 0) {
//                    String toastMessage = "Username: " + usernameEditText.getText().toString() + ", Password: " + passwordEditText.getText().toString();
//                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();

//                    tryLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());

                } else {
                    String toastMessage = "Username or Password are not populated";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}