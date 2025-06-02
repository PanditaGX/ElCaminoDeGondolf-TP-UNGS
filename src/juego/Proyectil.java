package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Proyectil {
    private double x, y;
    private double dx, dy;
    private double velocidad = 5.0;
    private double radio = 0.5;
    private double angulo;
    private Image imagen;

    public Proyectil(double origenX, double origenY, double objetivoX, double objetivoY, String archivo) {
        this.x = origenX;
        this.y = origenY;
        this.imagen = Herramientas.cargarImagen(archivo);
        this.angulo = 0;

        double difX = objetivoX - origenX;
        double difY = objetivoY - origenY;
        double distancia = Math.sqrt(difX * difX + difY * difY);

        this.dx = (difX / distancia) * velocidad;
        this.dy = (difY / distancia) * velocidad;
    }

    public void mover() {
        x += dx;
        y += dy;
    }

    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, angulo, radio * 2);
    }

    public boolean fueraDePantalla() {
        return x < 0 || x > 1080 || y < 0 || y > 720;
    }

    public boolean colisionaCon(TiposEnemigos e) {
        double dx = e.getX() - x;
        double dy = e.getY() - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        return distancia < (radio + 15);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
