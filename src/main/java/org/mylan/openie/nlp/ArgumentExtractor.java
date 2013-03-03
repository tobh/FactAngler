package org.mylan.openie.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.utils.Property;
import java.util.Properties;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/**
 * Describe class ArgumentExtractor here.
 *
 *
 * Created: Tue Feb  5 13:17:51 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ArgumentExtractor {
    private final Set<String> startNounTags;
    private final Set<String> continueNounTags;
    private final Set<String> enclosingTags;
    private final Set<String> symmetricTags;
    private final Map<String, String> symmetricPartnerTags;
    private final Set<String> nounTags;
    private final Set<String> splitTags;

    public ArgumentExtractor() {
        Properties properties = Property.create("analyse.properties");
        String language = properties.getProperty("language");

        startNounTags = new HashSet<String>();
        startNounTags.addAll(Arrays.asList(properties.getProperty(language + "StartNounTags").split(" ")));
        continueNounTags = new HashSet<String>();
        continueNounTags.addAll(Arrays.asList(properties.getProperty(language + "ContinueNounTags").split(" ")));
        enclosingTags = new HashSet<String>();
        enclosingTags.addAll(Arrays.asList(new String("\" '").split(" ")));
        symmetricTags = new HashSet<String>();
        symmetricTags.addAll(Arrays.asList(new String("\" `` '' ( ) ,").split(" ")));
        symmetricPartnerTags = new HashMap<String, String>();
        symmetricPartnerTags.put("\"", "\"");
        symmetricPartnerTags.put("``", "''");
        symmetricPartnerTags.put("(", ")");
        symmetricPartnerTags.put(",", ",");
        nounTags = new HashSet<String>();
        nounTags.addAll(Arrays.asList(properties.getProperty(language + "NounTags").split(" ")));
        splitTags = new HashSet<String>();
        splitTags.addAll(Arrays.asList(properties.getProperty(language + "SplitTags").split(" ")));
    }

    public List<Pattern> getArguments(final Sentence sentence) {
        List<List<PosComponent>> argumentComponents = cleanupArguments(extractArgumentComponents(sentence.getComponents()));
        List<Pattern> arguments = new ArrayList<Pattern>(argumentComponents.size());
        for (List<PosComponent> components : argumentComponents) {
            arguments.add(new Pattern(sentence, components));
            List<PosComponent> splited = new ArrayList<PosComponent>(4);
            boolean wasSplited = false;
            for (PosComponent component : components) {
                splited.add(component);
                if (splited.size() > 1 && splitTags.contains(component.toString())) {
                    splited.remove(splited.size() - 1);
                    arguments.add(new Pattern(sentence, splited));
                    splited = new ArrayList<PosComponent>(4);
                    wasSplited = true;
                }
            }
            if (wasSplited && !splited.isEmpty()) {
                arguments.add(new Pattern(sentence, splited));
            }
        }
        return arguments;
    }

    private List<List<PosComponent>> cleanupArguments(final List<List<PosComponent>> arguments) {
        for (int i = 0; i < arguments.size(); ++i) {
            List<PosComponent> argument = arguments.get(i);
            while (!argument.isEmpty() && continueNounTags.contains(argument.get(argument.size() - 1).toString())) {
                argument.remove(argument.size() - 1);
            }
            if (argument.isEmpty()) {
                arguments.remove(i);
            }
        }
        return arguments;
    }

    private List<List<PosComponent>> extractArgumentComponents(final List<PosComponent> components) {
        int componentCount = components.size();
        boolean isContinuation = false;
        List<List<PosComponent>> arguments = new LinkedList<List<PosComponent>>();

        List<PosComponent> currentArgument = new LinkedList<PosComponent>();
        for (int i = 0; i < componentCount; ++i) {
            String pos = components.get(i).toString();
            String text = components.get(i).getText();
            if (!isContinuation && enclosingTags.contains(text)) {
                String closeToken = text;
                List<PosComponent> enclosedArgument = new LinkedList<PosComponent>();
                enclosedArgument.add(components.get(i));
                for (int k = i + 1; k < componentCount; ++k) {
                    enclosedArgument.add(components.get(k));
                    if (components.get(k).getText().equals(closeToken)) {
                        arguments.add(enclosedArgument);
                        break;
                    }
                }
            } else if (startNounTags.contains(pos)) {
                if (!isContinuation) {
                    currentArgument = new LinkedList<PosComponent>();
                    // how to handle rbr at the end?
                    arguments.add(currentArgument);
                }
                currentArgument.add(components.get(i));
                isContinuation = true;
            } else if (isContinuation) {
                if (symmetricTags.contains(text)) {
                    int countOpen = 1;
                    String openToken = text;
                    String closeToken = symmetricPartnerTags.get(text);
                    List<PosComponent> enclosedComponents = new LinkedList<PosComponent>();
                    enclosedComponents.add(components.get(i));
                    for (int k = i + 1; countOpen > 0 && k < componentCount; ++k) {
                        String current = components.get(k).getText();
                        if (current.equals(closeToken)) {
                            --countOpen;
                        } else if (current.equals(openToken)) {
                            ++countOpen;
                        }
                        enclosedComponents.add(components.get(k));
                    }
                    if (countOpen == 0) {
                        currentArgument.addAll(enclosedComponents);
                    }
                    isContinuation = false;
                } else if (continueNounTags.contains(pos)) {
                    currentArgument.add(components.get(i));
                } else {
                    isContinuation = false;
                }
            }
        }
        return arguments;
    }
}
