public class Token {
    String lexema;
    String token;
    String pts;
    String linea;

    public Token(String lexema, String token, String pts, String linea) {
        this.lexema = lexema;
        this.token = token;
        this.pts = pts;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return lexema + "," + token + "," + pts + "," + linea;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getLexema() {
        return lexema;
    }

    public String getToken() {
        return token;
    }

    public String getPts() {
        return pts;
    }

    public String getLinea() {
        return linea;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }
}
