package org.endeavourhealth.common.config;

public class ConfigManagerException extends Exception {
    static final long serialVersionUID = 1L;

	public ConfigManagerException(String message, Exception exception) {
		super(message, exception);
	}

	public ConfigManagerException(String message) {
		super(message);
	}
}
