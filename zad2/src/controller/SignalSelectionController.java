package controller;
import java.net.URL;
import java.util.List;
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
import model.Signal;
import model.SignalsManager;

public class SignalSelectionController implements Initializable{

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<Signal> signalsComboBox;
    
    @FXML
    private Button okButton;
    
    private SignalsManager signalsManager;
    
    private String operation;
    
    private ObservableList<Signal> signals;
    
    private Signal sig1;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void initData(SignalsManager signalsManager, List<Signal> signals, String operation, Signal sig1) {
		this.signals = FXCollections.observableArrayList(signals);
		this.signalsManager = signalsManager;
		this.operation = operation;
		this.sig1 = sig1;
		signalsComboBox.setItems(this.signals);
		signalsComboBox.setValue(this.signals.get(0));
	}
	
    @FXML
    void okButtonAction(ActionEvent event) {
    	Signal sig2 = signalsComboBox.getValue();
    	switch(operation) {
    	case "Dodawanie":
    		signalsManager.addSignals(sig1, sig2, nameTextField.getText());
    		break;
    	case "Odejmowanie":
    		signalsManager.subSignals(sig1, sig2, nameTextField.getText());
    		break;
    	case "Mno¿enie":
    		signalsManager.mulSignals(sig1, sig2, nameTextField.getText());
    		break;
    	case "Dzielenie":
    		try {
				signalsManager.divSignals(sig1, sig2, nameTextField.getText());
			} catch (Exception e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.initStyle(StageStyle.UTILITY);
		        alert.setHeaderText("Wyst¹pi³ b³¹d");
				alert.setTitle("B³¹d");
		        alert.setContentText("Próba podzielenia przez zero!");
		        alert.showAndWait();
			}
    		break;
    	}
    }
}