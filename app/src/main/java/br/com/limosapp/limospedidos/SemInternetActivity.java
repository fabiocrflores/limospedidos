package br.com.limosapp.limospedidos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.limosapp.limospedidos.util.VerificaInternetUtil;

public class SemInternetActivity extends AppCompatActivity {

    Button btnTentar;
    public static boolean ativa = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_internet);
        btnTentar = findViewById(R.id.btnTentar);
        ativa = true;

        btnTentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new VerificaInternetUtil().verificaConexao(SemInternetActivity.this)){
                    ativa = false;
                    onBackPressed();
                }
            }
        });
    }
}
