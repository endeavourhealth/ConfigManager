package org.endeavourhealth.common.config;

public class ConfigManagerException extends Exception {
	public ConfigManagerException(String message, Exception exception) {
		super(message, exception);
	}
}
