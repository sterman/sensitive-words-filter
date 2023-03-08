package com.cnblogs.hoojo.sensitivewords.context;

import com.cnblogs.hoojo.sensitivewords.common.WordsCategory;
import com.cnblogs.hoojo.sensitivewords.exception.CreateWordsFilterException;
import com.cnblogs.hoojo.sensitivewords.filter.WordsFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultWordsFilterContextTest {

    public static final String[] WORDS_FILES = new String[]{
            "BadWord.txt"
            , "其他词库.txt"
            , "反动词库.txt"
            , "敏感词库大全.txt"
            , "暴恐词库.txt"
            , "民生词库.txt"
            , "色情词库.txt"
            , "贪腐词库.txt"
    };

    public static final String TEST_CONTENT_FILE = "test_text.txt";

    public List<WordsCategory> wordsCategoryList = Lists.newArrayList();

    public String testContent = StringUtils.EMPTY;

    @BeforeClass
    public void init() throws IOException {
        for (String wordsFile : WORDS_FILES) {
            WordsCategory wordsCategory = createNamedWordsFromResource(wordsFile);
            wordsCategoryList.add(wordsCategory);
        }

        testContent = Resources.toString(Resources.getResource(TEST_CONTENT_FILE), StandardCharsets.UTF_8);
    }

    WordsCategory createNamedWordsFromResource(String resource) {
        HashSet<String> wordsSet = Sets.newHashSet();

        CharSource charSource = Resources.asCharSource(Resources.getResource(resource), StandardCharsets.UTF_8);
        try (BufferedReader bufferedReader = charSource.openBufferedStream()) {
            while (true) {
                String s = bufferedReader.readLine();
                if (s == null) {
                    break;
                }
                wordsSet.add(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new WordsCategory(resource, wordsSet);
    }

    @Test
    public void testBuild() throws CreateWordsFilterException {
        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);

        Assert.assertNotNull(context);
        Assert.assertEquals(context.getCategoryNames().size(), wordsCategoryList.size());
        Assert.assertEquals(context.getFilterNames().size(), wordsCategoryList.size());
    }

    @Test
    public void testBuildFilter() throws CreateWordsFilterException {
        for (WordsCategory wordsCategory : wordsCategoryList) {
            WordsFilter wordsFilter = DefaultWordsFilterContext.buildFilter(FilterType.DFA, wordsCategory);

            Assert.assertNotNull(wordsFilter);
            Assert.assertEquals(wordsFilter.getWordsCategory().getWords().size(), wordsCategory.getWords().size());
        }
    }

    @Test
    public void testCreateOrUpdate() throws CreateWordsFilterException {
        WordsCategory wordsCategory = wordsCategoryList.get(0);
        ArrayList<String> words = Lists.newArrayList(wordsCategory.getWords());
        words.add("abcdefg");
        WordsCategory newWordsCategory1 = new WordsCategory(wordsCategory.getCategory(), words);

        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);
        WordsFilter updated = context.createOrUpdate(wordsCategory);

        Assert.assertEquals(updated.getWordsCategory().getWords().size(), wordsCategory.getWords().size());
    }

    @Test
    public void testContains() throws CreateWordsFilterException {
        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);
        AtomicReference<String> hitCategory = new AtomicReference<>();
        boolean contains = context.contains(
                false,
                testContent,
                (filterName, wordsCategory, result) -> {
                    if (result) {
                        hitCategory.set(wordsCategory);
                    }
                    return !result;
                }
        );
        if (contains) {
            System.out.printf("content contains key words in category [%s]", hitCategory.get());
        } else {
            System.out.print("content does't contains any key words");
        }
        Assert.assertTrue(contains);
    }

    @Test
    public void testMatch() throws CreateWordsFilterException {
        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);
        Set<String> matches = context.match(
                false,
                testContent,
                (filterName, wordsCategory, result) -> {
                    System.out.printf("%s\t%s\t%s", filterName, wordsCategory, result);
                    return true;
                }
        );
        Assert.assertFalse(matches.isEmpty());
    }

    @Test
    public void testHighlight() throws CreateWordsFilterException {
        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);
        String result = context.highlight(
                false,
                testContent
        );
        System.out.printf("before:\n%s\n", testContent);
        System.out.printf("after:\n%s\n", result);
        Assert.assertNotNull(result);
    }

    @Test
    public void testFilter() throws CreateWordsFilterException {
        DefaultWordsFilterContext context = DefaultWordsFilterContext.build(FilterType.DFA, wordsCategoryList);
        String result = context.filter(
                false,
                testContent,
                '*'
        );
        System.out.printf("before:\n%s\n", testContent);
        System.out.printf("after:\n%s\n", result);
        Assert.assertNotNull(result);
    }
}