package com.cnblogs.hoojo.sensitivewords.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统调试、操作、错误日志基类
 */
public abstract class ApplicationLogging {

    protected final Logger logger;

    public ApplicationLogging() {
        super();
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected final void debug(Object o) {

        logger.debug(String.valueOf(o));
    }

    protected final void debug(String msg) {

        logger.debug(msg);
    }

    protected final void debug(String msg, Object... objects) {

        logger.debug(msg, objects);
    }

    protected final void debug(Throwable ex) {
        logger.debug(ex.getMessage(), ex);
    }

    protected final void error(String msg) {
        logger.error(msg);
    }

    protected final void error(String msg, Object... objects) {
        logger.error(msg, objects);
    }

    protected final void error(Throwable ex) {
        logger.error(ex.getMessage(), ex);
    }

    protected final void info(String msg) {
        logger.info(msg);
    }

    protected final void info(String msg, Object... objects) {
        logger.info(msg, objects);
    }

    protected final void info(Throwable ex) {
        logger.info(ex.getMessage(), ex);
    }

    protected final void trace(Object o) {
        logger.trace(String.valueOf(o));
    }

    protected final void trace(String msg) {
        logger.trace(msg);
    }

    protected final void trace(String msg, Object... objects) {
        logger.trace(msg, objects);
    }

    protected final void trace(Throwable ex) {
        logger.trace(ex.getMessage(), ex);
    }

    protected final void warn(String msg) {
        logger.warn(msg);
    }

    protected final void warn(String msg, Object... objects) {
        logger.warn(msg, objects);
    }

    protected final void warn(Throwable ex) {
        logger.warn(ex.getMessage(), ex);
    }
}