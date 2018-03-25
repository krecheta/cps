package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import model.Signal;
import model.SignalsManager;

public class MainController implements Initializable{

    @FXML
    private ScatterChart<Double, Double> signalChart;
    
    @FXML
    private NumberAxis signalXAxis;
    
    @FXML
    private NumberAxis signalYAxis;
    
    @FXML
    private BarChart<String, Number> histogramChart;
    
    @FXML
    private Slider binsSlider;
    
    @FXML
    private Label samplingFrequencyLabel;
    
    @FXML
    private Label amplitudeLabel;

    @FXML
    private Label initTimeLabel;

    @FXML
    private Label timeDurationLabel;

    @FXML
    private Label periodLabel;

    @FXML
    private Label fillFactoryLabel;

    @FXML
    private Label avgValueLabel;

    @FXML
    private Label absAvgValueLabel;

    @FXML
    private Label avgPowerLabel;

    @FXML
    private Label varianceLabel;

    @FXML
    private Label effectiveValueLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;
    
    @FXML
    private ListView<Signal> signalsList;
    
    SignalsManager signalsManager;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		signalsManager = new SignalsManager();
		signalsList.setItems(signalsManager.getSignals());
		
		//listener dla listy - przy zmianie zaznaczenia rysuje wykresy i zmienia statystyki
		signalsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Signal>() {
			//wybrany element to newValue
			@Override
			public void changed(ObservableValue<? extends Signal> observable, Signal oldValue, Signal newValue) {
				drawChart();
				drawHistogram();
				showStatistics();
			}
		});
		
		//listener dla slidera - przy zmianie iloœci przedzia³ów rysuje nowy histogram
		binsSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isChanging) {
				if(!isChanging) {
					drawHistogram();
				}	
			}
		});
		
		signalXAxis.setAutoRanging(false);
		signalXAxis.setLabel("t[s]");
		
		signalYAxis.setAutoRanging(false);
		signalYAxis.setLabel("A");
		
		histogramChart.setLegendVisible(false);
		signalChart.setLegendVisible(false);
	}
	
	//listener do przycisku "dodaj" na panelu g³ównym - otwiera nowe okno
	@FXML
	private void addButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NewSignalView.fxml"));
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			
			//przekazanie wartoœci do AddSignalController
			loader.<NewSignalController>getController().initData(signalsManager, this);;
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showStatistics() {
		//pobieramy wybrany sygna³
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		//ustawiamy etykiety
		samplingFrequencyLabel.setText(String.valueOf(signal.getSamplingFrequency()));
		amplitudeLabel.setText(String.valueOf(signal.getAmplitude()));
		initTimeLabel.setText(String.valueOf(signal.getInitTime()));
		timeDurationLabel.setText(String.valueOf(signal.getDuration()));
		periodLabel.setText(String.valueOf(signal.getPeriod()));
		fillFactoryLabel.setText(String.valueOf(signal.getFillFactory()));
		avgValueLabel.setText(String.valueOf(signal.getAvgValue()));
		absAvgValueLabel.setText(String.valueOf(signal.getAvgAbsValue()));
		avgPowerLabel.setText(String.valueOf(signal.getAvgPower()));
		varianceLabel.setText(String.valueOf(signal.getVariance()));
		effectiveValueLabel.setText(String.valueOf(signal.getEffectiveValue()));
	}
	
	//rysuje wykres sygna³u
	private void drawChart() {
		//pobieramy wybrany sygna³
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		//parametry sygna³u
		double [] values = signal.getValues();
		int initTime = signal.getInitTime();
		int samplingFrequency = signal.getSamplingFrequency();
		
		//usuwamy istniej¹ce serie danych na wykresie
		signalChart.getData().clear();
		
		//dopasowujemy wartoœci osi x i y
		signalXAxis.setLowerBound(initTime - 1);
		signalXAxis.setUpperBound(initTime + signal.getDuration() + 1);
		//signalXAxis.setTickUnit((double)signal.getDuration() + 2 / 20);
		
		signalYAxis.setLowerBound(signal.getMinValue() - 2);
		signalYAxis.setUpperBound(signal.getMaxvalue() + 2);
		//signalYAxis.setTickUnit((double)signal.getMaxvalue() - signal.getMinValue() + 3 / 10);
		
		//dodajemy nowe dane
		ObservableList<ScatterChart.Data<Double, Double>> data = FXCollections.observableArrayList();
		
		for(int i = 0; i < values.length; i++) {
			data.add(new ScatterChart.Data<Double, Double>(
					(i/(double)samplingFrequency) + initTime, values[i]));
		}
		
		ScatterChart.Series<Double, Double> series = new ScatterChart.Series<>(data);
		signalChart.getData().add(series);
	}
	
	//rysuje histogram
	private void drawHistogram() {
		
		//pobieramy wybrany sygna³
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		//pobieramy liczbê przedzia³ów ze slajdera
		signalsManager.setHistogramBins((int)binsSlider.getValue());
		
		//bugfix
		histogramChart.getXAxis().setAnimated(false);
		histogramChart.setAnimated(true);
		
		//usuwamy istniej¹ce serie danych na wykresie
		histogramChart.getData().clear();
		
		//dodajemy nowe dane
		BarChart.Series<String, Number> series = new BarChart.Series<>();
		
		Map<String, Integer> values = signalsManager.getHistogramData(signal);
		
		values.forEach( (k,v) -> series.getData().add(new BarChart.Data<String, Number> (k,v)) );
		histogramChart.getData().add(series);
	}

}

