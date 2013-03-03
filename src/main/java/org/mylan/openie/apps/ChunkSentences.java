package org.mylan.openie.apps;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.mylan.openie.nlp.BrownPosTagger;

/**
 * Tokenizes the inputFile and writes the tokenized String to outputFile
 *
 *
 * Created: Fri Jan 18 15:23:14 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ChunkSentences {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: ChunkSentence inputFile outputFile");
            System.exit(1);
        }

        StringBuilder input = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(args[0]).getAbsoluteFile()));
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    input.append(line);
                    input.append("\n");
                }
            }
            finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder output = new StringBuilder();
        BrownPosTagger tagger = new BrownPosTagger();
        Scanner scanner = new Scanner(input.toString());
        while (scanner.hasNextLine()) {
            String[] tokens = tagger.tokenize(scanner.nextLine());
            for (String token : tokens) {
                output.append(token);
                output.append(" ");
            }
            if (tokens.length > 0) {
                output.deleteCharAt(output.length() - 1);
            }
            output.append("\n");
        }

        try {
            PrintWriter out = new PrintWriter(new File(args[1]).getAbsoluteFile());
            try {
                out.print(output);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
