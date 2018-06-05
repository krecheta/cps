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
    							"szum o rozk�adzie jednostajnym", 
    							"szum gausowski", 
    							"sygna� sinusoidalny",
    							"sygna� sinus. wyprostowany jednopo��wkowo", 
    							"sygna� sinus. wyprostowany dwupo��wkowo",
    							"sygna� prostok�tny", 
    							"sygna� prostok�tny symetryczny", 
    							"sygna� tr�jk�tny", 
    							"skok jednostkowy", 
    							"impuls jednostkowy", 
    							"szum impulsowy"
    							);
    
    SignalsManager signalsManager;
    MainController mainController;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		signalsComboBox.setItems(possibleSignals);
		//ustawiamy na start zaznaczenie na pierwszy element i wy��czamy odpowiednie pola
		signalsComboBox.setValue(possibleSignals.get(0));
		periodTextField.setDisable(true);
		fillFactoryTextField.setDisable(true);
		timeJumpTextField.setDisable(true);
		jumpTextField.setDisable(true);
		propabilityTextField.setDisable(true);
	}
	
	//metoda wywo�ywana z MainController
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
			case "szum o rozk�adzie jednostajnym":
				signalsManager
						.addSignal(new UniformDistributionNoise(name, amplitude, initTime, timeDuration, samplingFreq));
				break;

			case "szum gausowski":
				signalsManager.addSignal(new GaussianNoise(name, amplitude, initTime, timeDuration, samplingFreq));
				break;

			case "sygna� sinusoidalny":
				signalsManager.addSignal(new SinusoidalSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna� sinus. wyprostowany jednopo��wkowo":
				signalsManager.addSignal(new SinusoidalOneHalfSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna� sinus. wyprostowany dwupo��wkowo":
				signalsManager.addSignal(new SinusoidalTwoHalfSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText())));
				break;

			case "sygna� prostok�tny":
				signalsManager.addSignal(new RectangularSignal(name, amplitude, initTime, timeDuration, samplingFreq,
						Integer.parseInt(periodTextField.getText()),
						Double.parseDouble(fillFactoryTextField.getText())));
				break;

			case "sygna� prostok�tny symetryczny":
				signalsManager.addSignal(new SymmetricalRectangularSignal(name, amplitude, initTime, timeDuration,
						samplingFreq, Integer.parseInt(periodTextField.getText()),
						Double.parseDouble(fillFactoryTextField.getText())));
				break;
			case "sygna� tr�jk�tny":
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
	        alert.setHeaderText("Wyst�pi� b��d");
	        alert.setTitle("B��d");
	        alert.setContentText("Podano nieprawid�owe parametry sygna�u!");
	        alert.showAndWait();
		}
	}
	
	//listener do ComboBoxa, je�eli zmienimy sygna� to zostan� wy��czone odpowiednie pola
	@FXML
	private void comboAction(ActionEvent event) {
	    String selectedItem = signalsComboBox.getValue();
	    
	    periodTextField.setDisable(true);
		fillFactoryTextField.setDisable(true);
		timeJumpTextField.setDisable(true);
		jumpTextField.setDisable(true);
		propabilityTextField.setDisable(true);
	    
		if(selectedItem.equals("sygna� sinusoidalny") || 
				selectedItem.equals("sygna� sinus. wyprostowany jednopo��wkowo") ||
				selectedItem.equals("sygna� sinus. wyprostowany dwupo��wkowo")) {
			
			periodTextField.setDisable(false);
		
		} else if(selectedItem.equals("sygna� prostok�tny") ||
				selectedItem.equals("sygna� prostok�tny symetryczny") ||
				selectedItem.equals("sygna� tr�jk�tny")) {
			
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