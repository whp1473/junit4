package junit.tests.framework;

import junit.framework.TestCase;

/**
 * Tests class used in SuiteTest
 */
public class NotPublicTestCase extends TestCase {
    protected void testNotPublic() {
    }

    public void testPublic() {
    }
}