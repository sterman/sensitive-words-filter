package com.cnblogs.hoojo.sensitivewords.context;

/**
 * 过滤器拦截器
 *
 * @param <T> 操作结果类型
 */
@FunctionalInterface
public interface FilterInterceptor<T> {
    /**
     * 处理多个过滤的过滤结果
     *
     * @param filterName    过滤器名称
     * @param wordsCategory 关键字分类
     * @param result        过滤结果
     * @return 是否继续下一个处理器
     */
    boolean perFilter(String filterName, String wordsCategory, T result);
}
