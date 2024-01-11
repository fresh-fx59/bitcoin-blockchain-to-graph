package com.example.bitcoinblockchaintograph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.text.NumberFormat;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BitcoinBlockchainToGraphApplication {

	private static Log logger = LogFactory.getLog(BitcoinBlockchainToGraphApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BitcoinBlockchainToGraphApplication.class, args);

		Runtime runtime = Runtime.getRuntime();

		final NumberFormat format = NumberFormat.getInstance();

		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		final long mb = 1024 * 1024;
		final String mega = " MB";

		logger.info("========================== Memory Info ==========================");
		logger.info("Free memory: " + format.format(freeMemory / mb) + mega);
		logger.info("Allocated memory: " + format.format(allocatedMemory / mb) + mega);
		logger.info("Max memory: " + format.format(maxMemory / mb) + mega);
		logger.info("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega);
		logger.info("=================================================================\n");
	}
}