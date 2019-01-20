
package scheduler;

import scheduler.DAO.DB;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Model.Customer;
import scheduler.Model.User;

public class CustomerScreenController implements Initializable {

    User user; 
    
    @FXML Button btnNew;
    @FXML Button btnModify;
    @FXML Button btnDelete;
    @FXML Button btnBack;
    
    @FXML TableView<Customer> tableCustomers;
    
    @FXML TableColumn<Customer, Integer> CustID;
    @FXML TableColumn<Customer, String> CustName;
    @FXML TableColumn<Customer, String> CustAddress;
    @FXML TableColumn<Customer, String> CustPhone;
    
    ObservableList<Customer> customerList = FXCollections.observableArrayList();
    
    @FXML
    private void btnNew_onAction(ActionEvent event){
         try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("NewCustomer.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);
            NewCustomerController ctrl = loader.getController();
            ctrl.setUser(user);
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
    }
    
     @FXML
    private void btnModify_onAction(ActionEvent event) throws SQLException, ClassNotFoundException{  
        try{
            if(tableCustomers.getSelectionModel().getSelectedItem() != null){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ModifyCustomer.fxml"));
                Parent p = loader.load();
                Scene sc = new Scene(p);

                Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
                st.setTitle("Modify Customer: " + user.getUserName());
                st.setScene(sc);
                
                ModifyCustomerController ctrl = loader.getController();
                
                st.show();     
                ctrl.ReceiveCustomer(tableCustomers.getSelectionModel().getSelectedItem().getCustomerId());
                ctrl.setUser(user);
            }
        }
        catch(IOException ex){}
    }
    
     @FXML
    private void btnDelete_onAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException{
       if(DB.DeleteCustomer(tableCustomers.getSelectionModel().getSelectedItem())){
          Alert a = new Alert(Alert.AlertType.CONFIRMATION);
          a.setTitle("Success");
          a.setContentText("Customer Deleted");
          a.showAndWait();
          FillTables();
       }
    }
    
     @FXML
    private void btnBack_onAction(ActionEvent event) throws SQLException, IOException, ClassNotFoundException{
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainScreen.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Scheduler: " + user.getUserName());
            st.setScene(sc);
            MainScreenController ctrl = loader.getController();
            ctrl.setUser(user);
            st.show();           
    }
    
    private void FillTables(){
         
        try{
            CustName.setCellValueFactory(new PropertyValueFactory("customerName"));
            CustAddress.setCellValueFactory(new PropertyValueFactory("addressWhole"));
            CustPhone.setCellValueFactory(new PropertyValueFactory("phone"));
            CustID.setCellValueFactory(new PropertyValueFactory("customerId"));
            tableCustomers.setItems(DB.ReturnCustomers());
        }
        catch(SQLException ex){}
        catch(Exception ex){}
    }
    
     public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FillTables();
    }    
}
