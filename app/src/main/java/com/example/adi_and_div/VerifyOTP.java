package com.example.adi_and_div;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adi_and_div.utils.UtilsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class VerifyOTP extends AppCompatActivity{
    private EditText collegeMail_ET, otp_ET;
    private String collegeMail, otp;
    private Button verifyBtn;
    ProgressBar progressBar;
    UtilsService utilsService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        verifyBtn = findViewById(R.id.verifyBtn);
        collegeMail_ET = findViewById(R.id.collegeMailVerify);
        otp_ET = findViewById(R.id.otp);
        progressBar = findViewById(R.id.progressBar);
        utilsService = new UtilsService();
        // Handle Login Button
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilsService.hideKeyboard(view, VerifyOTP.this);
                collegeMail = collegeMail_ET.getText().toString();
                otp = otp_ET.getText().toString();

                if (validateData(view)) {
                    verifyUser(view);
                }
            }
        });
    }
    private void verifyUser (View view){
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("collegeMail", collegeMail);
        params.put("verificationToken", otp);

        String apiKey = "http://192.168.29.118:8000/api/v1/users/verify";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(VerifyOTP.this, "Verification successful. Please login.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOTP.this, LoginActivity.class));
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException je) {
                    je.printStackTrace();
                    Toast.makeText(VerifyOTP.this, "An error occurred while parsing the error response", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(VerifyOTP.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                        Toast.makeText(VerifyOTP.this, "An error occurred while parsing the error response", Toast.LENGTH_SHORT).show();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                return params;
            }
        };

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }



    public boolean validateData (View view) {
        boolean result;

        if(!TextUtils.isEmpty(collegeMail)){
            if(!TextUtils.isEmpty(otp)){
                result = true;
            } else {
                utilsService.showSnackBar(view, "Please enter password");
                result = false;
            }
        } else {
            utilsService.showSnackBar(view, "Please enter college mail");
            result = false;
        }
        return result;
    }
}

