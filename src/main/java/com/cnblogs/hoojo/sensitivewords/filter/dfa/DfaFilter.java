package com.cnblogs.hoojo.sensitivewords.filter.dfa;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * DFA 脱敏算法实现支持类
 */
public final class DfaFilter extends BaseWordsFilter<HashMap<Character, DfaNode>> {

    public DfaFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected boolean putOneWord(HashMap<Character, DfaNode> state, String word) throws RuntimeException {

        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            return false;
        }

        Character fisrtChar = word.charAt(0);
        DfaNode node = state.get(fisrtChar);
        if (node == null) {
            node = new DfaNode(fisrtChar);
            state.put(fisrtChar, node);
        }

        for (int i = 1; i < word.length(); i++) {
            Character nextChar = word.charAt(i);

            DfaNode nextNode = null;
            if (!node.isLeaf()) {
                nextNode = node.getChilds().get(nextChar);
            }
            if (nextNode == null) {
                nextNode = new DfaNode(nextChar);
            }

            node.addChild(nextNode);
            node = nextNode;

            if (i == word.length() - 1) {
                node.setWord(true);
            }
        }

        return true;
    }

    @Override
    protected boolean processor(boolean partMatch, String content, Callback callback) throws RuntimeException {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        content = StringUtils.trim(content);
        if (content.length() < 2) {
            return false;
        }

        for (int index = 0; index < content.length(); index++) {
            char fisrtChar = content.charAt(index);

            DfaNode node = getState().get(fisrtChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            int charCount = 1;
            for (int i = index + 1; i < content.length(); i++) {
                char wordChar = content.charAt(i);

                node = node.getChilds().get(wordChar);
                if (node != null) {
                    charCount++;
                } else {
                    break;
                }

                if (partMatch && node.isWord()) {
                    if (callback.call(StringUtils.substring(content, index, index + charCount))) {
                        return true;
                    }
                    break;
                } else if (node.isWord()) {
                    if (callback.call(StringUtils.substring(content, index, index + charCount))) {
                        return true;
                    }
                }

                if (node.isLeaf()) {
                    break;
                }
            }

            if (partMatch) {
                index += charCount;
            }
        }

        return false;
    }

    @Override
    protected HashMap<Character, DfaNode> createState() {
        return Maps.newHashMap();
    }

}
