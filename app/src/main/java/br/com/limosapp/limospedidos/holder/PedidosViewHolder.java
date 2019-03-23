package br.com.limosapp.limospedidos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limospedidos.R;

public class PedidosViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private TextView txtPedido, txtDataPedido, txtUsuario, txtTelefone, txtEndereco, txtBairro, txtCidade;
    private TextView txtProdutos, txtDesconto, txtFrete, txtCashback, txtTotal;
    public Button btnDetalhes, btnAprovar, btnRecusar;
    private Locale ptBr = new Locale("pt", "BR");

    public PedidosViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtPedido = itemView.findViewById(R.id.txtPedido);
        txtDataPedido = itemView.findViewById(R.id.txtDataPedido);
        txtUsuario = itemView.findViewById(R.id.txtUsuario);
        txtTelefone = itemView.findViewById(R.id.txtTelefone);
        txtEndereco = itemView.findViewById(R.id.txtEndereco);
        txtBairro = itemView.findViewById(R.id.txtBairro);
        txtCidade = itemView.findViewById(R.id.txtCidade);
        txtProdutos = itemView.findViewById(R.id.txtProdutos);
        txtDesconto = itemView.findViewById(R.id.txtDesconto);
        txtFrete = itemView.findViewById(R.id.txtFrete);
        txtCashback = itemView.findViewById(R.id.txtCashback);
        txtTotal = itemView.findViewById(R.id.txtTotal);
        btnDetalhes = itemView.findViewById(R.id.btnDetalhes);
        btnAprovar = itemView.findViewById(R.id.btnAprovar);
        btnRecusar = itemView.findViewById(R.id.btnRecusar);
    }

    public void setPedido(int pedido) {
        txtPedido.setText(view.getContext().getString(R.string.n_pedido, String.valueOf(pedido)));
    }

    public void setDatapedido(String data, String hora) {
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

    public void setValorprodutos(double valorprodutos) {
        txtProdutos.setText(view.getContext().getString(R.string.produtos, NumberFormat.getCurrencyInstance(ptBr).format(valorprodutos)));
    }

    public void setValordesconto(double valordesconto) {
        txtDesconto.setText(view.getContext().getString(R.string.desconto, NumberFormat.getCurrencyInstance(ptBr).format(valordesconto)));
    }

    public void setValorfrete(double valorfrete) {
        txtFrete.setText(view.getContext().getString(R.string.frete, NumberFormat.getCurrencyInstance(ptBr).format(valorfrete)));
    }

    public void setValorcash(double valorcash) {
        txtCashback.setText(view.getContext().getString(R.string.cashback, NumberFormat.getCurrencyInstance(ptBr).format(valorcash)));
    }

    public void setValortotal(double valortotal) {
        txtTotal.setText(view.getContext().getString(R.string.total, NumberFormat.getCurrencyInstance(ptBr).format(valortotal)));
    }

    public void setImagemtitulobtnaprovar(int status) {
        switch (status) {
            case 0:
                btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_aprovar), null, null, null);
                btnAprovar.setText(R.string.aprovar);
                break;
            case 1:
                btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_enviar), null, null, null);
                btnAprovar.setText(R.string.enviar);
                break;
            case 2:
                btnAprovar.setCompoundDrawablesWithIntrinsicBounds(view.getResources().getDrawable(R.drawable.ic_concluir), null, null, null);
                btnAprovar.setText(R.string.concluir);
                break;
        }
    }

    public void setTitulobtnrecusar(int status) {
        if (status == 0) {
            btnRecusar.setText(R.string.recusar);
        }else{
            btnRecusar.setText(R.string.cancelar);
        }
    }
}
