package br.com.limosapp.limospedidos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.limosapp.limospedidos.util.VerificaInternet;

public class SemInternetActivity extends AppCompatActivity {

    Button btnTentar;
    public static boolean ativa = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_internet);
        btnTentar = findViewById(R.id.btnTentar);

        btnTentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new VerificaInternet().verificaConexao(SemInternetActivity.this)){
                    onDestroy();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ativa = false;
    }
}
