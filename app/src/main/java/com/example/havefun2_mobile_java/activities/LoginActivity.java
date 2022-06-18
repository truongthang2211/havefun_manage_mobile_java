package com.example.havefun2_mobile_java.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    Context context;
    public static Activity LoginAct;
    private boolean isShow= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        LoginAct = this;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", 0);
        String name = pref.getString("hostuserObject", "undefined");
        if (!name.equals("undefined")){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        Button eye_p = findViewById(R.id.eye_pass);
        TextView username = (TextView) findViewById(R.id.email_sign);
        TextView password = (TextView) findViewById(R.id.pass_sign);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.log_sign_btn);
        MaterialButton resbtn = findViewById(R.id.resg_ign_btn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ServerURL = getString(R.string.server_address);
                String SignIn = ServerURL + "/api/users/hostsignin";
                JSONObject account = new JSONObject();
                try {
                    account.put("email",username.getText());
                    account.put("password",password.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SignIn, account, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                JSONObject dataAccount = response.getJSONObject("data");
                                if (dataAccount != null){
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("User", 0);
                                    SharedPreferences.Editor edit = pref.edit();

                                    edit.putString("hostuserObject",dataAccount.toString());
                                    edit.apply();
                                    Intent intent = new Intent(context,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else if (status == 201){
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Email hoặc password không hợp lệ")
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("api", error.toString());
                    }
                });
                MySingleton.getInstance(context).addToRequestQueue(request);

            }
        });
        eye_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShow) {
                    eye_p.setSelected(true);
                    isShow = true;
                    //Password visible
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    eye_p.setSelected(false);
                    isShow= false;
                    //Password not visible
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        resbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}