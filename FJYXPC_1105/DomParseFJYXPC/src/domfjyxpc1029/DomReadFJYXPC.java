package domfjyxpc1029;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import org.xml.sax.*;

import org.w3c.dom.*;

public class DomReadFJYXPC {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        File xmlFile = new File("FJYXPC_1029/DomParseFJYXPC/hallgatoFJYXPC.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document xgbt95 = dBuilder.parse(xmlFile);
        xgbt95.getDocumentElement().normalize();

        System.out.println("Gyökér elem: " + xgbt95.getDocumentElement().getNodeName());

        NodeList nList = xgbt95.getElementsByTagName("hallgato");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println("\nAktuális elem: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;

                String hid = elem.getAttribute("id");

                Node node1 = elem.getElementsByTagName("keresztnev").item(0);
                String kname = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("vezeteknev").item(0);
                String vname = node2.getTextContent();

                Node node3 = elem.getElementsByTagName("foglalkozas").item(0);
                String fname = node3.getTextContent();

                System.out.println("Hallgató ID: " + hid);
                System.out.println("Keresztnév: " + kname);
                System.out.println("Vezetéknév: " + vname);
                System.out.println("Foglalkozás: " + fname);
            }
        }
    }
}
