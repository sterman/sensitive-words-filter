package com.cnblogs.hoojo.sensitivewords.context;

import com.cnblogs.hoojo.sensitivewords.common.ApplicationLogging;
import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.exception.CreateWordsFilterException;
import com.cnblogs.hoojo.sensitivewords.exception.WordsFilterContextNotInitializedException;
import com.cnblogs.hoojo.sensitivewords.filter.WordsFilter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public final class DefaultWordsFilterContext extends ApplicationLogging implements WordsFilterContext {

    private final FilterType type;
    private final Map<String, WordsCategory> wordsCategories;
    private final Map<String, WordsFilter> wordsFilters;

    private DefaultWordsFilterContext(FilterType type, Collection<WordsCategory> wordsCategories) throws CreateWordsFilterException {
        this.type = type;
        this.wordsCategories = new ConcurrentSkipListMap<>();
        this.wordsFilters = new ConcurrentSkipListMap<>();

        for (WordsCategory rawWordSet : wordsCategories) {
            createOrUpdate(rawWordSet);
        }
    }

    /**
     * 使用指定的类型和词集，创建过滤器上下文实例
     *
     * @param type
     * @param rawWordSets
     * @return 新的独立过滤器上下文
     * @throws CreateWordsFilterException
     */
    public static DefaultWordsFilterContext build(FilterType type, Collection<WordsCategory> rawWordSets) throws CreateWordsFilterException {
        DefaultWordsFilterContext context = new DefaultWordsFilterContext(type, rawWordSets);
        return context;
    }

    /**
     * 使用指定的类型和词集，创建过滤器实例
     *
     * @param filterType
     * @param wordSet
     * @return 新过滤器
     * @throws CreateWordsFilterException
     */
    public static WordsFilter buildFilter(FilterType filterType, WordsCategory wordSet) throws CreateWordsFilterException {
        try {
            Constructor<? extends WordsFilter> constructor = filterType.getClazz().getConstructor(WordsCategory.class);
            return constructor.newInstance(wordSet);
        } catch (Exception e) {
            throw new CreateWordsFilterException("创建WordsFilter对象失败", e);
        }
    }

    private static void checkContent(String content) {
        if (Strings.isNullOrEmpty(content)) {
            throw new IllegalArgumentException("content不能为空");
        }
    }

    @Override
    public FilterType getType() {
        return type;
    }

    @Override
    public Set<String> getCategoryNames() {
        return wordsCategories.keySet();
    }

    @Override
    public Set<String> getFilterNames() {
        return wordsFilters.keySet();
    }

    @Override
    public boolean containsCategory(String category) {
        return this.wordsCategories.containsKey(category);
    }

    /**
     * 创建或更新已有的过滤器
     *
     * @param rawWordSet
     * @return
     * @throws CreateWordsFilterException
     */
    @Override
    public WordsFilter createOrUpdate(WordsCategory rawWordSet) throws CreateWordsFilterException {
        WordsFilter wordsFilter = buildFilter(this.type, rawWordSet);
        this.wordsFilters.put(wordsFilter.getName(), wordsFilter);

        this.wordsCategories.put(rawWordSet.getCategory(), rawWordSet);

        return wordsFilter;
    }

    /**
     * 是否包含敏感字符
     *
     * @param partMatch   是否支持匹配词语的一部分
     * @param content     被匹配内容
     * @param interceptor
     * @return 是否包含敏感字符
     */
    @Override
    public boolean contains(boolean partMatch, String content, FilterInterceptor<Boolean> interceptor) {
        checkContent(content);
        boolean finalResult = false;
        for (WordsFilter filter : this.wordsFilters.values()) {
            boolean filterResult = filter.contains(partMatch, content);
            finalResult = finalResult || filterResult;
            if (interceptor != null && !interceptor.perFilter(filter.getName(), filter.getWordsCategory().getCategory(), filterResult)) {
                break;
            }
        }
        return finalResult;
    }

    /**
     * 是否包含敏感字符
     *
     * @param partMatch 是否支持匹配词语的一部分
     * @param content   被匹配内容
     * @return 是否包含敏感字符
     */
    @Override
    public Set<String> match(boolean partMatch, String content) {
        return match(partMatch, content, null);
    }

    /**
     * 返回匹配到的敏感词语
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param interceptor
     * @return 返回匹配的敏感词语集合
     */
    @Override
    public Set<String> match(boolean partMatch, String content, FilterInterceptor<Set<String>> interceptor) {
        checkContent(content);
        HashSet<String> finalResult = Sets.newHashSet();
        for (WordsFilter filter : this.wordsFilters.values()) {
            Set<String> filterResult = filter.match(partMatch, content);
            finalResult.addAll(filterResult);
            if (interceptor != null && !interceptor.perFilter(filter.getName(), filter.getWordsCategory().getCategory(), filterResult)) {
                break;
            }
        }
        return finalResult;
    }

    /**
     * html高亮敏感词
     *
     * @param partMatch 是否部分匹配
     * @param content   被匹配的语句
     * @return 返回html高亮敏感词
     */
    @Override
    public String highlight(boolean partMatch, String content) {
        return highlight(partMatch, content, null);
    }

    /**
     * html高亮敏感词
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param interceptor
     * @return 返回html高亮敏感词
     */
    @Override
    public String highlight(boolean partMatch, String content, FilterInterceptor<String> interceptor) {
        checkContent(content);
        String finalResult = content;
        for (WordsFilter filter : this.wordsFilters.values()) {
            finalResult = filter.highlight(partMatch, finalResult);
            if (interceptor != null && !interceptor.perFilter(filter.getName(), filter.getWordsCategory().getCategory(), finalResult)) {
                break;
            }
        }
        return finalResult;
    }

    /**
     * 过滤敏感词，并把敏感词替换为指定字符
     *
     * @param partMatch   是否部分匹配
     * @param content     被匹配的语句
     * @param replaceChar 替换字符
     * @return 过滤后的字符串
     * @author hoojo
     */
    @Override
    public String filter(boolean partMatch, String content, Character replaceChar) {
        return filter(partMatch, content, replaceChar, null);
    }

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
    @Override
    public String filter(boolean partMatch, String content, Character replaceChar, FilterInterceptor<String> interceptor) {
        checkContent(content);
        String finalResult = content;
        for (WordsFilter filter : this.wordsFilters.values()) {
            finalResult = filter.filter(partMatch, finalResult, replaceChar);
            if (interceptor != null && !interceptor.perFilter(filter.getName(), filter.getWordsCategory().getCategory(), finalResult)) {
                break;
            }
        }
        return finalResult;
    }

    public final static class DefaultWordsFilterContextStates {
        private static final Map<FilterType, DefaultWordsFilterContext> CONTEXT_CACHE = new ConcurrentSkipListMap<>();

        /**
         * 按类型获取过滤器上下文
         *
         * @param filterType
         * @return 过滤器上下文
         * @throws WordsFilterContextNotInitializedException
         */
        public static WordsFilterContext getContext(FilterType filterType) throws WordsFilterContextNotInitializedException {
            DefaultWordsFilterContext context = CONTEXT_CACHE.get(filterType);
            if (context == null) {
                String msg = String.format("词组过滤器未初始化：%s", filterType);
                throw new WordsFilterContextNotInitializedException(msg);
            }
            return context;
        }

        /**
         * 使用给定的关键字集，重新加载指定类型的过滤器上下文。原有的过滤器将被丢弃。
         *
         * @param type
         * @param rawWordSets
         * @return 过滤器上下文
         * @throws CreateWordsFilterException
         */
        public static DefaultWordsFilterContext reloadContext(FilterType type, Collection<WordsCategory> rawWordSets) throws CreateWordsFilterException {
            DefaultWordsFilterContext context = build(type, rawWordSets);
            CONTEXT_CACHE.put(type, context);
            return context;
        }

        /**
         * 使用给定的关键字集，重新加载指定类型的过滤器。
         *
         * @param type
         * @param rawWordSet
         * @return 过滤器
         * @throws WordsFilterContextNotInitializedException
         * @throws CreateWordsFilterException
         */
        public static WordsFilter reloadFilter(FilterType type, WordsCategory rawWordSet) throws WordsFilterContextNotInitializedException, CreateWordsFilterException {
            WordsFilterContext context = getContext(type);
            return context.createOrUpdate(rawWordSet);
        }


        /**
         * 清空所有缓存的过滤器
         */
        public static void clear() {
            CONTEXT_CACHE.clear();
        }
    }

}
