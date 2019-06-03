package br.com.limosapp.limospedidos.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import br.com.limosapp.limospedidos.R;
import br.com.limosapp.limospedidos.model.Cupom;
import br.com.limosapp.limospedidos.model.Pedido;
import br.com.limosapp.limospedidos.model.PedidoProduto;
import br.com.limosapp.limospedidos.model.UsuariosCashBackExtrato;
import br.com.limosapp.limospedidos.util.ToastLayoutUtil;
import br.com.limosapp.limospedidos.util.VerificaInternetUtil;

public class PedidoProdutoAdapter extends ExpandableRecyclerAdapter<Pedido, PedidoProduto, PedidoProdutoAdapter.PedidoViewHolder, PedidoProdutoAdapter.ProdutoViewHolder> {

    private LayoutInflater mInflater;
    private Activity activity;

    private String idrestaurante, strstatus, dataFormatada, horaFormatada;
    private int novostatus;
    private Long datamili;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public PedidoProdutoAdapter(@NonNull List<Pedido> listaPedidos, Activity activity, String idrestaurante) {
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
    public void onBindParentViewHolder(@NonNull PedidoViewHolder pedidoViewHolder, int parentPosition, @NonNull Pedido pedido) {
        final int status = pedido.getStatus();
        final String idpedido = pedido.getIdpedido();
        final long npedido = pedido.getPedido();
        final String idusuario = pedido.getUsuario();
        final double valorcashganho = pedido.getValorCashGanho();
        final double valorcashutilizado = pedido.getValorcash();
        final String idcupomutilizado = (pedido.getIdcupomutilizado() == null?"":pedido.getIdcupomutilizado());
        pedidoViewHolder.setPedido(npedido);
        pedidoViewHolder.setDataPedido(pedido.getData(), pedido.getHora());
        pedidoViewHolder.setUsuario(pedido.getNomeusuario());
        pedidoViewHolder.setTelefone(pedido.getTelefone());
        pedidoViewHolder.setEndereco(pedido.getEndereco(), pedido.getNumero(), pedido.getComplemento());
        pedidoViewHolder.setBairro(pedido.getBairro());
        pedidoViewHolder.setCidade(pedido.getCidade(), pedido.getUf(), pedido.getCep());
        pedidoViewHolder.setFormaPagamento(pedido.getFormapagamento());
        pedidoViewHolder.setValorProdutos(pedido.getValorprodutos());
        pedidoViewHolder.setValorDesconto(pedido.getValordesconto());
        pedidoViewHolder.setValorFrete(pedido.getValorfrete());
        pedidoViewHolder.setValorCash(valorcashutilizado);
        pedidoViewHolder.setValorTotal(pedido.getValortotal());
        pedidoViewHolder.setImagemTituloBtnAprovar(status);

        pedidoViewHolder.btnAprovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new VerificaInternetUtil().verificaConexao(activity)) {
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
                                    atualizaStatusPedido(idusuario, idpedido, novostatus);
                                    salvaHorario(idpedido, novostatus);
                                    if(novostatus == 5){
                                        salvaExtratoCashBack(idusuario, npedido, valorcashganho);
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                }
            }

        });

        pedidoViewHolder.btnRecusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new VerificaInternetUtil().verificaConexao(activity)) {
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
                                    atualizaStatusPedido(idusuario, idpedido, novostatus);
                                    salvaHorario(idpedido, novostatus);
                                    salvaExtratoCashBack(idusuario, npedido, valorcashutilizado);
                                    if (!idcupomutilizado.isEmpty()) {
                                        atualizaCupomUtilizado(idusuario, idcupomutilizado);
                                    }
                                }
                            })
                            .setNegativeButton("Não", null)
                            .create()
                            .show();
                }
            }
        });
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProdutoViewHolder produtoViewHolder, int parentPosition, int childPosition, @NonNull PedidoProduto pedidoProduto) {
        produtoViewHolder.setProduto(pedidoProduto.getProduto());
        produtoViewHolder.setQuantidade(pedidoProduto.getQuantidade());
        produtoViewHolder.setValorUnitario(pedidoProduto.getValor());
        produtoViewHolder.setValorTotal(pedidoProduto.getValortotal());
        produtoViewHolder.setObservacao(pedidoProduto.getObs());
        produtoViewHolder.setComplemento(pedidoProduto.getComplemento());
    }

    @Override
    public void setExpandCollapseListener(@Nullable ExpandCollapseListener expandCollapseListener) {
        super.setExpandCollapseListener(expandCollapseListener);

    }

//    @Override
//    public void notifyChildChanged(int parentPosition, int childPosition) {
//        super.notifyChildChanged(parentPosition, childPosition);
//    }

    private void atualizaStatusPedido(String idusuario, String idpedido, int novostatus){
        DatabaseReference dbrestaurantepedidos = db.child("restaurantepedidos");
        DatabaseReference dbpedidos = db.child("pedidos");
        DatabaseReference dbusuariopedidos = db.child("usuariopedidos").child(idusuario);

        dataFormatada = dateFormat.format(new Date());
        horaFormatada = horaFormat.format(new Date());
        datamili = new Date().getTime();

        dbrestaurantepedidos.child(idrestaurante).child(idpedido).child("status").setValue(novostatus);
        dbpedidos.child(idpedido).child("status").setValue(novostatus);
        dbusuariopedidos.child(idpedido).child("statuspedido").setValue(novostatus);

        switch (novostatus){
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

        new ToastLayoutUtil(activity).mensagem(activity.getString(R.string.atualizado_sucesso, strstatus));
    }

    private void salvaHorario(String idpedido, int novostatus){
        DatabaseReference dbpedidohorarios = db.child("pedidohorarios").child(idpedido);

        Map<String,Object> taskMap = new HashMap<>();
        switch (novostatus){
            case 1:
                taskMap.put("aprovado", horaFormatada);
                break;
            case 2:
                taskMap.put("enviado", horaFormatada);
                break;
            case 3:
                taskMap.put("recusado", horaFormatada);
                break;
            case 4:
                taskMap.put("cancelado", horaFormatada);
                break;
            case 5:
                taskMap.put("concluido", horaFormatada);
                break;
        }
        dbpedidohorarios.updateChildren(taskMap);
    }

    private void salvaExtratoCashBack(String idusuario, Long idpedido, Double valorcash){
        DatabaseReference dbusuariocashbackextrato = db.child("usuariocashbackextrato/"+idusuario);

        UsuariosCashBackExtrato usuariosCashBackExtrato = new UsuariosCashBackExtrato();
        usuariosCashBackExtrato.setDatahora(dataFormatada + " " + horaFormatada);
        usuariosCashBackExtrato.setDatahoramseg(datamili);
        usuariosCashBackExtrato.setPedido(idpedido);
        usuariosCashBackExtrato.setRestaurante(idrestaurante);
        usuariosCashBackExtrato.setValor(valorcash);
        dbusuariocashbackextrato.push().setValue(usuariosCashBackExtrato);

        atualizaSaldoCashBack(idusuario, valorcash);
    }

    private void atualizaSaldoCashBack(String idusuario, final Double valorcash) {
        DatabaseReference dbusuarios = db.child("usuarios/"+idusuario+"/cashbacksaldo");
        dbusuarios.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(valorcash);
                }else {
                    currentData.setValue(Double.parseDouble(currentData.getValue().toString()) + valorcash);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    System.out.println("Firebase counter increment succeeded.");
                }
            }
        });
    }

    private void atualizaCupomUtilizado(final String idusuario, final String idcupomutilizado){
        final DatabaseReference dbusuariocuponsutilizados = db.child("usuariocuponsutilizados").child(idusuario).child(idrestaurante).child(idcupomutilizado);
        dbusuariocuponsutilizados.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Cupom cupom = new Cupom();
                    if (dataSnapshot.child("desconto").exists()) cupom.setDesconto(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("desconto").getValue()).toString()));
                    if (dataSnapshot.child("dom").exists()) cupom.setDom(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("dom").getValue()).toString()));
                    if (dataSnapshot.child("seg").exists()) cupom.setSeg(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("seg").getValue()).toString()));
                    if (dataSnapshot.child("ter").exists()) cupom.setTer(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("ter").getValue()).toString()));
                    if (dataSnapshot.child("qua").exists()) cupom.setQua(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("qua").getValue()).toString()));
                    if (dataSnapshot.child("qui").exists()) cupom.setQui(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("qui").getValue()).toString()));
                    if (dataSnapshot.child("sex").exists()) cupom.setSex(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("sex").getValue()).toString()));
                    if (dataSnapshot.child("sab").exists()) cupom.setSab(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("sab").getValue()).toString()));
                    if (dataSnapshot.child("validade").exists()) cupom.setValidade(Objects.requireNonNull(dataSnapshot.child("validade").getValue()).toString());
                    if (dataSnapshot.child("validademseg").exists()) cupom.setValidademseg(Long.parseLong(Objects.requireNonNull(dataSnapshot.child("validademseg").getValue()).toString()));

                    DatabaseReference dbusuariocuponsresgatados = db.child("usuariocuponsresgatados").child(idusuario).child(idrestaurante).child(idcupomutilizado);
                    dbusuariocuponsresgatados.setValue(cupom);
                    dbusuariocuponsutilizados.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class PedidoViewHolder extends ParentViewHolder {
        private View view;
        private ImageView imgExpandir;
        private TextView txtPedido, txtDataPedido, txtUsuario, txtTelefone, txtEndereco, txtBairro, txtCidade;
        private TextView txtFormaPagamento, txtProdutos, txtDesconto, txtFrete, txtCashback, txtTotal;
        private Button btnAprovar, btnRecusar;
        private Locale ptBr = new Locale("pt", "BR");

        PedidoViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            CardView cardView = view.findViewById(R.id.cardView);
            imgExpandir = view.findViewById(R.id.imgExpandir);
            txtPedido = itemView.findViewById(R.id.txtPedido);
            txtDataPedido = itemView.findViewById(R.id.txtDataPedido);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtEndereco = itemView.findViewById(R.id.txtEndereco);
            txtBairro = itemView.findViewById(R.id.txtBairro);
            txtCidade = itemView.findViewById(R.id.txtCidade);
            txtFormaPagamento = itemView.findViewById(R.id.txtFormaPagamento);
            txtProdutos = itemView.findViewById(R.id.txtProdutos);
            txtDesconto = itemView.findViewById(R.id.txtDesconto);
            txtFrete = itemView.findViewById(R.id.txtFrete);
            txtCashback = itemView.findViewById(R.id.txtCashback);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            btnAprovar = itemView.findViewById(R.id.btnAprovar);
            btnRecusar = itemView.findViewById(R.id.btnRecusar);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded()) {
                        collapseView();
                    } else {
                        expandView();
                    }
                }
            });
        }

        private void setPedido(long pedido) {
            txtPedido.setText(view.getContext().getString(R.string.n_pedido, String.valueOf(pedido)));
        }

        private void setDataPedido(String data, String hora) {
            txtDataPedido.setText(view.getContext().getString(R.string.data_pedido, data, hora));
        }

        private void setUsuario(String usuario) {
            txtUsuario.setText(view.getContext().getString(R.string.usuario, usuario));
        }

        private void setTelefone(String telefone) {
            txtTelefone.setText(view.getContext().getString(R.string.telefone, telefone.substring(0, 2), telefone.substring(2, 7), telefone.substring(7, 11)));
        }

        private void setEndereco(String endereco, String numero, String complemento) {
            String strendereco;
            if (complemento.isEmpty()) {
                strendereco = view.getContext().getString(R.string.endereco, endereco, numero);
            }else{
                strendereco = view.getContext().getString(R.string.endereco_complemento, endereco, numero, complemento);
            }
            txtEndereco.setText(strendereco);
        }

        private void setBairro(String bairro) {
            txtBairro.setText(view.getContext().getString(R.string.bairro, bairro));
        }

        private void setCidade(String cidade, String estado, String cep) {
            txtCidade.setText(view.getContext().getString(R.string.cidade_uf_cep, cidade, estado, cep));
        }

        private void setFormaPagamento(String formapagamento) {
            txtFormaPagamento.setText(view.getContext().getString(R.string.forma_pagamento, formapagamento));
        }

        private void setValorProdutos(double valorprodutos) {
            txtProdutos.setText(view.getContext().getString(R.string.produtos, NumberFormat.getCurrencyInstance(ptBr).format(valorprodutos)));
        }

        private void setValorDesconto(double valordesconto) {
            txtDesconto.setText(view.getContext().getString(R.string.desconto, NumberFormat.getCurrencyInstance(ptBr).format(valordesconto)));
        }

        private void setValorFrete(double valorfrete) {
            txtFrete.setText(view.getContext().getString(R.string.frete, NumberFormat.getCurrencyInstance(ptBr).format(valorfrete)));
        }

        private void setValorCash(double valorcash) {
            txtCashback.setText(view.getContext().getString(R.string.cashback, NumberFormat.getCurrencyInstance(ptBr).format(valorcash)));
        }

        private void setValorTotal(double valortotal) {
            txtTotal.setText(view.getContext().getString(R.string.total, NumberFormat.getCurrencyInstance(ptBr).format(valortotal)));
        }

        private void setImagemTituloBtnAprovar(int status) {
            switch (status) {
                case 0:
                    btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_aprovar), null, null, null);
                    break;
                case 1:
                    btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_enviar), null, null, null);
                    break;
                case 2:
                    btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_concluir), null, null, null);
                    break;
            }
        }

        @Override
        public void setExpanded(boolean isExpanded) {
            super.setExpanded(isExpanded);
            if (isExpanded) {
                imgExpandir.setBackgroundResource(R.mipmap.ic_esconder);
            }else{
                imgExpandir.setBackgroundResource(R.mipmap.ic_mostrar);
            }
        }
    }

    class ProdutoViewHolder extends ChildViewHolder {
        private View view;
        private TextView txtProduto, txtQuantidade, txtValorUnitario, txtValorTotal, txtObservacao, txtComplemento;
        private Locale ptBr = new Locale("pt", "BR");

        ProdutoViewHolder(final View view) {
            super(view);
            this.view = view;
            txtProduto = view.findViewById(R.id.txtProduto);
            txtQuantidade = view.findViewById(R.id.txtQuantidade);
            txtValorUnitario = view.findViewById(R.id.txtValorUnitario);
            txtValorTotal = view.findViewById(R.id.txtValorTotal);
            txtObservacao = view.findViewById(R.id.txtObservacao);
            txtComplemento = view.findViewById(R.id.txtComplemento);
        }

        private void setProduto(String produto) {
            txtProduto.setText(view.getContext().getString(R.string.item, produto));
        }

        private void setQuantidade(double quantidade) {
            txtQuantidade.setText(view.getContext().getString(R.string.quantidade, String.valueOf(quantidade)));
        }

        private void setValorUnitario(double valorunitario) {
            txtValorUnitario.setText(view.getContext().getString(R.string.valor_unitario, NumberFormat.getCurrencyInstance(ptBr).format(valorunitario)));
        }

        private void setValorTotal(double valortotal) {
            txtValorTotal.setText(view.getContext().getString(R.string.valor_total, NumberFormat.getCurrencyInstance(ptBr).format(valortotal)));
        }

        private void setObservacao(String observacao) {
            if (observacao.isEmpty()){
                txtObservacao.setVisibility(View.GONE);
            }else {
                txtObservacao.setText(view.getContext().getString(R.string.observacao, observacao));
            }
        }

        private void setComplemento(String complemento) {
            if (complemento.isEmpty()){
                txtComplemento.setVisibility(View.GONE);
            }else {
                txtComplemento.setText(complemento);
            }
        }
    }
}
