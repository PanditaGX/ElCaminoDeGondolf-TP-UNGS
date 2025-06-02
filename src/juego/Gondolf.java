package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
// Importamos la clase Rectangle para facilitar la gestión de la hitbox
import java.awt.Rectangle; 
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

public class Gondolf {
    private Image imagenDerecha;
    private Image imagenIzquierda;
    private boolean mirandoDerecha;
    private Image EstaticoDerecha;
    private Image EstaticoIzquierda;

   
    double x;
    double y;
    int ancho = 40;
    int alto = 40;
    private int vida;
    private int energiaMagica;
    private double velocidad = 4;
    private boolean seMueve = false;
    private Rectangle hitbox; 
    private int vidaMaxima = 100;
    private ScheduledFuture<?> regeneradorDeVida; // para julio, significa que estamos guardando una referencia al temporizador que creamos con SetInterval. Esa referencia nos permite cancelarlo más adelante
   //Es el objeto que devuelve el método scheduleAtFixedRate del ScheduledExecutorService. Nos permite:
   //Cancelar la tarea (.cancel(false))
   //Consultar si está cancelada (.isCancelled())
    
    private ScheduledFuture<?> regeneradorDeMana;

    public Gondolf(double xInicial, double yInicial, String archivo) {
        this.x = xInicial;
        this.y = yInicial;
        this.vida = 100;
        this.energiaMagica = 100;
        this.EstaticoDerecha = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Estatico-Derecha.gif");
        this.EstaticoIzquierda = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Estatico-Izquierda.gif");

        this.imagenDerecha = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Derecha.gif");
        this.imagenIzquierda = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Izquierda.gif");
        this.mirandoDerecha = true;
        
    
        // La hitbox se centrará en las coordenadas x, y de Gondolf
        this.hitbox = new Rectangle((int)(x - ancho / 2), (int)(y - alto / 2), ancho, alto);
        
    }

    private boolean estabaMoviendose = false;

    public void actualizar(Entorno entorno) {
        seMueve = false;
        //lógica de movimiento de Gondolf
        if (entorno.estaPresionada('W') || entorno.estaPresionada(entorno.TECLA_ARRIBA)) {
            if (y - velocidad - alto / 2 > 0) {
                y -= velocidad;
                seMueve = true;
            }
        }
        if (entorno.estaPresionada('S') || entorno.estaPresionada(entorno.TECLA_ABAJO)) {
            if (y + velocidad + alto / 2 < entorno.alto()) {
                y += velocidad;
                seMueve = true;
            }
        }
        if (entorno.estaPresionada('A') || entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {
            if (x - velocidad - ancho / 2 > 0) {
                x -= velocidad;
                mirandoDerecha = false;
                seMueve = true;
            }
        }
        if (entorno.estaPresionada('D') || entorno.estaPresionada(entorno.TECLA_DERECHA)) {
            if (x + velocidad + ancho / 2 < 1080) {
                x += velocidad;
                mirandoDerecha = true;
                seMueve = true;
            }
        }
        

        // Si antes estaba moviendose y ahora no, recarga el gif quieto para reiniciar animación
        if (estabaMoviendose && !seMueve) {
            if (mirandoDerecha) {
                EstaticoDerecha = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Estatico-Derecha.gif");
            } else {
                EstaticoIzquierda = Herramientas.cargarImagen("juego/img/Gondolf/Gondolf-Estatico-Izquierda.gif");
            }
        }

        estabaMoviendose = seMueve;

        
        // Se mueve junto con Gondolf
        this.hitbox.setLocation((int)(x - ancho / 2), (int)(y - alto / 2));
        
    }

    public void dibujar(Entorno entorno) {
        if (seMueve) {
            if (mirandoDerecha) {
                entorno.dibujarImagen(imagenDerecha, x, y, 0);
            } else {
                entorno.dibujarImagen(imagenIzquierda, x, y, 0);
            }
        } else {
            if (mirandoDerecha) {
                entorno.dibujarImagen(EstaticoDerecha, x, y, 0);
            } else {
                entorno.dibujarImagen(EstaticoIzquierda, x, y, 0);
            }
        }
            }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public int getVida() { return vida; }
    public void restarVida(int cantidad) {
        this.vida -= cantidad;
        if (this.vida < 0) this.vida = 0;
    }
    
    public void sumarVida(int cantidad) {
        this.vida += cantidad;
        if (this.vida > 100) this.vida = 100;
    }
   
    public void regenerarVida() {
        if (vida >= 20) return; // Solo inicia si la vida está baja
        if (regeneradorDeVida != null && !regeneradorDeVida.isCancelled()) return;

        regeneradorDeVida = SetInterval.setInterval(() -> {
            if (vida < vidaMaxima) {
                sumarVida(5);
            } else {
                System.out.println("Vida llena, deteniendo regeneración...");
                regeneradorDeVida.cancel(false);
                regeneradorDeVida = null;
            }
        }, 2, 3, TimeUnit.SECONDS);
    }
    

    

    public int getEnergiaMagica() { return energiaMagica; }
    public void restarEnergia(int cantidad) {
        this.energiaMagica -= cantidad;
        if (this.energiaMagica < 0) this.energiaMagica = 0;
    }

    public void recargarEnergia(int cantidad) {
        this.energiaMagica += cantidad;
        if (this.energiaMagica > 100) this.energiaMagica = 100;
    }
    
        public void regenerarMana(){
        if(energiaMagica >= 70) return;
        if(regeneradorDeMana != null && !regeneradorDeMana.isCancelled()) return;
        
        regeneradorDeMana = SetInterval.setInterval(() -> {
            if (energiaMagica < 100) {
                recargarEnergia(5);
            } else {
                System.out.println("Mana llena, deteniendo regeneración...");
                regeneradorDeMana.cancel(false);
                regeneradorDeMana = null;
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    //  Método de colisión para ProyectilEnemigo
    public boolean colisionaCon(ProyectilEnemigo pe) {
      
        // Crea un rectángulo temporal para el proyectil enemigo
        Rectangle proyectilHitbox = new Rectangle(
            (int)(pe.getX() - 3), 
            (int)(pe.getY() - 3), 
            6, // Ancho del proyectil
            6  // Alto del proyectil
        );

        // Comprueba si la hitbox de Gondolf se interseca con la hitbox del proyectil enemigo
        return this.hitbox.intersects(proyectilHitbox);
    }
   
}