
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


public class AppointmentScreenController implements Initializable {

    User user;
    
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
          Customer customer;

          LocalDate date; 
                  
          String url = txtURL.getText();
          String title = txtTitle.getText();
          String contact = txtContact.getText();
          String descrip = txtDesc.getText();
          String location;
          String type = comboType.getSelectionModel().getSelectedItem().toString();
     
          if(datepickerStart.getValue() == null){
              throw new InvalidInputException("Choose Date");
          }
          else{
              date = datepickerStart.getValue();
          }
          
          if(tableCustomers.getSelectionModel().getSelectedItem() == null){
              throw new InvalidInputException("Choose Customer");
          }
          else{
              customer = tableCustomers.getSelectionModel().getSelectedItem();
          }
          
         int  startHour = ConvertTo24HourClock(comboStartAM.getSelectionModel().getSelectedItem().toString(),  
                 Integer.parseInt(comboStartHr.getSelectionModel().getSelectedItem().toString()));
          int endHour = ConvertTo24HourClock(comboEndAM.getSelectionModel().getSelectedItem().toString(), 
                  Integer.parseInt(comboEndHr.getSelectionModel().getSelectedItem().toString()));
   
          LocalDateTime start = LocalDateTime.of(
                  date.getYear(), 
                  date.getMonth(), 
                  date.getDayOfMonth(), 
                  startHour,
                  Integer.parseInt(comboStartMin.getSelectionModel().getSelectedItem().toString()));
          
          LocalDateTime end = LocalDateTime.of(
                  date.getYear(), 
                  date.getMonth(), 
                  date.getDayOfMonth(), 
                  endHour,
                  Integer.parseInt(comboEndMin.getSelectionModel().getSelectedItem().toString()));
          
          if(comboLocation.getSelectionModel().getSelectedItem() == null){
              throw new InvalidInputException("Select Location");
          }
          else{
              location = comboLocation.getSelectionModel().getSelectedItem().toString();
          }
          
          Appointment theAppointment = new Appointment(customer.getCustomerId(),customer.getCustomerName(),user, 
                  title, descrip, location, contact, type, url, start, end);
          
          if(DB.ReturnAppointments().stream().anyMatch(c->TestForOverlap(c,theAppointment))){    //Lambda stream is an efficent way to iterate through ObservableList.
              throw new OverlappingAppointmentException("You cannot set an appointment at this time: ");
          }
          else if(TestForAfterHours(theAppointment)){
              throw new AfterHoursException("Appointment must start during business hours");
          }
          else if(endHour < startHour){
              throw new InvalidInputException("Start time must be before end time");
          }
          else{
              DB.CreateAppointment(new Appointment(customer.getCustomerId(),customer.getCustomerName(),user, 
                  title, descrip, location, contact, type, url, start, end));
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
      catch(InvalidInputException | IOException | OverlappingAppointmentException | AfterHoursException ex){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR"); 
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
      }
    }
    
    @FXML
    private void btnCancel_onAction(ActionEvent event) throws SQLException, ClassNotFoundException{
        try{
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
        catch(IOException ex){}
    }
    
    private void PopulateTable(){    
        try{
            cust.setCellValueFactory(new PropertyValueFactory("customerName"));
            tableCustomers.setItems(DB.ReturnCustomers());
        }
        catch(SQLException ex){}
        catch(Exception ex){}
    }
    
    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}
    
    private boolean TestForOverlap(Appointment appt, Appointment a){
       
       return  appt.getT_start().isEqual(a.getT_start())  ||   
              (appt.getT_start().isBefore(a.getT_end()) && appt.getT_start().isAfter(a.getT_start())) || 
              (appt.getT_end().isBefore(a.getT_end()) && appt.getT_end().isAfter(a.getT_start())) || 
              (appt.getT_start().isBefore(a.getT_start()) && appt.getT_end().isAfter(a.getT_end()));
    }
    
    private boolean TestForAfterHours(Appointment appt){
        LocalTime bussinessDayStart = LocalTime.of(8, 0);
        LocalTime bussinessDayEnd = LocalTime.of(17, 0);
        
        return appt.getT_start().toLocalTime().isBefore(bussinessDayStart) || appt.getT_start().toLocalTime().isAfter(bussinessDayEnd);
    }
    
    private int ConvertTo24HourClock(String AMPM, int hour){
        if(AMPM.equals("PM") &&  hour != 12){
            return  hour += 12;
          }
        else{
            return hour;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    PopulateTable();
    
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

    comboStartHr.setValue("12");
    comboEndHr.setValue("12");
    comboStartAM.setValue("AM");
    comboEndAM.setValue("AM");
    comboStartMin.setValue("00");
    comboEndMin.setValue("00");
    comboType.setValue("In Person");
    
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
