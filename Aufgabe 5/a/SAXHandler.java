import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

public class SAXHandler extends DefaultHandler {

  int pages = 0;
      
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.equalsIgnoreCase("PAGE")) {
      pages++;
    }
  }
        
  public int getPages() { 
    return pages;
  }  

}