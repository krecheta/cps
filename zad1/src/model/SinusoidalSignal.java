package model;

//sygna³ sinusoidalny
public class SinusoidalSignal extends Signal {
	
	protected int period;
	
	public SinusoidalSignal(String name, double amplitude, int initTime, int duration, int samplingFrequency, int period) {
		super(name, amplitude, initTime, duration, samplingFrequency);
		this.period = period;
		calculateValues();
		calculateStatistics();
	}
	
	protected void calculateValues() {
		values = new double[duration*samplingFrequency];
		for(int i = 0; i < values.length; i++) {
			values[i] = amplitude * Math.sin((2*Math.PI)/period * ((i/(double)samplingFrequency)-initTime));
		}
	}
	
	@Override
	public String getPeriod() {
		return String.valueOf(period);
	}

}
