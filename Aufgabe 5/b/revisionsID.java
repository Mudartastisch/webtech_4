import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

public class revisionsID {

  public static void main(String argv[]) {    

    String file = "../ndswiki-latest-pages-articles-multistream.xml";
    
    try {
    
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();

      SAXHandler handler = new SAXHandler();

      System.out.println("Parsing...");
      
      saxParser.parse(file, handler);
      
      System.out.println("Revision ID of article 'Erfurt': " + handler.getID());
 
    } catch (Exception e) {
    
      e.printStackTrace();
      
    }
  
  }

}