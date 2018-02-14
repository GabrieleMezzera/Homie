package com.smartmccg.homie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends AppCompatActivity {
    Button b1;
    EditText ed1,ed2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        b1 = findViewById(R.id.button);
        ed1 = findViewById(R.id.login);
        ed2 = findViewById(R.id.password);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("admin") && ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Login avvenuto",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            Home.class
                    );
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Credenziali errate",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}