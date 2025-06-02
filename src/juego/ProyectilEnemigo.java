package juego;

import entorno.Entorno;

import java.awt.Image;

public class ProyectilEnemigo {
    private double x, y, vx, vy; // Posición y velocidad 
    private double anguloRotacion; //rotar imagen
    private double velocidadActual; // acelerar o desacelerar
    private int tipoBala; // comportamientos 
    private Image imagenBala; //  tener una imagen diferente

    //tipo
    public static final int TIPO_NORMAL = 0;
    public static final int TIPO_GIRATORIA = 1; // rota
    public static final int TIPO_ACELERADORA = 2; //incrementa su velocidad 
    

  
    public ProyectilEnemigo(double x, double y, double vx, double vy, int tipo, Image imagen) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        // La velocidad actual 
        this.velocidadActual = Math.sqrt(vx * vx + vy * vy);
        // El ángulo inicial 
        this.anguloRotacion = Math.atan2(vy, vx);
        this.tipoBala = tipo;
        this.imagenBala = imagen;
    }

    
     // la posición y el comportamiento del proyectil.

    public void mover() {
        // movimiento básica paralos proyectiles
        x += vx;
        y += vy;

        // comportamiento segun el tipo de bala
        switch (tipoBala) {
            case TIPO_GIRATORIA:
                // gira su dirección de movimiento 5 grados por frame
                this.anguloRotacion += Math.toRadians(5);
                // Recalcula las componentes de velocidad para que la bala "gire"
                this.vx = Math.cos(this.anguloRotacion) * this.velocidadActual;
                this.vy = Math.sin(this.anguloRotacion) * this.velocidadActual;
                break;

            case TIPO_ACELERADORA:
                // La bala acelera un 2% cada frame
                this.velocidadActual *= 1.00;
                // Si la velocidad actual es muy pequeña, evitamos división por cero
                if (this.velocidadActual < 0.1) {
                    this.velocidadActual = 0.1; // Mínima velocidad
                }
                // Normaliza dirección actual y aplica la nueva velocidad
                double magnitudActual = Math.sqrt(vx * vx + vy * vy);
                if (magnitudActual != 0) {
                    this.vx = (vx / magnitudActual) * this.velocidadActual;
                    this.vy = (vy / magnitudActual) * this.velocidadActual;
                } else {
                    // Si  no se movía, empieza a acelerar
                    this.vx = Math.cos(this.anguloRotacion) * this.velocidadActual;
                    this.vy = Math.sin(this.anguloRotacion) * this.velocidadActual;
                }
                break;

            case TIPO_NORMAL:
            default:
                // Las balas normales solo siguen su trayectoria inicial (vx, vy)
                break;
        }
    }

    //proyectil en el entorno.
     
    public void dibujar(Entorno entorno) {
        if (imagenBala != null) {
            // Dibuja la imagen del proyectil 
        
            entorno.dibujarImagen(imagenBala, x, y, anguloRotacion, 0.19); // Escala el 0.1
        } else {
            // Dibuja un círculo rojo 
            entorno.dibujarCirculo(x, y, 6, java.awt.Color.RED);
        }
    }

    // Verifica si el proyectil está fuera de los límites de la pantalla.
     
     
     
    public boolean estaFueraDePantalla() {
        
        return x < -20 || x > 1100 || y < -20 || y > 740;
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }

   
    public int getTipoBala() { return tipoBala; }
}