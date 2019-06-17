/*
 * (C) ActiveViam 2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.sandbox.cfg.pivot;

import static com.activeviam.copper.columns.Columns.count;
import static com.activeviam.copper.columns.Columns.sum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.builders.BuildingContext;
import com.activeviam.copper.builders.dataset.Datasets.Dataset;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder.INamedCubeDescriptionBuilder;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo.LevelType;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;

/**
 * 
 * Configuration of the cube, hierarchies and aggregations.
 * 
 * @author ActiveViam
 *
 */
@Configuration
public class ActivePivotManagerConfig implements IActivePivotManagerDescriptionConfig {
	
	/* ********** */
	/* Formatters */
	/* ********** */
	public static final String DOUBLE_FORMAT = "DOUBLE[##.00]";


	/** The datastore schema {@link IDatastoreSchemaDescription description}. */
	@Autowired
	protected IDatastoreSchemaDescription datastoreDescription;

	@Override
	@Bean
	public IActivePivotManagerDescription managerDescription() {
		
		return StartBuilding.managerDescription("NanoPivot")
				.withCatalog("ActivePivot Catalog")
				.containingAllCubes()
				.withSchema("ActivePivot Schema")
				.withSelection(createNanoPivotSchemaSelectionDescription(this.datastoreDescription))
				.withCube(createCubeDescription())
				.build();
	}
	
	/**
	 * Creates the {@link ISelectionDescription} for NanoPivot Schema.
	 * 
	 * @param datastoreDescription : The datastore description
	 * @return The created selection description
	 */
	public static ISelectionDescription createNanoPivotSchemaSelectionDescription(
			final IDatastoreSchemaDescription datastoreDescription) {
		return StartBuilding.selection(datastoreDescription)
				.fromBaseStore("Risks")
				.withAllReachableFields()
				.build();
	}
	
	/**
	 * Creates the cube description.
	 * @return The created cube description
	 */
	public static IActivePivotInstanceDescription createCubeDescription() {
		return configureCubeBuilder(StartBuilding.cube("Sandbox Light")).build();
	}

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 * @param builder The builder to configure
	 * @return The configured builder
	 */
	public static ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
			final INamedCubeDescriptionBuilder builder) {
		
		return builder
				.withDimensions(ActivePivotManagerConfig::dimensions)
				
				//Suggestion : PostProcessor definitions can be added here
				
				.withDescriptionPostProcessor(
						StartBuilding.copperCalculations()
							.withDefinition(ActivePivotManagerConfig::coPPerCalculations)
							.build()
					)
				;
	}


	/**
	 * Adds the dimensions descriptions to the input
	 * builder.
	 *
	 * @param builder The cube builder
	 * @return The builder for chained calls
	 */
	public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions (ICanStartBuildingDimensions builder) {
		
		return builder
				
			.withDimension("Products")
				.withHierarchy("Underlier")
					.withLevels("UnderlierType", "UnderlierCode")
				.withHierarchy("Product").asDefaultHierarchy()
					.withLevel("ProductType")
					.withLevel("ProductName")
				.withHierarchy("Currency")
					.withLevel("UnderlierCurrency")
						.withFirstObjects("EUR", "GBP", "USD", "JPY")
				
			.withDimension("Booking")
				.withHierarchyOfSameName().asDefaultHierarchy()
					.withLevels("Desk", "Book")
				.withSingleLevelHierarchy("Traders").withPropertyName("Trader")
				.withSingleLevelHierarchy("Counterparties").withPropertyName("Counterparty")
						
			.withDimension("Date")
				.withHierarchyOfSameName()
					.withLevel("Date")
						.withType(LevelType.TIME)
						.withFormatter("DATE[yyyy-MM-dd]");
	}

	/* ******************* */
	/* Measures definition */
	/* ******************* */
	
	/**
	 * The CoPPer calculations to add to the cube
	 * @param context The context with which to build the calculations.
	 */
	public static void coPPerCalculations(BuildingContext context) {
		ActivePivotManagerConfig.someAggregatedMeasures(context).publish();
	}

	
	/**
	 * Define some calculations using the COPPER API.
	 *
	 * @param context The CoPPer build context.
	 *
	 * @return The Dataset of the aggregated measures.
	 */		
	protected static Dataset someAggregatedMeasures(final BuildingContext context) {
		
		return context.createDatasetFromFacts()
			.agg(
				count().as("Trade Count"),
				sum("ProductBaseMtm").as("PnL").withFormatter(DOUBLE_FORMAT),
				sum("PnlDelta").as("PnlDelta").withFormatter(DOUBLE_FORMAT),
				sum("PnlVega").as("PnlVega").withFormatter(DOUBLE_FORMAT),
				sum("Delta").as("Delta").withFormatter(DOUBLE_FORMAT),
				sum("Vega").as("Vega").withFormatter(DOUBLE_FORMAT)
			);

	}


}
