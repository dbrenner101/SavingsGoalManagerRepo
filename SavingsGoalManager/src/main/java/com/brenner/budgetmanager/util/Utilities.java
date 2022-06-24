package com.brenner.budgetmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utilities {

    private static final Logger log = LoggerFactory.getLogger(Utilities.class);
    
    private static final String COMMON_DATE_FORMAT = "yyyy-MM-dd";
    
    public static Date convertStringToDate(String dateString) throws ParseException {
        log.info("Entered convertStringToDate()");
        log.debug("Param: dateString: {}", dateString);
        
        SimpleDateFormat format = new SimpleDateFormat(COMMON_DATE_FORMAT);
        Date d = format.parse(dateString);
        
        
        log.debug("returning dateTime: {}", d.toString());
        
        log.info("Exiting convertStringToDate()");
        return d;
    }
}
