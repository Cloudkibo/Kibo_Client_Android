package com.cloudkibo.kibohost;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn;

    // 'kibo-app-id' : '5wdqvvi8jyvfhxrxmu73dxun9za8x5u6n59',
    // 'kibo-app-secret': 'jcmhec567tllydwhhy2z692l79j8bkxmaa98do1bjer16cdu5h79xvx',
    // 'kibo-client-id': 'cd89f71715f2014725163952'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String name = getIntent().getExtras().getString("name");
        String phone = getIntent().getExtras().getString("phone");
        String email = getIntent().getExtras().getString("email");
        String customer_id = getIntent().getExtras().getString("customer_id");

        com.cloudkibo.kiboengage.KiboEngage.initialize(getApplicationContext(), "5wdqvvi8jyvfhxrxmu73dxun9za8x5u6n59",
                "cd89f71715f2014725163952", "jcmhec567tllydwhhy2z692l79j8bkxmaa98do1bjer16cdu5h79xvx", customer_id,
                name, phone, email);

        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cloudkibo.kiboengage.MainActivity.class));
            }
        });
    }
}
