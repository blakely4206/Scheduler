
package scheduler.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Appointment {
 
    private SimpleIntegerProperty appointmentId;
    private SimpleIntegerProperty customerId;
    private SimpleIntegerProperty userId;
    
    private SimpleStringProperty userName;
    private SimpleStringProperty title;
    private SimpleStringProperty description;
    private SimpleStringProperty location;
    private SimpleStringProperty contact;
    private SimpleStringProperty type;
    private SimpleStringProperty url;
    private SimpleStringProperty customerName;
    private SimpleStringProperty start;
    private SimpleStringProperty end;
    
    private LocalDateTime t_start;
    private LocalDateTime t_end;
    
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mma EEE MM/dd/yyyy ");
    
    
    public Appointment(int appointmentId, String customerName, int customerId, User user, String title, String description, 
            String location, String contact, String type, String url, LocalDateTime start, LocalDateTime end) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerName =  new SimpleStringProperty(customerName);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.userName =  new SimpleStringProperty(user.getUserName());
        this.userId = new SimpleIntegerProperty(user.getUserID());
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.type = new SimpleStringProperty(type);
        this.url = new SimpleStringProperty(url);
        this.start = new SimpleStringProperty(start.format(dtf));
        this.end = new SimpleStringProperty(end.format(dtf));
        this.t_start = start;
        this.t_end = end;
    }
    
    public Appointment(int customerId, String customerName, User user, String title, String description, 
            String location, String contact, String type, String url, LocalDateTime start, LocalDateTime end) {
        this.appointmentId = new SimpleIntegerProperty(1);
        this.customerName =  new SimpleStringProperty(customerName);
        this.userName =  new SimpleStringProperty(user.getUserName());
        this.userId = new SimpleIntegerProperty(user.getUserID());
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.type = new SimpleStringProperty(type);
        this.url = new SimpleStringProperty(url);
        this.start = new SimpleStringProperty(start.format(dtf));
        this.end = new SimpleStringProperty(end.format(dtf));
        this.t_start = start;
        this.t_end = end;
        this.customerId = new SimpleIntegerProperty(customerId);
    }
    
    public int getAppointmentId() {
        return appointmentId.get();
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId = new SimpleIntegerProperty(customerId);
    }

    public String getUser() {
        return userName.get();
    }

    public void setUserId(String user) {
        this.userName = new SimpleStringProperty(user);
    }
    
    public int getUserId(){
        return userId.get();
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location = new SimpleStringProperty(location);
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact = new SimpleStringProperty(contact);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type = new SimpleStringProperty(type);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url = new SimpleStringProperty(url);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName = new SimpleStringProperty(customerName);
    }

    public String getStart() {
        return start.get();
    }

    public void setStart(LocalDateTime start) {
        this.start = new SimpleStringProperty(start.format(dtf));
    }

    public String getEnd() {
        return end.get();
    }

    public void setEnd(LocalDateTime end) {
        this.end = new SimpleStringProperty(end.format(dtf));
    }

    public LocalDateTime getT_start() {
        return t_start;
    }

    public void setT_start(LocalDateTime t_start) {
        this.t_start = t_start;
    }

    public LocalDateTime getT_end() {
        return t_end;
    }

    public void setT_end(LocalDateTime t_end) {
        this.t_end = t_end;
    }
    
    public String getStartTOD(){
        if(getT_start().getHour() >= 12){
            return "PM";
        }
        else{
            return "AM";
        }
    }
    
    public String getEndTOD(){
        if(getT_end().getHour() >= 12){
            return "PM";
        }
        else{
            return "AM";
        }
    }
}
