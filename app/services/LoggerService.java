package services;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;

import utilities.AcceptedLogger;
import utilities.RejectedLogger;

@Singleton
public class LoggerService {
	
	public final Logger acceptedLogger = Logger.getLogger(AcceptedLogger.class.getName());
	public final Logger rejectedLogger = Logger.getLogger(RejectedLogger.class.getName());
	
	public LoggerService() {
		try {
			AcceptedLogger.init();
			RejectedLogger.init();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem creating log files");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem creating log files");
		}
	}
}
