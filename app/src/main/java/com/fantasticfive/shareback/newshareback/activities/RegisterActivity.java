package com.fantasticfive.shareback.newshareback.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.dto.RegisterRequestDTO;
import com.fantasticfive.shareback.newshareback.helpers.RegisterHelper;
import com.fantasticfive.shareback.newshareback.dto.RegisterResponseDTO;

public class RegisterActivity
        extends AppCompatActivity
        implements View.OnClickListener, RegisterHelper.Callback{

    EditText etFname, etLname, etPass, etEmail;
    AppCompatButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        btnRegister.setOnClickListener(this);
    }

    private void init(){
        etFname = (EditText) findViewById(R.id.fname);
        etLname = (EditText) findViewById(R.id.lname);
        etPass = (EditText) findViewById(R.id.fname);
        etEmail = (EditText) findViewById(R.id.email);

        btnRegister = (AppCompatButton) findViewById(R.id.btnRegister);
    }


    @Override
    public void onClick(View view) {
        String fname = etFname.getText().toString();
        String lname = etLname.getText().toString();
        String email = etEmail.getText().toString();
        String pass  = etPass .getText().toString();

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(email);
        dto.setFname(fname);
        dto.setLname(lname);
        dto.setPass(pass);

        RegisterHelper helper = new RegisterHelper(this);
        helper.register(dto);
    }

    @Override
    public void onRegisterResponse(RegisterResponseDTO dto) {
        String result = dto.getResult();
        Toast.makeText(RegisterActivity.this, "Result" + result, Toast.LENGTH_SHORT).show();
    }
}
