package sh.fountain.fountain.runtime.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LevenshteinDistanceTests {

    @Test
    public void testEmptyStrings() {
        Assertions.assertEquals(0, LevenshteinDistance.compare("", ""));
    }

    @Test
    public void testLeftEmpty() {
        Assertions.assertEquals(1, LevenshteinDistance.compare("", "1"));
    }

    @Test
    public void testRightEmpty() {
        Assertions.assertEquals(1, LevenshteinDistance.compare("1", ""));
    }

    @Test
    public void testSame() {
        Assertions.assertEquals(0, LevenshteinDistance.compare("Hello!", "Hello!"));
    }

    @Test
    public void testRightShorter() {
        Assertions.assertEquals(3, LevenshteinDistance.compare("Hello!", "Hel"));
    }

    @Test
    public void testLeftShorter() {
        Assertions.assertEquals(3, LevenshteinDistance.compare("Hel", "Hello!"));
    }
}
