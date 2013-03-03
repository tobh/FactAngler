package org.mylan.openie.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mylan.openie.utils.Property;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Describe class BerkeleyParser here.
 *
 *
 * Created: Thu Nov 29 20:23:58 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */

public class BerkeleyParser {
    private static final Pattern componentPattern = Pattern.compile("\\)|\\([^\\(\\)]* [^\\(\\)]*");
    private static final Logger LOGGER = Logger.getLogger(BerkeleyParser.class);

    private final String berkeleyParsedFile;
    private final String parserCommand;

    public BerkeleyParser() {
        Properties properties = Property.create("analyse.properties");
        berkeleyParsedFile = properties.getProperty("parsedFile");
        parserCommand = properties.getProperty("parserCommand");
    }

    public List<PosComponent> parseFile() {
        List<PosComponent> sentences = new LinkedList<PosComponent>();

        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new FileInputStream(berkeleyParsedFile));

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.contains("( (NP (NP (JJ First) (NNS sentences)) (PP (IN of) (") &&
                    !line.equals("(())")) {
                    sentences.add(parseSyntaxTree(line));
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not open file with berkeley parsed sentences: " + berkeleyParsedFile);
            throw new RuntimeException();
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }

        return sentences;
    }

    public PosComponent parseSyntaxTree(final String parsedSentence) {
        PosComponent currentComposite = null;
        Scanner scanner = new Scanner(parsedSentence);
        String component = scanner.findInLine(componentPattern);
        while (component != null) {
            if (component.contains("(")) {
               PosComponent newComponent = buildPosComponent(component);
               if (currentComposite != null) {
                   currentComposite.add(newComponent);
               }
               currentComposite = newComponent;
            } else if (component.contains(")")) {
                if (!currentComposite.isRoot()) {
                    currentComposite = currentComposite.getParent();
                }
            } else {
                System.out.println("Unexpected condition: " + component);
            }
            component = scanner.findInLine(componentPattern);
        }
        return currentComposite;
    }

    private PosComponent buildPosComponent(final String component) {
        int whitespace = component.indexOf(" ");
        String posTag = component.substring(1, whitespace);

        PosComponent pos = null;
        if (whitespace + 1 < component.length()) {
            String token = component.substring(whitespace + 1, component.length());
            token = token.replaceAll("-LRB-", "(");
            token = token.replaceAll("\\*LRB\\*", "(");
            token = token.replaceAll("\\$\\*LRB\\*", "(");
            token = token.replaceAll("-RRB-", ")");
            token = token.replaceAll("\\*RRB\\*", ")");
            token = token.replaceAll("\\$\\*RRB\\*", ")");
            pos = new PosTag(posTag, token);
        } else {
            pos = new PosComposite(posTag);
        }
        return pos;
    }

    public String generateSyntaxTree(final String sentence) {
        String parsedSentence = "";
        try {
            Process process = Runtime.getRuntime().exec(parserCommand);

            OutputStream out = process.getOutputStream();
            out.write(sentence.getBytes());
            out.flush();
            out.close();
            process.waitFor();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error = "";
            if((error = stderr.readLine()) != null) {
                LOGGER.error("Specified parser produces the following error: " + error);
            }

            BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String current = "";
            while((current = stdin.readLine()) != null) {
                parsedSentence = current;
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't parse sentence: " + sentence);
        } catch (InterruptedException e) {
            // not useful
        }
        return parsedSentence;
    }
}
