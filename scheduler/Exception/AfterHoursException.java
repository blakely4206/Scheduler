
package scheduler.Exception;

public class AfterHoursException extends Exception{
    private final String heading;
    private final String message;
    
    public AfterHoursException(String message){
        
        this.heading = "Outside of Work Hours";
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

