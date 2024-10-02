import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XMLGenerator {
    
    // Method to generate XML formatted string from tokens
    public String generateXML(List<Token> tokens) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<TOKENS>").append(System.lineSeparator());
        
        for (Token token : tokens) {
            xmlBuilder.append(token.toString());
        }
        
        xmlBuilder.append("</TOKENS>").append(System.lineSeparator());
        return xmlBuilder.toString();
    }

    // Method to write XML content to a file
    public void writeXMLToFile(String xmlContent, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(xmlContent);
            System.out.println("XML file created successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
