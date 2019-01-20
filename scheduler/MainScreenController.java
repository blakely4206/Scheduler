
package scheduler;

import scheduler.DAO.DB;
import scheduler.Exception.InvalidInputException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Model.Appointment;
import scheduler.Model.User;

public class MainScreenController implements Initializable {

    User user;
    
    @FXML private Button btnCust;
    @FXML private Button btnAppt;
    @FXML private Button btnModify;
    
    @FXML private ComboBox comboMonthWeek;
    
    @FXML private TableView<Appointment> tableAppt;
    
    @FXML private TableColumn<Appointment, String> colType;
    @FXML private TableColumn<Appointment, String> colDesc;
    @FXML private TableColumn<Appointment, String> colURL;
    @FXML private TableColumn<Appointment, String> colStart;
    @FXML private TableColumn<Appointment, String> colEnd;
    @FXML private TableColumn<Appointment, String> colContact;
    @FXML private TableColumn<Appointment, String> colCust;
    @FXML private TableColumn<Appointment, String> colTitle;
    
    @FXML
    private void comboMonthWeek_onAction(ActionEvent event) throws SQLException, ParseException, ClassNotFoundException, IOException{
        switch (comboMonthWeek.getSelectionModel().getSelectedItem().toString()) {
            case "ALL":   tableAppt.setItems(DB.ReturnAppointments());
                break;
            case "THIS MONTH": tableAppt.setItems(DB.ReturnAppointmentsForTheMonth());
                break;
            default: tableAppt.setItems(DB.ReturnAppointmentsForTheWeek());
                break;
        }
    }
    
    @FXML private void btnCust_onAction(ActionEvent event){
         try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CustomerScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            CustomerScreenController ctrl = loader.getController();
            ctrl.setUser(user);
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Customers: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
    }
    
    @FXML private void btnDelete_onAction(ActionEvent event) throws ParseException, SQLException, ClassNotFoundException, IOException{
        if(tableAppt.getSelectionModel().getSelectedItem() != null){
            DB.DeleteAppointment(tableAppt.getSelectionModel().getSelectedItem());
        }
        PopulateTable();
    }
    
    @FXML private void btnAppt_onAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AppointmentScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            AppointmentScreenController ctrl = loader.getController();
            ctrl.setUser(user);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Appointments: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
    }
    @FXML
    private void btnReport_onAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Reports.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            ReportsController mctrl = loader.getController();
            
            mctrl.setUser(user);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Modify Appointment: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
    }
  
    @FXML
    private void btnModify_onAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException{
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ModifyAppointment.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            ModifyAppointmentController mctrl = loader.getController();
            
            mctrl.setUser(user);
            
            if(tableAppt.getSelectionModel().getSelectedItem() == null){
                throw new InvalidInputException("Please Select an Appointment to modify.");
            }
            else{
                mctrl.AcceptAppt(tableAppt.getSelectionModel().getSelectedItem());
            }
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Modify Appointment: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(InvalidInputException  ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error"); 
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } 
    }
    
     @FXML
    private void btnLogOut_onAction(ActionEvent event)throws IOException{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("LogIn.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

           
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler - Log In");
            st.setScene(sc);
            st.show();     
    }
    
    public User getUser(){
        return this.user;
    }
    
    public void setUser(User user) throws SQLException, ClassNotFoundException, IOException{
        this.user = user;
        DB.ReturnAppointmentByUser(user).forEach(a->AppointmentAlert(a)); //Lambda and foreach are used as a more efficent and less verbose way to iterate list
    }
    
    public void PopulateTable() throws ParseException, ClassNotFoundException, SQLException, IOException{
     
            colType.setCellValueFactory(new PropertyValueFactory("type"));
            colDesc.setCellValueFactory(new PropertyValueFactory("description"));
            colURL.setCellValueFactory(new PropertyValueFactory("url"));
            colStart.setCellValueFactory(new PropertyValueFactory("start"));
            colEnd.setCellValueFactory(new PropertyValueFactory("end"));
            colContact.setCellValueFactory(new PropertyValueFactory("contact"));
            colCust.setCellValueFactory(new PropertyValueFactory("customerName"));
            colTitle.setCellValueFactory(new PropertyValueFactory("title"));
            
            tableAppt.setItems(DB.ReturnAppointmentsForTheMonth());          
    }
    
    private void AppointmentAlert(Appointment appt){
        
        Duration nowThen = Duration.between(appt.getT_start(),LocalDateTime.now());
        
           if(ChronoUnit.MINUTES.between(LocalDateTime.now(), appt.getT_start()) <= 15){
               Alert alert = new Alert(Alert.AlertType.WARNING);
               alert.setTitle("Upcoming Appointment"); 
               alert.setContentText("Appointment soon: " + appt.getCustomerName() + " " + appt.getStart());
               alert.showAndWait();
           }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      comboMonthWeek.getItems().add("ALL");
      comboMonthWeek.getItems().add("THIS MONTH");
      comboMonthWeek.getItems().add("THIS WEEK");
      comboMonthWeek.getSelectionModel().select("THIS MONTH");
      
        try {
            PopulateTable();
        } catch (ParseException | ClassNotFoundException | IOException | SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
}
