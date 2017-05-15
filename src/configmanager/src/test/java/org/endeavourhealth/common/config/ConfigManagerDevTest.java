package org.endeavourhealth.common.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigManagerDevTest {
    @Test
    public void getConfiguration() throws Exception {
        ConfigManager.Initialize("global");
        String result = ConfigManager.getConfiguration("coding");
        Assert.assertEquals("{\r\n" +
                "   \"url\" : \"jdbc:postgresql://localhost:5432/coding\",\r\n" +
                "   \"username\" : \"postgres\",\r\n" +
                "   \"password\" : \"\"\r\n" +
                "}", result);
    }

	@Test
	public void testTimeout() throws Exception {
		final String INITIAL_DATA = "InitialData";
		final String CHANGED_DATA = "ChangedData";

		MockDatabaseLayer mockDb = new MockDatabaseLayer();
		ConfigManager._databaseLayer = mockDb;

		// Test basic retrieval
		mockDb.setConfiguration("test", "test", INITIAL_DATA);
		String result = ConfigManager.getConfiguration("test", "test");
		Assert.assertEquals(INITIAL_DATA, result);

		// Test Caching (should still be initial value)
		mockDb.setConfiguration("test", "test", CHANGED_DATA);
		result = ConfigManager.getConfiguration("test", "test");
		Assert.assertEquals(INITIAL_DATA, result);

		// Test Cache timeout (after timeout should be changed value);
		Thread.sleep(1 * 60 * 1000);
		result = ConfigManager.getConfiguration("test", "test");
		Assert.assertEquals(CHANGED_DATA, result);
	}
}