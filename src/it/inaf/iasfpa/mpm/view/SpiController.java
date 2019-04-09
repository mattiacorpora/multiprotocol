package it.inaf.iasfpa.mpm.view;

import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.um232hmanager.UM232HDeviceParameters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.sf.yad2xx.mpsse.SpiMode;

public class SpiController implements Initializable {

	@FXML
	public TextField clockfrequency;

	@FXML
	public ComboBox<Boolean> csStatusCombo;

	@FXML
	public ComboBox<String> spimode;

	private UM232HDeviceParameters param;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		clockfrequency.setText("100000");
		ObservableList<String> spimodeoption = FXCollections.observableArrayList("Mode 0", "Mode 2");
		ObservableList<Boolean> csStatus = FXCollections.observableArrayList(true, false);
		spimode.setItems(spimodeoption);
		csStatusCombo.setItems(csStatus);
		csStatusCombo.setValue(false);
		spimode.setValue(spimodeoption.get(0));
		
		
		clockfrequency.textProperty().addListener((obs, oldText, newText) -> {
		    param.setClockrate(Integer.parseInt(clockfrequency.getText()));
		});
		
		

	}

	@FXML
	private void changeSpiComboBox(ActionEvent event) {
		switch (spimode.getValue()) {
		case "Mode 0":
			param.setSpimode(SpiMode.M0);
			break;
		case "Mode 2":
			param.setSpimode(SpiMode.M2);
			break;
		default:
			param.setSpimode(SpiMode.M0);
			break;

		}

	}

	@FXML
	private void changeCsComboBox(ActionEvent event) {
		if (csStatusCombo.getValue()) {

			param.setCsStatus(true);
		} else {
			param.setCsStatus(false);
		}

	}
	
	public void setParameters(UM232HDeviceParameters param) {
		this.param = param;
		this.param.setClockrate(100000);
		this.param.setSpimode(SpiMode.M0);
		this.param.setCsStatus(false);
		this.param.setNumbRecByte(1);
		
		
	}
	
	

}
