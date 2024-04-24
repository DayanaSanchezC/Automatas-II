package Analizador;

import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;

public class Analizador {
    private static ArrayList<String> simbolos = new ArrayList<>();
    private static ArrayList<String> direcciones = new ArrayList<>();
    private static boolean error = false;

    public static void main(String[] args) {
        String nombreArchivo = seleccionarArchivo();
        Token[] tokens = procesarArchivo(nombreArchivo);
        generarTablaSimbolos(tokens, simbolos);
        generarTablaDirecciones(tokens);
        generarNuevaTabla(nombreArchivo, tokens, simbolos, direcciones);
        malDeclaradas(tokens, simbolos);
        caracter(tokens, simbolos);
        verificarOp(tokens);
    }

    private static void generarTablaSimbolos(Token[] tokens, ArrayList<String> simbolo) {
        try {
            if(!error){
            BufferedWriter bw = new BufferedWriter(new FileWriter("tabla_simbolos.txt"));
            boolean primeraVezVariables = false;
            for (int i = 0; i < tokens.length; i++) {
                Token token = tokens[i];
                if (token.token.equals("-15")) {
                    primeraVezVariables = true;
                    continue;
                }
                if (token.token.equals("-2")) {
                    break;
                }
                if (primeraVezVariables) {
                    if (simbolo.contains(token.lexema)) {
                        System.err.println("La variable " + token.lexema + " ya fue declarada");
                        error=true;
                    }
                    if (token.token.equals("-51")) {
                        bw.write(String.format("%-20s%-6s%-10s%-10s", token.lexema, token.token, "0", "Main"));
                        bw.newLine();
                        simbolos.add(token.lexema);
                    } else if (token.token.equals("-52")) {
                        bw.write(String.format("%-20s%-6s%-10s%-10s", token.lexema, token.token, "0.0", "Main"));
                        bw.newLine();
                        simbolos.add(token.lexema);
                    } else if (token.token.equals("-53")) {
                        bw.write(String.format("%-20s%-6s%-10s%-10s", token.lexema, token.token, "null", "Main"));
                        bw.newLine();
                        simbolos.add(token.lexema);
                    } else if (token.token.equals("-54")) {
                        bw.write(String.format("%-20s%-6s%-10s%-10s", token.lexema, token.token, "true", "Main"));
                        bw.newLine();
                        simbolos.add(token.lexema);
                    }

                }
            }
            bw.close();
            System.out.println("Tabla de símbolos generada correctamente.");
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generarTablaDirecciones(Token[] tokens) {
        try {
            if(!error){
            BufferedWriter bw = new BufferedWriter(new FileWriter("tabla_direcciones.txt"));
            for (int i = 0; i < tokens.length; i++) {
                Token token = tokens[i];
                if (token.token.equals("-55")) {
                    bw.write(String.format("%-20s%-6s%-10s%-10s", token.lexema, token.token, token.linea, "0"));
                    bw.newLine();
                    direcciones.add(token.lexema);
                }
            }

            // Cerrar archivo de tabla de direcciones
            bw.close();
            System.out.println("Tabla de direcciones generada correctamente.");
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    public static void generarNuevaTabla(String nombreArchivo, Token[] tokens, ArrayList<String> simbolo,
            ArrayList<String> direcciones) {
        try {
            if(!error){
            BufferedWriter bw = new BufferedWriter(new FileWriter("nuevatabla.txt"));

            for (int i = 0; i < tokens.length; i++) {
                Token token = tokens[i];
                if (simbolo.contains(token.lexema)) {
                    token.setPts(String.valueOf(simbolo.indexOf(token.lexema)));
                }
                if (direcciones.contains(token.lexema)) {
                    token.setPts(String.valueOf(direcciones.indexOf(token.lexema)));
                }
                bw.write(token.toString());
                bw.newLine();
            }

            File original = new File(nombreArchivo);
            original.delete();
            bw.close();
            File tablaModificada = new File("nuevatabla.txt");
            tablaModificada.renameTo(new File(nombreArchivo));
            System.out.println("Nueva tabla de tokens generada correctamente.");
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void caracter(Token[] tokens, ArrayList<String> simbolo) {
        boolean primeraVezVariables = false;

        for (int i = 0; i < tokens.length; i++) {
            Token token = tokens[i];
            if (token.token.equals("-15")) {
                primeraVezVariables = true;
                continue;
            }
            if (token.token.equals("-2")) {
                break;
            }
            if (primeraVezVariables) {
                if (!token.lexema.endsWith("&") && token.token.equals("-51")
                        || !token.lexema.endsWith("%") && token.token.equals("-52")
                        || !token.lexema.endsWith("$") && token.token.equals("-53")
                        || !token.lexema.endsWith("#") && token.token.equals("-54")) {
                    System.err.println("Error: Caracter de control incorrecto en el lexema " + token.lexema);
                    error=true;

                }
            }
        }
    }

    private static void malDeclaradas(Token[] tokens, ArrayList<String> simbolo) {
        ArrayList<String> variablesIncorrectas = new ArrayList<>();
        boolean primeraVezVariables = false;
        for (int i = 0; i < tokens.length; i++) {
            Token token = tokens[i];
            if (token.token.equals("-15")) {
                primeraVezVariables = true;
                continue;
            }
            if (token.token.equals("-2")) {
                break;
            }
            if (primeraVezVariables) {

                if (token.token.equals("-11") && i < tokens.length - 1) {
                    while (!token.token.equals("-75")) {
                        i++;
                        if (tokens[i].token.equals("-76")) {
                            continue;
                        } else if (tokens[i].token.equals("-51")) {
                            continue;
                        } else if (tokens[i].token.equals("-75")) {
                            break;
                        }
                        if (tokens[i] != null && tokens[i].token != null && !tokens[i].token.isEmpty() &&
                                !tokens[i].token.equals("-51") && !tokens[i].token.equals("-76")
                                && !tokens[i].token.equals("-75")) {
                            variablesIncorrectas.add(tokens[i].lexema);
                            error=true;

                        }

                    }
                }

                if (token.token.equals("-12") && i < tokens.length - 1) {
                    while (!token.token.equals("-75")) {
                        i++;
                        if (tokens[i].token.equals("-76")) {
                            continue;
                        } else if (tokens[i].token.equals("-52")) {
                            continue;
                        } else if (tokens[i].token.equals("-75")) {
                            break;
                        }
                        if (tokens[i] != null && tokens[i].token != null && !tokens[i].token.isEmpty() &&
                                !tokens[i].token.equals("-52") && !tokens[i].token.equals("-76")
                                && !tokens[i].token.equals("-75")) {
                            variablesIncorrectas.add(tokens[i].lexema);
                            error=true;

                        }

                    }
                }

                if (token.token.equals("-13") && i < tokens.length - 1) {
                    while (!token.token.equals("-75")) {
                        i++;
                        if (tokens[i].token.equals("-76")) {
                            continue;
                        } else if (tokens[i].token.equals("-53")) {
                            continue;
                        } else if (tokens[i].token.equals("-75")) {
                            break;
                        }
                        if (tokens[i] != null && tokens[i].token != null && !tokens[i].token.isEmpty() &&
                                !tokens[i].token.equals("-53") && !tokens[i].token.equals("-76")
                                && !tokens[i].token.equals("-75")) {
                            variablesIncorrectas.add(tokens[i].lexema);
                            error=true;

                        }

                    }
                }

                if (token.token.equals("-14") && i < tokens.length - 1) {
                    while (!token.token.equals("-75")) {
                        i++;
                        if (tokens[i].token.equals("-76")) {
                            continue;
                        } else if (tokens[i].token.equals("-54")) {
                            continue;
                        } else if (tokens[i].token.equals("-75")) {
                            break;
                        }
                        if (tokens[i] != null && tokens[i].token != null && !tokens[i].token.isEmpty() &&
                                !tokens[i].token.equals("-54") && !tokens[i].token.equals("-76")
                                && !tokens[i].token.equals("-75")) {
                            variablesIncorrectas.add(tokens[i].lexema);
                            error=true;

                        }

                    }
                }

            }
        }
        for (String variable : variablesIncorrectas) {
            System.err.println("Error: La variable " + variable + " no corresponde al Identificador");
        }
    }

    private static void verificarOp(Token[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].token.equals("-51") && esOperadorMatematico(tokens[i+1].getToken())) {
                i += 2;
                while (!tokens[i].token.equals("-75")) {
                    String siguienteToken = tokens[i].token;
                    if (!esOperadorMatematico(siguienteToken) && !siguienteToken.equals("-51")
                            && !siguienteToken.equals("-61")) {
                        System.out.println("Error de operación: Token inesperado " + tokens[i]);
                        error=true;

                    }
                    i++;
                }
            }
            if (tokens[i].token.equals("-52") && esOperadorMatematico(tokens[i+1].getToken())) {
                while (!tokens[i].token.equals("-75")&&!tokens[i].token.equals("-74")&&!tokens[i].token.equals("-73")) {
                    String siguienteToken = tokens[i].token;
                    if (!esOperadorMatematico(siguienteToken) && !siguienteToken.equals("-52")
                            && !siguienteToken.equals("-62")) {
                        System.out.println("Error de operación: Token inesperado " + tokens[i]);
                        error=true;

                    }
                    i++;
                }
            }
            if (tokens[i].token.equals("-53") && tokens[i + 1].token.equals("-26")) {
                i += 2;
                while (!tokens[i].token.equals("-75")) {
                    String siguienteToken = tokens[i].token;
                    if (!esOperadorMatematico(siguienteToken) && !siguienteToken.equals("-53")
                            && !siguienteToken.equals("-63")) {
                        System.out.println("Error de operación: Token inesperado " + tokens[i]);
                        error=true;

                    }
                    i++;
                }
            }
            if (tokens[i].token.equals("-54") && tokens[i + 1].token.equals("-26")) {
                i += 2;
                while (!tokens[i].token.equals("-75")) {
                    String siguienteToken = tokens[i].token;
                    if (!esOperadorMatematico(siguienteToken) && !siguienteToken.equals("-54")
                            && !siguienteToken.equals("-64") && !siguienteToken.equals("-65")) {
                        System.out.println("Error de operación: Token inesperado " + tokens[i]);
                        error=true;
                    }
                    i++;
                }
            }

        }
    }

    private static boolean esOperadorMatematico(String token) {
        return token.equals("-21") || token.equals("-22") || token.equals("-23") || token.equals("-24")
                || token.equals("-25") || token.equals("-26") || token.equals("-31") || token.equals("-32")
                || token.equals("-33") || token.equals("-34") || token.equals("-35") || token.equals("-36")
                || token.equals("-41") || token.equals("-42") || token.equals("-43") || token.equals("-73")
                || token.equals("-74") || token.equals("-3") || token.equals("-4");
    }

    

    public static String seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public static Token[] procesarArchivo(String nombreArchivo) {
        Token[] tokens = null;
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            int numLineas = contarLineas(nombreArchivo);
            tokens = new Token[numLineas];
            int i = 0;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", 4);
                if (partes.length != 4) {
                    System.err.println("Error, se espera que este en el siguiente formato\n" +
                            "Lexema,Token,Posicion,Linea");
                    continue;
                }
                String lexema = partes[0];
                String token = partes[1];
                String pts = partes[2];
                String lineaNum = partes[3];
                tokens[i] = new Token(lexema, token, pts, lineaNum);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public static int contarLineas(String nombreArchivo) throws IOException {
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            while (reader.readLine() != null)
                lineCount++;
        }
        return lineCount;
    }

}
