package juego;

import entorno.Herramientas;
import javax.sound.sampled.Clip;

public class Musica {
    private Clip clip;
    private boolean reproduciendo = false;

    public Musica(String archivo) {
        this.clip = Herramientas.cargarSonido(archivo);
    }

    public void play(boolean loop) {
        if (clip != null && !reproduciendo) {
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
            reproduciendo = true;
        }
    }

    public void stop() {
        if (clip != null && reproduciendo) {
            clip.stop();
            clip.setFramePosition(0);
            reproduciendo = false;
        }
    }

    public void cambiar(String nuevoArchivo, boolean loop) {
        stop();
        this.clip = Herramientas.cargarSonido(nuevoArchivo);
        play(loop);
    }
}
