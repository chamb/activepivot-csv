# activepivot-csv
ActivePivot application with a simple portfolio management data model (trades, products, risks and aggregations).

The project comes with a data generator that you must run at least once before starting the application.
The data generator executable is `com.activeviam.sandbox.generator.DataGenerator` and can be configured in the `src/main/resources/application.properties` file

Once the three data csv files are generated in the `data` directory, the application can be started. ActivePivot will parse the CSV files and load them into an in-memory cube, ready for analysis. The application executable class is `com.activeviam.sandbox.ActivePivotCSVApplication`.

The project is packaged with Spring Boot, when built with maven (`mvn clean install`) it produces a single executable jar file including all its dependencies. Simply run `java -jar activepivot-csv-1.0.0-SNAPSHOT`.
