/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/**
 *
 * @author Gino
 */



import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utilidad estilo "setInterval" (similar a JavaScript) 
 */
public class SetInterval {

    // scheduler único para toda la aplicación
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Programa la ejecución periódica de un callback.
     *
     * @param tarea         función (callback) que se ejecutará periódicamente
     * @param delayInicial  tiempo a esperar antes de la primera ejecución
     * @param intervalo     intervalo fijo entre ejecuciones consecutivas
     * @param unidadTiempo  unidad de tiempo para delayInicial e intervalo
     * @return              objeto ScheduledFuture que permite cancelar la tarea
     */
    public static ScheduledFuture<?> setInterval(Runnable tarea,
                                                  long delayInicial,
                                                  long intervalo,
                                                  TimeUnit unidadTiempo) {
        return scheduler.scheduleAtFixedRate(tarea, delayInicial, intervalo, unidadTiempo);
    }

    /**
     * Detiene el scheduler de forma ordenada.
     * Llamar cuando no se necesite más programar tareas.
     */
    public static void shutdown() {
        scheduler.shutdown();
    }
}
