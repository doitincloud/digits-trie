package doitincloud.digitstrie;

import doitincloud.commons.Utils;
import doitincloud.digitstrie.algorittm.DigitsTrie;

public class DigitsTrieTest {

    @org.junit.Test
    public void testOne() {
        DigitsTrie<String> dt = new DigitsTrie<String>();

        dt.put("1", "a");
        dt.put("12", "ab");
        dt.put("123", "abc");
        dt.put("1234", "abcd");
        dt.put("123456", "abcdef");
        dt.put("1234567", "abcdefg");
        dt.put("11", "aa");
        dt.put("111", "aaa");
        dt.put("112", "aab");
        dt.put("113", "aac");

        System.out.println("size: " + dt.size());

        System.out.println("count: " + dt.countStartWith(""));

        System.out.println("list: " + Utils.toJson(dt.all()));

        String key = "123";
        System.out.println("longest match("+key+"): "+ Utils.toJson(dt.longestMatch(key)));

        key = "173";
        System.out.println("search("+key+"): " + Utils.toJson(dt.get((key))));

        key = "17";
        System.out.println("search("+key+"): " + Utils.toJson(dt.get(key)));

        key = "17";
        System.out.println("starts with("+key+"): " + dt.hasStartsWith(key));

        key = "17";
        System.out.println("list starts with("+key+"): " + Utils.toJson(dt.allStartWith(key)));

        key = "10";
        dt.remove(key);

        System.out.println("after delete("+key+") size: " + dt.size());

        System.out.println("count: " + dt.countStartWith(""));

        System.out.println("list: " + Utils.toJson(dt.all()));    }

    @org.junit.Test
    public void add() {
    }

    @org.junit.Test
    public void all() {
    }

    @org.junit.Test
    public void size() {
    }

    @org.junit.Test
    public void add1() {
    }

    @org.junit.Test
    public void delete() {
    }

    @org.junit.Test
    public void search() {
    }

    @org.junit.Test
    public void startsWith() {
    }

    @org.junit.Test
    public void longestMatch() {
    }

    @org.junit.Test
    public void count() {
    }

    @org.junit.Test
    public void allStartWith() {
    }
}