package vertgreen.commandmeta;

import vertgreen.commandmeta.abs.Command;

import java.util.HashMap;
import java.util.Set;

public class CommandRegistry {

    private static HashMap<String, CommandEntry> registry = new HashMap<>();

    public static void registerCommand(String name, Command command) {
        CommandEntry entry = new CommandEntry(command, name);
        registry.put(name, entry);
    }
    
    public static void registerAlias(String command, String alias) {
        registry.put(alias, registry.get(command));
    }

    public static CommandEntry getCommand(String name) {
        return registry.get(name);
    }

    public static int getSize() {
        return registry.size();
    }

    public static Set<String> getRegisteredCommandsAndAliases() {
        return registry.keySet();
    }

    public static class CommandEntry {

        public Command command;
        public String name;

        CommandEntry(Command command, String name) {
            this.command = command;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setCommand(Command command) {
            this.command = command;
        }
    }
}
