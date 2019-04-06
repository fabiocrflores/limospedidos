package br.com.limosapp.limospedidos.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.limosapp.limospedidos.R;
import br.com.limosapp.limospedidos.firebase.PedidoFirebase;
import br.com.limosapp.limospedidos.firebase.PedidoProdutoFirebase;
import br.com.limosapp.limospedidos.holders.PedidoViewHolder;
import br.com.limosapp.limospedidos.holders.ProdutoViewHolder;
import br.com.limosapp.limospedidos.util.Toast_layout;

public class PedidoProdutoAdapter extends ExpandableRecyclerAdapter<PedidoFirebase, PedidoProdutoFirebase, PedidoViewHolder, ProdutoViewHolder> {

    private LayoutInflater mInflater;
    private Activity activity;
    private String idrestaurante, strstatus;
    private int novostatus;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbpedidos = db.child("restaurantepedidos");

    public PedidoProdutoAdapter(@NonNull List<PedidoFirebase> listaPedidos, Activity activity, String idrestaurante) {
        super(listaPedidos);
        mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.idrestaurante = idrestaurante;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View pedidoView = mInflater.inflate(R.layout.recyclerview_pedidos,parentViewGroup,false);
        return new PedidoViewHolder(pedidoView);
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View produtoView = mInflater.inflate(R.layout.recyclerview_pedidos_produtos,childViewGroup,false);
        return new ProdutoViewHolder(produtoView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull PedidoViewHolder pedidoViewHolder, int parentPosition, @NonNull PedidoFirebase pedidoFirebase) {
        final int status = pedidoFirebase.getStatus();
        final String idpedido = pedidoFirebase.getIdpedido();
        pedidoViewHolder.setPedido(pedidoFirebase.getPedido());
        pedidoViewHolder.setDataPedido(pedidoFirebase.getData(), pedidoFirebase.getHora());
        pedidoViewHolder.setUsuario(pedidoFirebase.getNomeusuario());
        pedidoViewHolder.setTelefone(pedidoFirebase.getTelefone());
        pedidoViewHolder.setEndereco(pedidoFirebase.getEndereco(), pedidoFirebase.getNumero(), pedidoFirebase.getComplemento());
        pedidoViewHolder.setBairro(pedidoFirebase.getBairro());
        pedidoViewHolder.setCidade(pedidoFirebase.getCidade(), pedidoFirebase.getUf(), pedidoFirebase.getCep());
        pedidoViewHolder.setFormaPagamento(pedidoFirebase.getFormapagamento());
        pedidoViewHolder.setValorProdutos(pedidoFirebase.getValorprodutos());
        pedidoViewHolder.setValorDesconto(pedidoFirebase.getValordesconto());
        pedidoViewHolder.setValorFrete(pedidoFirebase.getValorfrete());
        pedidoViewHolder.setValorCash(pedidoFirebase.getValorcash());
        pedidoViewHolder.setValorTotal(pedidoFirebase.getValortotal());
        pedidoViewHolder.setImagemTituloBtnAprovar(status);

        pedidoViewHolder.btnAprovar.setOnClickListener(new View.OnClickListener() {
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

        pedidoViewHolder.btnRecusar.setOnClickListener(new View.OnClickListener() {
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
    public void onBindChildViewHolder(@NonNull ProdutoViewHolder produtoViewHolder, int parentPosition, int childPosition, @NonNull PedidoProdutoFirebase pedidoProdutoFirebase) {
        produtoViewHolder.setProduto(pedidoProdutoFirebase.getProduto());
        produtoViewHolder.setQuantidade(pedidoProdutoFirebase.getQuantidade());
        produtoViewHolder.setValorUnitario(pedidoProdutoFirebase.getValor());
        produtoViewHolder.setValorTotal(pedidoProdutoFirebase.getValortotal());
        produtoViewHolder.setObservacao(pedidoProdutoFirebase.getObs());
        produtoViewHolder.setComplemento(pedidoProdutoFirebase.getComplemento());
    }

    @Override
    public void setExpandCollapseListener(@Nullable ExpandCollapseListener expandCollapseListener) {
        super.setExpandCollapseListener(expandCollapseListener);

    }

//    @Override
//    public void notifyChildChanged(int parentPosition, int childPosition) {
//        super.notifyChildChanged(parentPosition, childPosition);
//    }

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

        new Toast_layout(activity).mensagem(activity.getString(R.string.atualizado_sucesso, strstatus));
    }
}
