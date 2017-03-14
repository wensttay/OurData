/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifpb.simba.ourdata.validation;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Pedro Arthur
 */
public class ColumnCellValidation {
    
    public static boolean hasJustSpecialCharacters(String cell) {
        String splChrs = "-/@#$%^&_+=()" ;
        
        return cell.matches("[" + splChrs + "]+");
    }
    
    public static boolean isADate(String cell) {
        
        if(cell == null) return false;
        
        for(Locale locale : DateFormat.getAvailableLocales()) {
            
            for(Integer style = DateFormat.FULL; style <= DateFormat.SHORT; style++) {
                
                DateFormat dt = DateFormat.getDateInstance(style,locale);
                
                try {
                    
                    dt.parse(cell);
                    return true;
                } catch (java.text.ParseException ex) {
                    
                } 
            }
        }  
        return false;
    }
    
    public static boolean isDoubleOrFloat(String cell) {
        
        if(cell == null) return false;
        
        try {
            
            //point 
            Double d = Double.parseDouble(cell);
            return true;
            
        } catch(NumberFormatException ex) {
            
            //comma
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            try {
                
                Number number = format.parse(cell);
                return true;
                
            } catch (java.text.ParseException ex1) {
                return false;
            }
        }
    }
    
    public static boolean isWKT(String cell) {
        
        if(cell == null) return false;
        
        try {
            WKTReader reader = new WKTReader();
            reader.read(cell);
            
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
    
    public static boolean isInteger(String cell) {
        
        if(cell == null) return false;
        
        try {
            Integer integer = Integer.parseInt(cell);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
}
