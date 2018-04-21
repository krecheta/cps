package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Signal;
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
				if(newValue != null) {
					setButtonsDisable(false);
					showStatistics();
					drawChart();
					drawHistogram();
				} else {
					resetStatistics();
					resetCharts();
					setButtonsDisable(true);
				}
			}
		});
		
		//listener dla slidera - przy zmianie ilo�ci przedzia��w rysuje nowy histogram
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
		
		setButtonsDisable(true);
	}
	
	//listener do przycisku "dodaj" na panelu g��wnym - otwiera nowe okno
	@FXML
	private void addButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NewSignalView.fxml"));
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setResizable(false);
			
			//przekazanie warto�ci do AddSignalController
			loader.<NewSignalController>getController().initData(signalsManager, this);;
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//listener do przycisku usu�
	@FXML
	private void removeButtonAction(ActionEvent event) {
		signalsManager.removeSignal(signalsList.getSelectionModel().getSelectedItem());
		signalsList.getSelectionModel().select(null);
	}
	
	//listener do wczytywania sygna�u z pliku
	@FXML
	private void loadButtonAction(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(new Stage());
		
		if(file != null) {
			try {
				signalsManager.loadSignalFromFile(file.getAbsolutePath());
			} catch (IOException e) {
				showError("Wyst�pi� odczytu z pliku");
			}
		}
	}
	
	//listener do przycisku zapisz na panelu g��wnym
	@FXML
	private void saveButtonAction(ActionEvent event) {
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		try {
			FileChooser chooser = new FileChooser();
			File file = chooser.showSaveDialog(new Stage());
			
			if(file != null) {
				signalsManager.saveSignalToFile(signal, file.getAbsolutePath());
				showInformation("Sygna� zapisano do pliku " + file.getName());
			}
		} catch (FileNotFoundException e) {
			showError("Wyst�pi� zapisu do pliku");
		}
	}
	
	//listener do operacji arytmetycznych - pojawia si� nowe okno z mo�liwymi sygna�ami
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
			operation = "Mno�enie";
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
	
	private void showStatistics() {
		//pobieramy wybrany sygna�
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
	
	//rysuje wykres sygna�u
	private void drawChart() {
		//pobieramy wybrany sygna�
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		//parametry sygna�u
		double [] values = signal.getValues();
		int initTime = signal.getInitTime();
		int samplingFrequency = signal.getSamplingFrequency();
		
		//usuwamy istniej�ce serie danych na wykresie
		signalChart.getData().clear();
		
		//dopasowujemy warto�ci osi x i y
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
		
		//pobieramy wybrany sygna�
		Signal signal = signalsList.getSelectionModel().getSelectedItem();
		
		//pobieramy liczb� przedzia��w ze slajdera
		signalsManager.setHistogramBins((int)binsSlider.getValue());
		
		//bugfix
		histogramChart.getXAxis().setAnimated(false);
		histogramChart.setAnimated(true);
		
		//usuwamy istniej�ce serie danych na wykresie
		histogramChart.getData().clear();
		
		//dodajemy nowe dane
		BarChart.Series<String, Number> series = new BarChart.Series<>();
		
		Map<String, Integer> values = signalsManager.getHistogramData(signal);
		
		values.forEach( (k,v) -> series.getData().add(new BarChart.Data<String, Number> (k,v)) );
		histogramChart.getData().add(series);
	}
	
	private void resetCharts() {
		histogramChart.getData().clear();
		signalChart.getData().clear();
	}
	
	//wy�wietlanie komunikatu o b��dzie
	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initStyle(StageStyle.UTILITY);
		alert.setTitle("B��d");
        alert.setHeaderText("Wyst�pi� b�ad");
        alert.setContentText(message);
        alert.showAndWait();
	}
	
	//wy�wietlanie komunikatu
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
	}
}

