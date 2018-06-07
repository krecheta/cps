package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import model.GaussianNoise;
import model.PulseNoise;
import model.RectangularSignal;
import model.SignalsManager;
import model.SinusoidalOneHalfSignal;
import model.SinusoidalSignal;
import model.SinusoidalTwoHalfSignal;
import model.SymmetricalRectangularSignal;
import model.TriangularSignal;
import model.UniformDistributionNoise;
import model.UnitJumpSignal;

public class NewSignalController implements Initializable{

    @FXML
    private ComboBox<String> signalsComboBox;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private TextField amplitudeTextField;

    @FXML
    private TextField initTimeTextField;

    @FXML
    private TextField timeDurationTextField;

    @FXML
    private TextField periodTextField;

    @FXML
    private TextField fillFactoryTextField;

    @FXML
    private TextField samplingFrequencyTextField;
    
    @FXML
    private TextField timeJumpTextField;
    
    @FXML
    private TextField jumpTextField;

    @FXML
    private TextField propabilityTextField;

    @FXML
    private Button addSignalButton;
    
    //opcje wyboru do ComboBoxa
    ObservableList<String> possibleSignals = FXCollections.observableArrayList(
    							"szum o rozk³adzie jednostajnym", 
    							"szum gausowski", 
    							"sygna³ sinusoidalny",
    							"sygna³ sinus. wyprostowany jednopo³ówkowo", 
    							"sygna³ sinus. wyprostowany dwupo³ówkowo",
    							"sygna³ prostok¹tny", 
    							"sygna³ prostok¹tny symetryczny", 
    							"sygna³ trójk¹tny", 
    							"skok jednostkowy", 
    							"impuls jednostkowy", 
    							"szum impulsowy"
    							);
    
    SignalsManager signalsManager;
    MainController mainController;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		signalsComboBox.setItems(possibleSignals);
		//ustawiamy na start zaznaczenie na pierwszy element i wy³¹czamy odpowiednie pola
		signalsComboBox.setValue(possibleSignals.get(0));
		periodTextField.setDisable(true);
		fillFactoryTextField.setDisable(true);
		timeJumpTextField.setDisable(true);
		jumpTextField.setDisable(true);
		propabilityTextField.setDisable(true);
	}
	
	//metoda wywo³ywana z MainController
	public void initData(SignalsManager signalsManager, MainController mainController) {
		this.signalsManager = signalsManager;
		this.mainController = mainController;
	}
	
	//listener do przycisku "dodaj" w okienku dodawania
	@FXML
	private void addButtonAction(ActionEvent event) {
		String selectedItem = signalsComboBox.getValue();
		try {
			String name = nameTextField.getText();
			if(name.length() == 0) {
				throw new Exception();
			}
			double amplitude = Double.parseDouble(amplitudeTextField.getText());
			int initTime = Integer.parseInt(initTimeTextField.getText());
			int timeDuration = Integer.parseInt(timeDurationTextField.getText());
			int samplingFreq = Integer.parseInt(samplingFrequencyTextField.getText());
			switch (selectedItem) {
			case "szum o rozk³adzie jednostajnym":
				signalsManager
						.addSignal(new UniformDistributionNoise(name, amplitude, initTime, timeDuration, samplingFreq));
				break;

			case "szum gausowski":
				signalsManager.addSignal(new GaussianNoise(name, amplitude, initTime, timeDuration, samplingFreq));
				break;

			case "sygna³ sinusoidalny":
				signalsManager.addSignal(new SinusoidalSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna³ sinus. wyprostowany jednopo³ówkowo":
				signalsManager.addSignal(new SinusoidalOneHalfSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna³ sinus. wyprostowany dwupo³ówkowo":
				signalsManager.addSignal(new SinusoidalTwoHalfSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna³ prostok¹tny":
				signalsManager.addSignal(new RectangularSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Integer.parseInt(periodTextField.getText()),
						Double.parseDouble(fillFactoryTextField.getText())));
				break;

			case "sygna³ prostok¹tny symetryczny":
				signalsManager.addSignal(new SymmetricalRectangularSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText()),
						Double.parseDouble(fillFactoryTextField.getText())));
				break;
			case "sygna³ trójk¹tny":
				signalsManager.addSignal(new TriangularSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Integer.parseInt(periodTextField.getText()),
						Double.parseDouble(fillFactoryTextField.getText())));
				break;
			case "skok jednostkowy":
				signalsManager.addSignal(new UnitJumpSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Double.parseDouble(timeJumpTextField.getText())));
				break;
			case "szum impulsowy":
				signalsManager.addSignal(new PulseNoise(name, amplitude, initTime, timeDuration, samplingFreq,
						Double.parseDouble(propabilityTextField.getText())));
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.initStyle(StageStyle.UTILITY);
	        alert.setHeaderText("Wyst¹pi³ b³¹d");
	        alert.setTitle("B³¹d");
	        alert.setContentText("Podano nieprawid³owe parametry sygna³u!");
	        alert.showAndWait();
		}
	}
	
	//listener do ComboBoxa, je¿eli zmienimy sygna³ to zostan¹ wy³¹czone odpowiednie pola
	@FXML
	private void comboAction(ActionEvent event) {
	    String selectedItem = signalsComboBox.getValue();
	    
	    periodTextField.setDisable(true);
		fillFactoryTextField.setDisable(true);
		timeJumpTextField.setDisable(true);
		jumpTextField.setDisable(true);
		propabilityTextField.setDisable(true);
	    
		if(selectedItem.equals("sygna³ sinusoidalny") || 
				selectedItem.equals("sygna³ sinus. wyprostowany jednopo³ówkowo") ||
				selectedItem.equals("sygna³ sinus. wyprostowany dwupo³ówkowo")) {
			
			periodTextField.setDisable(false);
		
		} else if(selectedItem.equals("sygna³ prostok¹tny") ||
				selectedItem.equals("sygna³ prostok¹tny symetryczny") ||
				selectedItem.equals("sygna³ trójk¹tny")) {
			
			periodTextField.setDisable(false);
			fillFactoryTextField.setDisable(false);
		
		} else if(selectedItem.equals("skok jednostkowy")) {
			
			timeJumpTextField.setDisable(false);
			
		} else if(selectedItem.equals("impuls jednostkowy")) {
			
			jumpTextField.setDisable(false);
			
		} else if(selectedItem.equals("szum impulsowy")) {
			
			propabilityTextField.setDisable(false);
		}

	}
}