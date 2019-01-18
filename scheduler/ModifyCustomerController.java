
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
import scheduler.Model.Customer;
import scheduler.Model.User;

public class ModifyCustomerController implements Initializable {

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    User user; 
    
    private Customer cust;
    private Address add;
    private City city;
    private Country country;
    
    @FXML private ComboBox comboCountry;
    @FXML private ComboBox comboCity;
    
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    
    @FXML private TextField txtName;
    @FXML private TextField txtAddress;
    @FXML private TextField txtAddress2;
    @FXML private TextField txtPhone;
    @FXML private TextField txtPostal;
    
    public void ReceiveCustomer(int custId){
      this.cust = DB.ReturnCustomer(custId);
      this.add = DB.ReturnAddress(cust.getAddressId());
      this.city = DB.ReturnCity(add.getCityId());
      this.country = DB.ReturnCountry(city.getCountryId());
      Populate();
    }
    
    public void Populate(){
        txtName.setText(cust.getCustomerName());
        txtAddress.setText(add.getAddress());
        txtAddress2.setText(add.getAddress2());
        txtPhone.setText(add.getPhone());
        txtPostal.setText(add.getPostalCode());
        comboCountry.setValue(country);
        comboCity.setValue(city);
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
            st.setTitle("Scheduler: " + user.getUserName());
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
            st.setTitle("Scheduler: " + user.getUserName());
            
            AddCityController ctrl = new AddCityController();
            ctrl.Hold_NewCustomer_Values(txtName.getText(), txtAddress.getText(), 
                    txtAddress2.getText(), txtPhone.getText(), txtPostal.getText(), user);
            
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
      }
    }
    
    @FXML 
    private void btnSave_onAction(ActionEvent event)throws SQLException{
      
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
              int countryId = ((Country)comboCountry.getValue()).getCountryId();
              int cityId = ((City)comboCity.getValue()).getCityId();
              
              this.cust.setCustomerName(txtName.getText().trim());
              this.add.setAddress(txtAddress.getText().trim());
              this.add.setAddress2(txtAddress2.getText().trim());
              this.add.setPhone(txtPhone.getText().trim());
              this.add.setPostalCode(txtPostal.getText().trim());
              this.add.setCityId(cityId);
              
              DB.UpdateCustomer(cust);
              DB.UpdateAddress(add);
          }
      } 
        catch (InvalidInputException ex) {
          Alert a = new Alert(AlertType.ERROR);
          a.setTitle(ex.getHeading());
          a.setContentText(ex.getMessage());
          a.showAndWait();
        }
        
         try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CustomerScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            CustomerScreenController ctrl = loader.getController();
            ctrl.setUser(user);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            comboCountry.getItems().addAll(DB.ReturnCountries());
            City newCity = new City(0,"Add New City", 0);
            comboCity.getItems().add(newCity);
            comboCity.getItems().addAll(DB.ReturnCities());
        } catch (SQLException ex) {
            Logger.getLogger(NewCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
