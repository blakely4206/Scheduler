
package scheduler.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import scheduler.Model.User;

public class Logger {
    public static void Log(User u){
         try {
             
	    Path filePath = Paths.get("log.txt");
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            
            String logString = dateFormat.format(date) + " " + u.getUserID() + " " + u.getUserName() + System.lineSeparator();
            
            Files.write(filePath, logString.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            
    	} 
         catch (IOException ex) {
	}

    }
}
