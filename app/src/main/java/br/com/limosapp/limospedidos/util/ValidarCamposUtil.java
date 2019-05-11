package br.com.limosapp.limospedidos.util;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

public class ValidarCamposUtil {

    public static boolean validarNotNull(View pView, String pMessage) {
        if (pView instanceof EditText) {
            EditText edText = (EditText) pView;
            if (edText.getText() != null) {
                if (!TextUtils.isEmpty(edText.getText().toString())) {
                    return true;
                }
            }
            edText.setError(pMessage);
            edText.setFocusable(true);
            edText.requestFocus();
            return false;
        }
        return false;
    }

    public static boolean validarEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
