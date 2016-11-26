package com.fantasticfive.shareback.newshareback.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.helpers.LoginHelper;

public class LoginActivityNew extends AppCompatActivity implements View.OnClickListener, LoginHelper.Callback{

    EditText etUname, etPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity_new);
        init();

        //Listeners
        btnLogin.setOnClickListener(this);
    }

    protected void init(){
        etUname = (EditText) findViewById(R.id.uname);
        etPass = (EditText) findViewById(R.id.pass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    @Override
    public void onClick(View view) {
        String username = etUname.getText().toString();
        String password = etPass.getText().toString();
        Toast.makeText(LoginActivityNew.this, "Sending Login Request...", Toast.LENGTH_SHORT).show();
        new LoginHelper(this).sendLoginRequest(username, password);
    }

    @Override
    public void onLoginResponse(String userhash) {
        Toast.makeText(LoginActivityNew.this, userhash, Toast.LENGTH_SHORT).show();
        if (userhash != null){
            Intent intent = new Intent(this, NewMainActivity.class);
            SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constants.PREF_LOGIN_TOKEN, userhash);
            editor.apply();

            startActivity(intent);
        }
        else
            Toast.makeText(LoginActivityNew.this, "Invalid Details", Toast.LENGTH_SHORT).show();
    }
}