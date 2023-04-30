package com.example.abdiwakb.circuitcontroller;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class User extends Fragment implements View.OnClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user, container, false);
        Button btn_register = (Button)v.findViewById(R.id.btn_register);
        Button btn_manage = (Button)v.findViewById(R.id.btn_manage);


        btn_register.setOnClickListener(this);
        btn_manage.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btn_register:
                Intent intent = new Intent(getContext(), RegisterUser.class);
                startActivity(intent);
                break;
            case R.id.btn_manage:
                break;

        }
    }
}
