package scheduler.Exception;

public class InvalidInputException extends Exception {
   
    private final String heading;
    private final String message;
    
    
    public InvalidInputException(String message){
        
        this.heading = "Invalid Input";
        this.message = message;
    }
  
    
    public String getHeading(){
        return this.heading;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
}
