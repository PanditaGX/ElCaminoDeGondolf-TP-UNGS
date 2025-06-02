package juego;

import entorno.Entorno;
import entorno.Herramientas;

import java.awt.Color; // Necesitamos importar Color para dibujar el rectángulo
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

public class JefeFinal {
    private double x, y;
    private int vida = 2500; // Vida del jefe.
    private Image imagen; // Imagen principal del jefe
    private ArrayList<ProyectilEnemigo> proyectiles; // Lista de proyectiles que el jefe ha lanzado
    private int tiempoDisparo; // Contador principal para el ritmo de los patrones
    private int subTiempoDisparo; // Contador secundario para el ritmo dentro de un patrón
    private int faseActual; // Fase del jefe
    private Random rand; // Generador de números aleatorios para patrones
    private int vidaMaxima = 2500;

    // imágenes para diferentes tipos de balas
    private Image imagenBalaAzul;
    private Image imagenBalaVerde;
    private Image imagenBalaRoja;

    // Variables de la Hitbox
    private double hitboxAncho;  // Ancho del rectángulo de colisión
    private double hitboxAlto;   // Alto del rectángulo de colisión
    private double hitboxOffsetX; // Desplazamiento X del centro de la imagen al centro del hitbox
    private double hitboxOffsetY; // Desplazamiento Y del centro de la imagen al centro del hitbox

    // Variables para ajustar el punto de disparo de los proyectiles
    private double disparoOffsetX; // Desplazamiento X del punto de disparo respecto al centro del jefe
    private double disparoOffsetY; // Desplazamiento Y del punto de disparo respecto al centro del jefe

    // --- NUEVAS VARIABLES PARA LA BARRERA DE MOVIMIENTO DE GONDOLF ---
    private double limiteIzquierdoGondolf;
    private double limiteDerechoGondolf;
    private double limiteSuperiorGondolf;
    private double limiteInferiorGondolf;


    public JefeFinal(String imagenPath) {
        this.rand = new Random();
        this.x =540; 
        this.y = 10;
        
        this.imagen = Herramientas.cargarImagen(imagenPath);
        this.proyectiles = new ArrayList<>();
        this.tiempoDisparo = 0;
        this.subTiempoDisparo = 0;
        this.faseActual = 1; // El jefe comienza en la Fase 1

        // imágenes para los diferentes tipos de proyectiles
        this.imagenBalaAzul = Herramientas.cargarImagen("juego/img/proyectil2.png");
        this.imagenBalaVerde = Herramientas.cargarImagen("juego/img/proyectil3.png");
        this.imagenBalaRoja = Herramientas.cargarImagen("juego/img/proyectil.png");

        // DEfinimos la Hitbox para que quede con la imagen.
        // Los valores 80 y 100 son un buen punto de partida, ajústalos según el tamaño visual de tu imagen
        // La escala de dibujo es 1.5, así que un tamaño visual de 80*1.5 = 120, 100*1.5 = 150.
        this.hitboxAncho = 80 * 1.5;
        this.hitboxAlto = 100 * 1.5;
        this.hitboxOffsetX = 0;   // Centrado horizontalmente
        this.hitboxOffsetY = 80;  // 80 píxeles por debajo del centro de la imagen (ajusta según tu jefe)

      
        //  mover el punto de origen de los proyectiles.
        this.disparoOffsetX = 50;
        this.disparoOffsetY = 20; //valor para ajustar el origen de los disparos

        // LÍMITES DE LA BARRERA PARA GONDOLF 
        this.limiteIzquierdoGondolf = 0;
        
        this.limiteDerechoGondolf = 1080; 
        this.limiteSuperiorGondolf = 0; 
        this.limiteSuperiorGondolf = 155; // Gondolf no puede subir más allá de Y=10
        this.limiteInferiorGondolf = 720; // Gondolf puede llegar hasta el borde inferior de la pantalla
    }

    public void dibujar(Entorno entorno) {
        // Dibuja la imagen principal del jefe
        entorno.dibujarImagen(imagen, x, y, 0, 1.5); // Escala el jefe su tamaño original




        // Dibuja y mueve todos los proyectiles del jefe
        ArrayList<ProyectilEnemigo> aEliminar = new ArrayList<>();
        for (ProyectilEnemigo p : proyectiles) {
            p.mover(); // Actualiza la posición y comportamiento del proyectil
            p.dibujar(entorno); // Dibuja el proyectil
            if (p.estaFueraDePantalla()) {
                aEliminar.add(p); // Marca los proyectiles fuera de pantalla para eliminar
            }
        }
        proyectiles.removeAll(aEliminar); // Elimina los proyectiles marcados
    }

    public void actualizarYDisparar(Gondolf gondolf) {
        // Lógica de transición de fases basada en la vida del jefe
        if (vida <= vidaMaxima * (double) 3/4 && faseActual == 1) { // Transición a Fase 2 (más difícil, por ejemplo, al 75% de vida)
            faseActual = 2;
            tiempoDisparo = 0; // Reinicia los contadores para la nueva fase
            subTiempoDisparo = 0;
            System.out.println("fase 2 activada");
        } else if (vida <= vidaMaxima * (double) 1/4 && faseActual == 2) { // Transición a Fase 3 (¡La fase Lunatic, por ejemplo, al 25% de vida!)
            faseActual = 3;
            tiempoDisparo = 0; // Reinicia los contadores para la nueva fase
            subTiempoDisparo = 0;
            System.out.println("fase 3 activada");
        }
        // añadir más fases aquí (e.g., vida <= X && faseActual == 3 para fase 4)

        tiempoDisparo++; // Incrementa el contador principal de tiempo
        subTiempoDisparo++; // Incrementa el contador secundario de tiempo

        // Elige el patrón de ataque según la fase actual
        switch (faseActual) {
            case 1:
                // Fase 1: Un patrón inicial, quizás más sencillo
                ataqueConoRapido(gondolf);
                // Añade otro patrón de apoyo si quieres
                if (tiempoDisparo % 180 == 0) { //cada x segundos, lanza una ráfaga simple
                    disparar(gondolf); // El disparo simple que apunta
                }
                break;
            case 2:
                // Fase 2: Más densa, combina patrones
                ataqueCircularExpansivo(gondolf);
                if (tiempoDisparo % 75 == 0) { // Un disparo que guía al jugador, más seguido
                    disparar(gondolf);
                }
                // Podrías añadir un tercer patrón aquí que se ejecute a un ritmo diferente
                break;
            case 3:
                // Fase 3: ¡La fase Lunatic! Patrones muy complejos y densos, a menudo simultáneos
                ataquePatronDobleEspiral(gondolf); // Espirales
                ataqueBalasRastreadorasConTrampa(gondolf); // Balas que persiguen y trampas
                // Puedes añadir otro patrón aquí para máxima densidad
                // ataqueOlaMortal(gondolf); // Ejemplo de un patrón aún más loco
                break;
            default:
                // Comportamiento por defecto si la fase no está definida
                if (tiempoDisparo >= 60) { // Dispara cada segundo
                    disparar(gondolf);
                    tiempoDisparo = 0;
                }
                break;
        }
    }


    // Patrón 1: Ráfaga rápida en forma de cono/abanico
    private void ataqueConoRapido(Gondolf gondolf) {
        int disparosPorRafaga = 7; // Incrementado de 6 a 7
        double dispersion = Math.toRadians(95); // Incrementado de 90 a 95
        double velocidadProyectil = 2.1; // Incrementado de 2.0 a 2.1

        if (tiempoDisparo % 40 == 0) { // Reducido de 45 a 40 (más frecuente)
            double anguloCentral = Math.atan2(gondolf.getY() - (y + disparoOffsetY), gondolf.getX() - (x + disparoOffsetX));

            for (int i = 0; i < disparosPorRafaga; i++) {
                double anguloProyectil = anguloCentral - dispersion / 2 + (dispersion / (disparosPorRafaga - 1)) * i;
                
                double vx = Math.cos(anguloProyectil) * velocidadProyectil;
                double vy = Math.sin(anguloProyectil) * velocidadProyectil;

                proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx, vy, ProyectilEnemigo.TIPO_NORMAL, imagenBalaAzul));
            }
        }
    }

    // Patrón 2: Ráfaga circular expansiva
    private void ataqueCircularExpansivo(Gondolf gondolf) {
        int numProyectilesCirculo = 14; // Incrementado de 12 a 14
        double velocidadBase = 3.1; // Incrementado de 3.0 a 3.1

        if (tiempoDisparo % 55 == 0) { // Reducido de 60 a 55 (más frecuente)
            for (int i = 0; i < numProyectilesCirculo; i++) {
                double angulo = (2 * Math.PI / numProyectilesCirculo) * i;
                double vx = Math.cos(angulo) * velocidadBase;
                double vy = Math.sin(angulo) * velocidadBase;

                proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx, vy, ProyectilEnemigo.TIPO_NORMAL, imagenBalaVerde));
            }
        }
    }

    // Patrón 3.1: Doble Espiral de Balas
    private void ataquePatronDobleEspiral(Gondolf gondolf) {
        double velocidadEspiral = 1.1; // Incrementado de 1.0 a 1.1
        double offsetAngulo = Math.toRadians(subTiempoDisparo * 1.6); // Ajustado de 1.5 a 1.6 (espiral más "apretada")

        if (subTiempoDisparo % 11 == 0) { // Reducido de 12 a 11 (más frecuente)
            double angulo1 = offsetAngulo;
            double vx1 = Math.cos(angulo1) * velocidadEspiral;
            double vy1 = Math.sin(angulo1) * velocidadEspiral;
            proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx1, vy1, ProyectilEnemigo.TIPO_NORMAL, imagenBalaAzul));

            double angulo2 = -offsetAngulo;
            double vx2 = Math.cos(angulo2) * velocidadEspiral;
            double vy2 = Math.sin(angulo2) * velocidadEspiral;
            proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx2, vy2, ProyectilEnemigo.TIPO_NORMAL, imagenBalaVerde));
        }
    }

    // Patrón 3.2: Balas Rastreadoras con Trampas de Expansión
    private void ataqueBalasRastreadorasConTrampa(Gondolf gondolf) {
        double velocidadRastreadora = 7.1; // Incrementado de 7.0 a 7.1
        int cantidadBalasTrampa = 7; // Incrementado de 6 a 7
        double velocidadTrampa = 6.1; // Incrementado de 6.0 a 6.1

        if (tiempoDisparo % 40 == 0) { // Reducido de 45 a 40 (más frecuente)
            double dx = gondolf.getX() - (x + disparoOffsetX);
            double dy = gondolf.getY() - (y + disparoOffsetY);
            double distancia = Math.sqrt(dx * dx + dy * dy);
            if (distancia == 0) return;

            double vx = (dx / distancia) * velocidadRastreadora;
            double vy = (dy / distancia) * velocidadRastreadora;

            proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx, vy, ProyectilEnemigo.TIPO_ACELERADORA, imagenBalaRoja));
        }

        if (tiempoDisparo % 95 == 0) { // Reducido de 100 a 95 (más frecuente)
            double spawnX = rand.nextInt(1080);
            double spawnY = rand.nextInt(720);

            for (int i = 0; i < cantidadBalasTrampa; i++) {
                double angulo = (2 * Math.PI / cantidadBalasTrampa) * i;
                double vx = Math.cos(angulo) * velocidadTrampa;
                double vy = Math.sin(angulo) * velocidadTrampa;
                proyectiles.add(new ProyectilEnemigo(spawnX, spawnY, vx, vy, ProyectilEnemigo.TIPO_NORMAL, imagenBalaVerde));
            }
        }
    }

    // Método de disparo simple
    private void disparar(Gondolf gondolf) {
        double dx = gondolf.getX() - (x + disparoOffsetX);
        double dy = gondolf.getY() - (y + disparoOffsetY);
        double distancia = Math.sqrt(dx * dx + dy * dy);

        if (distancia == 0) return;

        double velocidad = 4.1;  // Incrementado de 4.0 a 4.1
        double vx = (dx / distancia) * velocidad;
        double vy = (dy / distancia) * velocidad;

        proyectiles.add(new ProyectilEnemigo(x + disparoOffsetX, y + disparoOffsetY, vx, vy, ProyectilEnemigo.TIPO_NORMAL, imagenBalaAzul));
    }

    //OBTENER LOS BORDES DEL HITBOX
    public double getHitboxIzquierdo() {
        return x + hitboxOffsetX - hitboxAncho / 2;
    }

    public double getHitboxDerecho() {
        return x + hitboxOffsetX + hitboxAncho / 2;
    }

    public double getHitboxSuperior() {
        return y + hitboxOffsetY - hitboxAlto / 2;
    }

    public double getHitboxInferior() {
        return y + hitboxOffsetY + hitboxAlto / 2;
    }

    // Colisión del jefe con Gondolf
    public boolean colisionaCon(Gondolf g) {
        
        double gondolfAncho = 20; // Ancho de Gondolf
        double gondolfAlto = 20;  // Alto de Gondolf
        double gondolfLeft = g.getX() - gondolfAncho / 2;
        double gondolfRight = g.getX() + gondolfAncho / 2;
        double gondolfTop = g.getY() - gondolfAlto / 2;
        double gondolfBottom = g.getY() + gondolfAlto / 2;

        // Comprueba la superposición de los rectángulos
        return this.getHitboxIzquierdo() < gondolfRight &&
               this.getHitboxDerecho() > gondolfLeft &&
               this.getHitboxSuperior() < gondolfBottom &&
               this.getHitboxInferior() > gondolfTop;
    }

    // Colisión del jefe con un Proyectil
    public boolean colisionaCon(Proyectil p) {
        
        double proyectilRadio = 10; // Radio del proyectil, ajusta si es diferente

        double testX = p.getX();
        double testY = p.getY();

        if (p.getX() < this.getHitboxIzquierdo())      testX = this.getHitboxIzquierdo();
        else if (p.getX() > this.getHitboxDerecho()) testX = this.getHitboxDerecho();

        if (p.getY() < this.getHitboxSuperior())        testY = this.getHitboxSuperior();
        else if (p.getY() > this.getHitboxInferior()) testY = this.getHitboxInferior();

        // Calcula la distancia entre el punto más cercano y el centro del círculo del proyectil
        double distX = p.getX() - testX;
        double distY = p.getY() - testY;
        double distancia = Math.sqrt((distX * distX) + (distY * distY));

        return distancia <= proyectilRadio;
    }

    public void restarVida(int cantidad) {
        vida -= cantidad;
        if (vida < 0) {
            vida = 0;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public ArrayList<ProyectilEnemigo> getProyectiles() {
        return proyectiles;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getVida(){
        return this.vida;
    }

    // Métodos para ajustar el tamaño y offset del hitbox
    public void setHitboxSize(double ancho, double alto) {
        this.hitboxAncho = ancho;
        this.hitboxAlto = alto;
    }

    public void setHitboxOffset(double offsetX, double offsetY) {
        this.hitboxOffsetX = offsetX;
        this.hitboxOffsetY = offsetY;
    }

    // Métodos para ajustar el punto de origen de los disparos
    public void setDisparoOffset(double offsetX, double offsetY) {
        this.disparoOffsetX = offsetX;
        this.disparoOffsetY = offsetY;
    }

    // APLICAR LA BARRERA
    public void aplicarBarreraAGondolf(Gondolf gondolf) {
        double gondolfMitadAncho = 10; 
        double gondolfMitadAlto = 10;  

        // Limitar la posición X
        if (gondolf.getX() - gondolfMitadAncho < limiteIzquierdoGondolf) {
            gondolf.x = limiteIzquierdoGondolf + gondolfMitadAncho;
        }
        if (gondolf.getX() + gondolfMitadAncho > limiteDerechoGondolf) {
            gondolf.x = limiteDerechoGondolf - gondolfMitadAncho;
        }

        // Limitar la posición Y
        if (gondolf.getY() - gondolfMitadAlto < limiteSuperiorGondolf) {
            gondolf.y = limiteSuperiorGondolf + gondolfMitadAlto;
        }
        if (gondolf.getY() + gondolfMitadAlto > limiteInferiorGondolf) {
            gondolf.y = limiteInferiorGondolf - gondolfMitadAlto;
        }
    }

    // ---  GETTERS PARA LOS LÍMITES DE LA BARRERA ---
    public double getLimiteIzquierdoGondolf() {
        return limiteIzquierdoGondolf;
    }

    public double getLimiteDerechoGondolf() {
        return limiteDerechoGondolf;
    }

    public double getLimiteSuperiorGondolf() {
        return limiteSuperiorGondolf;
    }

    public double getLimiteInferiorGondolf() {
        return limiteInferiorGondolf;
    }
    
}