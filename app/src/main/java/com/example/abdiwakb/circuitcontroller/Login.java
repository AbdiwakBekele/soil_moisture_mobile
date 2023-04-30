package com.example.abdiwakb.circuitcontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private String username = "admin";
    private String password = "admin";

    EditText editTxt_username;
    EditText editTxt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTxt_username = (EditText)findViewById(R.id.editTxt_username);
        editTxt_password = (EditText)findViewById(R.id.editTxt_password);
        Button btn_login = (Button)findViewById(R.id.btn_login);
    }

    public void login(View v){
        String inputUserName = editTxt_username.getText().toString();
        String inputPassword = editTxt_password.getText().toString();

        if(inputUserName.equals(username)){
            if(inputPassword.equals(password)){
                Intent intent = new Intent(Login.this, Main.class);
                startActivity(intent);
            }else{
                editTxt_password.setError("Incorrect Password");
            }
        }else{
            editTxt_username.setError("Incorrect Username");
        }


    }
}
