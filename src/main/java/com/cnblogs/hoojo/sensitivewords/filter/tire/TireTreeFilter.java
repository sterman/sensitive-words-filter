package com.cnblogs.hoojo.sensitivewords.filter.tire;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * tire tree 算法脱敏词库支持类
 *
 * @author hoojo
 * @version 1.0
 * @createDate 2018年2月9日 上午10:36:08
 * @file TireTreeFilterExecutor.java
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 */
public final class TireTreeFilter extends BaseWordsFilter<TireTreeNode> {

    public TireTreeFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected TireTreeNode createState() {
        return new TireTreeNode(' ');
    }

    @Override
    protected boolean putOneWord(TireTreeNode state, String word) throws RuntimeException {

        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            return false;
        }

        char fisrtChar = word.charAt(0);
        TireTreeNode node = state.find(fisrtChar);
        if (node == null) {
            node = new TireTreeNode(fisrtChar);
            state.addChild(node);
        }

        for (int i = 1; i < word.length(); i++) {
            char nextChar = word.charAt(i); // 转换成char型

            TireTreeNode nextNode = null;
            if (!node.isLeaf()) {
                nextNode = node.find(nextChar);
            }
            if (nextNode == null) {
                nextNode = new TireTreeNode(nextChar);
            }

            node.addChild(nextNode);
            node = nextNode;

            if (i == word.length() - 1) {
                node.setWord(true);
            }
        }

        return true;
    }

    /**
     * 判断一段文字包含敏感词语，支持敏感词结果回调
     *
     * @param partMatch 是否支持匹配词语的一部分
     * @param content   被匹配内容
     * @param callback
     * @return 是否匹配到的词语
     * @author hoojo
     * @createDate 2018年2月9日 下午2:54:59
     */
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

            TireTreeNode node = getState().find(fisrtChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            int charCount = 1;
            for (int i = index + 1; i < content.length(); i++) {
                char wordChar = content.charAt(i);

                node = node.find(wordChar);
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

}
