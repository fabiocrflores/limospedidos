package br.com.limosapp.limospedidos.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

public class PedidoFirebase implements Parent<PedidoProdutoFirebase> {
    private int status, pedido;
    private String idpedido, data, hora, nomeusuario, telefone, endereco, numero, complemento, bairro, cidade, uf, cep, formapagamento;
    private double valorprodutos, valordesconto, valorfrete, valorcash, valortotal;
    private boolean iniciaexpandido;
    private List<PedidoProdutoFirebase> listaPedidoProdutos;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFormapagamento() {
        return formapagamento;
    }

    public void setFormaPagamento(String formapagamento) {
        this.formapagamento = formapagamento;
    }

    public double getValorprodutos() {
        return valorprodutos;
    }

    public void setValorProdutos(double valorprodutos) {
        this.valorprodutos = valorprodutos;
    }

    public double getValordesconto() {
        return valordesconto;
    }

    public void setValorDesconto(double valordesconto) {
        this.valordesconto = valordesconto;
    }

    public double getValorfrete() {
        return valorfrete;
    }

    public void setValorFrete(double valorfrete) {
        this.valorfrete = valorfrete;
    }

    public double getValorcash() {
        return valorcash;
    }

    public void setValorCash(double valorcash) {
        this.valorcash = valorcash;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValorTotal(double valortotal) {
        this.valortotal = valortotal;
    }

    @Override
    public List<PedidoProdutoFirebase> getChildList() {
        return listaPedidoProdutos;
    }

    public void setChildItemList(List<PedidoProdutoFirebase> listaPedidoProduto) {
        listaPedidoProdutos = listaPedidoProduto;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return iniciaexpandido;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        iniciaexpandido = initiallyExpanded;
    }
}
