
package scheduler;

import scheduler.DAO.DB;
import scheduler.Exception.AfterHoursException;
import scheduler.Exception.OverlappingAppointmentException;
import scheduler.Exception.InvalidInputException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.Model.User;


public class ModifyAppointmentController implements Initializable {

    User user;
    
    Appointment appt;
    
    @FXML ComboBox comboType;
    @FXML ComboBox comboStartHr;
    @FXML ComboBox comboStartMin;
    @FXML ComboBox comboStartAM;
    @FXML ComboBox comboEndHr;
    @FXML ComboBox comboEndMin;
    @FXML ComboBox comboEndAM;
    @FXML ComboBox comboLocation;
    
    @FXML TextField txtURL;
    @FXML TextField txtTitle;
    @FXML TextField txtContact;
    @FXML TextField txtDesc;
    
    @FXML Button btnSave;
    @FXML Button btnCancel;
    
    @FXML DatePicker datepickerStart;
    
    @FXML TableView<Customer> tableCustomers;
    
    @FXML TableColumn<Customer, String> cust;
    
    @FXML
    private void btnSave_onAction(ActionEvent event)throws SQLException, ClassNotFoundException{
      try{     
          appt.setUrl(txtURL.getText());
          appt.setTitle(txtTitle.getText());
          appt.setContact(txtContact.getText());
          appt.setDescription(txtDesc.getText());
          appt.setType(comboType.getSelectionModel().getSelectedItem().toString());
          
          int startHour = Integer.parseInt(comboStartHr.getSelectionModel().getSelectedItem().toString());
          int endHour = Integer.parseInt(comboEndHr.getSelectionModel().getSelectedItem().toString());
          int startMin = Integer.parseInt(comboStartMin.getSelectionModel().getSelectedItem().toString());
          int endMin = Integer.parseInt(comboEndMin.getSelectionModel().getSelectedItem().toString());
       
          if(tableCustomers.getSelectionModel().getSelectedItem() == null){
              throw new InvalidInputException("Choose Customer");
          }
          else{
              appt.setCustomerId(tableCustomers.getSelectionModel().getSelectedItem().getCustomerId());
              appt.setCustomerName(tableCustomers.getSelectionModel().getSelectedItem().getCustomerName());
          }
          
          startHour = ConvertTo24HourClock(comboStartAM.getSelectionModel().getSelectedItem().toString(), startHour);
          endHour = ConvertTo24HourClock(comboEndAM.getSelectionModel().getSelectedItem().toString(), endHour);
          
          appt.setT_start(LocalDateTime.of(datepickerStart.getValue().getYear(), datepickerStart.getValue().getMonth(), 
                  datepickerStart.getValue().getDayOfMonth(), startHour, startMin));
          appt.setT_end(LocalDateTime.of(datepickerStart.getValue().getYear(), datepickerStart.getValue().getMonth(), 
                  datepickerStart.getValue().getDayOfMonth(), endHour, endMin));
          
          if(comboLocation.getSelectionModel().getSelectedItem() == null){
              throw new InvalidInputException("Select Location");
          }
          else{
              appt.setLocation(comboLocation.getSelectionModel().getSelectedItem().toString());
          }

          if(DB.ReturnAppointments().stream().anyMatch(c->TestForOverlap(c,appt))){    //Lambda stream is an efficent way to iterate through ObservableList.
              throw new OverlappingAppointmentException("You cannot set an appointment at this time: ");
          }
          else if(TestForAfterHours(appt)){
              throw new AfterHoursException("Appointment must start during business hours");
          }
          else if(appt.getT_start().isAfter(appt.getT_end())){
              throw new InvalidInputException("Start time before end.");
          }
          else{
              DB.UpdateAppointment(appt, user);
          }
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            MainScreenController ctrl = loader.getController();
            ctrl.setUser(user);

            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            st.show();               
      }
      catch(IOException | InvalidInputException | OverlappingAppointmentException | AfterHoursException ex){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR"); 
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
      }
    }
    
    @FXML
    private void btnCancel_onAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            MainScreenController ctrl = loader.getController();
            ctrl.setUser(user);

            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            st.show();           
    }
    
    private boolean TestForOverlap(Appointment appt, Appointment a){      
        return   appt.getAppointmentId() != a.getAppointmentId() &&
               (appt.getT_start().isEqual(a.getT_start())  ||   
                (appt.getT_start().isBefore(a.getT_end()) && appt.getT_start().isAfter(a.getT_start())) ||
                (appt.getT_end().isBefore(a.getT_end()) && appt.getT_end().isAfter(a.getT_start())) ||
                (appt.getT_start().isBefore(a.getT_start()) && appt.getT_end().isAfter(a.getT_end())));
    }
    
    private int ConvertTo24HourClock(String AMPM, int hour){
        if(AMPM.equals("PM") &&  AMPM.equals("12") == false){
            return  hour += 12;
          }
        else{
            return hour;
        }
    }
    
    private int ConvertTo12HourClock(LocalDateTime time){
        if(time.getHour() > 12){
            return time.getHour()-12;
        }
        else{
            return time.getHour()-1;
        }
    }
    
    private void PopulateTable(){
            
        try{
            cust.setCellValueFactory(new PropertyValueFactory("customerName"));
            tableCustomers.setItems(DB.ReturnCustomers());
        }
        catch(SQLException ex){}
        catch(Exception ex){}
    }
    
    public void AcceptAppt(Appointment a) throws ClassNotFoundException, IOException, SQLException{
     
    setAppt(a);
    
    comboType.getSelectionModel().select(a.getType());
    
    comboStartMin.getSelectionModel().select(a.getT_start().getMinute());
    comboEndMin.getSelectionModel().select(a.getT_end().getMinute());
    
    comboStartAM.getSelectionModel().select(a.getStartTOD());
    comboEndAM.getSelectionModel().select(a.getEndTOD());
    
    comboStartHr.getSelectionModel().select(ConvertTo12HourClock(a.getT_start()));
    comboEndHr.getSelectionModel().select(ConvertTo12HourClock(a.getT_end()));

    comboLocation.getSelectionModel().select(appt.getLocation());
    
    txtURL.setText(appt.getUrl());
    txtTitle.setText(appt.getTitle());
    txtContact.setText(appt.getContact());
    txtDesc.setText(appt.getDescription());
    
    datepickerStart.setValue(appt.getT_start().toLocalDate());
    
   
    cust.setCellValueFactory(new PropertyValueFactory("customerName"));
    tableCustomers.setItems(DB.ReturnCustomers());
    
     
    tableCustomers.getItems().forEach(c->SelectCustomer(c));     //Lambda and foreach is used to be more efficent and less verbose than using a for loop.
    }
    
    private void SelectCustomer(Customer c) {
        if(c.getCustomerName().equals(appt.getCustomerName())){
            tableCustomers.getSelectionModel().select(c);
          }
    }
    
    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}
    
    public Appointment getAppt() { return appt;}

    public void setAppt(Appointment appt) {this.appt = appt;}
    
    private boolean TestForAfterHours(Appointment appt){
        LocalTime bussinessDayStart = LocalTime.of(8, 0);
        LocalTime bussinessDayEnd = LocalTime.of(17, 0);
        
        return appt.getT_start().toLocalTime().isBefore(bussinessDayStart) || appt.getT_start().toLocalTime().isAfter(bussinessDayEnd);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
        for(int i = 0; i < 60; i++){
            String j;
            if(i < 10){
                j = "0" + Integer.toString(i);
            }
            else{
                j = Integer.toString(i);
            }
            
            if(i != 0 && i < 13){
                comboStartHr.getItems().add(j);
                comboEndHr.getItems().add(j);
            }
            
            comboStartMin.getItems().add(j);
            comboEndMin.getItems().add(j);
        }
        
        comboStartAM.getItems().add("AM");
        comboEndAM.getItems().add("AM");
        comboStartAM.getItems().add("PM");
        comboEndAM.getItems().add("PM");
        
       comboType.getItems().add("In Person");
       comboType.getItems().add("Phone");
       comboType.getItems().add("Web");
       
       comboLocation.getItems().add("Phoenix (US)");
       comboLocation.getItems().add("New York (US)");
       comboLocation.getItems().add("London (UK)");

        
    datepickerStart.getEditor().setDisable(true);
    datepickerStart.getStylesheets().add(getClass().getResource("CSS/AppointmentScreens.css").toExternalForm());
    datepickerStart.setValue(LocalDate.now());
    datepickerStart.setDayCellFactory((d) -> new DateCell() { //Lambda is efficent way to deal with callback function.
        @Override
        public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);

            if (item.isBefore(LocalDate.now()) || item.getDayOfWeek().equals(DayOfWeek.SATURDAY) || item.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                setDisable(true);
                setStyle("-fx-background-color: #EEEEEE;");
            }
        }
    });
    }    
}
