package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Signal;
import model.SignalConverter;
import model.SignalsManager;
import model.SinusoidalOneHalfSignal;

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
    
    @FXML
    private Button plusButton;

    @FXML
    private Button subButton;

    @FXML
    private Button mulButton;

    @FXML
    private Button divButton;
    
    @FXML
    private TextField frequencyTxtField;

    @FXML
    private Button samplingButton;
    
    @FXML
    private TextField bitsTxtField;

    @FXML
    private Button quantizationButton;
    
    @FXML
    private LineChart<String, Number> samplingChart;
    
    @FXML
    private LineChart<String, Number> quantizationChart;
    
    @FXML
    private LineChart<String, Number> reconstructedChart;
    
    @FXML
    private CategoryAxis samplXAxis;
    
    @FXML
    private NumberAxis samplYAxis;
    
    @FXML
    private ComboBox<String> recComboBox;
    
    @FXML
    private Button recButton;
    
    @FXML
    private Label mseLabel;
    
    @FXML
    private Label snrLabel;
    
    @FXML
    private Label psnrLabel;
    
    @FXML
    private Label mdLabel;
    
    SignalsManager signalsManager;
    SignalConverter conv;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		signalsManager = new SignalsManager();
		signalsList.setItems(signalsManager.getSignals());
		
		//listener dla listy - przy zmianie zaznaczenia rysuje wykresy i zmienia statystyki
		signalsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Signal>() {
			//wybrany element to newValue
			@Override
			public void changed(ObservableValue<? extends Signal> observable, Signal oldValue, Signal newValue) {
				if(newValue != null) {
					setButtonsDisable(false);
					showStatistics();
					drawChart();
					drawHistogram();
					samplingChart.getData().clear();
					quantizationChart.getData().clear();
					resetStatistics2();
					
				} else {
					resetStatistics2();
					resetStatistics();
					resetCharts();
					setButtonsDisable(true);
				}
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
		samplingChart.setLegendVisible(false);
		
		samplingChart.setCreateSymbols(false);
		quantizationChart.setCreateSymbols(false);
		reconstructedChart.setCreateSymbols(false);
		
		setButtonsDisable(true);
		
		ObservableList<String> possibleMethods = FXCollections.observableArrayList(
				"Ekstrapolacja zerowego rzêdu", 
				"Interpolacja pierwszego rzêdu", 
				"Funkcja sinc"
				);
		
		recComboBox.setItems(possibleMethods);
		recComboBox.setValue(possibleMethods.get(0));
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
	
	//listener do przycisku usuñ
	@FXML
	private void removeButtonAction(ActionEvent event) {
		signalsManager.removeSignal(signalsList.getSelectionModel().getSelectedItem());
		signalsList.getSelectionModel().select(null);
	}
	
	//listener do wczytywania sygna³u z pliku
	@FXML
	private void loadButtonAction(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(new Stage());
		
		if(file != null) {
			try {
				signalsManager.loadSignalFromFile(file.getAbsolutePath());
			} catch (IOException e) {
				showError("Wyst¹pi³ odczytu z pliku");
			}
		}
	}
	
	//listener do przycisku zapisz na panelu g³ównym
	@FXML
	private void saveButtonAction(ActionEvent event) {
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		try {
			FileChooser chooser = new FileChooser();
			File file = chooser.showSaveDialog(new Stage());
			
			if(file != null) {
				signalsManager.saveSignalToFile(signal, file.getAbsolutePath());
				showInformation("Sygna³ zapisano do pliku " + file.getName());
			}
		} catch (FileNotFoundException e) {
			showError("Wyst¹pi³ zapisu do pliku");
		}
	}
	
	//listener do operacji arytmetycznych - pojawia siê nowe okno z mo¿liwymi sygna³ami
	@FXML
	private void arthmeticOperationAction(ActionEvent event) {
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		List<Signal> signals = signalsManager.getSignalsToArthmeticOperation(signal);
		String operation;
			
		if(event.getSource() == plusButton) {
			operation = "Dodawanie";
		} else if(event.getSource() == subButton) {
			operation = "Odejmowanie";
		} else if(event.getSource() == mulButton) {
			operation = "Mno¿enie";
		} else {
			operation = "Dzielenie";
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignalSelectionView.fxml"));
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setTitle(operation);
			loader.<SignalSelectionController>getController().initData(signalsManager, signals, operation, signal);;
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//listener dla próbkowania na drugiej zak³adce
	@FXML
	private void samplingButtonAction(ActionEvent event) {
		double frequency = Double.parseDouble(frequencyTxtField.getText());
		conv = new SignalConverter(signalsList.getSelectionModel().getSelectedItem());
		try {
			drawSamplingChart(conv.sampleSignal(frequency));
			quantizationChart.getData().clear();
			reconstructedChart.getData().clear();
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
	}
	
	//listener dla kwantyzacji na drugiej zak³adce
	@FXML
	private void quantizationButtonAction(ActionEvent event) {
		int bits = 0;
		try {
			bits = Integer.parseInt(bitsTxtField.getText());
		} catch(Exception e) {
			showError("Nieprawid³owa liczba bitów!");
		}
		drawQuantizedSignal(conv.quantization(bits));
	}
	
	@FXML
	private void recButtonAction(ActionEvent event) {
		String selected = recComboBox.getValue();
		switch(selected){
		case "Ekstrapolacja zerowego rzêdu":
			drawReconstructed(conv.extrapolation());
			conv.calculateStatistics();
			showStatistics2();
			break;
		case "Interpolacja pierwszego rzêdu":
			drawReconstructed(conv.interpolation());
			conv.calculateStatistics();
			showStatistics2();
			break;
		case "Funkcja sinc":
			drawReconstructed(conv.sinc());
			conv.calculateStatistics();
			showStatistics2();
			break;
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
	
	private void showStatistics2() {
		mseLabel.setText(String.format("%.2f", conv.getMSE()));
		snrLabel.setText(String.format("%.2f", conv.getSNR()));
		psnrLabel.setText(String.format("%.2f", conv.getPSNR()));
		mdLabel.setText(String.format("%.2f", conv.getMD()));
	}
	
	private void resetStatistics() {
		samplingFrequencyLabel.setText("-");
		amplitudeLabel.setText("-");
		initTimeLabel.setText("-");
		timeDurationLabel.setText("-");
		periodLabel.setText("-");
		fillFactoryLabel.setText("-");
		avgValueLabel.setText("-");
		absAvgValueLabel.setText("-");
		avgPowerLabel.setText("-");
		varianceLabel.setText("-");
		effectiveValueLabel.setText("-");
	}
	
	private void resetStatistics2() {
		mseLabel.setText("-");
		snrLabel.setText("-");
		psnrLabel.setText("-");
		mdLabel.setText("-");
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
	
	private void drawSamplingChart(double[] values) {
		int initTime = signalsList.getSelectionModel().getSelectedItem().getInitTime();
		double samplingFrequency = Double.parseDouble(frequencyTxtField.getText());
		//usuwamy istniej¹ce serie danych na wykresie
		samplingChart.getData().clear();
		
		//dodajemy nowe dane
		ObservableList<LineChart.Data<String, Number>> data1 = FXCollections.observableArrayList();
		for(int i = 0; i < values.length; i++) {
			data1.add(new LineChart.Data<String, Number>(
					String.format("%.2f", i/(double)samplingFrequency + initTime), values[i]));
		}
		LineChart.Series<String, Number> series1 = new LineChart.Series<>(data1);
		
		double[] values2 = signalsList.getSelectionModel().getSelectedItem().getValues();
		int samplingFrequency2 = signalsList.getSelectionModel().getSelectedItem().getSamplingFrequency();
		
		ObservableList<LineChart.Data<String, Number>> data2 = FXCollections.observableArrayList();
		for(int i = 0; i < values2.length; i++) {
			data2.add(new LineChart.Data<String, Number>(
					String.format("%.2f", i/(double)samplingFrequency2 + initTime), values2[i]));
		}
		LineChart.Series<String, Number> series2 = new LineChart.Series<>(data2);
		
		samplingChart.getData().add(series2);
		samplingChart.getData().add(series1);	
	}
	
	private void drawQuantizedSignal(Map<String, Double> quantization) {
		int initTime = signalsList.getSelectionModel().getSelectedItem().getInitTime();
		//usuwamy istniej¹ce serie danych na wykresie
		quantizationChart.getData().clear();
		
		ObservableList<LineChart.Data<String, Number>> data1 = FXCollections.observableArrayList();
		for(Entry<String, Double> entry : quantization.entrySet()) {
			data1.add(new LineChart.Data<String, Number>(entry.getKey(), entry.getValue()));
		}
		LineChart.Series<String, Number> series1 = new LineChart.Series<>(data1);
		
		double[] values2 = conv.getSampledValues();
		double samplingFrequency2 = conv.getSamplingFrequency();
		
		ObservableList<LineChart.Data<String, Number>> data2 = FXCollections.observableArrayList();
		for(int i = 0; i < values2.length; i++) {
			data2.add(new LineChart.Data<String, Number>(
					String.format("%.2f", i/(double)samplingFrequency2 + initTime), values2[i]));
		}
		LineChart.Series<String, Number> series2 = new LineChart.Series<>(data2);
		
		quantizationChart.getData().add(series2);
		quantizationChart.getData().add(series1);	
		
	}
	
	private void drawReconstructed(Map<String, Double> reconstructedValues) {
		reconstructedChart.getData().clear();
		
		//sygna³ zrekonstruowany
		ObservableList<LineChart.Data<String, Number>> data1 = FXCollections.observableArrayList();
		for(Entry<String, Double> entry : reconstructedValues.entrySet()) {
			data1.add(new LineChart.Data<String, Number>(entry.getKey(), entry.getValue()));
		}
		LineChart.Series<String, Number> series1 = new LineChart.Series<>(data1);

		//sygna³ oryginalny
		int initTime = signalsList.getSelectionModel().getSelectedItem().getInitTime();
		double[] values2 = signalsList.getSelectionModel().getSelectedItem().getValues();
		int samplingFrequency2 = signalsList.getSelectionModel().getSelectedItem().getSamplingFrequency();
		
		ObservableList<LineChart.Data<String, Number>> data2 = FXCollections.observableArrayList();
		for(int i = 0; i < values2.length; i++) {
			data2.add(new LineChart.Data<String, Number>(
					String.format("%.2f", i/(double)samplingFrequency2 + initTime), values2[i]));
		}
		LineChart.Series<String, Number> series2 = new LineChart.Series<>(data2);

		reconstructedChart.getData().add(series2);
		reconstructedChart.getData().add(series1);	
	}
	
	private void resetCharts() {
		histogramChart.getData().clear();
		signalChart.getData().clear();
		samplingChart.getData().clear();
		quantizationChart.getData().clear();
		reconstructedChart.getData().clear();
	}
	
	//wyœwietlanie komunikatu o b³êdzie
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("B³¹d");
        alert.setHeaderText("Wyst¹pi³ b³ad");
        alert.setContentText(message);
        alert.showAndWait();
	}
	
	//wyœwietlanie komunikatu
	private void showInformation(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText("Informacja");
        alert.setTitle("Informacja");
        alert.setContentText(message);
        alert.showAndWait();
	}
	
	private void setButtonsDisable(boolean b) {
		saveButton.setDisable(b);
		removeButton.setDisable(b);
		plusButton.setDisable(b);
		subButton.setDisable(b);
		mulButton.setDisable(b);
		divButton.setDisable(b);
		samplingButton.setDisable(b);
		quantizationButton.setDisable(b);
	}
}

