package failingTest;

import baseTest.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FailingTest extends BaseTest {
    @Test
    public void failingTest() {
        assertEquals("actual", "expected", "Error: do not fix failing test.");
    }
}
