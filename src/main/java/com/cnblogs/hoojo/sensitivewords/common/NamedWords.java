package com.cnblogs.hoojo.sensitivewords.common;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public class NamedWords {
    private final String category;
    private final Set<String> words;

    public NamedWords(String category, Collection<String> words) {
        this.category = category;
        this.words = ImmutableSet.copyOf(words);
    }

    public String getCategory() {
        return category;
    }

    public Set<String> getWords() {
        return words;
    }
}
