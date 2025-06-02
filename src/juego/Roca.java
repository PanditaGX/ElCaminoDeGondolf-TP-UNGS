package juego; // Indica en qué "carpeta" está guardado este archivo.

import java.awt.Image; // Esto me permite usar imágenes en el juego.
import entorno.Entorno; // Una clase que me ayuda a manejar la pantalla y cosas del juego.
import entorno.Herramientas; // Acá hay herramientas útiles, como para cargar imágenes.
import java.awt.Color;


public class Roca { 
    private int ancho, alto; // Cada roca va a tener su propio ancho y alto.
    private double x; // Guarda la posición horizontal de la roca.
    double y; // Guarda la posición vertical de la roca.
    private double angulo; // Por si quiero rotar la roca, guardo el ángulo acá.
    private Image imagen; // Acá voy a guardar la imagen que se va a mostrar de la roca.

    public Roca(double x, double y, String archivo) { // Este es el "constructor", lo que se ejecuta cuando creo una nueva roca.
        this.x = x; // Le digo que la posición horizontal de esta roca es la que me pasaron.
        this.y = y; // Igual, pero para la posición vertical.
        this.ancho = 64; // Todas las rocas empiezan con este ancho. Tiene que ser de las mismas dimensiones que la grilla para evitar falsa colision
        this.alto = 64; // Y con esta altura. aca igual
        this.imagen = Herramientas.cargarImagen(archivo); // Acá cargo la imagen de la roca desde un archivo.
        this.angulo = 0; // Al principio, la roca no está rotada.
    }
    
   

    public void dibujar(Entorno entorno) { // Este método se encarga de mostrar la roca en la pantalla.
        entorno.dibujarImagen(imagen, x, y, angulo); // Le digo al "entorno" que dibuje la imagen de la roca en su posición y con su ángulo.
    }

    public boolean colisionaCon(Gondolf g) { // método me dice si la roca chocó con gondolf 
        double izquierdaRoca = this.x - this.ancho / 2; // Calculan los bordes ancho y alto
        double derechaRoca = this.x + this.ancho / 2; 
        double arribaRoca = this.y - this.alto / 2;  
        double abajoRoca = this.y + this.alto / 2;
        double izquierdaGondolf = g.getX() - g.getAncho() / 2; // Igual, calculo los bordes
        double derechaGondolf = g.getX() + g.getAncho() / 2;
        double arribaGondolf = g.getY() - g.getAlto() / 2;
        double abajoGondolf = g.getY() + g.getAlto() / 2;

        boolean colisionX = derechaRoca > izquierdaGondolf && izquierdaRoca < derechaGondolf; // Miro si los bordes horizontales se superponen.
        boolean colisionY = abajoRoca > arribaGondolf && arribaRoca < abajoGondolf; // Miro si los bordes verticales se superponen.

        return colisionX && colisionY; // ¡Solo hay choque si se superponen en ambos sentidos!
    }

    // Getters (para poder preguntar desde afuera cuál es el ancho, alto, etc. de la roca)
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public double getX() { return x; }
    public double getY() { return y; }
    
    

    private static double distancia(double x1, double y1, double x2, double y2) { // Una función para calcular la distancia entre dos puntos.
        return Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }
    private static int indicePosicion = 0;

 //Genero 10 piedras en lugares especificos lejos de gondolf
 public static double[] generarPosicionSegura(double evitarX, double evitarY, double radioEvitar) {
     int intentos = 0;
     while (intentos < 10) {
         double x = 0;
         double y = 0;

         switch (indicePosicion) {
             case 0: x = 100; y = 100; break;
             case 1: x = 200; y = 150; break;
             case 2: x = 300; y = 200; break;
             case 3: x = 400; y = 250; break;
             case 4: x = 500; y = 300; break;
             case 5: x = 600; y = 350; break;
             case 6: x = 700; y = 400; break;
             case 7: x = 800; y = 450; break;
             case 8: x = 900; y = 500; break;
             case 9: x = 1000; y = 550; break;
         }

         indicePosicion = (indicePosicion + 1) % 10; // Avanza al siguiente

         if (distancia(x, y, evitarX, evitarY) >= radioEvitar) {
             return new double[]{x, y};
         }

         intentos++;
     }

     // Si ninguna posición es segura, devuelve la primera
     return new double[]{100, 100};
 }
 


}