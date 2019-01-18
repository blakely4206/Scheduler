
package scheduler.Exception;

public class OverlappingAppointmentException extends Exception{
    private final String heading;
    private final String message;
    
    public OverlappingAppointmentException(String message){
        
        this.heading = "Overlapping Appointments";
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