
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
    
    public class TablaDeSimbolos {
        private List<Token> tokens;
        private List<Simbolo> simbolos;

       
        public static void main (String[] args){
            TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();
            tablaDeSimbolos.leerArchivo();
            tablaDeSimbolos.crearTablaDeSimbolos();
            tablaDeSimbolos.validarTabla();
            
            System.out.println("\nTabla de Tokens: ");
            tablaDeSimbolos.mostrarSimbolos();
            
            System.out.println("\nTabla de Simbolos: ");
            tablaDeSimbolos.crearArchivos();
        }

        private String Valor(int token) {
            switch (token) {
                case -51:
                    return "0";
                case -52:
                    return "0.0";
                case -53:
                    return "null";
                case -54:
                    return "true";
                case -55:
                    return "cero";
                default:
                    System.out.println("Error al tratar de asignar valor." + token);
                    System.exit(1);
                    return "";
            }
        }

        private void mostrarSimbolos() {
            for (Simbolo simbolo : simbolos) {
                System.out.println(simbolo.toString());
            }
        }
       

        private void mostrarTokens() {
            for (Token token : tokens) {
                System.out.println(token.toString());
            }
        }

        private String formatoSimbolos() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-15s%-15s%-10s%-10s", "ID", "TOKEN", "VALOR", "AMBITO","\n"));
            sb.append(String.format("\n"));
            for (Simbolo simbolo : simbolos) {
                sb.append(simbolo.toString()).append("\n");
            }
            return sb.toString();
        }
        private String formatoDirecciones() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-15s%-15s%-10s%-10s", "ID", "TOKEN", "VALOR", "AMBITO","\n"));
            sb.append(String.format("\n"));
            for (Simbolo simbolo : simbolos) {
                sb.append(simbolo.toString()).append("\n");
            }
            return sb.toString();
        }

        private String formatoTokens() {
            StringBuilder sb = new StringBuilder();
            sb.append("Lexema|Token|pocision|Linea\n");
            for (Token token : tokens) {
                sb.append(token.toString()).append("\n");
            }
            return sb.toString();
        }

        private void crearArchivos() {
            try {
                BufferedWriter tokenWriter = new BufferedWriter(new FileWriter( "TablaDeTokens.txt"));
                tokenWriter.write(formatoTokens());
                tokenWriter.close();

                BufferedWriter simboloWriter = new BufferedWriter(new FileWriter( "TablaDeSimbolos.txt"));
                simboloWriter.write(formatoSimbolos());
                simboloWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean Tipo(Token token) {
            for (Simbolo simbolo : simbolos) {
                if (simbolo.getId().equals(token.getNombre())) {
                    return simbolo.getToken() == token.getToken();
                }
            }
            return false;
        }

        private boolean existeSimbolo(Token token) {
            for (Simbolo simbolo : simbolos) {
                if (simbolo.getId().equals(token.getNombre())) {
                    return simbolo.getToken() == token.getToken();
                }
            }
            return false;
        }

        private void validarTabla() {
            boolean buscar = false;
            for (Token token : tokens) {
                if (token.getToken() == -2) {
                    buscar = true;
                    continue;
                }
                if (buscar && token.getToken() >= -51 && token.getToken() <= -54) {
                    if (!existeSimbolo(token)) {
                        System.out.println("No se declaro un Identificador" + token.getNombre() + "'");
                        System.exit(1);
                    } else if (!Tipo(token)) {
                        System.out.println("El Identificador no es del tipo correcto" + token.getNombre() + "'");
                        System.exit(1);
                    }
                }
            }
        }

        private void crearTablaDeSimbolos() {
            simbolos = new ArrayList<>();
            Set<String> nombresRepetidos = new HashSet<>();

            for (Token token : tokens) {
                if (token.getToken() == -2) {
                    break;
                }
                if (token.getTabla() == -2 && token.getToken() != -55) {
                    if (!nombresRepetidos.contains(token.getToken())) {
                        Simbolo simbolo = new Simbolo(token.getNombre(), token.getToken(), Valor(token.getToken()));
                        simbolos.add(simbolo);
                        nombresRepetidos.add(token.getNombre());
                    } else {
                        System.out.println("Error, hay repetidos: '" + token.getNombre() + "'");
                        System.exit(1);
                    }
                }
            }
        }

        private void leerArchivo() {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (.txt)", "txt");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String archivoSeleccionado = fileChooser.getSelectedFile().getPath();

                try (BufferedReader reader = new BufferedReader(new FileReader(archivoSeleccionado))) {
                    tokens = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 4) {
                            Token token = new Token(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                            tokens.add(token);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Ningún archivo seleccionado");
                System.exit(1);
            }
        }
    
    
    private static class Token {
        private String nombre;
        private int token;
        private int tabla;
        private int linea;

        public Token(String nombre, int token, int tabla, int linea) {
            this.nombre = nombre;
            this.token = token;
            this.tabla = tabla;
            this.linea = linea;
        }

        public String getNombre() {
            return nombre;
        }

        public int getToken() {
            return token;
        }

        public int getTabla() {
            return tabla;
        }

        public int getLinea() {
            return linea;
        }

        public String toString() {
            return nombre + "|" + token + "|" + tabla + "|" + linea;
        }
    }
        
    private static class Simbolo {
        private String id;
        private int token;
        private String valor;

        public Simbolo(String id, int token, String valor) {
            this.id = id;
            this.token = token;
            this.valor = valor;
        }

        public String getId() {
            return id;
        }

        public int getToken() {
            return token;
        }

        public String getValor() {
            return valor;
        }

        public String toString() {
            return String.format("%-15s%-15d%-10s%-10s", id, token, valor,"Main");
        }
        
    }
}