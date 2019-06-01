package br.com.limosapp.limospedidos.common;

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
import br.com.limosapp.limospedidos.model.Pedido;
import br.com.limosapp.limospedidos.model.PedidoProduto;

public class AdapterPedidoCommon {

    public static void criaAdapter(final Activity activity, final String idrestaurante, final String idpedido, final DataSnapshot postSnapshot, final List<Pedido> listaPedidos,
                                   final RecyclerView recyclerView, final ProgressBar pBarPedidos){
        DatabaseReference dbprodutos = FirebaseDatabase.getInstance().getReference().child("pedidos").child(idpedido).child("produtos");
        dbprodutos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PedidoProdutoAdapter adapter;
                PedidoProduto pedidoProduto = new PedidoProduto();
                List<PedidoProduto> listaPedidoProdutos = new ArrayList<>();

                for (DataSnapshot postSnapshotProduto: dataSnapshot.getChildren()) {
                    if (postSnapshotProduto.child("produto").exists()) pedidoProduto.setProduto(Objects.requireNonNull(postSnapshotProduto.child("produto").getValue()).toString());
                    if (postSnapshotProduto.child("quantidade").exists()) pedidoProduto.setQuantidade(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("quantidade").getValue()).toString()));
                    if (postSnapshotProduto.child("valor").exists()) pedidoProduto.setValor(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("valor").getValue()).toString()));
                    if (postSnapshotProduto.child("valortotal").exists()) pedidoProduto.setValorTotal(Double.parseDouble(Objects.requireNonNull(postSnapshotProduto.child("valortotal").getValue()).toString()));
                    if (postSnapshotProduto.child("obs").exists()) pedidoProduto.setObs(Objects.requireNonNull(postSnapshotProduto.child("obs").getValue()).toString());
                    if (postSnapshotProduto.child("complemento").exists()) pedidoProduto.setComplemento(Objects.requireNonNull(postSnapshotProduto.child("complemento").getValue()).toString());
                    listaPedidoProdutos.add(pedidoProduto);
                }

                Pedido pedido = new Pedido();
                pedido.setIdpedido(idpedido);
                if (postSnapshot.child("status").exists()) pedido.setStatus(Integer.parseInt(Objects.requireNonNull(postSnapshot.child("status").getValue()).toString()));
                if (postSnapshot.child("pedido").exists()) pedido.setPedido(Integer.parseInt(Objects.requireNonNull(postSnapshot.child("pedido").getValue()).toString()));
                if (postSnapshot.child("data").exists()) pedido.setData(Objects.requireNonNull(postSnapshot.child("data").getValue()).toString());
                if (postSnapshot.child("hora").exists()) pedido.setHora(Objects.requireNonNull(postSnapshot.child("hora").getValue()).toString());
                if (postSnapshot.child("usuario").exists()) pedido.setUsuario(Objects.requireNonNull(postSnapshot.child("usuario").getValue()).toString());
                if (postSnapshot.child("nomeusuario").exists()) pedido.setNomeusuario(Objects.requireNonNull(postSnapshot.child("nomeusuario").getValue()).toString());
                if (postSnapshot.child("telefone").exists()) pedido.setTelefone(Objects.requireNonNull(postSnapshot.child("telefone").getValue()).toString());
                if (postSnapshot.child("endereco").exists()) pedido.setEndereco(Objects.requireNonNull(postSnapshot.child("endereco").getValue()).toString());
                if (postSnapshot.child("numero").exists()) pedido.setNumero(Objects.requireNonNull(postSnapshot.child("numero").getValue()).toString());
                if (postSnapshot.child("complemento").exists()) pedido.setComplemento(Objects.requireNonNull(postSnapshot.child("complemento").getValue()).toString());
                if (postSnapshot.child("bairro").exists()) pedido.setBairro(Objects.requireNonNull(postSnapshot.child("bairro").getValue()).toString());
                if (postSnapshot.child("cidade").exists()) pedido.setCidade(Objects.requireNonNull(postSnapshot.child("cidade").getValue()).toString());
                if (postSnapshot.child("uf").exists()) pedido.setUf(Objects.requireNonNull(postSnapshot.child("uf").getValue()).toString());
                if (postSnapshot.child("cep").exists()) pedido.setCep(Objects.requireNonNull(postSnapshot.child("cep").getValue()).toString());
                if (postSnapshot.child("formapagamento").exists()) pedido.setFormaPagamento(Objects.requireNonNull(postSnapshot.child("formapagamento").getValue()).toString());
                if (postSnapshot.child("valorprodutos").exists()) pedido.setValorProdutos(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorprodutos").getValue()).toString()));
                if (postSnapshot.child("valordesconto").exists()) pedido.setValorDesconto(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valordesconto").getValue()).toString()));
                if (postSnapshot.child("valorfrete").exists()) pedido.setValorFrete(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorfrete").getValue()).toString()));
                if (postSnapshot.child("valorcash").exists()) pedido.setValorCash(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorcash").getValue()).toString()));
                if (postSnapshot.child("valortotal").exists()) pedido.setValorTotal(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valortotal").getValue()).toString()));
                if (postSnapshot.child("valorcashganho").exists()) pedido.setValorCashGanho(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("valorcashganho").getValue()).toString()));
                if (postSnapshot.child("hashcupom").exists()) pedido.setIdcupomutilizado(Objects.requireNonNull(postSnapshot.child("hashcupom").getValue()).toString());
                pedido.setChildItemList(listaPedidoProdutos);

                listaPedidos.add(pedido);
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
