package scheduler.Exception;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import scheduler.LogInController;

public class InvalidUsernameorPasswordException extends Exception {
   
    private String heading = "Invalid Login";
    private String message = "Verify username and password.";
    
    public InvalidUsernameorPasswordException(Locale local){
        
        if(local.getLanguage().equals(new Locale("es").getLanguage())){
         try {
               Properties esProperties = new Properties();
               esProperties.load(LogInController.class.getResourceAsStream("es.properties"));
               heading = esProperties.getProperty("invalid");
               message = esProperties.getProperty("verify");
           } 
           catch (IOException ex) {
              System.out.println("Espanol Properties File Error: " + ex.getMessage());
           }
       }
    }
  
    public String getHeading(){
        return this.heading;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
}
