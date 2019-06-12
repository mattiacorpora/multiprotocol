package it.inaf.iasfpa.mpm.view;

import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.um232hmanager.UM232HDeviceParameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * FifoController
 * @author M. Corpora, IASFPA, mattia.corpora@inaf.it
 *
 */

public class FifoController implements Initializable {
	
	@FXML
	public RadioButton syncRadioButton, asyncRadioButton;
	
	@FXML
	public TextField mtuField, delayField;
	
	private UM232HDeviceParameters param;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		syncRadioButton.setSelected(true);
		mtuField.setText("0");
		delayField.setText("0");
	}

	@FXML
	public void syncRadioButtonEvent(ActionEvent event) {
		param.setSyncMode(true);
	}
	
	@FXML
	public void asyncRadioButtonEvent(ActionEvent event) {
		param.setSyncMode(false);
	}
	
	public void setParameters(UM232HDeviceParameters par) {
		this.param = par;
	}
}
