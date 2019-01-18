
package scheduler.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Customer {

    private SimpleIntegerProperty customerId;
    private SimpleStringProperty customerName;
    private SimpleIntegerProperty addressId;
    private SimpleStringProperty phone;
    private SimpleStringProperty addressWhole;
    private SimpleStringProperty city;
    private SimpleStringProperty postal;
    private SimpleStringProperty country;
    private SimpleStringProperty address;
    private SimpleStringProperty address2;
    
    
    public Customer(int customerId, String customerName, int addressId){
   
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.addressId = new SimpleIntegerProperty(addressId);
    }
    
    public Customer(int customerId, String customerName, String address, String address2, String city, String country, String postal, String phone){
   
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.addressWhole = new SimpleStringProperty(address + " " + address2 + ", " + city + " " + postal + " " + country);
        this.postal = new SimpleStringProperty(postal);
        this.city = new SimpleStringProperty(city);
        this.country = new SimpleStringProperty(country);
        this.phone = new SimpleStringProperty(phone);
    }
    
    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId = new SimpleIntegerProperty(customerId);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName = new SimpleStringProperty(customerName);
    }

    public int getAddressId() {
        return addressId.get();
    }

    public void setAddressId(int addressId) {
        this.addressId = new SimpleIntegerProperty(addressId);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(SimpleStringProperty phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address = new SimpleStringProperty(address);
    }

    public String getPostal() {
        return postal.get();
    }

    public void setPostal(SimpleStringProperty postal) {
        this.postal = postal;
    }
    
    public String getCountry() {
        return country.get();
    }

    public void setCountry(SimpleStringProperty country) {
        this.country = country;
    }
    
    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(SimpleStringProperty address2) {
        this.address2 = address2;
    }
    
    public String getAddressWhole() {
        return addressWhole.get();
    }

    public void setAddressWhole(String addressWhole) {
        this.addressWhole = new SimpleStringProperty(addressWhole);
    }
}
