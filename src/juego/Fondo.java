package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Fondo {
    private Image imagenActual; // La imagen que se está mostrando actualmente
    private double x;
    private double y;
    private double escala;

    // Declaramos las imágenes de fondo específicas para cada nivel
    private Image imgFondoNivel1;
    private Image imgFondoNivel2;
    private Image imgFondoNivel3;
    private Image imgFondoJefeFinal;
   
    public Fondo(double xInicial, double yInicial, double escalaInicial) {
        this.x = xInicial;
        this.y = yInicial;
        this.escala = escalaInicial;

        // Cargamos TODAS las imágenes de fondo en el constructor.
        // Esto asegura que estén listas para usarse cuando se necesiten.
        this.imgFondoNivel1 = Herramientas.cargarImagen("juego/img/Scene/Fondos/fondo_nivel1.png");
        this.imgFondoNivel2 = Herramientas.cargarImagen("juego/img/Scene/Fondos/fondo_nivel2.png");
        this.imgFondoNivel3 = Herramientas.cargarImagen("juego/img/Scene/Fondos/fondo-03.png");
        this.imgFondoJefeFinal = Herramientas.cargarImagen("juego/img/Scene/Fondos/fondo_niveljefefinal.png");

        // Por defecto, iniciamos con el fondo del nivel 1. Puedes cambiar esto
        // si quieres que el juego empiece en un menú, por ejemplo.
        this.imagenActual = imgFondoNivel1;
    }

    // Constructor sin escala (asume escala 1.0 por defecto)
    public Fondo(double xInicial, double yInicial) {
        this(xInicial, yInicial, 0);
    }

    // Método para cambiar la imagen de fondo actual según el nombre del nivel
    public void setFondo(String nombreFondo) {
        switch (nombreFondo) {
            case "nivel1":
                this.imagenActual = imgFondoNivel1;
                break;
            case "nivel2":
                this.imagenActual = imgFondoNivel2;
                break;
            case "nivel3":
                this.imagenActual = imgFondoNivel3;
                break;
            case "jefeFinal":
                this.imagenActual = imgFondoJefeFinal;
                break;
            default:
                this.imagenActual = imgFondoNivel1; // Asignamos un fondo por defecto en caso de error
                break;
        }
    }

    // Método para dibujar el fondo en el entorno
    public void dibujar(Entorno entorno) {
        // Solo dibujamos si hay una imagen asignada para evitar errores.
        if (imagenActual != null) {
            entorno.dibujarImagen(imagenActual, x, y, 0, escala);
        }
    }
}