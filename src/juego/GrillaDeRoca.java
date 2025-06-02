/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/**
 *
 * @author Gino
 */
import java.util.*;


public class GrillaDeRoca {
    private int anchoPantalla;
    private int altoPantalla;
    private int tamañoCelda;
    private int cantidadRocas;

    private int offsetX;
    private int offsetY;

    private ArrayList<Coordenada> coordenadasGeneradas;

    public GrillaDeRoca(int anchoPantalla, int altoPantalla, int tamañoCelda, int cantidadRocas, int offsetX, int offsetY) {
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        this.tamañoCelda = tamañoCelda;
        this.cantidadRocas = cantidadRocas;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.coordenadasGeneradas = new ArrayList<>();
        generarCoordenadas();
    }

    private void generarCoordenadas() {
        int columnas = (anchoPantalla - offsetX) / tamañoCelda;
        int filas = (altoPantalla - offsetY) / tamañoCelda;

        Set<String> celdasOcupadas = new HashSet<>();
        Random rnd = new Random();

        int intentos = 0;
        int intentosMaximos = 5000;
        int zonaSegura = 2; //bloques a los lados del bloque del centro (total 5x5)

        while (coordenadasGeneradas.size() < cantidadRocas && intentos < intentosMaximos) {
            int col = rnd.nextInt(columnas);
            int fila = rnd.nextInt(filas);

            int colCentro = columnas / 2;
            int filaCentro = filas / 2;

            // Evitar colocar rocas en la celda del centro y sus adyacentes (3x3) -> math abs devuelve un valor absoluto el modulo muchachos, introduccion a la matematica xd
            if (Math.abs(col - colCentro) <= zonaSegura && Math.abs(fila - filaCentro) <= zonaSegura) {
                intentos++;
                continue;
            }

            String idCelda = col + "-" + fila; // para julio, id celda es la coordenada (n-m) en modo "objeto"
            String celdaCentro = colCentro + "-" + filaCentro;

            if (!celdasOcupadas.contains(idCelda) && !idCelda.equals(celdaCentro)) {
                celdasOcupadas.add(idCelda);

                int offset = tamañoCelda / 5;
                int desvioX = rnd.nextInt(offset);
                int desvioY = rnd.nextInt(offset);

                int newX = offsetX + col * tamañoCelda + desvioX;
                int newY = offsetY + fila * tamañoCelda + desvioY;

                coordenadasGeneradas.add(new Coordenada(newX, newY)); // despues de desplazar las coordendas de modo tal que no quede todo alineado, las guarda
            }

            intentos++;
        }
        if (intentos >= intentosMaximos) {
            System.out.println("No se pudieron ubicar todas las rocas sin superposición.");
        }
    }

    public ArrayList<Coordenada> getCoordenadas() {
        return coordenadasGeneradas;
    }
}



