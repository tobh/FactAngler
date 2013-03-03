package org.mylan.openie.utils;

/**
 * Describe class Worker here.
 *
 *
 * Created: Thu Feb 14 16:51:33 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class Worker {
    private final String[] commands;

    public Worker() {
        this("*");
    }

    public Worker(final String ... commands) {
        this.commands = commands;
    }

    public void inform(final String input) {
        for (String command : commands) {
            if (input.equals(command) || command.equals("*")) {
                execute(input);
                break;
            }
        }
    }

    protected abstract void execute(final String input);
}
