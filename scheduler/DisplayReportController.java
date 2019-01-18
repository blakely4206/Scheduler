
package scheduler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import scheduler.Model.User;

public class DisplayReportController implements Initializable {

   User user;
   
   @FXML public WebView webReport;
  
   @FXML
   private void btnBack_onAction(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Reports.fxml"));
            Parent p = loader.load();
            Scene sc = new Scene(p);

            ReportsController mctrl = loader.getController();
            
            mctrl.setUser(user);
            
            Stage st = (Stage)((Node)event.getSource()).getScene().getWindow();
            st.setTitle("Reports: " + user.getUserName());
            st.setScene(sc);
            st.show();           
        }
        catch(IOException ex){}
   }
    
    public User getUser() {return user;}

    public void setUser(User user) {
        this.user = user;   
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webReport.getStylesheets().add(getClass().getResource("CSS/Report.css").toExternalForm());
    }    
    
}
