package it.inaf.iasfpa.mpm.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.utils.Script;
import it.inaf.iasfpa.mpm.utils.ScriptManager;
import it.inaf.iasfpa.mpm.utils.SingleCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ScriptGuiController implements Initializable {
	
	@FXML
	private TextField message, numberRepeat, waitTime, nOfZero;
	
	@FXML
	private Button addLine, open, save, load, removeLine;
	
	@FXML
	private RadioButton sendRB, receiveRB, sendRecRB;
	
	@FXML
	private CheckBox repeatBox, waitBox, threadBox;
	
	@FXML
	private ComboBox<String> errorControl, mtype, format, slaveAddress, recOptCombo;
	
	@FXML
	private TableView<SingleCommand> monitoring, control;
		 
	
	public boolean i2cMode = false;
	private ObservableList<SingleCommand> listMonitoring = FXCollections.observableArrayList();
	private ObservableList<SingleCommand> listcontrol = FXCollections.observableArrayList();
	private MainController main;
	
	
	
	public MainController getMain() {
		return main;
	}

	public void setMain(MainController main) {
		this.main = main;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		sendRB.setSelected(true);
		ObservableList<String> errorOpt = FXCollections.observableArrayList("XOR", "CRC16", "None");
		errorControl.setItems(errorOpt);
		ObservableList<String> formatOpt = FXCollections.observableArrayList("HEX", "CHAR");
		format.setItems(formatOpt);
		ObservableList<String> recOpt = FXCollections.observableArrayList("with ACK", "with NAK", "None");
		recOptCombo.setItems(recOpt);
		recOptCombo.setValue(recOpt.get(2));
		nOfZero.setText("0");
		format.setValue(formatOpt.get(0));
		errorControl.setValue(errorOpt.get(2));
		numberRepeat.setText("1");
		waitTime.setText("0");
		// inizio costruzione tabelle
		TableColumn<SingleCommand, String> cmd = new TableColumn<SingleCommand, String>("CMD");
		cmd.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleCommand, String> wait = new TableColumn<SingleCommand, String>("WAIT");
		wait.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleCommand, String> rt = new TableColumn<SingleCommand, String>("RT");
		rt.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleCommand, String> type = new TableColumn<SingleCommand, String>("MSG TYPE");
		type.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleCommand, String> msg = new TableColumn<SingleCommand, String>("MSG");
		msg.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleCommand, String> address = new TableColumn<SingleCommand, String>("ADDRESS");
		address.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleCommand, String> rOpt = new TableColumn<SingleCommand, String>("REC OPT");
		rOpt.setCellValueFactory(new PropertyValueFactory<>("rOpt"));
		TableColumn<SingleCommand, String> checksum = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum.setCellValueFactory(new PropertyValueFactory<>("crc"));
		
		TableColumn<SingleCommand, String> cmd1 = new TableColumn<SingleCommand, String>("CMD");
		cmd1.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleCommand, String> wait1 = new TableColumn<SingleCommand, String>("WAIT");
		wait1.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleCommand, String> rt1 = new TableColumn<SingleCommand, String>("RT");
		rt1.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleCommand, String> type1 = new TableColumn<SingleCommand, String>("MSG TYPE");
		type1.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleCommand, String> msg1 = new TableColumn<SingleCommand, String>("MSG");
		msg1.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleCommand, String> address1 = new TableColumn<SingleCommand, String>("ADDRESS");
		address1.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleCommand, String> rOpt1 = new TableColumn<SingleCommand, String>("REC OPT");
		rOpt1.setCellValueFactory(new PropertyValueFactory<>("rOpt"));
		TableColumn<SingleCommand, String> checksum1 = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum1.setCellValueFactory(new PropertyValueFactory<>("crc"));
		
		monitoring.getColumns().addAll(cmd, wait, rt, type, msg, address, rOpt, checksum);
		control.getColumns().addAll(cmd1, wait1, rt1, type1, msg1, address1, rOpt1, checksum1);
		monitoring.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		control.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		monitoring.setItems(listMonitoring);
		control.setItems(listcontrol);
		// fine costruzione tabelle
		
	}
	
	@FXML
	private void addButtonEvent() {
		String tempcmd = null;
		String rept = null;
		String wt = null;
		String sa = null;
		String ro = null;
		String tempmsg = message.getText();
		if(sendRB.isSelected()) {
			tempcmd = "S";
		}
		else if(receiveRB.isSelected()) {
			tempcmd = "R";
		} else if(sendRecRB.isSelected()) {
			tempcmd = "S/R";
		}
		
		if(repeatBox.isSelected()) {
			rept = numberRepeat.getText();
		} else {
			rept = "none";
		}
		
		if(waitBox.isSelected()) {
			wt = waitTime.getText();
		} else {
			wt = "none";
		}
		
		if(i2cMode) {
			sa = slaveAddress.getValue();
			ro = recOptCombo.getValue();
		} else{
			sa = "none";
			ro = "none";
		}
		
		
		if(format.getValue().equals("HEX")) {
			for(int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "00";
			}
		} else {
			for(int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "0";
			}
		}
		
		
		
		
		
		if(threadBox.isSelected() == false) {
			listcontrol.add(new SingleCommand(tempcmd, wt, rept, format.getValue(),
					tempmsg, ro, sa, errorControl.getValue()));
			
		}
		else if(threadBox.isSelected() == true) {
			listMonitoring.add(new SingleCommand(tempcmd, "0", rept, format.getValue(),
					tempmsg, ro, sa, errorControl.getValue()));
					
		}
			
	}
	
	@FXML
	private void removeLineButtonEvent() {
		SingleCommand rmlineM = monitoring.getSelectionModel().getSelectedItem();
		int mIndex = monitoring.getSelectionModel().getSelectedIndex();
		SingleCommand rmlineC = control.getSelectionModel().getSelectedItem();
		int cIndex = control.getSelectionModel().getSelectedIndex();
		if (rmlineM != null) {
			listMonitoring.remove(mIndex);
			
		}
		
		if (rmlineC != null) {
			listcontrol.remove(cIndex);
			
		}
		
	}
	
	@FXML
	private void saveButtonEvent() {
		Script script = new Script();
		ScriptManager sm = new ScriptManager();
		SingleCommand[] mon = new SingleCommand[listMonitoring.size()];
		SingleCommand[] con = new SingleCommand[listcontrol.size()];
		for(int i = 0; i < mon.length; i++) {
			mon[i] = listMonitoring.get(i);
		}
		for(int i = 0; i < con.length; i++) {
			con[i] = listcontrol.get(i);
		}
		script.setControl(con);
		script.setMonitoring(mon);
		File sFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Json Files", "*.json"));
		sFile = fileChooser.showSaveDialog(null);
		sm.createFileScript(script, sFile);
		
		
	}
	
	@FXML
	public void loadButtonEvent() {
		Script script = new Script();
		
		SingleCommand[] mon = new SingleCommand[listMonitoring.size()];
		SingleCommand[] con = new SingleCommand[listcontrol.size()];
		for(int i = 0; i < mon.length; i++) {
			mon[i] = listMonitoring.get(i);
		}
		for(int i = 0; i < con.length; i++) {
			con[i] = listcontrol.get(i);
		}
		script.setControl(con);
		script.setMonitoring(mon);
		main.loadScriptText(script);
		
		
	}
	
	@FXML
	public void openButtonEvent() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Json File", "*.json"));
		ScriptManager sm = new ScriptManager();
		Script script = sm.scriptFromJson(fileChooser.showOpenDialog(null));
		listcontrol.addAll(script.getControl());
		listMonitoring.addAll(script.getMonitoring());
		
	}
	
	public void setSlaveAddress(ObservableList<Integer> slaveList){{
		ObservableList<String> os = FXCollections.observableArrayList();
		
		for(int i = 0; i < slaveList.size(); i++) {
			os.add(String.valueOf(slaveList.get(i)));
		}
		slaveAddress.setItems(os);
	
	}
	}

}
