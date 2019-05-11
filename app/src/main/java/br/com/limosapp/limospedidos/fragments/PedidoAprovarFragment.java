package br.com.limosapp.limospedidos.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.limosapp.limospedidos.R;
import br.com.limosapp.limospedidos.firebase.PedidoFirebase;

public class PedidoAprovarFragment extends Fragment {
    private RecyclerView rvPedidosAprovar;
    private ProgressBar pBarPedidos;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbRestaurantePedidos = db.child("restaurantepedidos");
    private DatabaseReference dbPedidos = db.child("pedidos");

    private List<PedidoFirebase> listaPedidosAprovar;

    private String idrestaurante;

    public PedidoAprovarFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedido_aprovar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Inicializa(view);


        dbRestaurantePedidos.keepSynced(true);
        dbPedidos.keepSynced(true);

        rvPedidosAprovar.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPedidosAprovar.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        rvPedidosAprovar.setVisibility(View.VISIBLE);


    }

    private void Inicializa(View view){
        rvPedidosAprovar = view.findViewById(R.id.rvPedidosAprovar);
        pBarPedidos = view.findViewById(R.id.pBarPedidos);
    }


    public static PedidoAprovarFragment newInstance() {
        return new PedidoAprovarFragment();
    }

    public void carregaListaPedidosAprovar(){
        Query queryRestaurantePedidosAprovar = dbRestaurantePedidos.child(idrestaurante).orderByChild("status").equalTo(0);
        queryRestaurantePedidosAprovar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotAprovar) {
                listaPedidosAprovar = new ArrayList<>();
                rvPedidosAprovar.setAdapter(null);

                if (dataSnapshotAprovar.getValue() == null){
                    pBarPedidos.setVisibility(View.GONE);
                }else {
//                    criarNotificacao();
                    for (DataSnapshot postSnapshotAprovar : dataSnapshotAprovar.getChildren()) {
                        criaAdapter(postSnapshotAprovar.getKey(), postSnapshotAprovar, 0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
