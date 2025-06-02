package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Juego extends InterfaceJuego {
    private Entorno entorno;
    private GestorDePantallas gestorPantallas;
    private boolean juegoTerminado = false;
    private Gondolf gondolf;
    private TiposEnemigos[] enemigos;
    private int maxEnemigosVivos = 7;
    private int[] enemigosPorNivel = {30,55,85}; // Cantidad de enemigos a eliminar por nivel
    private int nivelActual = 0; // 0 para el nivel 1, 1 para el nivel 2, etc.
    private int enemigosEliminadosNivelActual = 0; // Contador de enemigos eliminados en el nivel actual
    private Fondo fondoJuego;
    private Musica musicaMenu;
    private Musica musicaNivel1;
    private Musica musicaNivel2;
    private Musica musicaNivel3;
    private Musica musicaJefeFinal;
    private Musica musicaVictoria;
    private Musica musicaDerrota;
    private Roca[] rocas = new Roca[12];
    private Menu menu;
    private PoderesCopados hechizo1;
    private PoderesCopados hechizo2;
    private PoderesCopados hechizo3;
    private ArrayList<Proyectil> proyectiles = new ArrayList<>();
    private double vidaInicialEnemigos = 5.0;
    private JefeFinal jefeFinal = null;
    private boolean jefeDerrotado = false;
    private int  dañojefe = 3;
    private boolean esperandoInputParaSiguienteNivel = false;

    public Juego() {
        this.entorno = new Entorno(this, "El camino de Gondolf - TP", 1280, 720);
        this.gestorPantallas = new GestorDePantallas();
        this.fondoJuego = new Fondo(entorno.ancho() / 2.3, entorno.alto() / 2, 0.99);
        this.gondolf = new Gondolf(540, 360, "juego/img/Gondolf/Gondolf-Estatico-Derecha.gif");
        this.enemigos = new TiposEnemigos[maxEnemigosVivos];
        this.menu = new Menu(1080, 0, 200, 720, "juego/img/Scene/Menu_De_Juego/menu.png");
        this.musicaMenu = new Musica("juego/music/Menu.wav");
        this.musicaNivel1 = new Musica("juego/music/level1.wav");
        this.musicaNivel2 = new Musica("juego/music/level2.wav");
        this.musicaNivel3 = new Musica("juego/music/level3.wav");
        this.musicaJefeFinal = new Musica("juego/music/boss_theme.wav");
        this.musicaVictoria = new Musica("juego/music/victory.wav");
        this.musicaDerrota = new Musica("juego/music/gameover.wav");
        //Despues del nombre del disparo se maneja el el costo , lo segundo el radio y el tercero el daño
        this.hechizo1 = new PoderesCopados("Disparo", 1, 0, 2, Color.CYAN, 1180, 240, true, true, "juego/img/skills/skill1.png");
        this.hechizo2 = new PoderesCopados("Fuego", 0, 100, 10, Color.ORANGE, 1180, 300, false, false, "juego/img/skills/fuego.png");
        this.hechizo3 = new PoderesCopados("Explosion", 25, 150, 40, Color.MAGENTA, 1180, 360, false, false, "juego/img/skills/explosion.png");
        
        this.hechizo1.seleccionar();
        this.entorno.iniciar();
        
        
        GrillaDeRoca generador = new GrillaDeRoca(1080, 720, 64, rocas.length, 30, 64);
        ArrayList<Coordenada> coords = generador.getCoordenadas();

        for (int i = 0; i < rocas.length; i++) {
            Coordenada c = coords.get(i);
            rocas[i] = new Roca(c.getX(), c.getY(), "juego/img/Scene/Rocks/roca64.png");
        }
    }

    public void tick() {
        String pantalla = gestorPantallas.getPantallaActual();
        
        if (esperandoInputParaSiguienteNivel) {
            gestorPantallas.dibujar(entorno); 

            if (entorno.estaPresionada(entorno.TECLA_ENTER)) {
                esperandoInputParaSiguienteNivel = false;
                
                switch (nivelActual) {
                    case 1 -> {
                        // Después de Nivel 1 (para Nivel 2)
                        musicaNivel2.play(true);
                        this.fondoJuego.setFondo("nivel2");
                    }
                    case 2 -> {
                        // Después de Nivel 2 (para Nivel 3)
                        musicaNivel3.play(true);
                        this.fondoJuego.setFondo("nivel3");
                    }
                    case 3 -> {
                        musicaJefeFinal.play(true);
                        this.fondoJuego.setFondo("jefeFinal");
                    }
                    case 0 -> {
                        // Si venimos de la pantalla de inicio después de reiniciar
                        esperandoInputParaSiguienteNivel = false;
                        musicaNivel1.play(true);
                        this.fondoJuego.setFondo("nivel1");
                    }
                    default -> {
                    }
                }
            }
            return; // No ejecutar la lógica del juego mientras esperamos el input
        }

        switch (pantalla) {
            case "menu" -> {
                gestorPantallas.dibujar(entorno);
                musicaMenu.play(true);

                if (entorno.estaPresionada(entorno.TECLA_ENTER)) {
                    gestorPantallas.cambiarPantalla("juego"); // Cambia directamente a la pantalla de juego
                    detenerTodaMusica();
                    musicaNivel1.play(true); // Inicia la música del Nivel 1 inmediatamente
                    this.fondoJuego.setFondo("nivel1"); // Establece el fondo del Nivel 1
                } else if (entorno.estaPresionada('E')) {
                    System.exit(0);
                }
                return;
            }

            case "victoria" -> {
                gestorPantallas.dibujar(entorno);
                
                musicaVictoria.play(false); // Reproduce una vez
                if (entorno.estaPresionada('R')) {
                    reiniciarJuego();
                    gestorPantallas.cambiarPantalla("juego");
                    
                    musicaVictoria.stop();
                    musicaNivel1.play(true);
                } else if (entorno.estaPresionada('E')) {
                    System.exit(0);
                }
                return;
            }

            case "derrota" -> {
                gestorPantallas.dibujar(entorno);
              
                musicaDerrota.play(false); // Reproduce una vez
                if (entorno.estaPresionada('R')) {
                    reiniciarJuego();
                    gestorPantallas.cambiarPantalla("juego");
                   
                    musicaDerrota.stop();
                    musicaNivel1.play(true);
                } else if (entorno.estaPresionada('E')) {
                    System.exit(0);
                }
                return;
            }
            
        }

        // --- logica del juego principal ---
       
        if (!juegoTerminado) {
            this.fondoJuego.dibujar(this.entorno);
            double prevX = gondolf.x;
            double prevY = gondolf.y;

            gondolf.actualizar(this.entorno);
            if (nivelActual == enemigosPorNivel.length && jefeFinal != null) {
                jefeFinal.aplicarBarreraAGondolf(gondolf);
            }
            
            gondolf.regenerarVida(); // regenera vida a partir de 20
            gondolf.regenerarMana(); // regenera mana a partir de 20
            
             if(gondolf.getEnergiaMagica() > 100){               
                SetInterval.setInterval(() -> gondolf.recargarEnergia(7), 0, 3, TimeUnit.SECONDS);
            }
            
            
            if (nivelActual < enemigosPorNivel.length) {
                for (Roca roca : rocas) {
                    roca.dibujar(this.entorno);
                    if (roca.colisionaCon(gondolf)) {
                        gondolf.x = prevX;
                        gondolf.y = prevY;
                    }
                }
            }
            gondolf.dibujar(this.entorno);

            double vidaEnemigoActual = vidaInicialEnemigos + (nivelActual * 2);

            // Generación de nuevos enemigos
            
            if (!esperandoInputParaSiguienteNivel && nivelActual < enemigosPorNivel.length) {
                for (int i = 0; i < enemigos.length; i++) {
                    if (enemigos[i] == null && enemigosEliminadosNivelActual + TiposEnemigos.contarEnemigosVivos(enemigos) < enemigosPorNivel[nivelActual]) {
                        enemigos[i] = TiposEnemigos.generarEnemigoDesdeBorde(entorno.ancho(), entorno.alto(), "juego/img/Enemigos/Bat/Bat_Fly_0.png", vidaEnemigoActual);
                    }
                }
            }


            for (int i = 0; i < enemigos.length; i++) {
                TiposEnemigos enemigo = enemigos[i];
                if (enemigo != null) {
                    enemigo.moverHacia(gondolf);
                    // *** APLICAR SEPARACIÓN DE OTROS ENEMIGOS ***
                    enemigo.separarDeEnemigos(enemigos); // Le pasas el array completo de enemigos
                    
                    enemigo.dibujar(entorno);
                    if (enemigo.colisionaCon(gondolf)) {
                        gondolf.restarVida(5);
                        enemigos[i] = null;
                        enemigosEliminadosNivelActual++;
                        if (gondolf.getVida() <= 0) {
                            gestorPantallas.cambiarPantalla("derrota");
                            detenerTodaMusica();
                            musicaDerrota.play(false);
                            return;
                        }
                    }
                }
            }

            // logica para el avance de nivel
            if (nivelActual < enemigosPorNivel.length && enemigosEliminadosNivelActual >= enemigosPorNivel[nivelActual]) {
                nivelActual++;
                enemigosEliminadosNivelActual = 0;
              
                esperandoInputParaSiguienteNivel = true;
                detenerTodaMusica(); // Detener la música actual}

                this.gondolf = new Gondolf(540, 360, "juego/img/Gondolf/Gondolf-Estatico-Derecha.gif"); //lo puse para que siempre este en el centro pero tambien reincia la vida.
                switch (nivelActual) {
                    case 1 -> // Esto NO se ejecutará aquí si solo hay 3 niveles + jefe.
                        gestorPantallas.cambiarPantalla("nivel2");
                    case 2 -> // Para Nivel 3
                        gestorPantallas.cambiarPantalla("nivel3");
                    case 3 -> // Para Jefe Final
                        gestorPantallas.cambiarPantalla("jefeFinal");
                    default -> {
                    }
                }

                // Eliminar enemigos existentes para el nuevo nivel, antes de la pausa
                for (int i = 0; i < enemigos.length; i++) {
                    enemigos[i] = null;
                }
                proyectiles.clear(); // Limpiar proyectiles también
                return; // Importante: salir del tick para que la pantalla de transición se muestre y espere input
            }


            // Lógica del jefe final (solo si se está en el nivel del jefe final y no en transición)
            if (nivelActual == enemigosPorNivel.length && !esperandoInputParaSiguienteNivel) {
                if (jefeFinal == null) {
                    jefeFinal = new JefeFinal("juego/img/Enemigos/Jefefinal/Jefe.gif");
                }

                if (jefeFinal.estaVivo()) {
                    jefeFinal.actualizarYDisparar(gondolf);
                    jefeFinal.dibujar(entorno);

                    if (jefeFinal.colisionaCon(gondolf)) {
                        gondolf.restarVida(20);
                        if (gondolf.getVida() <= 0) {
                            juegoTerminado = true;
                            gestorPantallas.cambiarPantalla("derrota");
                            detenerTodaMusica();
                            musicaDerrota.play(false);
                            return;
                        }
                    }

                    for (int i = 0; i < proyectiles.size(); i++) {
                        Proyectil p = proyectiles.get(i);
                        if (jefeFinal.colisionaCon(p)) {
                            jefeFinal.restarVida(hechizo1.getDaño());
                            proyectiles.remove(i);
                            i--;
                        }
                    }

                    ArrayList<ProyectilEnemigo> proyectilesJefe = jefeFinal.getProyectiles();
                    for (int i = 0; i < proyectilesJefe.size(); i++) {
                        ProyectilEnemigo pe = proyectilesJefe.get(i);
                        if (gondolf.colisionaCon(pe)) {
                            gondolf.restarVida(dañojefe);
                            proyectilesJefe.remove(i);
                            i--;
                            if (gondolf.getVida() <= 0) {
                                juegoTerminado = true;
                                gestorPantallas.cambiarPantalla("derrota");
                                detenerTodaMusica();
                                musicaDerrota.play(true);
                                return;
                            }
                        } else if (pe.estaFueraDePantalla()) {
                            proyectilesJefe.remove(i);
                            i--;
                        }
                    }

                    if (!jefeFinal.estaVivo()) {
                        jefeDerrotado = true;
                    }
                }
            }


            for (int i = 0; i < proyectiles.size(); i++) {
                Proyectil p = proyectiles.get(i);
                p.mover();
                p.dibujar(entorno);

                for (int j = 0; j < enemigos.length; j++) {
                    if (enemigos[j] != null && p.colisionaCon(enemigos[j])) {
                        enemigos[j].recibirDaño(hechizo1.getDaño());
                        if (!enemigos[j].estaVivo()) {
                            enemigos[j] = null;
                            enemigosEliminadosNivelActual++;
                        }
                        proyectiles.remove(i);
                        i--;
                        break;
                    }
                }

                if (i >= 0 && i < proyectiles.size() && proyectiles.get(i).fueraDePantalla()) {
                    proyectiles.remove(i);
                    i--;
                }
            }

            if (jefeDerrotado) {
                juegoTerminado = true;
                gestorPantallas.cambiarPantalla("victoria");
                detenerTodaMusica();
                musicaVictoria.play(false);
                this.jefeFinal = null;
                return;
            }


            menu.dibujar(entorno, gondolf, jefeFinal, enemigosEliminadosNivelActual, (nivelActual < enemigosPorNivel.length ? enemigosPorNivel[nivelActual] : -1), nivelActual);

            hechizo1.dibujarBoton(entorno);
            hechizo2.dibujarBoton(entorno);
            hechizo3.dibujarBoton(entorno);


            if (hechizo2.estaSeleccionado()) {
                double mouseX = entorno.mouseX();
                double mouseY = entorno.mouseY();
                if (mouseX < 1080) {
                    Image img = Herramientas.cargarImagen("juego/img/Gondolf/Ataques/spell.gif");
                    double escala = (double)hechizo2.getRadio() * 2 / 750.0;
                    entorno.dibujarImagen(img, mouseX, mouseY, 0, escala);
                }
            }

            if (hechizo3.estaSeleccionado()) {
                double mouseX = entorno.mouseX();
                double mouseY = entorno.mouseY();
                if (mouseX < 1080) {
                    Image img = Herramientas.cargarImagen("juego/img/Gondolf/Ataques/spell.gif");
                    double escala = (double)hechizo3.getRadio() * 2 / 750.0;
                    entorno.dibujarImagen(img, mouseX, mouseY, 0, escala);
                }
            }

            if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
                double mouseX = entorno.mouseX();
                double mouseY = entorno.mouseY();

                if (hechizo1.estaSobreBoton(mouseX, mouseY)) {
                    hechizo1.seleccionar();
                    hechizo2.deseleccionar();
                    hechizo3.deseleccionar();
                } else if (hechizo2.estaSobreBoton(mouseX, mouseY)) {
                    hechizo2.seleccionar();
                    hechizo1.deseleccionar();
                    hechizo3.deseleccionar();
                } else if (hechizo3.estaSobreBoton(mouseX, mouseY)) {
                    hechizo3.seleccionar();
                    hechizo1.deseleccionar();
                    hechizo2.deseleccionar();
                } else if (mouseX < 1080) {
                    PoderesCopados seleccionado = null;
                    if (hechizo1.estaSeleccionado()) seleccionado = hechizo1;
                    else if (hechizo2.estaSeleccionado()) seleccionado = hechizo2;
                    else if (hechizo3.estaSeleccionado()) seleccionado = hechizo3;

                    if (seleccionado != null && gondolf.getEnergiaMagica() >= seleccionado.getCosto()) {
                        seleccionado.lanzar(entorno, gondolf, enemigos, jefeFinal, mouseX, mouseY, proyectiles);
                        gondolf.restarEnergia(seleccionado.getCosto());

                        if (!seleccionado.getNombre().equals("Disparo")) {
                            for (int i = 0; i < enemigos.length; i++) {
                                if (enemigos[i] != null && !enemigos[i].estaVivo()) {
                                    enemigos[i] = null;
                                    enemigosEliminadosNivelActual++;
                                }
                            }
                        }

                        if (jefeFinal != null && !jefeFinal.estaVivo()) {
                            jefeDerrotado = true;
                        }

                        if (!seleccionado.esRepetible()) {
                            seleccionado.deseleccionar();
                        }
                    }
                }
            }
        }
    }


    private void detenerTodaMusica() {
        musicaMenu.stop();
        musicaNivel1.stop();
        musicaNivel2.stop();
        musicaNivel3.stop();
        musicaJefeFinal.stop();
        musicaVictoria.stop();
        musicaDerrota.stop();
    }

    private void reiniciarJuego() {
        this.gondolf = new Gondolf(540, 360, "juego/img/Gondolf/Gondolf-Estatico-Derecha.gif");
        this.enemigos = new TiposEnemigos[maxEnemigosVivos];
        this.enemigosEliminadosNivelActual = 0;
        this.nivelActual = 0; 
        this.juegoTerminado = false;
        this.hechizo1.seleccionar();
        this.hechizo2.deseleccionar();
        this.hechizo3.deseleccionar();
        this.proyectiles.clear();
        this.jefeDerrotado = false;
        this.jefeFinal = null;
        this.esperandoInputParaSiguienteNivel = false; 
        this.fondoJuego.setFondo("nivel1");
        detenerTodaMusica(); // Detener cualquier música residual
        
        GrillaDeRoca generador = new GrillaDeRoca(1080, 720, 64, rocas.length, 30, 64);
        ArrayList<Coordenada> coords = generador.getCoordenadas();

        for (int i = 0; i < rocas.length; i++) {
            Coordenada c = coords.get(i);
            rocas[i] = new Roca(c.getX(), c.getY(), "juego/img/Scene/Rocks/roca64.png");
        }
    }

    public static void main(String[] args) {
        new Juego();
    }
}