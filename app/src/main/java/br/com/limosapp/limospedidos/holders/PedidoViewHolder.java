package br.com.limosapp.limospedidos.holders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limospedidos.R;

public class PedidoViewHolder extends ParentViewHolder {
    private View view;
    private ConstraintLayout constraintLayoutDetalhes;
    private ImageView imgExpandir;
    private TextView txtPedido, txtDataPedido, txtUsuario, txtTelefone, txtEndereco, txtBairro, txtCidade;
    private TextView txtFormaPagamento, txtProdutos, txtDesconto, txtFrete, txtCashback, txtTotal;
    public Button btnAprovar, btnRecusar;
    private Locale ptBr = new Locale("pt", "BR");

    public PedidoViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        CardView cardView = view.findViewById(R.id.cardView);
        imgExpandir = view.findViewById(R.id.imgExpandir);
        txtPedido = itemView.findViewById(R.id.txtPedido);
        txtDataPedido = itemView.findViewById(R.id.txtDataPedido);
        txtUsuario = itemView.findViewById(R.id.txtUsuario);
        txtTelefone = itemView.findViewById(R.id.txtTelefone);
        constraintLayoutDetalhes = itemView.findViewById(R.id.constraintLayoutDetalhes);
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

    public void setPedido(int pedido) {
        txtPedido.setText(view.getContext().getString(R.string.n_pedido, String.valueOf(pedido)));
    }

    public void setDataPedido(String data, String hora) {
        txtDataPedido.setText(view.getContext().getString(R.string.data_pedido, data, hora));
    }

    public void setUsuario(String usuario) {
        txtUsuario.setText(view.getContext().getString(R.string.usuario, usuario));
    }

    public void setTelefone(String telefone) {
        txtTelefone.setText(view.getContext().getString(R.string.telefone, telefone.substring(0, 2), telefone.substring(2, 7), telefone.substring(7, 11)));
    }

    public void setEndereco(String endereco, String numero, String complemento) {
        String strendereco;
        if (complemento.equals("")) {
            strendereco = view.getContext().getString(R.string.endereco, endereco, numero);
        }else{
            strendereco = view.getContext().getString(R.string.endereco_complemento, endereco, numero, complemento);
        }
        txtEndereco.setText(strendereco);
    }

    public void setBairro(String bairro) {
        txtBairro.setText(view.getContext().getString(R.string.bairro, bairro));
    }

    public void setCidade(String cidade, String estado, String cep) {
        txtCidade.setText(view.getContext().getString(R.string.cidade_uf_cep, cidade, estado, cep));
    }

    public void setFormaPagamento(String formapagamento) {
        txtFormaPagamento.setText(view.getContext().getString(R.string.forma_pagamento, formapagamento));
    }

    public void setValorProdutos(double valorprodutos) {
        txtProdutos.setText(view.getContext().getString(R.string.produtos, NumberFormat.getCurrencyInstance(ptBr).format(valorprodutos)));
    }

    public void setValorDesconto(double valordesconto) {
        txtDesconto.setText(view.getContext().getString(R.string.desconto, NumberFormat.getCurrencyInstance(ptBr).format(valordesconto)));
    }

    public void setValorFrete(double valorfrete) {
        txtFrete.setText(view.getContext().getString(R.string.frete, NumberFormat.getCurrencyInstance(ptBr).format(valorfrete)));
    }

    public void setValorCash(double valorcash) {
        txtCashback.setText(view.getContext().getString(R.string.cashback, NumberFormat.getCurrencyInstance(ptBr).format(valorcash)));
    }

    public void setValorTotal(double valortotal) {
        txtTotal.setText(view.getContext().getString(R.string.total, NumberFormat.getCurrencyInstance(ptBr).format(valortotal)));
    }

    public void setImagemTituloBtnAprovar(int status) {
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
            constraintLayoutDetalhes.setVisibility(View.VISIBLE);
            imgExpandir.setBackgroundResource(R.mipmap.ic_esconder);
        }else{
            constraintLayoutDetalhes.setVisibility(View.GONE);
            imgExpandir.setBackgroundResource(R.mipmap.ic_mostrar);
        }
    }
}
