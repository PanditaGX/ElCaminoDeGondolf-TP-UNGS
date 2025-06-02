package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class GestorDePantallas {

    private String pantallaActual;
    private Image IconoSalir;
    private Image IconoEnter;
    private Image imgMenuPrincipal;
    private Image  imgMenuSecundaria;
    private Image imgTransicionNivel2;
    private Image imgTransicionNivel3;
    private Image imgTransicionJefeFinal;
    private Image imgVictoria; // Agregamos la imagen de victoria
    private Image imgDerrota;  // Agregamos la imagen de derrota

    public GestorDePantallas() {
        this.pantallaActual = "menu"; // pantalla inicial por defecto
        this.imgMenuPrincipal = Herramientas.cargarImagen("juego/img/Scene/Menu_De_Inicio/menu2_principal_fondo.png");
        this.IconoSalir = Herramientas.cargarImagen("juego/img/Scene/Menu_De_Inicio/salir.png");
        this.IconoEnter = Herramientas.cargarImagen("juego/img/Scene/Menu_De_Inicio/Preciona_entender.png");
     
        this.imgMenuSecundaria = Herramientas.cargarImagen("juego/img/Scene/Menu_De_Inicio/el_camino_de_gondolf_v2.png");
     
    
        this.imgTransicionNivel2 = Herramientas.cargarImagen("juego/img/Scene/Transiciones/nivel2.png");
        this.imgTransicionNivel3 = Herramientas.cargarImagen("juego/img/Scene/Transiciones/nivel2.png");
        this.imgTransicionJefeFinal = Herramientas.cargarImagen("juego/img/Scene/Transiciones/SiguienteNivel.png");
        this.imgVictoria = Herramientas.cargarImagen("juego/img/Scene/Transiciones/victory.png"); 
        this.imgDerrota = Herramientas.cargarImagen("juego/img/Scene/Transiciones/Derrota.png");   
        
    }

    public void cambiarPantalla(String nuevaPantalla) {
        this.pantallaActual = nuevaPantalla;
    }

    public String getPantallaActual() {
        return pantallaActual;
    }

    public void dibujar(Entorno entorno) {
        switch (pantallaActual) {
            case "menu":
                dibujarMenu(entorno);
                break;
           
            case "nivel2":
                dibujarTransicion(entorno, imgTransicionNivel2, "Nivel 2");
                break;
            case "nivel3":
                dibujarTransicion(entorno, imgTransicionNivel3, "Nivel 3");
                break;
            case "jefeFinal":
                dibujarTransicion(entorno, imgTransicionJefeFinal, "¡Jefe Final!");
                break;
            case "victoria":
                dibujarVictoria(entorno);
                break;
            case "derrota":
                dibujarDerrota(entorno);
                break;
            default: 
                break;
        }
    }

    private void dibujarMenu(Entorno entorno) {
        entorno.dibujarImagen(imgMenuPrincipal, entorno.ancho() / 2, entorno.alto() / 2, 0, 1.0);
        entorno.dibujarImagen(imgMenuSecundaria, entorno.ancho() / 2, entorno.alto() / 5, 0, 0.5);
        entorno.dibujarImagen(IconoSalir, entorno.ancho() / 1.090, entorno.alto() / 1.050, 0, 0.2);
        entorno.dibujarImagen(IconoEnter, entorno.ancho() /1.29 , entorno.alto() / 2 ,0 , 0.2);
    }

    
    private void dibujarTransicion(Entorno entorno, Image imagenTransicion, String mensajeNivel) {
        entorno.dibujarImagen(imagenTransicion,entorno.ancho() / 2, entorno.alto() / 2, 0, 1.0);
        
    }

    private void dibujarVictoria(Entorno entorno) {
        entorno.dibujarImagen(imgVictoria, entorno.ancho() / 2, entorno.alto() / 2, 0, 1.0);
        

    }

    private void dibujarDerrota(Entorno entorno) {
        entorno.dibujarImagen(imgDerrota, entorno.ancho() / 2, entorno.alto() / 2, 0, 1.0);
        
    }
}