package com.example.adi_and_div;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import javax.xml.transform.ErrorListener;

public class MainActivity extends AppCompatActivity {
    private Button loginBtn, signupBtn;
    private EditText name_ET, collegeName_ET, collegeMail_ET, branch_ET, batch_ET, phone_ET, password_ET;
    ProgressBar progressBar;

    private String name, collegeName, collegeMail, branch, batch, phone, password;
    UtilsService utilsService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signupBtn = findViewById(R.id.signupBtn);
        name_ET = findViewById(R.id.name);
        collegeName_ET = findViewById(R.id.collegeName);
        collegeMail_ET = findViewById(R.id.collegeMail);
        branch_ET = findViewById(R.id.branch);
        batch_ET = findViewById(R.id.batch);
        phone_ET = findViewById(R.id.phone);
        password_ET = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        utilsService = new UtilsService();
        // Handle Login Button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.adi_and_div.LoginActivity.class);
                startActivity(intent);
            }
        });

        // Handle Sign Up Button (Optional functionality)
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilsService.hideKeyboard(view, MainActivity.this);
                name = name_ET.getText().toString();
                collegeName = collegeName_ET.getText().toString();
                collegeMail = collegeMail_ET.getText().toString();
                branch = branch_ET.getText().toString();
                batch = batch_ET.getText().toString();
                phone = phone_ET.getText().toString();
                password = password_ET.getText().toString();

                if(validateData(view)){
                    registerUser(view);
                }
            }
        });
    }

    private void registerUser(View view){
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("collegeName", collegeName);
        params.put("collegeMail", collegeMail);
        params.put("branch", branch);
        params.put("batch", batch);
        params.put("phoneNumber", phone);
        params.put("password", password);

        String apiKey = "http://192.168.29.118:8000/api/v1/users/register";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        Toast.makeText(MainActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, VerifyOTP.class));
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException je) {
                    je.printStackTrace();
                    Toast.makeText(MainActivity.this, "An error occurred while parsing the error response", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response != null){
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,  "utf-8"));
                        
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(MainActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                        Toast.makeText(MainActivity.this, "An error occurred while parsing the error response", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue  = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public boolean validateData(View view){
        boolean result;

        if(!TextUtils.isEmpty(name)){
            if(!TextUtils.isEmpty(collegeName)){
                if(!TextUtils.isEmpty(collegeMail)){
                    if(!TextUtils.isEmpty(branch)){
                        if(!TextUtils.isEmpty(batch)){
                            if(!TextUtils.isEmpty(phone)){
                                if(!TextUtils.isEmpty(password)){
                                    result = true;
                                } else {
                                    utilsService.showSnackBar(view, "Please enter password");
                                    result = false;
                                }
                            } else {
                                utilsService.showSnackBar(view, "Please enter phone");
                                result = false;
                            }
                        } else {
                            utilsService.showSnackBar(view, "Please enter batch");
                            result = false;
                        }
                    } else {
                        utilsService.showSnackBar(view, "Please enter branch");
                        result = false;
                    }
                } else {
                    utilsService.showSnackBar(view, "Please enter college mail");
                    result = false;
                }
            } else {
                utilsService.showSnackBar(view, "Please enter college name");
                result = false;
            }
        } else {
            utilsService.showSnackBar(view, "Please enter name");
            result = false;
        }

        return result;
    }
}