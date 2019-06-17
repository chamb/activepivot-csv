/*
 * (C) ActiveViam 2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.sandbox.cfg;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.activeviam.sandbox.generator.DataGenerator;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.ICSVSourceConfiguration;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.store.transaction.ITransactionManager;
import com.qfs.util.timing.impl.StopWatch;

/**
 * Spring configuration for data sources
 * 
 * @author ActiveViam
 *
 */
public class DataLoadingConfig {

    private static final Logger LOGGER = Logger.getLogger(DataLoadingConfig.class.getSimpleName());

    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;


    
	/*
	 * **************************** Data loading *********************************
	 */
    @Bean
    @DependsOn(value = "startManager")
    public Void loadData() throws Exception {
    	
    	CSVSource<Path> source = new CSVSource<>();
		final Properties sourceProps = new Properties();
		final String parserThreads = env.getProperty("csvSource.parserThreads", "4");
		sourceProps.setProperty(ICSVSourceConfiguration.PARSER_THREAD_PROPERTY, parserThreads);
		source.configure(sourceProps);

    	ICSVParserConfiguration tradeConfig = source.createParserConfiguration(Arrays.asList(
    			"Id",
    			"ProductId",
    			"ProductQtyMultiplier",
    			"Desk",
    			"Book",
    			"Trader",
    			"Counterparty", 
    			"Date",
    			"Status",
    			"IsSimulated"));
    	tradeConfig.setSeparator(DataGenerator.CSV_SEPARATOR);
    	tradeConfig.setNumberSkippedLines(1);

    	ICSVParserConfiguration productConfig = source.createParserConfiguration(Arrays.asList(
    			"Id",
    			"ProductName",
    			"ProductType",
    			"UnderlierCode",
    			"UnderlierCurrency",
    			"UnderlierType",
    			"UnderlierValue",
    			"ProductBaseMtm",
    			"BumpedMtmUp",
    			"BumpedMtmDown",
    			"Theta",
    			"Rho"));
    	productConfig.setSeparator(DataGenerator.CSV_SEPARATOR);
    	productConfig.setNumberSkippedLines(1);
    	
    	ICSVParserConfiguration riskConfig = source.createParserConfiguration(Arrays.asList(
    			"TradeId",
    			"Delta",
    			"Gamma",
    			"Vega",
    			"Pnl",
    			"PnlDelta",
    			"PnlVega"));
    	riskConfig.setSeparator(DataGenerator.CSV_SEPARATOR);
    	riskConfig.setNumberSkippedLines(1);
    	
    	FileSystemCSVTopicFactory topicFactory = new FileSystemCSVTopicFactory(false);
    	source.addTopic(topicFactory.createDirectoryTopic(
    			"Trades", "./data", "glob:*trades*.csv", tradeConfig));
    	source.addTopic(topicFactory.createDirectoryTopic(
    			"Products", "./data", "glob:*products*.csv", productConfig));
    	source.addTopic(topicFactory.createDirectoryTopic(
    			"Risks", "./data", "glob:*risks*.csv", riskConfig));

    	CSVMessageChannelFactory<Path> factory = new CSVMessageChannelFactory<Path>(source, datastore);
    	
    	final ITransactionManager tm = datastore.getTransactionManager();
    	
    	// Load data into ActivePivot
    	final long before = System.nanoTime();
    	
    	// Transaction for TV data
	    tm.startTransaction();
	    	
	    source.fetch(Arrays.asList(
	    		factory.createChannel("Trades"),
	    		factory.createChannel("Products"),
	    		factory.createChannel("Risks")));
	    	
	    tm.commitTransaction();


    	
    	final long elapsed = System.nanoTime() - before;
    	LOGGER.info("Data load completed in " + elapsed / 1000000L + "ms");
    	
    	printStoreSizes();
    	
    	topicFactory.close();
    	source.close();

    	return null;
    	
    }

	private void printStoreSizes() {

		// Print stop watch profiling
		StopWatch.get().printTimings();
		StopWatch.get().printTimingLegend();

		// print sizes
		SchemaPrinter.printStoresSizes(datastore.getHead().getSchema());
	}

}
