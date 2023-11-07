package net.heyimamethyst.fairyfactions.config;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig
{

    private String configContents = "";

    public List<Pair> getConfigsList() {
        return configsList;
    }

    private final List<Pair> configsList = new ArrayList<>();

    public void addComment(String comment)
    {
        configContents += "\n" + "#" + comment + "\n" + "\n";
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, String comment)
    {
        configsList.add(keyValuePair);

        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, int minMun, int maxNum, String comment)
    {
        configsList.add(keyValuePair);

        if(keyValuePair.getSecond() instanceof Integer)
        {
            if (!(((int)keyValuePair.getSecond()) >= minMun && ((int)keyValuePair.getSecond()) <= maxNum))
            {
                throw new IllegalArgumentException(keyValuePair.getFirst() + " out of range");
            }
        }

        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, double minMun, double maxNum, String comment)
    {
        configsList.add(keyValuePair);

        if(keyValuePair.getSecond() instanceof Integer)
        {
            if (!(((int)keyValuePair.getSecond()) >= minMun && ((int)keyValuePair.getSecond()) <= maxNum))
            {
                throw new IllegalArgumentException(keyValuePair.getFirst() + " out of range");
            }
        }

        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    }

    public void addKeyValuePair(Pair<String, ?> keyValuePair, float minMun, float maxNum, String comment)
    {
        configsList.add(keyValuePair);

        if(keyValuePair.getSecond() instanceof Integer)
        {
            if (!(((int)keyValuePair.getSecond()) >= minMun && ((int)keyValuePair.getSecond()) <= maxNum))
            {
                throw new IllegalArgumentException(keyValuePair.getFirst() + " out of range");
            }
        }

        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + " #"
                + comment + " | default: " + keyValuePair.getSecond() + "\n";
    }

    @Override
    public String get(String namespace)
    {
        return configContents;
    }
}
