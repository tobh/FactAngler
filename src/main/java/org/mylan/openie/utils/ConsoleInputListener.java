package org.mylan.openie.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Describe class ConsoleInput here.
 *
 *
 * Created: Thu Feb 14 16:32:13 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ConsoleInputListener {
    private final List<Worker> workers;
    private final String inputReady;

    public ConsoleInputListener() {
        this("Ready");
    }

    public ConsoleInputListener(final String inputReady) {
        workers = new ArrayList<Worker>();
        this.inputReady = inputReady + "\n'quit' to exit";
    }

    public void add(final Worker ... workers) {
        for (Worker worker : workers) {
            this.workers.add(worker);
        }
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        System.out.println(inputReady);
        while (true) {
            if (in.hasNextLine()) {
                String command = in.nextLine();
                if (command.equals("quit")) {
                    break;
                }
                for (Worker worker : workers) {
                    worker.inform(command);
                }
                System.out.println(inputReady);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        in.close();
    }
}
