package scheduler;

import scheduler.DAO.DB;
import scheduler.Exception.InvalidInputException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import scheduler.Model.Appointment;
import scheduler.Model.User;
 
public class ReportsController implements Initializable {

    User user;
    
    @FXML ComboBox comboUsers;
    @FXML ComboBox comboTypes;
    @FXML ComboBox comboMonth;
    @FXML ComboBox comboYear;
    @FXML ComboBox comboLocation;
    @FXML ComboBox comboYear2;
    @FXML ComboBox comboMonth2;
    
    @FXML WebView webReport;
    StringBuilder s = new StringBuilder();
    
    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}
    
    
    @FXML
    private void btnBack_onAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException{
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            MainScreenController mctrl = loader.getController();
            
            mctrl.setUser(user);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        
    }
    
    @FXML
    private void btnLocation_onAction(ActionEvent event) 
            throws SQLException, ClassNotFoundException, IOException, ParseException{
        try{
            if(comboLocation.getSelectionModel().getSelectedItem() == null){
                throw new InvalidInputException("Select a Location");
            } 
            else if(comboYear2.getSelectionModel().getSelectedItem() == null){
                throw new InvalidInputException("Select a Year");
            }
            else if(comboMonth2.getSelectionModel().getSelectedItem() == null){
                throw new InvalidInputException("Select a Month");
            }
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DisplayReport.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            DisplayReportController mctrl = loader.getController();
            
            mctrl.setUser(user);
            
            String location = comboLocation.getSelectionModel().getSelectedItem().toString();
            String month = comboMonth2.getSelectionModel().getSelectedItem().toString();
            int year = Integer.parseInt(comboYear2.getSelectionModel().getSelectedItem().toString());
            
            s.append("<h3 align=\"center\">APPOINTMENTS IN ");
            s.append(location);
            s.append(" Office for the month of ");
            s.append(month);
            s.append(" ");
            s.append(year);
            s.append("</h3><table border=\"1\"><tr><th>User</th><th>Customer</th><th>Contact</th>"
                    + "<th>Type</th><th>Location</th><th>Start-End</th><th>URL</th></tr>");
            
            DB.ReturnAppointmentByLocation(location,month,year).forEach(appt->AppendAppointmentToStringBuilderByLocation(appt));  //Lambda and forEach is a less verbose and more efficient way 
                                                                                                                        //to build report by iterating the list of appointments.
            s.append("</ul>");
            
            mctrl.webReport.getEngine().loadContent(s.toString());
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("User Report: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(InvalidInputException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR"); 
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void btnUserReport_onAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException{
     try{
        if(comboUsers.getSelectionModel().getSelectedItem() == null){
            throw new InvalidInputException("Select a User");
        } 
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DisplayReport.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            DisplayReportController mctrl = loader.getController();
            User selectedUser = (User)comboUsers.getSelectionModel().getSelectedItem();
            
            mctrl.setUser(user);
            
            s.append("<h3 align=\"center\">");
            s.append("APPOINTMENTS FOR ");
            s.append(selectedUser.getUserName());
            s.append("</h3><table border=\"1\"><tr><th>Customer</th><th>Contact</th>"
                    + "<th>Type</th><th>Location</th><th>Start-End</th><th>URL</th></tr>");
            
            DB.ReturnAppointmentByUser(selectedUser).forEach(appt->AppendAppointmentToStringBuilder(appt));  //Lambda and forEach is a less verbose and more efficient way 
                                                                                                             //to build report by iterating the list of appointments.
            
            s.append("</ul>");
            mctrl.webReport.getEngine().loadContent(s.toString());
             
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("User Report: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(InvalidInputException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR"); 
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    private void AppendAppointmentToStringBuilderByLocation(Appointment appt){
             s.append("<tr>");         
             s.append("<th>");
             s.append(appt.getUser());
             s.append("</th>");
             s.append("<th>");
             s.append(appt.getCustomerName());
             s.append("</th><th>");
             s.append(appt.getContact());
             
             if(!"".equals(appt.getTitle())){
                s.append(", ");
                s.append(appt.getTitle());
             }
             
             s.append("</th><th>");
             s.append(appt.getType());
             s.append("</th><th>");
             s.append(appt.getLocation());
             s.append("</th><th>");
             s.append(appt.getStart());
             s.append(" - ");
             s.append(appt.getEnd());
             s.append("</th><th>");
             s.append(appt.getUrl());
             s.append("</th></tr>");
    }
    
    private void AppendAppointmentToStringBuilder(Appointment appt){
             s.append("<tr>");   
             s.append("<th>");
             s.append(appt.getCustomerName());
             s.append("</th><th>");
             s.append(appt.getContact());
             
             if(!"".equals(appt.getTitle())){
                s.append(", ");
                s.append(appt.getTitle());
             }
             
             s.append("</th><th>");
             s.append(appt.getType());
             s.append("</th><th>");
             s.append(appt.getLocation());
             s.append("</th><th>");
             s.append(appt.getStart());
             s.append(" - ");
             s.append(appt.getEnd());
             s.append("</th><th>");
             s.append(appt.getUrl());
             s.append("</th></tr>");
    }
    
    @FXML
    private void btnTypeReport_onAction(ActionEvent event) 
            throws SQLException, ParseException, ClassNotFoundException, IOException{
         try{
        if(comboTypes.getSelectionModel().getSelectedItem() == null){
            throw new InvalidInputException("Select a User");
        }
        else if(comboYear.getSelectionModel().getSelectedItem() == null){
            throw new InvalidInputException("Select a Year");
        }
        else if(comboMonth.getSelectionModel().getSelectedItem() == null){
            throw new InvalidInputException("Select a Month");
        }
        else{
                String type = comboTypes.getSelectionModel().getSelectedItem().toString();               
                String month = comboMonth.getSelectionModel().getSelectedItem().toString();
                int year = Integer.parseInt(comboYear.getSelectionModel().getSelectedItem().toString());

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("DisplayReport.fxml"));
                Parent p = loader.load();
                Scene sc = new Scene(p);

                DisplayReportController mctrl = loader.getController();
                
                mctrl.setUser(user);
                
                s.append("<h3 align=\"center\">");
                s.append("Number of Appointments of  Type '");
                s.append(type);
                s.append("' during ");
                s.append(month);
                s.append(" ");
                s.append(year);
                s.append("</h3>");
                s.append("<ul><li>");
                s.append(Integer.toString(DB.ReturnAppointmentCountByType(type, month, year))); 
                s.append("</li></ul>");
                
                mctrl.webReport.getEngine().loadContent(s.toString());

                Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
                st.setTitle("User Report: " + user.getUserName());
                st.setScene(sc);
                st.show();           
            }
        }
        catch(InvalidInputException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR"); 
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboTypes.getItems().add("In Person");
        comboTypes.getItems().add("Web");
        comboTypes.getItems().add("Phone");
        
        comboLocation.getItems().add("Phoenix (US)");
        comboLocation.getItems().add("New York (US)");
        comboLocation.getItems().add("London (UK)");
        
        try {
            comboUsers.setItems(DB.ReturnUsers());
        } 
        catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int i = LocalDate.now().getYear() - 4; i < LocalDate.now().getYear() + 2; i++){
            comboYear.getItems().add(i);
            comboYear2.getItems().add(i);
        }
        
        Stream<String> stream = Arrays.stream(new DateFormatSymbols().getMonths());
        stream.forEach(month->{                             //Lambda and forEach used to be less verbose and more efficent than for loop;
            comboMonth.getItems().add(month);
            comboMonth2.getItems().add(month);
        });    
        
    }    
    
}
