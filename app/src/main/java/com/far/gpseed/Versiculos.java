package com.far.gpseed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Versiculos extends Activity {

    TextView tvMensaje, tvVersiculo;
    Button btnAmen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_versiculos);
        tvMensaje = (TextView)findViewById(R.id.txtVersiculo);
        tvVersiculo = (TextView)findViewById(R.id.txtReferencia);
        btnAmen = (Button)findViewById(R.id.btnAmen);

        tvMensaje.setText(getIntent().getStringExtra("TEXTO"));
        tvVersiculo.setText(getIntent().getStringExtra("VERSICULO"));

        btnAmen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
