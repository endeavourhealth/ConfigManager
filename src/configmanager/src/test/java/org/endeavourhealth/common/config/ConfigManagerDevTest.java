package org.endeavourhealth.common.config;

import org.junit.Assert;
import org.junit.Test;

public class ConfigManagerDevTest {

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