package juego;

import entorno.Entorno;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import entorno.Herramientas;

public class Menu {
    private double x;
    private double y;
    private double ancho;
    private double alto;
    private double angulo;
    private Image imagen;
    private List<PoderesCopados> poderes; // Aunque no se usan para dibujar aquí, se mantienen.

    public Menu(double x, double y, double ancho, double alto, String archivo) {
        this.x = x;
        this.y = y;
        this.angulo = 0;
        this.ancho = ancho;
        this.alto = alto;
        this.poderes = new ArrayList<>();
        Image original = Herramientas.cargarImagen(archivo);
        this.imagen = original.getScaledInstance((int)ancho, (int)alto, Image.SCALE_SMOOTH);
    }

    // Método dibujar modificado para incluir el total de enemigos del nivel
    public void dibujar(Entorno entorno, Gondolf gondolf, JefeFinal jefeFinal,int enemigosEliminadosActual, int totalEnemigosNivel, int nivelActual) {
        entorno.dibujarImagen(imagen, x + ancho / 2, y + alto / 2, angulo);

        // Establece la fuente para el texto que se mostrará en el menú.
        // Si 'Medieval Pixel' no está en el sistema, usa una fuente por defecto.
        entorno.cambiarFont("Medieval Pixel", 16, Color.WHITE);

        // -- Información de Gondolf --
        dibujarBarra(entorno, gondolf.getVida(),1, 100, x + 40, y + 90, 100, 10, Color.RED, "gondolf");
        entorno.escribirTexto("VIDA", x + 40, y + 80);

        dibujarBarra(entorno, gondolf.getEnergiaMagica(),1, 100, x + 40, y + 130, 100, 10, Color.CYAN, "gondolf");
         entorno.escribirTexto("MAGIA", x + 40, y + 120);

         if(jefeFinal != null){
             dibujarBarra(entorno, jefeFinal.getVida(),25, 2500, x + 50, y + 625, 100, 10, Color.RED, "jefe");

         }

        //  Información del Nivel y Enemigos 
        entorno.escribirTexto("Nivel: " + (nivelActual + 1), x + 50, y + 500); 
        // Decide si mostrar el contador de enemigos o "Jefe"
        String textoEnemigos = (totalEnemigosNivel == -1) ? "Jefe" : (enemigosEliminadosActual + "/" + totalEnemigosNivel);
        entorno.escribirTexto("Enemigos: " + textoEnemigos, x + 50, y + 470); 

        
    }

 /**
 * Dibuja una barra (vida, maná, etc.) con pasos discretos controlados por aspectRatio.
 *
 * @param entorno      motor gráfico donde se dibuja
 * @param valorActual  valor actual de la barra (0-valorMaximo)
 * @param aspectRatio  cuántas unidades de valor equivalen a 1 “unidad” de barra
 *                     - 1  → la barra se acorta 1 pixel por cada punto que se pierde
 *                     - 25 → la barra se acorta 1 pixel por cada 25 puntos que se pierden
 * @param valorMaximo  valor máximo de la barra
 * @param x, y         posición del extremo izquierdo-medio de la barra
 * @param largo        largo total (en píxeles) de la barra gris de fondo
 * @param alto         alto de la barra
 * @param color        color de la parte rellena
 */
private void dibujarBarra(Entorno entorno,int valorActual,int aspectRatio,int valorMaximo,double x,double y,int largo,int alto,Color color, String typeOfCharacter) {

    // Dibuja la barra de fondo
    entorno.dibujarRectangulo(x + largo / 2.0, y, largo, alto, 0, Color.DARK_GRAY);

    // Cálculo de pasos
    int pasosActuales = valorActual / aspectRatio;
    int pasosTotales = valorMaximo / aspectRatio;

    // Asegurar que no haya división por cero
    if (pasosTotales <= 0) pasosTotales = 1;

    // Ancho proporcional de la barra rellena
    int ancho = (int) Math.round(((double) pasosActuales / pasosTotales) * largo);
    
    if(typeOfCharacter.equals("jefe")){
        if(valorActual <= (valorMaximo * (double) 3/4) && valorActual > valorMaximo * (double) 1/4){ 
            entorno.dibujarRectangulo(x + ancho / 2.0, y, ancho, alto, 0, Color.RED);          
            entorno.escribirTexto("JEFE FINAL", x,  y - 10 );

        }
        else if(valorActual <= valorMaximo * (double) 1/4) {
           entorno.dibujarRectangulo(x + ancho / 2.0, y, ancho, alto, 0, Color.RED); 
           entorno.escribirTexto("JEFE FINAL", x,  y - 10 );

        } 
        else{
         entorno.dibujarRectangulo(x + ancho / 2.0, y, ancho, alto, 0,  Color.RED); //fase 3
         entorno.escribirTexto("JEFE FINAL",x,  y - 10 );

        }
    }
    else{
           // Dibuja solo si hay algo para mostrar
        if (ancho > 0) {
            entorno.dibujarRectangulo(x + ancho / 2.0, y, ancho, alto, 0, color);
        }
    }


}

    public void agregarPoder(PoderesCopados poder) {
        poderes.add(poder);
    }
}