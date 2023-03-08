package com.cnblogs.hoojo.sensitivewords.context;

import com.cnblogs.hoojo.sensitivewords.common.NamedWords;
import com.cnblogs.hoojo.sensitivewords.exception.CreateWordsFilterException;
import com.cnblogs.hoojo.sensitivewords.filter.WordsFilter;

import java.util.Set;

public interface WordsFilterContext {

    /**
     * 创建或更新已有的过滤器
     *
     * @param rawWordSet
     * @return
     * @throws CreateWordsFilterException
     */
    WordsFilter createOrUpdate(NamedWords rawWordSet) throws CreateWordsFilterException;

    /**
     * 是否包含敏感字符
     *
     * @param partMatch   是否支持匹配词语的一部分
     * @param content     被匹配内容
     * @param interceptor
     * @return 是否包含敏感字符
     */
    boolean contains(boolean partMatch, String content, FilterInterceptor<Boolean> interceptor);

    /**
     * 是否包含敏感字符
     *
     * @param partMatch 是否支持匹配词语的一部分
     * @param content   被匹配内容
     * @return 是否包含敏感字符
     */
    Set<String> getWords(boolean partMatch, String content);

    /**
     * 返回匹配到的敏感词语
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param interceptor
     * @return 返回匹配的敏感词语集合
     */
    Set<String> getWords(boolean partMatch, String content, FilterInterceptor<Set<String>> interceptor);

    /**
     * html高亮敏感词
     *
     * @param partMatch 是否部分匹配
     * @param content   被匹配的语句
     * @return 返回html高亮敏感词
     */
    String highlight(boolean partMatch, String content);

    /**
     * html高亮敏感词
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param interceptor
     * @return 返回html高亮敏感词
     */
    String highlight(boolean partMatch, String content, FilterInterceptor<String> interceptor);

    /**
     * 过滤敏感词，并把敏感词替换为指定字符
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param replaceChar 替换字符
     * @return 过滤后的字符串
     * @author hoojo
     */
    String filter(boolean partMatch, String content, Character replaceChar);

    /**
     * html高亮敏感词
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param replaceChar 替换字符
     * @param interceptor
     * @return 过滤后的字符串
     * @author hoojo
     */
    String filter(boolean partMatch, String content, Character replaceChar, FilterInterceptor<String> interceptor);
}
