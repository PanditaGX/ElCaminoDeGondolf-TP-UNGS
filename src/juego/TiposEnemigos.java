package juego;

import entorno.Entorno;
import java.awt.Image;
import entorno.Herramientas;

public class TiposEnemigos {
    private double x;
    private double y;
    private double velocidad;
    private double radio;
    private double radioSeparacionEnemigos; 
    private double angulo;
    private Image imagen;
    private double vida;

    public TiposEnemigos(double x, double y, String archivo, double vidaInicial) {
        this.x = x;
        this.y = y;
        this.radio = 0.5;
        this.radioSeparacionEnemigos = 40;
                                         
        this.velocidad = 3.5;
        this.imagen = Herramientas.cargarImagen(archivo);
        this.angulo = 0;
        this.vida = vidaInicial;
    }

    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, angulo, radio * 2);
    }

    public void moverHacia(Gondolf g) {
        double dx = g.getX() - this.x;
        double dy = g.getY() - this.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);

        if (distancia != 0) {
            this.x += (dx / distancia) * velocidad;
            this.y += (dy / distancia) * velocidad;
        }
    }

    // Método para la separación entre enemigos
    public void separarDeEnemigos(TiposEnemigos[] todosLosEnemigos) {
        double fuerzaSeparacionX = 0;
        double fuerzaSeparacionY = 0;

        for (TiposEnemigos otroEnemigo : todosLosEnemigos) {
            // Asegúrate de no compararte contigo mismo y que el otro enemigo exista y esté vivo
            if (otroEnemigo != null && otroEnemigo != this && otroEnemigo.estaVivo()) {
                double dx = this.x - otroEnemigo.getX();
                double dy = this.y - otroEnemigo.getY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

                // Si están demasiado cerca (usando el nuevo radio de separación)
                if (distancia > 0 && distancia < this.radioSeparacionEnemigos) {
                    // La fuerza es inversamente proporcional a la distancia (más cerca, más fuerte)
                    // y el término (radioSeparacionEnemigos - distancia) asegura que la fuerza sea
                    // mayor cuanto más se solapen.
                    fuerzaSeparacionX += (dx / distancia) * (this.radioSeparacionEnemigos - distancia);
                    fuerzaSeparacionY += (dy / distancia) * (this.radioSeparacionEnemigos - distancia);
                }
            }
        }

        // Aplica la fuerza de separación, normalizándola para que no sea demasiado fuerte
        double magnitudFuerza = Math.sqrt(fuerzaSeparacionX * fuerzaSeparacionX + fuerzaSeparacionY * fuerzaSeparacionY);
        if (magnitudFuerza > 0) {
            // Puedes ajustar este divisor (ej. 0.5) para controlar qué tan fuerte es la separación
            // en relación con la velocidad de movimiento del enemigo.
            this.x += (fuerzaSeparacionX / magnitudFuerza) * (velocidad * 0.5);
            this.y += (fuerzaSeparacionY / magnitudFuerza) * (velocidad * 0.5);
        }
    }

    public boolean colisionaCon(Gondolf g) {
        double dx = g.getX() - this.x;
        double dy = g.getY() - this.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        return distancia < (this.radio + 20); // Usa el 'radio' original para Gondolf
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVida() { return vida; }
    public double getRadio() { return radio; } // Devuelve el radio original

    public void recibirDaño(int daño) {
        if (daño < 0) return;
        this.vida -= daño;
        if (this.vida < 0) this.vida = 0;
    }

    public boolean estaVivo() {
        return this.vida > 0;
    }

    public static TiposEnemigos generarEnemigoDesdeBorde(int ancho, int alto, String archivo, double vidaInicial) {
        int borde = (int)(Math.random() * 4);
        double x = 0, y = 0;

        switch (borde) {
            case 0:
                x = Math.random() * ancho;
                y = -20;
                break;
            case 1:
                x = Math.random() * ancho;
                y = alto + 20;
                break;
            case 2:
                x = -20;
                y = Math.random() * alto;
                break;
            case 3:
                x = ancho + 20;
                y = Math.random() * alto;
                break;
        }
        return new TiposEnemigos(x, y, archivo, vidaInicial);
    }

    public static int contarEnemigosVivos(TiposEnemigos[] enemigos) {
        int vivos = 0;
        for (TiposEnemigos e : enemigos) {
            if (e != null && e.estaVivo()) vivos++;
        }
        return vivos;
    }
}