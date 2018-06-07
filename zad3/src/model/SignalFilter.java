package model;

import java.util.TreeMap;

public class SignalFilter {
	

	public static Signal signalWeave(Signal firstSignal, Signal secondSignal, String newSignalName){
		// set length of values weave function
		double[] weaveValues = new double[firstSignal.getValues().length + secondSignal.getValues().length -1];
		
		double time = (firstSignal.getDuration() + secondSignal.getDuration()) / (weaveValues.length*1.0);
		weaveValues = countWeaveValues(weaveValues, firstSignal, secondSignal );

	 Signal weaveSignal=  new Signal(newSignalName, 0, (int)Math.ceil( 1/time), weaveValues);
	 weaveSignal.amplitude = weaveSignal.getMaxvalue();
	 weaveSignal.duration =(int) Math.ceil(( weaveValues.length / weaveSignal.getSamplingFrequency()));
	 
	 return weaveSignal;
	}

	

	private static double[] countWeaveValues(double[] weaveValues, Signal firstSignal, Signal secondSignal) {
		double sum;
		double[] firstSignalValues = firstSignal.getValues();
		double[] secondSignalValues = secondSignal.getValues();

		 for (int i = 0; i < weaveValues.length; i++) {
	            sum = 0;
	            for (int j = 0; j < firstSignalValues.length; j++) {
	                if (i - j >= 0 && i - j < secondSignalValues.length) {
	                    sum += firstSignalValues[j] * secondSignalValues[secondSignalValues.length - (i - j) - 1 ];
	                }
	            }
	            weaveValues[i] = sum;
	        }
	  return weaveValues;
	}
	
	public static double[] countWeaveValuesWithoutSignal( double[] firstSignalValues, double[] secondSignalValues) {
		double sum;
		double[] weaveValues = new double[firstSignalValues.length + secondSignalValues.length - 1];
		
		 for (int i = 0; i < weaveValues.length; i++) {
	            sum = 0;
	            for (int j = 0; j < firstSignalValues.length; j++) {
	                if (i - j >= 0 && i - j < secondSignalValues.length) {
	                    sum += firstSignalValues[j] * secondSignalValues[secondSignalValues.length - (i - j) - 1 ];
	                }
	            }
	            weaveValues[i] = sum;
	        }
	  return weaveValues;
	}
	
	 public static double[] lowFilter(int RowFilterNumber) {
		 double[] result = new double[RowFilterNumber];
	        int  centerOfRow = (RowFilterNumber - 1) / 2;
	        double factor;

	        for (int i = 0; i < RowFilterNumber; i++) {
	            if (i == centerOfRow) {
	                factor = 2.0 / RowFilterNumber;
	            } else {
	                factor = (Math.sin( (2 * Math.PI * (i - centerOfRow)) / RowFilterNumber ) / (Math.PI * (i - centerOfRow)));
	            }
	            result[i] = factor;
	        }

	        return result;
	    }
	   public static double[] mediumFilter(double[] factors) {
	        double [] result = new double[factors.length];
	        double factor;

	        for (int i = 0; i < factors.length; i++) {
	            factor = factors[i] * 2 * Math.sin( (Math.PI * i) / 2.0 );
	            result[i] = factor;
	        }
	        return result;
	    }

	    public static double[] highFilter(double[] factors) {
	    	double[] result = new double[factors.length];
	        double factor;

	        for (int i = 0; i < factors.length; i++) {
	            factor = factors[i] * Math.pow( -1.0, i );
	            result[i] = factor;
	        }
	        return result;
	    }


	    public static double[] hammingWindow(double[] factors) {
	     
	    	double[] result = new double[factors.length];
	        double factor;

	        for (int i = 0; i < factors.length; i++) {
	            factor = (0.53836 - (0.46164 * Math.cos( (2 * Math.PI * i) / (factors.length *1.0))));
	            result[i] = factor * factors[i];
	        }
	        return result;
	    }

	    public static double[] hanningWindow(double[] factors) {
	        double[] result = new double[factors.length];
	        double factor;

	        for (int i = 0; i < factors.length; i++) {
	            factor = (0.5 - (0.5 * Math.cos( (2 * Math.PI * i) / (1.0*factors.length) )));
	            result[i] = factor * factors[i];
	        }
	        return result;
	    }

	    public static double[] blackmanWindow(double[] factors) {
	        double[] result = new double[factors.length];
	        double factor;

	        for (int i = 0; i < factors.length; i++) {
	            factor = (0.42 - (0.5 * Math.cos( (2 * Math.PI * i) / (1.0*factors.length) )) + 0.08 * Math.cos( (4 * Math.PI * i) /(1.0*factors.length) ));
	            result[i] = factor * factors[i];
	        }
	        return result;
	    }
	}

