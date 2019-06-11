package it.inaf.iasfpa.mpm.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.utils.Script;
import it.inaf.iasfpa.mpm.utils.ScriptManager;
import it.inaf.iasfpa.mpm.utils.SingleCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
	private Button addMonitoringLine, addControlLine, open, save, load;

	@FXML
	private RadioButton sendRB, receiveRB, sendRecRB;

	@FXML
	private CheckBox repeatBox, waitBox;

	@FXML
	private ComboBox<String> errorControl, mtype, format, slaveAddress, recOptCombo;

	@FXML
	private TableView<SingleCommand> monitoring, control;
	private ContextMenu singleCommandRightClickMenuM = new ContextMenu();
	private ContextMenu singleCommandRightClickMenuC = new ContextMenu();
	private MenuItem deleteItemM = new MenuItem("Remove");
	private MenuItem moveUpItemM = new MenuItem("Move Up");
	private MenuItem moveDownItemM = new MenuItem("Move Down");
	private MenuItem deleteItemC = new MenuItem("Remove");
	private MenuItem moveUpItemC = new MenuItem("Move Up");
	private MenuItem moveDownItemC = new MenuItem("Move Down");
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
		ObservableList<String> recOpt = FXCollections.observableArrayList("withACK", "withNACK", "None");
		recOptCombo.setItems(recOpt);
		recOptCombo.setValue(recOpt.get(2));
		nOfZero.setText("0");
		format.setValue(formatOpt.get(0));
		errorControl.setValue(errorOpt.get(2));
		numberRepeat.setText("1");
		waitTime.setText("0");

		// costruzione tabella monitoring
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
		TableColumn<SingleCommand, String> recOptc = new TableColumn<SingleCommand, String>("REC OPT");
		recOptc.setCellValueFactory(new PropertyValueFactory<>("roptc"));
		TableColumn<SingleCommand, String> checksum = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum.setCellValueFactory(new PropertyValueFactory<>("crc"));
		monitoring.getColumns().add(cmd);
		monitoring.getColumns().add(wait);
		monitoring.getColumns().add(rt);
		monitoring.getColumns().add(type);
		monitoring.getColumns().add(msg);
		monitoring.getColumns().add(address);
		monitoring.getColumns().add(recOptc);
		monitoring.getColumns().add(checksum);
		monitoring.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		monitoring.setItems(listMonitoring);

		// costruzione tabella control
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
		TableColumn<SingleCommand, String> recOptc1 = new TableColumn<SingleCommand, String>("REC OPT");
		recOptc1.setCellValueFactory(new PropertyValueFactory<>("roptc"));
		TableColumn<SingleCommand, String> checksum1 = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum1.setCellValueFactory(new PropertyValueFactory<>("crc"));
		control.getColumns().add(cmd1);
		control.getColumns().add(wait1);
		control.getColumns().add(rt1);
		control.getColumns().add(type1);
		control.getColumns().add(msg1);
		control.getColumns().add(address1);
		control.getColumns().add(recOptc1);
		control.getColumns().add(checksum1);
		control.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		control.setItems(listcontrol);
		// fine costruzione tabelle
		
		//context menu
		
		
		this.singleCommandRightClickMenuM.getItems().addAll(deleteItemM, moveUpItemM, moveDownItemM);
		this.singleCommandRightClickMenuC.getItems().addAll(deleteItemC, moveUpItemC, moveDownItemC);
		monitoring.setContextMenu(singleCommandRightClickMenuM);
		control.setContextMenu(singleCommandRightClickMenuC);
		
		
        deleteItemM.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                listMonitoring.remove(monitoring.getSelectionModel().getSelectedIndex());
            }
        });
        
        moveUpItemM.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
                int lastPosition = monitoring.getSelectionModel().getSelectedIndex();
                if(lastPosition != 0) {
                	SingleCommand sm = monitoring.getSelectionModel().getSelectedItem();
                    listMonitoring.remove(lastPosition);
                    listMonitoring.add(lastPosition-1, sm);
                }
                
           
            }
        });
        
        moveDownItemM.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
            	int lastPosition = monitoring.getSelectionModel().getSelectedIndex();
                if(lastPosition != listMonitoring.size()-1) {
                	SingleCommand sm = monitoring.getSelectionModel().getSelectedItem();
                    listMonitoring.remove(lastPosition);
                    listMonitoring.add(lastPosition+1, sm);
                }
            	
            }
        });
        
        
        
        
        deleteItemC.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
                listcontrol.remove(control.getSelectionModel().getSelectedIndex());
            }
        });
        
                       
        moveUpItemC.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
            	int lastPosition = control.getSelectionModel().getSelectedIndex();
                if(lastPosition != 0) {
                	SingleCommand sm = control.getSelectionModel().getSelectedItem();
                    listcontrol.remove(lastPosition);
                    listcontrol.add(lastPosition-1, sm);
                }
            }
        });
        
        moveDownItemC.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
            	int lastPosition = control.getSelectionModel().getSelectedIndex();
                if(lastPosition != listcontrol.size()-1) {
                	SingleCommand sm = control.getSelectionModel().getSelectedItem();
                    listcontrol.remove(lastPosition);
                    listcontrol.add(lastPosition+1, sm);
                }
            }
        });
        
        

	}

	@FXML
	private void addMonitoringButtonEvent() {
		String tempcmd = null;
		String rept = null;
		String wt = null;
		String sa = null;
		String ro = null;
		String tempmsg = message.getText();
		if (sendRB.isSelected()) {
			tempcmd = "S";
		} else if (receiveRB.isSelected()) {
			tempcmd = "R";
		} else if (sendRecRB.isSelected()) {
			tempcmd = "S/R";
		}

		if (repeatBox.isSelected()) {
			rept = numberRepeat.getText();
		} else {
			rept = "0";
		}

		if (waitBox.isSelected()) {
			wt = waitTime.getText();
		} else {
			wt = "0";
		}

		if (i2cMode) {
			sa = slaveAddress.getValue();
			ro = recOptCombo.getValue();
		} else {
			sa = "none";
			ro = "none";
		}

		if (format.getValue().equals("HEX")) {
			for (int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "00";
			}
		} else {
			for (int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "0";
			}
		}

		listMonitoring.add(new SingleCommand(tempcmd, wt, rept, format.getValue(), tempmsg, ro, sa, errorControl.getValue()));

		

	}
	
	@FXML
	private void addControlButtonEvent() {
		String tempcmd = null;
		String rept = null;
		String wt = null;
		String sa = null;
		String ro = null;
		String tempmsg = message.getText();
		if (sendRB.isSelected()) {
			tempcmd = "S";
		} else if (receiveRB.isSelected()) {
			tempcmd = "R";
		} else if (sendRecRB.isSelected()) {
			tempcmd = "S/R";
		}

		if (repeatBox.isSelected()) {
			rept = numberRepeat.getText();
		} else {
			rept = "0";
		}

		if (waitBox.isSelected()) {
			wt = waitTime.getText();
		} else {
			wt = "0";
		}

		if (i2cMode) {
			sa = slaveAddress.getValue();
			ro = recOptCombo.getValue();
		} else {
			sa = "none";
			ro = "none";
		}

		if (format.getValue().equals("HEX")) {
			for (int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "00";
			}
		} else {
			for (int i = 0; i < Integer.parseInt(nOfZero.getText()); i++) {
				tempmsg += "0";
			}
		}

		listcontrol.add(new SingleCommand(tempcmd, wt, rept, format.getValue(), tempmsg, ro, sa, errorControl.getValue()));
			

	}

	

	@FXML
	private void saveButtonEvent() {
		Script script = new Script();
		ScriptManager sm = new ScriptManager();
		SingleCommand[] mon = new SingleCommand[listMonitoring.size()];
		SingleCommand[] con = new SingleCommand[listcontrol.size()];
		for (int i = 0; i < mon.length; i++) {
			mon[i] = listMonitoring.get(i);
		}
		for (int i = 0; i < con.length; i++) {
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
		for (int i = 0; i < mon.length; i++) {
			mon[i] = listMonitoring.get(i);
		}
		for (int i = 0; i < con.length; i++) {
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

	public void setSlaveAddress(ObservableList<Integer> slaveList) {
		{
			ObservableList<String> os = FXCollections.observableArrayList();

			for (int i = 0; i < slaveList.size(); i++) {
				os.add(String.valueOf(slaveList.get(i)));
			}
			slaveAddress.setItems(os);

		}
	}
	
	public void i2cModeSelected() {
		if(!this.i2cMode) {
			slaveAddress.setDisable(true);
			recOptCombo.setDisable(true);
		}
	}

}
