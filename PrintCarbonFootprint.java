package CarbonFootprintAssignment;

import java.util.Map;
import java.util.Map.Entry;

public class PrintCarbonFootprint {
	
	public static void print(Map<String, Double> carbonFootprints) {
	      for (Entry<String, Double> entry : carbonFootprints.entrySet()) {
	          System.out.println("Carbon Footprint for " + entry.getKey() + " is: " + entry.getValue());
	      }
	  }
}
