
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class Analizador {
    private static HashMap<String, String> tablaSimbolos = new HashMap<>(); // Tabla de símbolos
    private static HashMap<String, String> tablaDirecciones = new HashMap<>(); // Tabla de direcciones
    private static int posicionEnTabla = 1; // Posición actual en la tabla de símbolos

    public static void main(String[] args) {
        boolean primeraVezVariables = true;

        try {
            BufferedReader br = new BufferedReader(new FileReader("entrada.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("tabla_simbolos.txt"));

            // Escribir encabezados
            bw.write(String.format("%-15s%-15s%-10s%-10s", "ID", "TOKEN", "VALOR", "AMBITO"));
            bw.newLine();

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().equals("variables")) {
                    if (!primeraVezVariables) {
                        continue;
                    } else {
                        primeraVezVariables = false;
                    }
                }

                if (linea.trim().equals("inicio")) {
                    break;
                }

                String[] tokens = linea.split("[\\s,;]+");
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    if (token.contains("$") || token.contains("%") || token.contains("#") || token.contains("&")) {
                        String[] partes = token.split("[$%&#]+");
                        if (partes.length > 0) {
                            String nombre = partes[0].trim();
                            String simbolo = obtenerSimbolo(token);
                            String tipo = obtenerTipoVariable(token);
                            String valor = obtenerValor(token);

                            if (!tablaSimbolos.containsKey(nombre)) {
                                tablaSimbolos.put(nombre, tipo);
                                bw.write(String.format("%-15s", nombre +simbolo));
                                bw.write(String.format("%-15s", tipo));
                                bw.write(String.format("%-10s", valor));
                                bw.write(String.format("%-10s", "main"));
                                bw.newLine();

                                if (tipo.equals("-51") || tipo.equals("-52") || tipo.equals("-53") || tipo.equals("-54")) {
                                    if (!tablaDirecciones.containsKey(nombre)) {
                                        tablaDirecciones.put(nombre, "@" + posicionEnTabla);
                                        posicionEnTabla++;
                                    } else {
                                        System.err.println("Error: Identificador duplicado - " + nombre);
                                    }
                                } else {
                                    System.err.println("Error: Uso no adecuado del tipo de dato - " + nombre);
                                }
                            } else {
                                System.err.println("Error: Identificador ya declarado - " + nombre);
                            }
                        }
                    }
                }
            }

            // Cerrar archivo de tabla de símbolos
            bw.close();
            System.out.println("Tabla de símbolos generada correctamente.");

            // Generar tabla de direcciones
            generarTablaDirecciones();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

       private static void generarTablaDirecciones() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("entrada.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("tabla_direcciones.txt"));
            bw.write(String.format("%-15s%-15s%-15s%-10s", "ID", "TOKEN", "NO. LINEA", "VCI"));
            bw.newLine();
            HashSet<String> contadorDir = new HashSet<>(); 
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] tokens = linea.split("[\\s,;]+");
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    if (token.contains("@") ) {
                        String[] partes = token.split("[@]+"); // Dividir el token por los caracteres especiales
                        if (partes.length > 0) {
                            String nombre = partes[0]; // Tomar la primera parte como el nombre
                            if (!contadorDir.contains(nombre)) { // Verificar si el nombre ya ha sido encontrado
                                contadorDir.add(nombre); // Agregar el nombre al conjunto de nombres encontrados
                                String simbolo = obtenerSimbolo(token);
                                String tipo = obtenerTipoVariable(token);
                                bw.write(String.format("%-15s", nombre + simbolo+"@"));
                                bw.write(String.format("%-15s", tipo));
                                bw.write(String.format("%-15s", "1"));
                                bw.write(String.format("%-10s", "0"));
                                bw.newLine();
                            }
                        }
                    }
                }
            }
    
            // Cerrar archivo de tabla de direcciones
            bw.close();
            System.out.println("Tabla de direcciones generada correctamente.");
    
        } catch (IOException e) {
            e.printStackTrace();
        }
        NuevaTablaDeTokens.generarNuevaTabla();
    }
    private static String obtenerSimbolo(String token) {
        if (token.contains("$")) {
            return "$";
        } else if (token.contains("%")) {
            return "%";
        } else if (token.contains("#")) {
            return "#";
        } else if (token.contains("&")) {
            return "&";
        }
        return "";
    }

    private static String obtenerTipoVariable(String token) {
        String tipo = "";
        if (token.contains("&")) {
            tipo = "-51"; // Token para entero
        } else if (token.contains("%")) {
            tipo = "-52"; // Token para real
        } else if (token.contains("$")) {
            tipo = "-53"; // Token para cadena
        } else if (token.contains("#")) {
            tipo = "-54"; // Token para lógico
        }
        else if (token.contains("@")) {
            tipo = "-55"; // Token para lógico
        }
        return tipo;
    }

    private static String obtenerValor(String token) {
        String valor = "";
        if (token.contains("&")) {
            valor = "0";
        } else if (token.contains("%")) {
            valor = "0.0";
        } else if (token.contains("$")) {
            valor = "null";
        } else if (token.contains("#")) {
            valor = "true";
        }
        return valor;
    }
}

class NuevaTablaDeTokens {
    public static void generarNuevaTabla() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("entrada.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("nuevaTablaTokens.txt"));

            HashMap<String, Integer> tablaTokens = new HashMap<>();
            int posicionEnTabla = 0;
            int posicionDir=0;

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                String nombre = partes[0];

                if (nombre.contains("$") || nombre.contains("%") || nombre.contains("#") || nombre.contains("&")) {
                    if (!tablaTokens.containsKey(nombre)) {
                        tablaTokens.put(nombre, posicionEnTabla);
                        posicionEnTabla++;
                    }
                }
                if (nombre.contains("@") ){
                    if (!tablaTokens.containsKey(nombre)) {
                        tablaTokens.put(nombre, posicionDir);
                        posicionDir++;
                    }
                }

                int posicion = tablaTokens.getOrDefault(nombre, -1);
                bw.write(String.format("%-15s%-15s%-15s%-10s", nombre, partes[1], posicion, partes[3]));
                bw.newLine();
            }

            br.close();
            bw.close();
            System.out.println("Nueva tabla de tokens generada correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

