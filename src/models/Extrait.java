package models;

public class Extrait {

    private String encode;
    private String decode;

    public Extrait(String encode, String decode) {
        this.encode = encode;
        this.decode = decode;
    }

    public String getEncode() {
        return encode;
    }

    public String getDecode() {
        return decode;
    }

    public String toString(){
        return getDecode();
    }
}
