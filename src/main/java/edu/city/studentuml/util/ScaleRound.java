package edu.city.studentuml.util;

public class ScaleRound {
    
    private ScaleRound() {
        throw new IllegalStateException("Utility class");
      }


      /**
       * Rounds a double to the nearest value with a minimum difference of 0.05.
       *
       * @param d the double to round
       * @return the rounded value
       */
    public static double roundTo05(double d) {
        // Convert to whole number
        double wholeNumber = d * 100;
    
        // Divide by 5 to get number of 0.05 intervals
        double intervals = wholeNumber / 5;
    
        // Round to nearest integer
        long rounded = Math.round(intervals);
    
        // Multiply by 5 to get number of 0.25 intervals
        double result = rounded * 5.0;
    
        // Divide by 100 to convert back to decimal
        result /= 100;
    
        return result;
    }
}
