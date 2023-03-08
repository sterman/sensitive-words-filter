package com.cnblogs.hoojo.sensitivewords.context;

import com.cnblogs.hoojo.sensitivewords.filter.WordsFilter;
import com.cnblogs.hoojo.sensitivewords.filter.bucket.HashBucketFilter;
import com.cnblogs.hoojo.sensitivewords.filter.dat.DatFilter;
import com.cnblogs.hoojo.sensitivewords.filter.dfa.DfaFilter;
import com.cnblogs.hoojo.sensitivewords.filter.simhash.SimHashFilter;
import com.cnblogs.hoojo.sensitivewords.filter.tire.TireTreeFilter;
import com.cnblogs.hoojo.sensitivewords.filter.ttmp.TtmpFilter;
import com.google.common.base.MoreObjects;

/**
 * 敏感词算法实现类型
 *
 * @author hoojo
 * @version 1.0
 * @createDate 2018年2月2日 下午4:28:11
 * @file FilterType.java
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 */
public enum FilterType {

    DFA("DFA脱敏算法实现支持类", DfaFilter.class),
    TIRE("TIRE TREE算法脱敏支持类", TireTreeFilter.class),
    HASH_BUCKET("二级HASH(HASH BUCKET)脱敏算法实现", HashBucketFilter.class),
    DAT("DAT双数组算法脱敏实现", DatFilter.class),
    TTMP("TTMP算法脱敏支持类", TtmpFilter.class),
    SIMHASH("SIMHASH算法脱敏实现", SimHashFilter.class);

    private final String desc;
    private final Class<? extends WordsFilter> clazz;

    FilterType(String desc, Class<? extends WordsFilter> clazz) {
        this.desc = desc;
        this.clazz = clazz;
    }

    public String getDesc() {
        return desc;
    }

    public Class<? extends WordsFilter> getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("desc", desc)
                .add("clazz", clazz)
                .toString();
    }
}
