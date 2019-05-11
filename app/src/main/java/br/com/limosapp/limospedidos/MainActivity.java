package br.com.limosapp.limospedidos;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.limosapp.limospedidos.adapters.PedidoProdutoAdapter;
import br.com.limosapp.limospedidos.firebase.PedidoFirebase;
import br.com.limosapp.limospedidos.firebase.PedidoProdutoFirebase;

public class MainActivity extends AppCompatActivity {
    private SimpleDraweeView imgFotoRestaurante;
    private TextView txtNomeUsuario, txtLogout;

    private RecyclerView rvPedidosEnviar;
    private RecyclerView rvPedidosConcluir;
    private BottomNavigationView bnvPedidosMenu;
    private ProgressBar pBarPedidos;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbUsuarioWeb = db.child("usuariosweb");
    private DatabaseReference dbRestaurante = db.child("restaurantes");
    private DatabaseReference dbRestaurantePedidos = db.child("restaurantepedidos");
    private DatabaseReference dbPedidos = db.child("pedidos");


    private List<PedidoFirebase> listaPedidosEnviar;
    private List<PedidoFirebase> listaPedidosConcluir;

    private String idusuario, idrestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        inicializar();

        dbUsuarioWeb.keepSynced(true);
        dbRestaurante.keepSynced(true);
        dbRestaurantePedidos.keepSynced(true);
        dbPedidos.keepSynced(true);

        rvPedidosEnviar.setLayoutManager(new LinearLayoutManager(this));
        rvPedidosEnviar.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPedidosEnviar.setVisibility(View.GONE);
        rvPedidosConcluir.setLayoutManager(new LinearLayoutManager(this));
        rvPedidosConcluir.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPedidosConcluir.setVisibility(View.GONE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){
            verificaUsuarioWeb(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
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

        bnvPedidosMenu.setOnNavigationItemSelectedListener(navlistener);
    }

    private void inicializar(){
        imgFotoRestaurante = findViewById(R.id.imgFotoRestaurante);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        txtLogout = findViewById(R.id.txtLogout);
        rvPedidosAprovar = findViewById(R.id.rvPedidosAprovar);
        rvPedidosEnviar = findViewById(R.id.rvPedidosEnviar);
        rvPedidosConcluir = findViewById(R.id.rvPedidosConcluir);
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

                    if (idusuario != null && !idusuario.isEmpty()) {
                        if (postSnapshot.child("nome").exists())
                            txtNomeUsuario.setText(Objects.requireNonNull(postSnapshot.child("nome").getValue()).toString());
                        if (postSnapshot.child("restaurante").exists())
                            idrestaurante = Objects.requireNonNull(postSnapshot.child("restaurante").getValue()).toString();
                    }

                    if (!idrestaurante.isEmpty()) {
                        carregaFotoRestaurante();
                        carregaListaPedidosAprovar();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void carregaFotoRestaurante() {
        dbRestaurante.child(idrestaurante).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotoperfil").exists() && !dataSnapshot.child("fotoperfil").toString().isEmpty())
                    imgFotoRestaurante.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void carregaListaPedidosEnviar(){
        Query queryRestaurantePedidosEnviar = dbRestaurantePedidos.child(idrestaurante).orderByChild("status").equalTo(1);
        queryRestaurantePedidosEnviar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotEnviar) {
                listaPedidosEnviar = new ArrayList<>();
                rvPedidosEnviar.setAdapter(null);

                if (dataSnapshotEnviar.getValue() == null){
                    pBarPedidos.setVisibility(View.GONE);
                }else {
                    for (DataSnapshot postSnapshotEnviar : dataSnapshotEnviar.getChildren()) {
                        criaAdapter(postSnapshotEnviar.getKey(), postSnapshotEnviar, 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void carregaListaPedidosConcluir(){
        Query queryRestaurantePedidosConcluir = dbRestaurantePedidos.child(idrestaurante).orderByChild("status").equalTo(2);
        queryRestaurantePedidosConcluir.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotConcluir) {
                listaPedidosConcluir = new ArrayList<>();
                rvPedidosConcluir.setAdapter(null);

                if (dataSnapshotConcluir.getValue() == null){
                    pBarPedidos.setVisibility(View.GONE);
                }else {
                    for (DataSnapshot postSnapshotConlcuir : dataSnapshotConcluir.getChildren()) {
                        criaAdapter(postSnapshotConlcuir.getKey(), postSnapshotConlcuir, 2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void criaAdapter(final String idpedido, final DataSnapshot postSnapshot, final int status){
        DatabaseReference dbprodutos = dbPedidos.child(idpedido).child("produtos");
        dbprodutos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PedidoProdutoAdapter adapter;
                PedidoProdutoFirebase pedidoProdutoFirebase = new PedidoProdutoFirebase();
                List<PedidoProdutoFirebase> listaPedidoProdutos = new ArrayList<>();

                for (DataSnapshot postSnapshotProduto: dataSnapshot.getChildren()) {
                    if (postSnapshotProduto.child("produto").exists()) pedidoProdutoFirebase.setProduto(Objects.requireNonNull(postSnapshotProduto.child("produto").getValue()).toString());
                    if (postSnapshotProduto.child("quantidade").exists()) pedidoProdutoFirebase.setQuantidade(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("quantidade").getValue()).toString()));
                    if (postSnapshotProduto.child("valor").exists()) pedidoProdutoFirebase.setValor(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("valor").getValue()).toString()));
                    if (postSnapshotProduto.child("valortotal").exists()) pedidoProdutoFirebase.setValorTotal(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("valortotal").getValue()).toString()));
                    if (postSnapshotProduto.child("obs").exists()) pedidoProdutoFirebase.setObs(Objects.requireNonNull(postSnapshotProduto.child("obs").getValue()).toString());
                    if (postSnapshotProduto.child("complemento").exists()) pedidoProdutoFirebase.setComplemento(Objects.requireNonNull(postSnapshotProduto.child("complemento").getValue()).toString());
                    listaPedidoProdutos.add(pedidoProdutoFirebase);
                }

                PedidoFirebase pedidoFirebase = new PedidoFirebase();
                pedidoFirebase.setIdpedido(idpedido);
                if (postSnapshot.child("status").exists()) pedidoFirebase.setStatus(Integer.parseInt(Objects.requireNonNull(postSnapshot.child("status").getValue()).toString()));
                if (postSnapshot.child("pedido").exists()) pedidoFirebase.setPedido(Integer.parseInt(Objects.requireNonNull(postSnapshot.child("pedido").getValue()).toString()));
                if (postSnapshot.child("data").exists()) pedidoFirebase.setData(Objects.requireNonNull(postSnapshot.child("data").getValue()).toString());
                if (postSnapshot.child("hora").exists()) pedidoFirebase.setHora(Objects.requireNonNull(postSnapshot.child("hora").getValue()).toString());
                if (postSnapshot.child("nomeusuario").exists()) pedidoFirebase.setNomeusuario(Objects.requireNonNull(postSnapshot.child("nomeusuario").getValue()).toString());
                if (postSnapshot.child("telefone").exists()) pedidoFirebase.setTelefone(Objects.requireNonNull(postSnapshot.child("telefone").getValue()).toString());
                if (postSnapshot.child("endereco").exists()) pedidoFirebase.setEndereco(Objects.requireNonNull(postSnapshot.child("endereco").getValue()).toString());
                if (postSnapshot.child("numero").exists()) pedidoFirebase.setNumero(Objects.requireNonNull(postSnapshot.child("numero").getValue()).toString());
                if (postSnapshot.child("complemento").exists()) pedidoFirebase.setComplemento(Objects.requireNonNull(postSnapshot.child("complemento").getValue()).toString());
                if (postSnapshot.child("bairro").exists()) pedidoFirebase.setBairro(Objects.requireNonNull(postSnapshot.child("bairro").getValue()).toString());
                if (postSnapshot.child("cidade").exists()) pedidoFirebase.setCidade(Objects.requireNonNull(postSnapshot.child("cidade").getValue()).toString());
                if (postSnapshot.child("uf").exists()) pedidoFirebase.setUf(Objects.requireNonNull(postSnapshot.child("uf").getValue()).toString());
                if (postSnapshot.child("cep").exists()) pedidoFirebase.setCep(Objects.requireNonNull(postSnapshot.child("cep").getValue()).toString());
                if (postSnapshot.child("formapagamento").exists()) pedidoFirebase.setFormaPagamento(Objects.requireNonNull(postSnapshot.child("formapagamento").getValue()).toString());
                if (postSnapshot.child("valorprodutos").exists()) pedidoFirebase.setValorProdutos(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorprodutos").getValue()).toString()));
                if (postSnapshot.child("valordesconto").exists()) pedidoFirebase.setValorDesconto(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valordesconto").getValue()).toString()));
                if (postSnapshot.child("valorfrete").exists()) pedidoFirebase.setValorFrete(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorfrete").getValue()).toString()));
                if (postSnapshot.child("valorcash").exists()) pedidoFirebase.setValorCash(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorcash").getValue()).toString()));
                if (postSnapshot.child("valortotal").exists()) pedidoFirebase.setValorTotal(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valortotal").getValue()).toString()));
                pedidoFirebase.setChildItemList(listaPedidoProdutos);

                switch (status){
                    case 0:
                        listaPedidosAprovar.add(pedidoFirebase);
                        adapter = new PedidoProdutoAdapter(listaPedidosAprovar, MainActivity.this, idrestaurante);
                        rvPedidosAprovar.setAdapter(adapter);
                        break;
                    case 1:
                        listaPedidosEnviar.add(pedidoFirebase);
                        adapter = new PedidoProdutoAdapter(listaPedidosEnviar, MainActivity.this, idrestaurante);
                        rvPedidosEnviar.setAdapter(adapter);
                        break;
                    case 2:
                        listaPedidosConcluir.add(pedidoFirebase);
                        adapter = new PedidoProdutoAdapter(listaPedidosConcluir, MainActivity.this, idrestaurante);
                        rvPedidosConcluir.setAdapter(adapter);
                        break;
                }

                pBarPedidos.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void criarNotificacao() {
        try {
            Uri notification = Uri.parse("android.resource://"
                    + getBaseContext().getPackageName() + "/" + R.raw.efeito);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("1", "LimosPedidos", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder = builder
                .setSmallIcon(R.drawable.img_icon)
                .setContentTitle("Novos pedidos")
                .setContentText("Existem novos pedidos aguardando aprovação!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    pBarPedidos.setVisibility(View.VISIBLE);

                    switch (item.getItemId()){
                        case R.id.btnMenuAprovar:
                            rvPedidosAprovar.setVisibility(View.VISIBLE);
                            rvPedidosEnviar.setVisibility(View.GONE);
                            rvPedidosConcluir.setVisibility(View.GONE);
                            carregaListaPedidosAprovar();
                            break;
                        case R.id.btnMenuEnviar:
                            rvPedidosAprovar.setVisibility(View.GONE);
                            rvPedidosEnviar.setVisibility(View.VISIBLE);
                            rvPedidosConcluir.setVisibility(View.GONE);
                            carregaListaPedidosEnviar();
                            break;
                        case R.id.btnMenuConcluir:
                            rvPedidosAprovar.setVisibility(View.GONE);
                            rvPedidosEnviar.setVisibility(View.GONE);
                            rvPedidosConcluir.setVisibility(View.VISIBLE);
                            carregaListaPedidosConcluir();
                            break;
                    }
                    return true;
                }
            };

}
