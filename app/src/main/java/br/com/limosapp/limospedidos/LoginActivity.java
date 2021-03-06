package br.com.limosapp.limospedidos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.limosapp.limospedidos.util.ToastLayoutUtil;

import static br.com.limosapp.limospedidos.util.ValidarCamposUtil.validarNotNull;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtSenha;
    Button btnLogin;
    ProgressBar pBarLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsuarioWeb = FirebaseDatabase.getInstance().getReference().child("usuariosweb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializar();
        mAuth = FirebaseAuth.getInstance();
        dbUsuarioWeb.keepSynced(true);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validarNotNull(edtEmail, getString(R.string.preenchaocampo,"e-mail"))) {
                    return;
                }
                if (validarNotNull(edtSenha, getString(R.string.preenchaocampo,"senha"))) {
                    login(edtEmail.getText().toString(), edtSenha.getText().toString());
                }
            }
        });
    }

    private void inicializar(){
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        pBarLogin = findViewById(R.id.pBarLogin);
    }

    private void login(final String email, String password){
        pBarLogin.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verificaUsuarioWeb(email);
                        } else {
                            pBarLogin.setVisibility(View.GONE);
                            new ToastLayoutUtil(LoginActivity.this).mensagem("E-mail ou senha inv??lido");
                        }
                    }
                });
    }

    private void verificaUsuarioWeb(String email){
        Query queryUsuarios;
        queryUsuarios = dbUsuarioWeb.orderByChild("email").equalTo(email);
        queryUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String idusuario = "";

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    idusuario = postSnapshot.getKey();
                }
                if (idusuario != null && !idusuario.isEmpty()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    FirebaseAuth.getInstance().signOut();
                    new ToastLayoutUtil(LoginActivity.this).mensagem("E-mail ou senha inv??lido");
                }
                pBarLogin.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}