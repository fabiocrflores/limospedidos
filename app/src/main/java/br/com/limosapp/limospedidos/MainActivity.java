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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Objects;

import br.com.limosapp.limospedidos.fragment.PedidoAprovarFragment;
import br.com.limosapp.limospedidos.fragment.PedidoConcluirFragment;
import br.com.limosapp.limospedidos.fragment.PedidoEnviarFragment;
import br.com.limosapp.limospedidos.util.VerificaInternetUtil;

import static br.com.limosapp.limospedidos.application.Application.opcaomenu;

public class MainActivity extends AppCompatActivity {
    private SimpleDraweeView imgFotoRestaurante;
    private TextView txtNomeUsuario, txtLogout;

    private BottomNavigationView bnvPedidosMenu;
    private ProgressBar pBarRestaurante;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbUsuarioWeb = db.child("usuariosweb");
    private DatabaseReference dbRestaurante = db.child("restaurantes");
    private DatabaseReference dbRestaurantePedidos = db.child("restaurantepedidos");
    private DatabaseReference dbPedidos = db.child("pedidos");

    private String idusuario, idrestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        inicializar();
        opcaomenu = 0;

        dbUsuarioWeb.keepSynced(true);
        dbRestaurante.keepSynced(true);
        dbRestaurantePedidos.keepSynced(true);
        dbPedidos.keepSynced(true);

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
        bnvPedidosMenu = findViewById(R.id.bnvPedidosMenu);
        pBarRestaurante = findViewById(R.id.pBarPedidos);
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
                if (dataSnapshot.child("fotoperfil").exists() && !dataSnapshot.child("fotoperfil").toString().isEmpty()) {
                    imgFotoRestaurante.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString());
                }
                pBarRestaurante.setVisibility(View.GONE);
                abreFragment(0);
                verificaNovoPedidoAprovar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void abreFragment(int posicao){
        Fragment fragment = null;
        switch (posicao) {
            case 0:
                fragment = PedidoAprovarFragment.newInstance();
                break;
            case 1:                 
                fragment = PedidoEnviarFragment.newInstance();
                break;
            case 2:
                fragment = PedidoConcluirFragment.newInstance();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("idrestaurante", idrestaurante);
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentManager fM = getSupportFragmentManager();
            fM.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fM.beginTransaction().replace(R.id.frameLayout, fragment).commit();
        }
    }

    private void verificaNovoPedidoAprovar(){
        Query queryRestaurantePedidosAprovar = dbRestaurantePedidos.child(idrestaurante).orderByChild("status").equalTo(0);
        queryRestaurantePedidosAprovar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotAprovar) {
                if (dataSnapshotAprovar.getValue() != null) {
                    criarNotificacao();
                }
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
                .setSmallIcon(R.drawable.imgicon)
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
                    if (new VerificaInternetUtil().verificaConexao(MainActivity.this)) {
                        switch (item.getItemId()) {
                            case R.id.btnopcaomenu:
                                opcaomenu = 0;
                                abreFragment(0);
                                break;
                            case R.id.btnMenuEnviar:
                                opcaomenu = 1;
                                abreFragment(1);
                                break;
                            case R.id.btnMenuConcluir:
                                opcaomenu = 2;
                                abreFragment(2);
                                break;
                        }
                    }
                    return true;
                }
            };

}
