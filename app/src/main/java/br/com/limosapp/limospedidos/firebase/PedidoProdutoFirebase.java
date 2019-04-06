package br.com.limosapp.limospedidos.firebase;

public class PedidoProdutoFirebase {
    private String produto, complemento, obs;
    private double quantidade, valor, valortotal;

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValorTotal(double valortotal) {
        this.valortotal = valortotal;
    }
}
