
package scheduler.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.Model.Address;
import scheduler.Model.Appointment;
import scheduler.Model.City;
import scheduler.Model.Country;
import scheduler.Model.Customer;
import scheduler.Model.User;

public class DB {
        private static Connection conn = null;

        private static String driver;
        private static String db;
        private static String url;
        private static String user;
        private static String pass;
        
        private static User theUser;
        
        private static void Connect() throws SQLException, ClassNotFoundException, IOException{
     
            Properties dbProperties = new Properties();
            dbProperties.load(DB.class.getResourceAsStream("DB.properties"));
            
            driver = dbProperties.getProperty("jdbc.driver");
            url = dbProperties.getProperty("jdbc.url");
            user = dbProperties.getProperty("jdbc.username");
            pass = dbProperties.getProperty("jdbc.password");
        
            Class.forName(driver);
            
            conn = DriverManager.getConnection(url,user,pass);
       }   
        
        public static User ValidateUser(String username, String pwrd) throws ClassNotFoundException, IOException, SQLException{
      
            Connect();   
            
            String query = "SELECT * FROM user WHERE user.userName= ? AND user.password= ? ;";
            
            PreparedStatement prepped_query = conn.prepareStatement(query);
            prepped_query.setString(1, username);
            prepped_query.setString(2, pwrd);
            
            ResultSet results = prepped_query.executeQuery();
            
            while(results.next()){     
                    if(results.getString("userName").equals(username) && results.getString("password").equals(pwrd)){
                        theUser = new User(results.getInt("userId"),results.getString("userName"));
                         
                        return theUser;
                    }
                }
            Close();
            
            return null;
        }
        
        private static void Close() throws SQLException{conn.close(); }
        
        public static User ReturnUser(String username) throws SQLException, ClassNotFoundException, IOException{
          
                Connect();   
                
                String query = "SELECT * FROM user WHERE userName= ?;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, username);
                
                ResultSet results = prepped_query.executeQuery();
                if(results.next()){     
                    Close();
                    return new User(results.getInt("userId"), results.getString("userName")); 
                }
                
            Close();
            return null;
        }
        
        
        public static ObservableList<User> ReturnUsers()throws SQLException, ClassNotFoundException, IOException{
            ObservableList<User> theList = FXCollections.observableArrayList();
            
                Connect();   
                
                String query = "SELECT * FROM user";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                
                ResultSet results = prepped_query.executeQuery();
                while(results.next()){     
                   theList.add(new User(results.getInt("userId"), results.getString("userName")));
                }
                
                Close();
            
            return theList;
        }

        public static ObservableList<Appointment> ReturnAppointmentByUser(User user) throws SQLException, ClassNotFoundException, IOException{
            ObservableList<Appointment> apptList = FXCollections.observableArrayList();

                Connect();   
                
                String query = 
                "SELECT * FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE appointment.userId= ?  AND end > current_timestamp ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setInt(1, user.getUserID());
                ResultSet results = prepped_query.executeQuery();
                
            while(results.next()){
                
                LocalDateTime start = results.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = results.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
 
                apptList.add(new Appointment(
                        results.getInt("appointmentId"),
                        results.getString("customerName"),
                        results.getInt("customerId"),
                        new User(results.getInt("userId"), results.getString("userName")),
                        results.getString("title"), 
                        results.getString("description"), 
                        results.getString("location"), 
                        results.getString("contact"), 
                        results.getString("type"),
                        results.getString("url"), 
                        start, 
                        end));  
            }
                Close();
            
            return apptList; 
        }
        
        public static ObservableList<Appointment> ReturnAppointmentByLocation(String location, String monthString, int year) 
                throws SQLException, ClassNotFoundException, IOException, ParseException{
            
            ObservableList<Appointment> apptList = FXCollections.observableArrayList();
  
                Connect();   
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("MMM").parse(monthString));
                int month = c.get(Calendar.MONTH) + 1;   
                
                String query = 
                "SELECT * FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE appointment.location= ?  AND MONTH(start)= ?  AND YEAR(start)= ?  ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, location);
                prepped_query.setInt(2, month);
                prepped_query.setInt(3, year);
                
                ResultSet results = prepped_query.executeQuery();
                
            while(results.next()){
                
                LocalDateTime start = results.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = results.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
 
                apptList.add(new Appointment(
                        results.getInt("appointmentId"),
                        results.getString("customerName"),
                        results.getInt("customerId"),
                        new User(results.getInt("userId"), results.getString("userName")),
                        results.getString("title"), 
                        results.getString("description"), 
                        results.getString("location"), 
                        results.getString("contact"), 
                        results.getString("type"),
                        results.getString("url"), 
                        start, 
                        end));  
            }
                Close();
            
            return apptList; 
        }
        
        public static boolean DeleteCustomer(Customer customer) throws SQLException, ClassNotFoundException, IOException{
               
                Connect();
                
                String deleteAppts = "DELETE FROM appointment WHERE customerId=? ;";
                String deleteCust = "DELETE FROM customer WHERE customerId= ? ;";
                
                PreparedStatement prepped_deleteAppts = conn.prepareStatement(deleteAppts);
                PreparedStatement prepped_deleteCust = conn.prepareStatement(deleteCust);
                
                prepped_deleteAppts.setInt(1, customer.getCustomerId());
                prepped_deleteCust.setInt(1, customer.getCustomerId());
                
                prepped_deleteAppts.executeUpdate();
                prepped_deleteCust.executeUpdate();
                
                Close();
                return true;
        }
        
        public static ObservableList ReturnCustomers()throws SQLException, ClassNotFoundException, IOException{

            ObservableList<Customer> customerList = FXCollections.observableArrayList();
       
                Connect();   
                Statement statement = conn.createStatement();
                String query = 
                "SELECT customer.customerId, customer.customerName, address.address,"+
                "address.address2, city.city, country.country, address.postalCode, address.phone "+
                "FROM  customer "+ 
                "LEFT JOIN  address ON  customer.addressId =  address.addressId "+
                "LEFT JOIN  city ON  address.cityId =  city.cityId "+
                "LEFT JOIN country ON city.countryId = country.countryId "+
                "ORDER BY customer.customerName ASC;";

                statement.executeQuery(query);
                ResultSet results = statement.executeQuery(query);
            
            while(results.next()){
                customerList.add(new Customer(results.getInt("customerId"),results.getString("customerName"),results.getString("address"), 
                results.getString("address2"), results.getString("city"), results.getString("country"), results.getString("postalCode"), 
                results.getString("phone")));
            }
            Close();
            
            return customerList; 
        }
        
        public static boolean DeleteAppointment(Appointment appt) throws SQLException, ClassNotFoundException, IOException{
             
                Connect();
                
                String deleteAppts = "DELETE FROM appointment WHERE appointmentId=? ;";
                
                PreparedStatement prepped_deleteAppts = conn.prepareStatement(deleteAppts);
                
                prepped_deleteAppts.setInt(1, appt.getAppointmentId());
                
                prepped_deleteAppts.executeUpdate();
                
                Close();
                return true;
            
        }
        
        public static ObservableList<Appointment> ReturnAppointments()throws SQLException, ClassNotFoundException, IOException{
            
            ObservableList<Appointment> apptList = FXCollections.observableArrayList();

                Connect();   

                String query = 
                "SELECT * FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE appointment.createdBy= ?  AND end > current_timestamp ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, theUser.getUserName());
                
                ResultSet results = prepped_query.executeQuery();
                
            while(results.next()){
                
                LocalDateTime start = results.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = results.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
 
                apptList.add(new Appointment(
                        results.getInt("appointmentId"),
                        results.getString("customerName"),
                        results.getInt("customerId"),
                        new User(results.getInt("userId"), results.getString("userName")),
                        results.getString("title"), 
                        results.getString("description"), 
                        results.getString("location"), 
                        results.getString("contact"), 
                        results.getString("type"),
                        results.getString("url"), 
                        start, 
                        end));  
            }
                Close();
            
            return apptList; 
        }
        
        public static ObservableList<Appointment> ReturnAppointmentsForTheWeek()throws SQLException, ClassNotFoundException, IOException{
            
            ObservableList<Appointment> apptList = FXCollections.observableArrayList();

            Calendar c = Calendar.getInstance();
            int week = c.get(Calendar.WEEK_OF_YEAR);
                
                Connect();   

                String query = 
                "SELECT * FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE appointment.createdBy= ?  AND end > current_timestamp AND WEEK(start)= ? AND YEAR(start)= ?  "+ 
                "ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, theUser.getUserName());
                prepped_query.setInt(2,week);
                prepped_query.setInt(3,LocalDate.now().getYear());
                
                ResultSet results = prepped_query.executeQuery();
                
            while(results.next()){
                
                LocalDateTime start = results.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = results.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
 
                apptList.add(new Appointment(
                        results.getInt("appointmentId"),
                        results.getString("customerName"),
                        results.getInt("customerId"),
                        new User(results.getInt("userId"), results.getString("userName")),
                        results.getString("title"), 
                        results.getString("description"), 
                        results.getString("location"), 
                        results.getString("contact"), 
                        results.getString("type"),
                        results.getString("url"), 
                        start, 
                        end));  
            }
                Close();
            
            return apptList; 
        }
        
        public static ObservableList<Appointment> ReturnAppointmentsForTheMonth()throws SQLException, ParseException, ClassNotFoundException, IOException{
            
            ObservableList<Appointment> apptList = FXCollections.observableArrayList();

            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("MMM").parse(LocalDateTime.now().getMonth().toString()));
            int month = c.get(Calendar.MONTH) + 1; 
                
                Connect();   

                String query = 
                "SELECT * FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE appointment.createdBy= ?  AND end > current_timestamp AND MONTH(start)= ?  AND YEAR(start)= ? "+
                "ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, theUser.getUserName());
                prepped_query.setInt(2, month);
                prepped_query.setInt(3, LocalDateTime.now().getYear());
                 
                ResultSet results = prepped_query.executeQuery();
                
            while(results.next()){
                
                LocalDateTime start = results.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = results.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
 
                apptList.add(new Appointment(
                        results.getInt("appointmentId"),
                        results.getString("customerName"),
                        results.getInt("customerId"),
                        new User(results.getInt("userId"), results.getString("userName")),
                        results.getString("title"), 
                        results.getString("description"), 
                        results.getString("location"), 
                        results.getString("contact"), 
                        results.getString("type"),
                        results.getString("url"), 
                        start, 
                        end));  
            }
                Close();
            
            return apptList; 
        }
        
        public static int ReturnAppointmentCountByType(String type, String monthString, int year)
                throws SQLException, ParseException, ClassNotFoundException, IOException{
            
            int count = 0;

                Connect();   

                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("MMM").parse(monthString));
                int month = c.get(Calendar.MONTH) + 1;   
                
                String query = 
                "SELECT COUNT(*) AS typeCount FROM  appointment  LEFT JOIN  customer ON  appointment.customerId =  customer.customerId "+
                "LEFT JOIN user ON appointment.userId = user.userId "+
                "WHERE type= ? AND MONTH(start)= ?  AND YEAR(start)= ? "+
                "ORDER BY start ASC;";
                
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, type);
                prepped_query.setInt(2, month);
                prepped_query.setInt(3, year);
                
                ResultSet results = prepped_query.executeQuery();
                
            if(results.next()){
                count = results.getInt("typeCount");
            }
            
                Close();
            
            return count; 
        }
        
        public static ArrayList ReturnCountries()throws SQLException, ClassNotFoundException, IOException{
            ArrayList<Country> theList = new ArrayList<>();

            Connect();   
            String query = "SELECT * FROM country ORDER BY country.country ASC;";
            
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeQuery(query);
            
            ResultSet results = statement.executeQuery();

            while(results.next()){
                theList.add(new Country(results.getInt("countryId"), results.getString("country")));
            }

            Close();

            return theList;
        }
        
        public static ArrayList ReturnCities()throws SQLException, ClassNotFoundException, IOException{
            ArrayList<City> theList = new ArrayList<>();

            Connect();  
            
            String query = "SELECT * FROM city ORDER BY city.city ASC";
            
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeQuery();
            
            ResultSet results = statement.executeQuery(query);

            while(results.next()){
                theList.add(new City(results.getInt("cityId"), results.getString("city"), results.getInt("countryId")));
            }

            Close();

            return theList;
        }
        
        public static void CreateCustomer(String customerName, Address address, User user)throws SQLException, ClassNotFoundException, IOException{
                Connect();   
                
                String insert = "INSERT INTO address "
                        + "(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) " +
                          "VALUES (?,?,?,?,?,CURDATE(),?,?)";
       
                PreparedStatement prepped_insert = conn.prepareStatement(insert);
                prepped_insert.setString(1, address.getAddress());
                prepped_insert.setString(2, address.getAddress2());
                prepped_insert.setInt(3, address.getCityId());
                prepped_insert.setString(4, address.getPostalCode());
                prepped_insert.setString(5, address.getPhone());
                prepped_insert.setString(6, user.getUserName());
                prepped_insert.setString(7, user.getUserName());
                
                prepped_insert.executeUpdate();
                
                String query = "SELECT addressId FROM address WHERE address= ? AND address2= ?  AND cityId= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, address.getAddress());
                prepped_query.setString(2, address.getAddress2());
                prepped_query.setInt(3, address.getCityId());
                ResultSet results = prepped_query.executeQuery();
                               
                int addressId = 0;
                while(results.next()){
                    addressId = results.getInt("addressId");
                }
                
                insert = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) " +
                          "VALUES (?,?,?,current_timestamp,?,?)";
                
                PreparedStatement update = conn.prepareStatement(insert);
                
                update.setString(1, customerName);
                update.setInt(2, addressId);
                update.setInt(3, 1);
                update.setString(4, user.getUserName());
                update.setString(5, user.getUserName());
                update.execute();
                
                Close();
        }
        public static void CreateAppointment(Appointment appt)throws SQLException, ClassNotFoundException, IOException{
           
         Connect();        
                
                ZonedDateTime z_start = appt.getT_start().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime z_end = appt.getT_end().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

                Timestamp t_start = Timestamp.valueOf(z_start.toLocalDateTime()); 
                Timestamp t_end = Timestamp.valueOf(z_end.toLocalDateTime()); 

                String insert = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, "
                        + "end, createDate, createdBy, lastUpdate, lastUpdateBy) " + 
                        "VALUES (?,?,?,?,?,?,?,?,?,?, current_timestamp,?,current_timestamp, ?);"; 
                
                PreparedStatement prepped_insert = conn.prepareStatement(insert);
                
                prepped_insert.setInt(1, appt.getCustomerId());
                prepped_insert.setInt(2, appt.getUserId());
                prepped_insert.setString(3, appt.getTitle());
                prepped_insert.setString(4, appt.getDescription());
                prepped_insert.setString(5, appt.getLocation());
                prepped_insert.setString(6, appt.getContact());
                prepped_insert.setString(7, appt.getType());
                prepped_insert.setString(8, appt.getUrl());
                prepped_insert.setTimestamp(9, t_start);
                prepped_insert.setTimestamp(10, t_end);               
                prepped_insert.setString(11, appt.getUser());
                prepped_insert.setString(12, appt.getUser());           
                
                prepped_insert.executeUpdate();

                Close();
        }
       
        public static void CreateCity(String city, int countryId, String user) throws ClassNotFoundException, IOException{
            try {   
                Connect();
             
                String insert = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy) " +
                         "VALUES (?,?,CURDATE(),?,?)";
                
                PreparedStatement prepped_insert = conn.prepareStatement(insert);
                prepped_insert.setString(1, city);
                prepped_insert.setInt(2, countryId);
                prepped_insert.setString(3, user);
                prepped_insert.setString(4, user);
                
                prepped_insert.executeUpdate();
                
                Close();
            }
                catch (SQLException ex) {
                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public static Customer ReturnCustomer(int customerId)throws SQLException, ClassNotFoundException, IOException{
      
                Connect();
            
                String query = "SELECT * FROM customer WHERE customerId= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                
                prepped_query.setInt(1, customerId);
                ResultSet results = prepped_query.executeQuery();
                results.next();
                Customer c = new Customer(results.getInt("customerId"), results.getString("customerName"), results.getInt("addressId"));
                
                Close();
                return c;
        }
        
        public static Address ReturnAddress(int addressId)throws SQLException, ClassNotFoundException, IOException{
          
                Connect();
            
                String query = "SELECT * FROM address WHERE addressId= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setInt(1, addressId);
                
                ResultSet results = prepped_query.executeQuery();
                
                results.next();
                Address a = new Address(results.getInt("addressId"), results.getString("address"), results.getString("address2"),
                       results.getInt("cityId"), results.getString("postalCode"), results.getString("phone")) ;
                
                Close();
                return a;
        }
        
        public static City ReturnCity(int cityId)throws SQLException, ClassNotFoundException, IOException{
            
                Connect();
                
                String query = "SELECT * FROM city WHERE cityId= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setInt(1, cityId);
                
                ResultSet results = prepped_query.executeQuery();
                results.next();
                City c = new City(results.getInt("cityId"), results.getString("city"), results.getInt("countryId"));
                
                Close();
                return c;
        }
        
        public static City ReturnCity(String cityName)throws SQLException, ClassNotFoundException, IOException{
        
                Connect();
                
                String query = "SELECT * FROM city WHERE city= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setString(1, cityName);
                
                ResultSet results = prepped_query.executeQuery();
                results.next();
                City c = new City(results.getInt("cityId"), results.getString("city"), results.getInt("countryId"));
                
                Close();
                return c;
        }
        
        public static Country ReturnCountry(int countryId)throws SQLException, ClassNotFoundException, IOException{
          
                Connect();
            
                Statement statement = conn.createStatement();
                
                String query = "SELECT * FROM country WHERE countryId= ? ;";
                PreparedStatement prepped_query = conn.prepareStatement(query);
                prepped_query.setInt(1, countryId);
                
                ResultSet results = prepped_query.executeQuery();
                results.next();
                Country c = new Country(results.getInt("countryId"), results.getString("country"));
                
                Close();
                return c;
        }
        
        public static void UpdateCustomer(Customer cust)throws SQLException, ClassNotFoundException, IOException{
   
                Connect();

                String update = "UPDATE customer SET customerName= ? WHERE customerId = ? ;";
                
                PreparedStatement statement = conn.prepareStatement(update);
                
                statement.setString(1, cust.getCustomerName());
                statement.setInt(2, cust.getCustomerId());
 
                statement.executeUpdate();    
                
                Close();
        }
        
        public static void UpdateAddress(Address address)throws SQLException, ClassNotFoundException, IOException{
            
                Connect();
                
                String update = "UPDATE address SET address= ? , address2= ? , cityId= ? , postalCode= ? , phone= ? WHERE addressId= ? ;";
                
                PreparedStatement statement = conn.prepareStatement(update);
                statement.setString(1, address.getAddress());
                statement.setString(2, address.getAddress2());
                statement.setInt(3, address.getCityId());
                statement.setString(4, address.getPostalCode());
                statement.setString(5, address.getPhone());
                statement.setInt(6, address.getAddressId());
                
                statement.executeUpdate();     
                
                Close();
        }
        
        public static void UpdateAppointment(Appointment appt, User user) throws SQLException, ClassNotFoundException, IOException{
            Connect();
            
            ZonedDateTime z_start = appt.getT_start().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime z_end = appt.getT_end().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

            Timestamp t_start = Timestamp.valueOf(z_start.toLocalDateTime()); 
            Timestamp t_end = Timestamp.valueOf(z_end.toLocalDateTime());
            
            String update = "UPDATE appointment "+
                            "SET customerId = ? , title = ? , description = ? , location = ? , contact = ? , "+
                            "type = ? , url = ? , start = ? , end = ? , lastUpdateBy = ? "+
                            "WHERE appointmentId = ?;";
            
            PreparedStatement statement = conn.prepareStatement(update);
            
            statement.setInt(1, appt.getCustomerId());
            statement.setString(2, appt.getTitle());
            statement.setString(3, appt.getDescription());
            statement.setString(4, appt.getLocation());
            statement.setString(5, appt.getContact());
            statement.setString(6, appt.getType());
            statement.setString(7, appt.getUrl());
            statement.setTimestamp(8, t_start);
            statement.setTimestamp(9, t_end);
            statement.setString(10, user.getUserName());
            statement.setInt(11, appt.getAppointmentId());
            
            statement.executeUpdate();
            
            Close();
        }
        
        public static boolean AppointmentOverlap(Appointment appt, User user)throws SQLException, ClassNotFoundException, IOException{

                Connect();
                
                ZonedDateTime z_start = appt.getT_start().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime z_end = appt.getT_end().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));            

                Timestamp t_start = Timestamp.valueOf(z_start.toLocalDateTime()); 
                Timestamp t_end = Timestamp.valueOf(z_end.toLocalDateTime());
                
                String query = "SELECT * FROM appointment WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
                        + "AND createdBy = ? ";
                
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setTimestamp(1, t_start);
                statement.setTimestamp(2, t_end);
                statement.setTimestamp(3, t_start);
                statement.setTimestamp(4, t_end);
                statement.setString(5, user.getUserName());
                ResultSet results = statement.executeQuery();  
                
                if(results.next()){
                    Close();
                    return true;
                }
                else{
                    Close();
                    return false;
                }
        }
}
