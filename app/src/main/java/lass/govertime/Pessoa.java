package lass.govertime;

/**
 * Created by Nailson on 12/05/2018.
 */

public class Pessoa {

    private String nome;
    private String imagem;
    private String imagem1;
    private String fonte;
    private String sobre;
    private String voto;
    private String link;
    private String texto;
    private String data;

    public Pessoa() {
    }

    public Pessoa(String nome, String imagem, String imagem1, String fonte, String sobre, String voto, String link, String texto, String data) {
        this.nome = nome;
        this.imagem = imagem;
        this.imagem1 = imagem1;
        this.fonte = fonte;
        this.sobre = sobre;
        this.voto = voto;
        this.link = link;
        this.texto = texto;
        this.data = data;
    }

    public String getImagem1() {
        return imagem1;
    }

    public void setImagem1(String imagem1) {
        this.imagem1 = imagem1;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getSobre() {
        return sobre;
    }

    public void setSobre(String sobre) {
        this.sobre = sobre;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}