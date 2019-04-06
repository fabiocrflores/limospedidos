package br.com.limosapp.limospedidos.holders;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limospedidos.R;

public class ProdutoViewHolder extends ChildViewHolder {
    private View view;
    private TextView txtProduto, txtQuantidade, txtValorUnitario, txtValorTotal, txtObservacao, txtComplemento;
    private Locale ptBr = new Locale("pt", "BR");

    public ProdutoViewHolder(final View view) {
        super(view);
        this.view = view;
        txtProduto = view.findViewById(R.id.txtProduto);
        txtQuantidade = view.findViewById(R.id.txtQuantidade);
        txtValorUnitario = view.findViewById(R.id.txtValorUnitario);
        txtValorTotal = view.findViewById(R.id.txtValorTotal);
        txtObservacao = view.findViewById(R.id.txtObservacao);
        txtComplemento = view.findViewById(R.id.txtComplemento);
    }

    public void setProduto(String produto) {
        txtProduto.setText(view.getContext().getString(R.string.item, produto));
    }

    public void setQuantidade(double quantidade) {
        txtQuantidade.setText(view.getContext().getString(R.string.quantidade, NumberFormat.getCurrencyInstance(ptBr).format(quantidade)));
    }

    public void setValorUnitario(double valorunitario) {
        txtValorUnitario.setText(view.getContext().getString(R.string.valor_unitario, NumberFormat.getCurrencyInstance(ptBr).format(valorunitario)));
    }

    public void setValorTotal(double valortotal) {
        txtValorTotal.setText(view.getContext().getString(R.string.valor_total, NumberFormat.getCurrencyInstance(ptBr).format(valortotal)));
    }

    public void setObservacao(String observacao) {
        if (observacao.equals("")){
            txtObservacao.setVisibility(View.GONE);
        }else {
            txtObservacao.setText(view.getContext().getString(R.string.observacao, observacao));
        }
    }

    public void setComplemento(String complemento) {
        if (complemento.equals("")){
            txtComplemento.setVisibility(View.GONE);
        }else {
            txtComplemento.setText(complemento);
        }
    }
}
