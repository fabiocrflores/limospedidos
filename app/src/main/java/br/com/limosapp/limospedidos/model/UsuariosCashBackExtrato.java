package br.com.limosapp.limospedidos.model;

public class UsuariosCashBackExtrato {

    private String datahora, restaurante;
    private Double valor;
    private Long datahoramseg, pedido;

    public UsuariosCashBackExtrato() {

    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Long getDatahoramseg() {
        return datahoramseg;
    }

    public void setDatahoramseg(Long datahoramseg) {
        this.datahoramseg = datahoramseg;
    }

    public Long getPedido() {
        return pedido;
    }

    public void setPedido(Long pedido) {
        this.pedido = pedido;
    }
}
