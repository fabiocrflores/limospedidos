package br.com.limosapp.limospedidos.fragment;

import android.app.Activity;
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
import java.util.Objects;

import br.com.limosapp.limospedidos.R;
import br.com.limosapp.limospedidos.model.Pedido;

import static br.com.limosapp.limospedidos.application.Application.opcaomenu;
import static br.com.limosapp.limospedidos.common.AdapterPedidoCommon.criaAdapter;

public class PedidoEnviarFragment extends Fragment {
    private Activity activity;
    private RecyclerView rvPedidosEnviar;
    private ProgressBar pBarPedidos;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbRestaurantePedidos = db.child("restaurantepedidos");

    private List<Pedido> listaPedidosEnviar;

    private String idrestaurante;

    public PedidoEnviarFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedido_enviar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Inicializa(view);

        activity = getActivity();

        Bundle bundle;
        bundle = getArguments();
        idrestaurante = Objects.requireNonNull(bundle).getString("idrestaurante");

        dbRestaurantePedidos.keepSynced(true);

        rvPedidosEnviar.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPedidosEnviar.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        rvPedidosEnviar.setHasFixedSize(true);

        carregaListaPedidosEnviar();
    }

    private void Inicializa(View view){
        rvPedidosEnviar = view.findViewById(R.id.rvPedidosEnviar);
        pBarPedidos = view.findViewById(R.id.pBarPedidos);
    }

    public static PedidoEnviarFragment newInstance() {
        return new PedidoEnviarFragment();
    }

    private void carregaListaPedidosEnviar(){
        Query queryRestaurantePedidosEnviar = dbRestaurantePedidos.child(idrestaurante).orderByChild("status").equalTo(1);
        queryRestaurantePedidosEnviar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotAprovar) {
                if (opcaomenu == 1) {
                    listaPedidosEnviar = new ArrayList<>();
                    rvPedidosEnviar.setAdapter(null);

                    if (dataSnapshotAprovar.getValue() == null) {
                        pBarPedidos.setVisibility(View.GONE);
                    } else {
                        for (DataSnapshot postSnapshotAprovar : dataSnapshotAprovar.getChildren()) {
                            criaAdapter(activity, idrestaurante, postSnapshotAprovar.getKey(), postSnapshotAprovar, listaPedidosEnviar, rvPedidosEnviar, pBarPedidos);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}