
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import jaxb.Feed;
import jaxb.Feed.Entry;
import jaxb.Feed.Entry.Author;
import jaxb.Feed.Entry.Link;

/**
 * Provides an interface to the feed document
 */
public class FeedModel {

    // Constant file reference to the feed document
    private final static File FEED_FILE = new File("feed.xml");

    // Constant file reference to your XSL style sheet
    private final static File XSL_SHEET = new File("xsl/feed-to-html.xsl");

    private static final Schema schema = loadSchema();
    private Feed feed;
    private final Marshaller marshaller;
    private Unmarshaller unmarshaller;

    /**
     * Default constructor
     */
    public FeedModel() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Feed.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            this.feed = (Feed) unmarshaller.unmarshal(FEED_FILE);
            try {
                unmarshaller.setSchema(schema);
                unmarshaller.setEventHandler(new handleEvent());
            } catch (RuntimeException re) {
                re.printStackTrace();
            }

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        this.marshaller = createMarshaller(Feed.class, schema);

        // Unserialize XML data into new Java content trees
        // This will also validate the XML data when the schema is set
    }

    public Entry findEntryByID(String ID) {
        List<Entry> entries = feed.getEntry();
        for (Entry e : entries) {
            if (e.getId().equals(ID)) {
                return e;
            }
        }
        return null;
    }

    public String addEntry(String title, String url, String summary, String author) throws JAXBException, FileNotFoundException, RuntimeException {
        /*
         @XmlType(name = "", propOrder = {
         "id",
         "title",
         "updated",
         "author",
         "link",
         "summary"
         })
         */

        Entry nextEntry = new Entry();
        //ID
        nextEntry.setId(Integer.toString(url.hashCode()));
        //title
        nextEntry.setTitle(title);
        //updated
        nextEntry.setUpdated(getXMLGregorianCalendarNow());
        //author
        Author nextEntryAuthor = new Author();
        nextEntryAuthor.setName(author);
        nextEntry.setAuthor(nextEntryAuthor);
        //link
        Link nextEntryLink = new Link();
        nextEntryLink.setHref(url);
        nextEntry.setLink(nextEntryLink);
        //summary
        nextEntry.setSummary(summary);

        /*
         Test if Article already exists (by ID)     
         */
        Entry duplicate = findEntryByID(nextEntry.getId());
        if (duplicate == null) {
            try {
                feed.getEntry().add(0, nextEntry);
                unmarshaller.setSchema(schema);
                unmarshaller.setEventHandler(new handleEvent());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            feed.getEntry().get(0).setUpdated(getXMLGregorianCalendarNow());
            boolean bool = false;

            try {

                // tries to create new file in the system
                bool = FEED_FILE.createNewFile();

                // prints
                System.out.println("File exists already: " + bool);

                // deletes file from the system
                FEED_FILE.delete();

                // delete() is invoked
                System.out.println("delete() method is invoked");

            } catch (Exception e) {
                e.printStackTrace();
            }
            marshaller.marshal(feed, new FileOutputStream("feed.xml"));
            String output = feed.getEntry().get(0).getId();
            return output;
            
        } else {
            StringBuilder out = new StringBuilder();
            out.append("\nFound entry with same ID\n");
            out.append("ID: ").append(duplicate.getId()).append("\n");
            out.append("Title: ").append(duplicate.getTitle()).append("\n");
            out.append("Updated: ").append(duplicate.getUpdated()).append("\n");
            out.append("Author: ").append(duplicate.getAuthor().getName()).append("\n");
            out.append("Link: ").append(duplicate.getLink().getHref()).append("\n");
            out.append("Summary: ").append(duplicate.getSummary()).append("\n");
            System.out.println(out.toString());
            return "Did not add entry";
        }
    }

    // TO DO Implement the functionality specified in the assignment
    /**
     * TO DO Test your implementation
     *
     * @param args
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        FeedModel model = new FeedModel();
        System.out.println(model.addEntry("TestTitle", "http://test.de/test", "A test for addEntry function", "117516"));

    }

    /**
     * Get the current date and time as an instance of XMLGregorianCalendar.
     * This is necessary because the JAXB Binding Compiler converts `xs:date` to
     * `XMLGregorianCalendar` instead of `Data`.
     *
     * @return current date
     */
    private static XMLGregorianCalendar getXMLGregorianCalendarNow() {
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

            return datatypeFactory.newXMLGregorianCalendar(
                    new GregorianCalendar()
            );
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException(
                    "DatatypeFactory was not properly configured.", ex
            );
        }
    }

    /**
     * Loads and instantiates the projects XML Schema file
     *
     * @return reference to a Schema object
     */
    public static Schema loadSchema() {
        URL schemaFilePath = FeedModel.class.getResource("feed.xsd");

        try {
            return SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(schemaFilePath);
        } catch (SAXException ex) {
            throw new RuntimeException("Error during schema parsing", ex);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Could not load feed schema", ex);
        }

    }

    /**
     * Creates a properly configured Marshaller for serializing XML
     *
     * @param type Class of the used Java object that is represented
     * @param schema Schema to validate against when writing
     * @return the marshaller
     */
    private static Marshaller createMarshaller(Class type, Schema schema) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setSchema(schema);

            // Add the xml-stylesheet processing instruction
            String xslDeclaration = "<?xml-stylesheet type=\"text/xsl\" href=\""
                    + XSL_SHEET.toString() + "\"?>";
            marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                    xslDeclaration);

            return marshaller;
        } catch (JAXBException ex) {
            throw new RuntimeException("Could not create Marshaller.", ex);
        }
    }

    private static class handleEvent implements ValidationEventHandler {

        public boolean handleEvent(ValidationEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("\nEVENT\n");
            sb.append("SEVERITY:  ").append(event.getSeverity()).append("\n");
            sb.append("MESSAGE:  ").append(event.getMessage()).append("\n");
            sb.append("LINKED EXCEPTION:").append(event.getLinkedException()).append("\n");
            sb.append("LOCATOR").append("\n");
            sb.append("    LINE NUMBER:  ").append(event.getLocator().getLineNumber()).append("\n");
            sb.append("    COLUMN NUMBER:  ").append(event.getLocator().getOffset()).append("\n");
            sb.append("    OBJECT:  ").append(event.getLocator().getObject()).append("\n");
            sb.append("    NODE:  ").append(event.getLocator().getNode()).append("\n");
            sb.append("    URL:  ").append(event.getLocator().getURL()).append("\n");
            sb.append("\t");
            throw new RuntimeException(sb.toString());
        }
    }
}
