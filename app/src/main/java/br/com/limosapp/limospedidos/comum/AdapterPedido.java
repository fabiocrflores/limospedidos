package br.com.limosapp.limospedidos.comum;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.limosapp.limospedidos.adapters.PedidoProdutoAdapter;
import br.com.limosapp.limospedidos.firebase.PedidoFirebase;
import br.com.limosapp.limospedidos.firebase.PedidoProdutoFirebase;

public class AdapterPedido {

    public static void criaAdapter(final Activity activity, final String idrestaurante, final String idpedido, final DataSnapshot postSnapshot, final List<PedidoFirebase> listaPedidos,
                                   final RecyclerView recyclerView, final ProgressBar pBarPedidos){
        DatabaseReference dbprodutos = FirebaseDatabase.getInstance().getReference().child("pedidos").child(idpedido).child("produtos");
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

                listaPedidos.add(pedidoFirebase);
                adapter = new PedidoProdutoAdapter(listaPedidos, activity, idrestaurante);
                recyclerView.setAdapter(adapter);
                pBarPedidos.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
