import java.math.BigDecimal;

public class Ativo {
    private String idTransacao;
    private String cnpjEmissor;
    private BigDecimal valor;
    private String ratingRisco;

    public Ativo(String idTransacao, String cnpjEmissor, String valorStr, String ratingRisco) {
        this.idTransacao = idTransacao;
        this.cnpjEmissor = cnpjEmissor;
        this.valor = new BigDecimal(valorStr); // Converte texto puro para dinheiro seguro
        this.ratingRisco = ratingRisco;
    }

    public String getIdTransacao() { return idTransacao; }
    public String getCnpjEmissor() { return cnpjEmissor; }
    public BigDecimal getValor() { return valor; }
    public String getRatingRisco() { return ratingRisco; }
}