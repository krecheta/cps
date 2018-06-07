package model;

public class SymmetricalRectangularSignal extends RectangularSignal {

	public SymmetricalRectangularSignal(String name, double amplitude, int initTime, int duration,
			int samplingFrequency, int period, double fillFactory) {
		super(name, amplitude, initTime, duration, samplingFrequency, period, fillFactory);
		
	}
	
	@Override
	protected void calculateValues() {
		values = new double[samplingFrequency*duration];
		int kInterval = period * samplingFrequency;
		int k = 0;
		double t, t1;
		t1 = initTime/samplingFrequency;
		
		for(int i=0; i < values.length; i++) {
			if(i%kInterval == 0 && i != 0) {
				k++;
			}
			t = i/(double)samplingFrequency;
			
			if(t >= (k*period + t1) 
					&& t < (fillFactory*period + k*period + t1)) {
				values[i] = amplitude;
			} else if(t >= (fillFactory*period - k*period + t1) 
					&& t < (period + k*period + t1)) {
				values[i] = -amplitude;
			}
		}
	}
}
