package co.edu.uniquindio.poo.notification.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el patrón Command para invocar comandos.
 */
public class NotificationInvoker {
    private List<NotificationCommand> commands = new ArrayList<>();
    
    public void addCommand(NotificationCommand command) {
        commands.add(command);
    }
    
    public void executeCommands() {
        for (NotificationCommand command : new ArrayList<>(commands)) {
            command.execute();
        }
        commands.clear();
    }
}
