
package scheduler;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DAO.DB;
import scheduler.Model.Country;
import scheduler.Model.User;

public class AddCityController implements Initializable {

    User user;
    
    private String name;
    private String address;
    private String address2;
    private String phone;
    private String postal;
    
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    
    @FXML private ComboBox comboCountry;
   
    @FXML private TextField txtName;
    
    public void Hold_NewCustomer_Values(String name, String address, String address2, String phone, String postal, User user){
        this.name = name;
        this.address = address;
        this.address2 = address2;
        this.phone = phone;
        this.postal = postal;
        this.user = user;
    }
    
    @FXML
    private void btnSave_onAction(ActionEvent event){
        
        Country c = (Country)comboCountry.getValue();
        String city = txtName.getText().trim().toUpperCase();
        
        System.out.println(c.getCountry() + city);
        DB.CreateCity(city, c.getCountryId(), user.getUserName());
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("NewCustomer.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("New Customer");
             
            st.setScene(sc);
            
            NewCustomerController ctrl = loader.getController();
            ctrl.Receive_Held_Values(name, address, address2, phone, postal, ((Country)comboCountry.getValue()), DB.ReturnCity(city), user);
            ctrl.setUser(user);
            st.show();  
        }
        catch(IOException ex){}
    }
    
    @FXML
    private void btnCancel_onAction(ActionEvent event){
         try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("NewCustomer.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("New Customer");
            st.setScene(sc);
            st.show(); 
            
            NewCustomerController ctrl = new NewCustomerController();
            ctrl.Receive_Held_Values(name, address, address2, phone, postal, user);
            ctrl.setUser(user);
        }
        catch(IOException ex){}
    }
    
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        
         System.out.println(user.getUserName());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            comboCountry.getItems().addAll(DB.ReturnCountries());
        } 
        catch (SQLException ex) {
            Logger.getLogger(AddCityController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
