package controller;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
    	switch(operation) {
    	case "add":
    		Signal sig2 = signalsComboBox.getValue();
    		signalsManager.addSignals(sig1, sig2, nameTextField.getText());
    		break;
    	case "sub":
    		break;
    	case "mul":
    		break;
    	case "div":
    		break;
    	}
    }
}