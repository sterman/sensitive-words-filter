package com.cnblogs.hoojo.sensitivewords.filter;

import com.cnblogs.hoojo.sensitivewords.common.ApplicationLogging;
import com.cnblogs.hoojo.sensitivewords.common.NamedWords;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Set;

/**
 * 敏感词库抽象接口实现
 *
 * @author hoojo
 * @version 1.0
 * @createDate 2018年2月2日 下午4:09:02
 * @file AbstractSensitiveWordsFilter.java
 * @package com.cnblogs.hoojo.sensitivewords
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 */
public abstract class BaseWordsFilter<S> extends ApplicationLogging implements WordsFilter {

    private static final String HTML_HIGHLIGHT = "<font color='red'>%s</font>";
    private final NamedWords words;
    private final String name;
    private final S state;

    public BaseWordsFilter(NamedWords words) {
        this.words = words;
        this.name = String.format("%s|%s", this.getName(), words.getCategory());
        this.state = load(words);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NamedWords getWords() {
        return words;
    }

    public S getState() {
        return state;
    }

    protected S load(NamedWords wordSet) {
        S state = createState();
        loadStart(wordSet, state);
        for (String s : wordSet.getWords()) {
            this.putOneWord(state, s);
        }
        loadCompete(wordSet, state);
        return state;
    }

    protected abstract S createState();

    protected void loadStart(NamedWords wordSet, S state) {

    }

    protected void loadCompete(NamedWords wordSet, S state) {

    }

    protected abstract boolean putOneWord(S state, String word) throws RuntimeException;

    /**
     * 判断一段文字包含敏感词语，支持敏感词结果回调
     *
     * @param partMatch 是否支持匹配词语的一部分
     * @param content   被匹配内容
     * @param callback  回调接口
     * @return 是否匹配到的词语
     * @author hoojo
     * @createDate 2018年2月9日 下午2:54:59
     */
    protected abstract boolean processor(boolean partMatch, String content, Callback callback) throws RuntimeException;

    @Override
    public boolean contains(boolean partMatch, String content) throws RuntimeException {

        return processor(partMatch, content, new Callback() {
            @Override
            public boolean call(String word) {
                return true; // 有敏感词立即返回
            }
        });
    }

    @Override
    public Set<String> getWords(boolean partMatch, String content) throws RuntimeException {
        final Set<String> words = Sets.newHashSet();

        processor(partMatch, content, new Callback() {
            @Override
            public boolean call(String word) {
                words.add(word);
                return false; // 继续匹配后面的敏感词
            }
        });

        return words;
    }

    @Override
    public String highlight(boolean partMatch, String content) throws RuntimeException {
        Set<String> words = this.getWords(partMatch, content);

        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            content = content.replaceAll(word, String.format(HTML_HIGHLIGHT, word));
        }

        return content;
    }

    @Override
    public String filter(boolean partMatch, String content, char replaceChar) throws RuntimeException {
        Set<String> words = this.getWords(partMatch, content);

        Iterator<String> iter = words.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            content = content.replaceAll(word, Strings.repeat(String.valueOf(replaceChar), word.length()));
        }

        return content;
    }

    /**
     * 匹配到敏感词的回调接口
     *
     * @author hoojo
     * @createDate 2018年3月21日 上午11:46:15
     */
    protected interface Callback {

        /**
         * 匹配掉敏感词回调
         *
         * @param word 敏感词
         * @return true 立即停止后续任务并返回，false 继续执行
         * @author hoojo
         * @createDate 2018年3月21日 上午11:48:11
         */
        boolean call(String word);
    }
}
