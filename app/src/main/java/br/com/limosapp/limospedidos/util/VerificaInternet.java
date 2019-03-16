package br.com.limosapp.limospedidos.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import br.com.limosapp.limospedidos.R;
import br.com.limosapp.limospedidos.SemInternetActivity;

public class VerificaInternet {

    public boolean verificaConexao(Activity activity) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conectivtyManager != null;
        if (conectivtyManager.getActiveNetworkInfo() == null || !conectivtyManager.getActiveNetworkInfo().isAvailable() || !conectivtyManager.getActiveNetworkInfo().isConnected()) {
            if (SemInternetActivity.ativa){
                new Toast_layout(activity).mensagem(activity.getString(R.string.sem_internet));
            }else {
                Intent intent = new Intent(activity, SemInternetActivity.class);
                activity.startActivity(intent);
            }
            return false;
        }else {
            return true;
        }
    }
}


