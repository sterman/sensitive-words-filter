package com.cnblogs.hoojo.sensitivewords.filter;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;

import java.util.Set;

/**
 * 过滤器接口
 */
public interface WordsFilter {

    /**
     * 过滤器名称
     */
    String getName();

    /**
     * 关键字
     */
    WordsCategory getWordsCategory();

    /**
     * 是否包含敏感字符
     *
     * @param partMatch 是否支持匹配词语的一部分
     * @param content   被匹配内容
     * @return 是否包含敏感字符
     */
    boolean contains(boolean partMatch, String content);

    /**
     * 返回匹配到的敏感词语
     *
     * @param partMatch 是否部分匹配
     * @param content   被匹配的语句
     * @return 返回匹配的敏感词语集合
     */
    Set<String> match(boolean partMatch, String content);

    /**
     * html高亮敏感词
     *
     * @param partMatch 是否部分匹配
     * @param content   被匹配的语句
     * @return 返回html高亮敏感词
     */
    String highlight(boolean partMatch, String content);

    /**
     * 过滤敏感词，并把敏感词替换为指定字符
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param replaceChar 替换字符
     * @return 过滤后的字符串
     */
    String filter(boolean partMatch, String content, char replaceChar);

}
