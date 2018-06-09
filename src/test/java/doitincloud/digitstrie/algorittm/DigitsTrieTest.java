package doitincloud.digitstrie.algorittm;

import org.junit.Before;

import static org.junit.Assert.*;

public class DigitsTrieTest {

    DigitsTrie<String> dt;

    @Before
    public void initialization() {

        dt = new DigitsTrie<String>();

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
    }

}