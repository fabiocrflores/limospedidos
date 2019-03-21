package br.com.limosapp.limospedidos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.Objects;

import br.com.limosapp.limospedidos.firebase.PedidosFirebase;
import br.com.limosapp.limospedidos.holder.PedidosViewHolder;

public class MainActivity extends AppCompatActivity {
    private SimpleDraweeView imgFotoRestaurante;
    private TextView txtNomeUsuario, txtLogout;
    private RecyclerView rvPedidos;
    private ProgressBar pBarPedidos;

    private FirebaseAuth mAuth;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbUsuarioWeb = db.child("usuariosweb");
    private DatabaseReference dbRestaurante = db.child("restaurantes");
    private DatabaseReference dbpedidos = db.child("restaurantepedidos");

    private String idusuario, idusuarioaut, idrestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        inicializar();

        dbUsuarioWeb.keepSynced(true);
        dbRestaurante.keepSynced(true);
        dbpedidos.keepSynced(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){
            idusuarioaut = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            verificaUsuarioWeb(mAuth.getCurrentUser().getEmail());

        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void inicializar(){
        imgFotoRestaurante = findViewById(R.id.imgFotoRestaurante);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        txtLogout = findViewById(R.id.txtLogout);
        rvPedidos = findViewById(R.id.rvPedidos);
        pBarPedidos = findViewById(R.id.pBarPedidos);
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
                    if (dataSnapshot.child("nome").exists()) txtNomeUsuario.setText(Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString());
                    if (dataSnapshot.child("restaurante").exists()) idrestaurante = Objects.requireNonNull(dataSnapshot.child("restaurante").getValue()).toString();
                }

                carregaFotoRestaurante(idrestaurante);
                carregaListaPedidos(idrestaurante, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void carregaFotoRestaurante(String idrestaurante) {
        dbRestaurante.child(idrestaurante).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotoperfil").exists() && !dataSnapshot.child("fotoperfil").toString().equals(""))
                    imgFotoRestaurante.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void carregaListaPedidos(String idrestaurante, int status){

        Query queryPedidos;
        queryPedidos = dbpedidos.child(idrestaurante).orderByChild("status").equalTo(status).orderByChild("pedido");
        FirebaseRecyclerAdapter<PedidosFirebase, PedidosViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PedidosFirebase, PedidosViewHolder>(
                PedidosFirebase.class,
                R.layout.recyclerview_pedidos,
                PedidosViewHolder.class,
                queryPedidos
        ) {
            @Override
            protected void populateViewHolder(PedidosViewHolder viewHolder, final PedidosFirebase model, final int position) {
                final String idcupom = getRef(position).getKey();
                viewHolder.setDesconto(model.getDesconto());
                viewHolder.setValidade(model.getValidade());
                viewHolder.verifValidade(model.getValidade());
                viewHolder.verifAtivo(model.getAtivo());
                viewHolder.setSeg(model.getSeg());
                viewHolder.setTer(model.getTer());
                viewHolder.setQua(model.getQua());
                viewHolder.setQui(model.getQui());
                viewHolder.setSex(model.getSex());
                viewHolder.setSab(model.getSab());
                viewHolder.setDom(model.getDom());
                viewHolder.setResgatado(MainActivity.idusuario, idrestaurante, idcupom);

                viewHolder.btnresgatarcupom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        salvarCupomUsuario(MainActivity.idusuario, idcupom, idrestaurante, model.getDesconto(), model.getValidade(), model.getValidademseg(), model.getSeg(), model.getTer(), model.getQua(), model.getQui(), model.getSex(), model.getSab(), model.getDom());
                    }
                });
            }
        };
        rvPedidos.setAdapter(firebaseRecyclerAdapter);
    }
}
