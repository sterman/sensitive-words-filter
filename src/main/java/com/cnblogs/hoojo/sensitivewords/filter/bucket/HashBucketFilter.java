package com.cnblogs.hoojo.sensitivewords.filter.bucket;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * hash bucket 脱敏过滤算法实现
 */
public final class HashBucketFilter extends BaseWordsFilter<Map<Character, Map<Integer, Set<String>>>> {

    public HashBucketFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected Map<Character, Map<Integer, Set<String>>> createState() {
        return new HashMap<>();
    }

    @Override
    protected boolean putOneWord(Map<Character, Map<Integer, Set<String>>> state, String word) throws RuntimeException {

        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            return false;
        }


        char firstChar = word.charAt(0);

        Map<Integer, Set<String>> buckets = state.get(firstChar);
        if (buckets == null) {
            buckets = Maps.newHashMap();
            state.put(firstChar, buckets);
        }

        Set<String> words = buckets.get(word.length());
        if (words == null) {
            words = new HashSet<String>();
            buckets.put(word.length(), words);
        }
        words.add(word);

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

            Map<Integer, Set<String>> buckets = getState().get(wordChar);
            Set<Integer> sizes = buckets.keySet();
            for (int size : sizes) {

                if (i + size > content.length()) {
                    continue;
                }

                String word = content.substring(i, i + size);
                Set<String> words = buckets.get(size);
                // 判断是否是脏词
                if (words.contains(word)) {
                    if (callback.call(word)) {
                        return true;
                    }

                    if (partMatch) {
                        i += word.length();
                    }
                }
            }
        }

        return false;
    }
}
