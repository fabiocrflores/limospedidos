package br.com.limosapp.limospedidos.firebase;

public class PedidosFirebase {
    private int Pedido;
    private String data, hora, nomeusuario, endereco, numero, bairro, cidade, cep;
    private double valorprodutos, valordesconto, valorfrete, valorcash, valortotal;

    public int getPedido() {
        return Pedido;
    }

    public void setPedido(int pedido) {
        Pedido = pedido;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNomeusuario() {
        return nomeusuario;
    }

    public void setNomeusuario(String nomeusuario) {
        this.nomeusuario = nomeusuario;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public double getValorprodutos() {
        return valorprodutos;
    }

    public void setValorprodutos(double valorprodutos) {
        this.valorprodutos = valorprodutos;
    }

    public double getValordesconto() {
        return valordesconto;
    }

    public void setValordesconto(double valordesconto) {
        this.valordesconto = valordesconto;
    }

    public double getValorfrete() {
        return valorfrete;
    }

    public void setValorfrete(double valorfrete) {
        this.valorfrete = valorfrete;
    }

    public double getValorcash() {
        return valorcash;
    }

    public void setValorcash(double valorcash) {
        this.valorcash = valorcash;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }
}
