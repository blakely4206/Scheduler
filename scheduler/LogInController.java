
package scheduler;

import scheduler.Utility.Logger;
import scheduler.DAO.DB;
import scheduler.Exception.InvalidUsernameorPasswordException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Model.User;

public class LogInController implements Initializable {

    User user;
    
    @FXML private Button btnLogIn;
    @FXML private Label lblPwrd;
    @FXML private PasswordField txtPwrd;
    @FXML private TextField txtUsername;
    @FXML private Label lblUsrname;

    Locale local;
    
    @FXML
    private void btnLogin_onAction(ActionEvent event)throws SQLException, ClassNotFoundException{
        if(!txtPwrd.getText().isEmpty() && !txtUsername.getText().isEmpty()) {
            try{
                setUser(DB.ValidateUser(txtUsername.getText().trim(), SHA1(txtPwrd.getText().trim())));
                
                if(user == null){
                    throw new InvalidUsernameorPasswordException(local);
                }
                
                Logger.Log(user);
                
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
            catch(IOException ex){
                System.out.println("IO Error: " + ex.getMessage());
            }
            catch(InvalidUsernameorPasswordException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(ex.getHeading()); 
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        } 
    }
    
public static String SHA1(String password) {
    try{
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] b = digest.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(0xff & b[i]);
            if(hex.length() == 1) sb.append('0');
            sb.append(hex);
        }

        return sb.toString();
    } 
    catch(UnsupportedEncodingException | NoSuchAlgorithmException ex){
        System.out.println(ex.getLocalizedMessage());
    }
        return null;
        
}
    
    public void setUser(User user){
        this.user = user;
    }
    
    public User getUser(){
        return user;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       local = Locale.getDefault();
       
       if(local.getLanguage().equals(new Locale("es").getLanguage())){
           Properties esProperties = new Properties();
           try {
               esProperties.load(LogInController.class.getResourceAsStream("es.properties"));
               lblPwrd.setText(esProperties.getProperty("password"));
               lblUsrname.setText(esProperties.getProperty("username"));
           } 
           catch (IOException ex) {
              System.out.println("Language Properties File Error: " + ex.getMessage());
           }
       }
    }   
}
