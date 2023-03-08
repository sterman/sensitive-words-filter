package com.cnblogs.hoojo.sensitivewords.filter.simhash;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * google simhash 算法实现脱敏过滤
 * <p>
 * 由于simhash是对大文本进行比较，并且比较的是在支持分词的基础上对分词对象进行比较，进而确定相识度。
 * 故 在脱敏方面支持不是很友好，在大文本情况下，效率低下。
 * 改变情况，需要分词库支持。
 */
public final class SimHashFilter extends BaseWordsFilter<Map<Character, Map<String, Set<String>>>> {

    public SimHashFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected Map<Character, Map<String, Set<String>>> createState() {
        return Maps.newHashMap();
    }

    @Override
    protected boolean putOneWord(Map<Character, Map<String, Set<String>>> state, String word) throws RuntimeException {

        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            return false;
        }

        Character firstChar = word.charAt(0);

        Map<String, Set<String>> hashs = state.get(firstChar);
        if (hashs == null) {
            hashs = Maps.newHashMap();
            state.put(firstChar, hashs);
        }

        String hash = SimHashUtils._simhash(word);
        String[] chunks = SimHashUtils.chunk(hash);

        Map<String, Set<String>> map = SimHashUtils.cartesianProduct(chunks);
        Set<String> keys = map.keySet();
        for (String chunk : keys) {
            if (!hashs.containsKey(chunk)) {
                hashs.put(chunk, map.get(chunk));
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

        for (int i = 0; i < content.length(); i++) {
            Character wordChar = content.charAt(i);

            // 判断是否属于脏字符
            if (!getState().containsKey(wordChar)) {
                continue;
            }

            Map<String, Set<String>> hashs = getState().get(wordChar);
            int j = i + 1;
            while (j < content.length()) {
                String word = content.substring(i, j + 1);
                // 判断是否是脏词
                if (SimHashUtils.contains(word, hashs)) {

                    if (callback.call(word)) {
                        return true;
                    }

                    if (partMatch) {
                        i += word.length();
                    }
                }

                j++;
            }
        }

        return false;
    }

}
