package org.mylan.openie.apps;

import java.net.MalformedURLException;
import java.util.Scanner;

import org.mylan.openie.corpus.WebPage;
import org.mylan.openie.relation.SinglePassRelationExtractor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Describe class ExtractRelationsFromWebPage here.
 *
 *
 * Created: Fri May  2 23:38:01 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ExtractRelationsFromWebPage {
    public static void main(String[] args) throws MalformedURLException {
        if (args.length != 1) {
            System.out.println("usage: ExtractRelationsFromWebPage filename");
            System.exit(1);
        }

        WebPage corpus  = new WebPage();

        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new FileInputStream(args[0]));

            while (fileScanner.hasNextLine()) {
                corpus.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + args[0]);
            System.exit(1);
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }

        SinglePassRelationExtractor extractor = new SinglePassRelationExtractor();
        extractor.extract(corpus);
    }
}
