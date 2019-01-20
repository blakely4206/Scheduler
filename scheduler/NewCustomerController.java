
package scheduler;

import scheduler.DAO.DB;
import scheduler.Exception.InvalidInputException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Model.Address;
import scheduler.Model.City;
import scheduler.Model.Country;
import scheduler.Model.User;

public class NewCustomerController implements Initializable {

    User user;
    
    @FXML private ComboBox comboCountry;
    @FXML private ComboBox comboCity;
    
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    
    @FXML private TextField txtName;
    @FXML private TextField txtAddress;
    @FXML private TextField txtAddress2;
    @FXML private TextField txtPhone;
    @FXML private TextField txtPostal;
    
    public void Receive_Held_Values(String name, String address, String address2, String phone, String postal, Country country, City city, User user){
    
        txtName.setText(name);

        txtAddress.setText(address);
        txtAddress2.setText(address2);
        txtPhone.setText(phone);
        txtPostal.setText(postal);
        comboCountry.setValue(country);
        comboCity.setValue(city);

        setUser(user);
    }
    
    public void Receive_Held_Values(String name, String address, String address2, String phone, String postal, User user){

        txtName.setText(name);
        txtAddress.setText(address);
        txtAddress2.setText(address2);
        txtPhone.setText(phone);
        txtPostal.setText(postal);

        setUser(user);
    }
    
    @FXML
    private void btnCancel_onAction(ActionEvent event){
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
    
    @FXML
    private void comboCity_onAction(ActionEvent event){

        if("Add New City".equals(comboCity.getValue().toString())){
          try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AddCity.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Add New City: " + user.getUserName());
            
            AddCityController ctrl = loader.getController();
            ctrl.Hold_NewCustomer_Values(txtName.getText(), txtAddress.getText(), 
                    txtAddress2.getText(), txtPhone.getText(), txtPostal.getText(), user);
            ctrl.setUser(user);
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
      }
    }
    
    @FXML 
    private void btnSave_onAction(ActionEvent event)throws SQLException, IOException, ClassNotFoundException{
      
        try{
          if(comboCountry.getValue() == null){
              throw new InvalidInputException("SELECT COUNTRY");
          }
          else if(comboCity.getValue() == null){
              throw new InvalidInputException("SELECT CITY");
          }
          else if(txtName.getText().isEmpty()){
              throw new InvalidInputException("ENTER NAME");
          }
          else if(txtAddress.getText().isEmpty()){
              throw new InvalidInputException("ENTER Address");
          }
          else if(txtPhone.getText().isEmpty()){
              throw new InvalidInputException("ENTER PHONE NUMBER");
          }
          else if(txtPostal.getText().isEmpty()){
              throw new InvalidInputException("ENTER POSTAL CODE");
          }
       
          else{
              Country country = ((Country)comboCountry.getValue());
              City city = ((City)comboCity.getValue());
              String _name = txtName.getText().trim();
              String _address = txtAddress.getText().trim();
              String _address2 = txtAddress2.getText().isEmpty() ? "" : txtAddress2.getText().trim();
              String _phone = txtPhone.getText().trim();
              String _postal = txtPostal.getText().trim();
              
              DB.CreateCustomer(_name,new Address(_address, _address2, city, _postal, _phone),user);
           
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("CustomerScreen.fxml"));
                Parent p = loader.load();
                Scene sc = new Scene(p);

                CustomerScreenController ctrl = loader.getController();
                ctrl.setUser(user);

                Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
                st.setTitle("Customer: " + user.getUserName());
                st.setScene(sc);
                st.show();           
            }
        } 
        catch (InvalidInputException ex) {
          Alert a = new Alert(AlertType.ERROR);
          a.setTitle(ex.getHeading());
          a.setContentText(ex.getMessage());
          a.showAndWait();
        }
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            comboCountry.getItems().addAll(DB.ReturnCountries());
            City newCity = new City(0,"Add New City", 0);
            comboCity.getItems().add(newCity);
            comboCity.getItems().addAll(DB.ReturnCities());
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(NewCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
