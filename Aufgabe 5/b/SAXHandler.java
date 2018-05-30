import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

public class SAXHandler extends DefaultHandler {

  String id = "";
  boolean page = false; 
  boolean title = false; 
  boolean erfurt = false;  
  boolean revision = false;  
  boolean getid = false;
      
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.equalsIgnoreCase("PAGE")) {
      page = true;
    }
    else if (page && qName.equalsIgnoreCase("TITLE")) {
      title = true;
    }
    else if (erfurt && qName.equalsIgnoreCase("REVISION")) {
      erfurt = false;
      revision = true;
    }  
    else if (revision && qName.equalsIgnoreCase("ID")) {
      revision = false;
      getid = true;
    }
  }
  
  public void characters(char ch[], int start, int length) {
    if (title) {
      String fullTitle = new String(ch, start, length);
    
      if (fullTitle.equals("Erfurt")) {
      
        System.out.println("Found article titled '" + fullTitle + "'.");
        erfurt = true;
        
      }
      
      title = false;
    }
    
    if (getid) {
      id = new String(ch, start, length);
      getid = false;
    }
  }  
  
  public void endElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.equalsIgnoreCase("PAGE")) {
      page = false;
    }
  }
        
  public String getID() { 
    return id;
  }  

}