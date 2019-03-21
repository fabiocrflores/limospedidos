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

import br.com.limosapp.limospedidos.util.Toast_layout;

import static br.com.limosapp.limospedidos.util.ValidarCampos.validarNotNull;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtSenha;
    Button btnEntrar;
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

        btnEntrar.setOnClickListener(new View.OnClickListener() {
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
        btnEntrar = findViewById(R.id.btnEntrar);
        pBarLogin = findViewById(R.id.pBarLogin);
    }

    private void login(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pBarLogin.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            verificaUsuarioWeb(email);
                        } else {
                            pBarLogin.setVisibility(View.VISIBLE);
                            new Toast_layout(LoginActivity.this).mensagem("E-mail ou senha inválido");
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
                if (idusuario != null && !idusuario.equals("")) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else {
                    new Toast_layout(LoginActivity.this).mensagem("E-mail ou senha inválido");
                }
                pBarLogin.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}