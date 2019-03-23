package br.com.limosapp.limospedidos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.limosapp.limospedidos.firebase.PedidosFirebase;
import br.com.limosapp.limospedidos.holder.PedidosViewHolder;
import br.com.limosapp.limospedidos.util.Toast_layout;

public class MainActivity extends AppCompatActivity {
    private SimpleDraweeView imgFotoRestaurante;
    private TextView txtNomeUsuario, txtLogout;
    private RecyclerView rvPedidos;
    private BottomNavigationView bnvPedidosMenu;
    private ProgressBar pBarPedidos;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbUsuarioWeb = db.child("usuariosweb");
    private DatabaseReference dbRestaurante = db.child("restaurantes");
    private DatabaseReference dbpedidos = db.child("restaurantepedidos");

    private String idusuario, idusuarioaut, idrestaurante, strstatus;
    private int novostatus;

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
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        rvPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

        bnvPedidosMenu.setOnNavigationItemSelectedListener(navlistener);
    }

    private void inicializar(){
        imgFotoRestaurante = findViewById(R.id.imgFotoRestaurante);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        txtLogout = findViewById(R.id.txtLogout);
        rvPedidos = findViewById(R.id.rvPedidos);
        bnvPedidosMenu = findViewById(R.id.bnvPedidosMenu);
        pBarPedidos = findViewById(R.id.pBarPedidos);
    }

    private void verificaUsuarioWeb(String email){
        Query queryUsuarios;
        queryUsuarios = dbUsuarioWeb.orderByChild("email").equalTo(email);
        queryUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    idusuario = postSnapshot.getKey();

                    if (idusuario != null && !idusuario.equals("")) {
                        if (postSnapshot.child("nome").exists())
                            txtNomeUsuario.setText(Objects.requireNonNull(postSnapshot.child("nome").getValue()).toString());
                        if (postSnapshot.child("restaurante").exists())
                            idrestaurante = Objects.requireNonNull(postSnapshot.child("restaurante").getValue()).toString();
                    }
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

    public void carregaListaPedidos(final String idrestaurante, int status){
        Query queryPedidos;
        queryPedidos = dbpedidos.child(idrestaurante).orderByChild("status").equalTo(status);

        FirebaseRecyclerOptions<PedidosFirebase> options = new FirebaseRecyclerOptions.Builder<PedidosFirebase>()
                .setQuery(queryPedidos, PedidosFirebase.class)
                .build();

        FirebaseRecyclerAdapter<PedidosFirebase, PedidosViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PedidosFirebase, PedidosViewHolder>(options) {

            @NonNull
            @Override
            public PedidosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new PedidosViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recyclerview_pedidos, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(PedidosViewHolder viewHolder, int position, PedidosFirebase model) {
                final int status = model.getStatus();
                final String idpedido = getRef(position).getKey();
                viewHolder.setPedido(model.getPedido());
                viewHolder.setDatapedido(model.getData(), model.getHora());
                viewHolder.setUsuario(model.getNomeusuario());
                viewHolder.setTelefone(model.getTelefone());
                viewHolder.setEndereco(model.getEndereco(), model.getNumero(), model.getComplemento());
                viewHolder.setBairro(model.getBairro());
                viewHolder.setCidade(model.getCidade(), model.getUf(), model.getCep());
                viewHolder.setValorprodutos(model.getValorprodutos());
                viewHolder.setValordesconto(model.getValordesconto());
                viewHolder.setValorfrete(model.getValorfrete());
                viewHolder.setValorcash(model.getValorcash());
                viewHolder.setValortotal(model.getValortotal());
                viewHolder.setImagemtitulobtnaprovar(status);
                viewHolder.setTitulobtnrecusar(status);

                viewHolder.btnDetalhes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                viewHolder.btnAprovar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (status) {
                            case 0:
                                strstatus = "aprovar";
                                novostatus = 1;
                                break;
                            case 1:
                                strstatus = "enviar";
                                novostatus = 2;
                                break;
                            case 2:
                                strstatus = "concluir";
                                novostatus = 5;
                                break;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Confirmação")
                                .setMessage("Tem certeza que deseja " + strstatus + " este pedido?")
                                .setPositiveButton(strstatus.substring(0, 1).toUpperCase().concat(strstatus.substring(1)), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        atualizarStatusPedido(idpedido, novostatus);
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .create()
                                .show();
                    }

                });

                viewHolder.btnRecusar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status == 0) {
                            strstatus = "recusar";
                            novostatus = 3;
                        } else {
                            strstatus = "cancelar";
                            novostatus = 4;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Confirmação")
                                .setMessage("Tem certeza que deseja " + strstatus + " este pedido?")
                                .setPositiveButton(strstatus.substring(0, 1).toUpperCase().concat(strstatus.substring(1)), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        atualizarStatusPedido(idpedido, novostatus);
                                    }
                                })
                                .setNegativeButton("Não", null)
                                .create()
                                .show();
                    }
                });
            }

            @Override
            public void onDataChanged() {
                if (pBarPedidos != null) {
                    pBarPedidos.setVisibility(View.GONE);
                }
            }
        };
        firebaseRecyclerAdapter.startListening();
        rvPedidos.setAdapter(firebaseRecyclerAdapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    pBarPedidos.setVisibility(View.VISIBLE);

                    switch (item.getItemId()){
                        case R.id.btnMenuAprovar:
                            carregaListaPedidos(idrestaurante, 0);
                            break;
                        case R.id.btnMenuEnviar:
                            carregaListaPedidos(idrestaurante, 1);
                            break;
                        case R.id.btnMenuConcluir:
                            carregaListaPedidos(idrestaurante, 2);
                            break;
                    }
                    return true;
                }
            };

    private void atualizarStatusPedido(String idpedido, int status){
        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put("status", status);
        dbpedidos.child(idrestaurante).child(idpedido).updateChildren(taskMap);

        switch (status){
            case 1:
                strstatus = "aprovado";
                break;
            case 2:
                strstatus = "enviado";
                break;
            case 3:
                strstatus = "recusado";
                break;
            case 4:
                strstatus = "cancelado";
                break;
            case 5:
                strstatus = "concluído";
                break;
        }

        new Toast_layout(this).mensagem(getString(R.string.atualizado_sucesso, strstatus));
    }
}
