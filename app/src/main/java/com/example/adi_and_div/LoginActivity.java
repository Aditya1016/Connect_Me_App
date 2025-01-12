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
import com.example.adi_and_div.R;
import com.example.adi_and_div.utils.UtilsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn, signupBtn;
    private EditText collegeMail_ET, password_ET;
    private String collegeMail, password;
    ProgressBar progressBar;
    UtilsService utilsService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        collegeMail_ET = findViewById(R.id.collegeMail);
        password_ET = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        utilsService = new UtilsService();
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, com.example.adi_and_div.MainActivity.class);
                startActivity(intent);
            }
        });
        // Handle Login Button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilsService.hideKeyboard(view, LoginActivity.this);
                collegeMail = collegeMail_ET.getText().toString();
                password = password_ET.getText().toString();

                if(validateData(view)){
                    loginUser(view);
                }
            }
        });
    }
    private void loginUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("collegeMail", collegeMail);
        params.put("password", password);

        String apiKey = "http://192.168.29.118:8000/api/v1/users/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(LoginActivity.this, "Login successful. Redirecting...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                        } finally {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException | UnsupportedEncodingException je) {
                                je.printStackTrace();
                                Toast.makeText(LoginActivity.this, "An error occurred while parsing the error response", Toast.LENGTH_SHORT).show();
                            } finally {
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        int socketTimeout = 10000; // Reduce timeout for better responsiveness
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    public boolean validateData (View view){
        boolean result;

        if(!TextUtils.isEmpty(collegeMail)){
                if(!TextUtils.isEmpty(password)){
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