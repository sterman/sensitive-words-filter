package com.cnblogs.hoojo.sensitivewords.filter.dat;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * 双数组算法过滤敏感词
 *
 * @author hoojo
 * @version 1.0
 * @createDate 2018年3月21日 下午3:28:21
 * @package com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 */
public final class DatFilter extends BaseWordsFilter<DatCacheNode> {

    public DatFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected DatCacheNode createState() {
        return new DatCacheNode();
    }

    @Override
    protected boolean putOneWord(DatCacheNode state, String word) throws RuntimeException {
        if (StringUtils.isBlank(word)) {
            return false;
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            return false;
        }

        state.getWords().add(word);

        for (Character character : word.toCharArray()) {
            state.getChars().add(character);
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
            if (!getState().getChars().contains(wordChar)) {
                continue;
            }

            int j = i + 1;
            while (j < content.length()) {

                // 判断下一个字符是否属于脏字符
                wordChar = content.charAt(j);
                if (!getState().getChars().contains(wordChar)) {
                    break;
                }

                String word = content.substring(i, j + 1);
                // 判断是否是脏词
                if (getState().getWords().contains(word)) {

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
