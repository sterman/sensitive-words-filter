package com.cnblogs.hoojo.sensitivewords.filter.ttmp;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.filter.BaseWordsFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * ttmp 过滤明干成实现
 *
 * @author hoojo
 * @version 1.0
 * @createDate 2018年3月20日 下午6:09:01
 * @file TtmpFilterExecutor.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.ttmp
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 */
public final class TtmpFilter extends BaseWordsFilter<TtmpCacheNode> {

    public TtmpFilter(WordsCategory wordSet) {
        super(wordSet);
    }

    @Override
    protected TtmpCacheNode createState() {
        return new TtmpCacheNode();
    }

    @Override
    protected boolean putOneWord(TtmpCacheNode state, String word) throws RuntimeException {

        state.setMaxWordLength(Math.max(state.getMaxWordLength(), word.length()));
        state.setMinWordLength(Math.min(state.getMinWordLength(), word.length()));

        for (int i = 0; i < 7 && i < word.length(); i++) {
            byte[] fastCheck = state.getFastCheck();
            fastCheck[word.charAt(i)] |= (byte) (1 << i);

            state.setFastCheck(fastCheck);
        }

        for (int i = 7; i < word.length(); i++) {
            byte[] fastCheck = state.getFastCheck();
            fastCheck[word.charAt(i)] |= 0x80;

            state.setFastCheck(fastCheck);
        }

        if (word.length() == 1) {
            state.getCharCheck()[word.charAt(0)] = true;
        } else {
            state.getEndCheck()[word.charAt(word.length() - 1)] = true;

            byte[] fastLength = state.getFastLength();
            fastLength[word.charAt(0)] |= (byte) (1 << (Math.min(7, word.length() - 2)));

            state.setFastLength(fastLength);

            state.getHash().add(word);
        }

        return false;
    }

    protected boolean processor(boolean partMatch, String content, Callback callback) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        content = StringUtils.trim(content);

        int index = 0;
        while (index < content.length()) {
            int count = 1;

            if (partMatch) {
                if (index > 0 || (getState().getFastCheck()[content.charAt(index)] & 1) == 0) {
                    // 匹配到下一个“可能是脏词”首字符的位置
                    while (index < content.length() - 1 && (getState().getFastCheck()[content.charAt(++index)] & 1) == 0)
                        ;
                }
            }

            // 取得下一个脏词文本的第一个字符
            char begin = content.charAt(index);

            // 表示是简单脏词，单个字脏词
            if (getState().getMinWordLength() == 1 && getState().getCharCheck()[begin]) {

                if (callback.call(String.valueOf(begin))) {
                    return true;
                }
            }

            // 比对的次数是 当前文本剩余比对长度 或者 脏词的最大长度
            for (int j = 1; j <= Math.min(getState().getMaxWordLength(), content.length() - index - 1); j++) {
                char current = content.charAt(index + j);

                if ((getState().getFastCheck()[current] & 1) == 0) { // 非首字符
                    ++count;
                }

                if ((getState().getFastCheck()[current] & (1 << Math.min(j, 7))) == 0) { // 当前字符在脏词中的位置超过7位
                    break;
                }

                if (j + 1 >= getState().getMinWordLength()) { // 当前比对词长度小于等于最大脏词的长度
                    // 判断当前字符是否是脏词最后一个字符
                    if ((getState().getFastLength()[begin] & (1 << Math.min(j - 1, 7))) > 0 && getState().getEndCheck()[current]) {
                        String sub = content.substring(index, index + j + 1);

                        if (getState().getHash().contains(sub)) { // 判断是否是脏词
                            if (callback.call(sub)) {
                                return true;
                            }
                        }
                    }
                }
            }

            if (partMatch) {
                index++;
            } else {
                index += count;
            }
        }

        return false;
    }

}
