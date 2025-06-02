package juego;

import entorno.Entorno;
import java.awt.Color;
import java.util.ArrayList; // Necesitamos ArrayList para proyectiles
import entorno.Herramientas;
import java.awt.Image;

public class PoderesCopados {
    private String nombre;
    private int costoMagia;
    private int radio; // Rango del hechizo AoE
    private int daño; // Cantidad de daño que inflige el hechizo
    private Color color;
    private double botonX;
    private double botonY;
    private boolean seleccionado;
    private boolean permanente; // No usado actualmente, pero útil para futuras expansiones
    private boolean repetible; // Si se puede lanzar varias veces sin reseleccionar
    private String rutaArchivo;

    // Constructor con el nuevo atributo de daño
    public PoderesCopados(String nombre, int costoMagia, int radio, int daño, Color color, double botonX, double botonY, boolean permanente, boolean repetible, String rutaArchivo) {
        this.nombre = nombre;
        this.costoMagia = costoMagia;
        this.radio = radio;
        this.daño = daño; // ACA INCIASMOS EL DAÑO PARA JUEGO:JAVA NO TOCAR
        this.color = color;
        this.botonX = botonX;
        this.botonY = botonY;
        this.permanente = permanente;
        this.repetible = repetible;
        this.seleccionado = false;
        this.rutaArchivo = rutaArchivo;
    }

    public void dibujarBoton(Entorno entorno) {
        Image imagenSkill = Herramientas.cargarImagen(rutaArchivo);
        if (rutaArchivo.isEmpty()){ // Usar isEmpty() es más robusto que equals("")
                Color fondo = seleccionado ? Color.YELLOW : Color.LIGHT_GRAY;
                entorno.dibujarRectangulo(botonX, botonY, 100, 40, 0, fondo);
        } else {
            // Ajusta la escala visual para indicar selección
            double escala = seleccionado ? 0.045 : 0.05;
            entorno.dibujarImagen(imagenSkill, botonX, botonY, 0, escala);
            // El texto del nombre o costo si lo deseas, aunque la imagen es suficiente
            // entorno.cambiarFont("Arial", 14, Color.BLACK);
            // entorno.escribirTexto(nombre, botonX - 30, botonY + 10);
        }
    }

    public boolean estaSobreBoton(double mx, double my) {
        // Usa constantes para la mitad del ancho y alto del botón
        final double HALF_WIDTH = 50;
        final double HALF_HEIGHT = 20;
        return (mx >= botonX - HALF_WIDTH && mx <= botonX + HALF_WIDTH &&
                my >= botonY - HALF_HEIGHT && my <= botonY + HALF_HEIGHT);
    }

    public void seleccionar() { this.seleccionado = true; }
    public void deseleccionar() { this.seleccionado = false; }
    public boolean estaSeleccionado() { return this.seleccionado; }

    public int getCosto() { return this.costoMagia; }
    public int getRadio() { return this.radio; } // Devuelve el radio (rango)
    public int getDaño() { return this.daño; } // Devuelve el daño
    public Color getColor() { return this.color; }
    public String getNombre() { return this.nombre; }
    public boolean esRepetible() { return this.repetible; }
    public boolean esPermanente() { return this.permanente; }
    
    // El método lanzar ahora toma el ArrayList de proyectiles directamente
    public void lanzar(Entorno entorno, Gondolf gondolf, TiposEnemigos[] enemigos, JefeFinal jefeFinal, double mouseX, double mouseY, ArrayList<Proyectil> proyectiles) {

        if (this.nombre.equals("Disparo")) {
            // El disparo solo crea un proyectil. Su daño se define en la clase Proyectil o al chocar.
            Proyectil nuevo = new Proyectil(gondolf.getX(), gondolf.getY(), mouseX, mouseY, "juego/img/Gondolf/Ataques/proyectil.gif");
            proyectiles.add(nuevo);
        } else if (this.nombre.equals("Fuego")) {
            // Dibujar el área de efecto para el "Fuego"
            entorno.dibujarCirculo(mouseX, mouseY, this.radio * 2, this.color); // El *2 es solo visual para hacerlo más grande

            // Aplicar daño a enemigos normales dentro del rango
            for (int i = 0; i < enemigos.length; i++) {
                if (enemigos[i] != null && afectaA(enemigos[i], mouseX, mouseY)) {
                    enemigos[i].recibirDaño(this.daño); // Usamos el atributo de daño del hechizo
                }
            }
            // Aplicar daño al Jefe Final si está vivo y dentro del rango
            if (jefeFinal != null && jefeFinal.estaVivo() && afectaA(jefeFinal, mouseX, mouseY)) {
                jefeFinal.restarVida(this.daño); // Usamos el atributo de daño del hechizo
            }

            // Si el hechizo no es repetible, se deselecciona después de un solo uso
            if (!this.esRepetible()) {
                this.deseleccionar();
            }
        } else if (this.nombre.equals("Explosion")) {
            // Dibujar el área de efecto para la "Explosión"
            entorno.dibujarCirculo(mouseX, mouseY, this.radio * 2, this.color); // El *2 es solo visual para hacerlo más grande

            // Aplicar daño a enemigos normales dentro del rango
            for (int i = 0; i < enemigos.length; i++) {
                if (enemigos[i] != null && afectaA(enemigos[i], mouseX, mouseY)) {
                    enemigos[i].recibirDaño(this.daño); // Usamos el atributo de daño del hechizo
                }
            }
            // Aplicar daño al Jefe Final si está vivo y dentro del rango
            if (jefeFinal != null && jefeFinal.estaVivo() && afectaA(jefeFinal, mouseX, mouseY)) {
                jefeFinal.restarVida(this.daño); // Usamos el atributo de daño del hechizo
            }

            // Si el hechizo no es repetible, se deselecciona después de un solo uso
            if (!this.esRepetible()) {
                this.deseleccionar();
            }
        }
    }
    
    // Método para determinar si un enemigo normal está dentro del rango del hechizo
    public boolean afectaA(TiposEnemigos enemigo, double centroAoEX, double centroAoEY) {
        double dx = enemigo.getX() - centroAoEX;
        double dy = enemigo.getY() - centroAoEY;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        // Colisión basada en el radio del hechizo y el radio del enemigo
        return distancia <= (this.radio + (enemigo.getRadio() * 2)); // Ajusta el radio del enemigo
    }

    // Método sobrecargado para determinar si el Jefe Final está dentro del rango del hechizo
    public boolean afectaA(JefeFinal jefe, double centroAoEX, double centroAoEY) {
        // Obtenemos las coordenadas del punto más cercano del hitbox del jefe al centro del AoE
        double closestX = Math.max(jefe.getHitboxIzquierdo(), Math.min(centroAoEX, jefe.getHitboxDerecho()));
        double closestY = Math.max(jefe.getHitboxSuperior(), Math.min(centroAoEY, jefe.getHitboxInferior()));

        // Calculamos la distancia entre el centro del AoE y el punto más cercano del hitbox del jefe
        double dx = centroAoEX - closestX;
        double dy = centroAoEY - closestY;
        double distancia = Math.sqrt((dx * dx) + (dy * dy));

        // Si la distancia es menor o igual al radio del hechizo, hay colisión
        return distancia <= this.radio;
    }

  
}