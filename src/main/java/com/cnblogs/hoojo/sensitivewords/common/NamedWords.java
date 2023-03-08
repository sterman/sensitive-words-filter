package com.cnblogs.hoojo.sensitivewords.common;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public class NamedWords {
    private final String category;
    private final Set<String> words;

    public NamedWords(String category, Collection<String> words) {
        if (Strings.isNullOrEmpty(category)) {
            throw new IllegalArgumentException("关键字分类不能为空");
        }
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("关键字不能为空");
        }
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
