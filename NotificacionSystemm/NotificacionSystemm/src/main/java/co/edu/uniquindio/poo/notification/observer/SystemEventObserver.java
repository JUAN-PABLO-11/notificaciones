package co.edu.uniquindio.poo.notification.observer;

import co.edu.uniquindio.poo.notification.event.SystemEventData;

/**
 * Interfaz para observadores que responden a eventos tipados del sistema.
 */
public interface SystemEventObserver {
    /**
     * Método llamado cuando ocurre un evento del sistema.
     * 
     * @param eventData Datos del evento ocurrido
     */
    void onEvent(SystemEventData eventData);
}
