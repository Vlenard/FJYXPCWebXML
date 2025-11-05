package domfjyxpc1029;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;

public class DomRead1FJYXPC {
    public static void main(String[] args) throws Exception {
        File xmlFile = new File("XGBT95_orarend.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        System.out.println("Gyökér elem: " + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("ora");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println("\nAktuális elem: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;

                String oid = elem.getAttribute("id");
                String tipus = elem.getAttribute("tipus");

                String targy = elem.getElementsByTagName("targy").item(0).getTextContent();

                Element idopontElem = (Element) elem.getElementsByTagName("idopont").item(0);
                String nap = idopontElem.getElementsByTagName("nap").item(0).getTextContent();
                String tol = idopontElem.getElementsByTagName("tol").item(0).getTextContent();
                String ig = idopontElem.getElementsByTagName("ig").item(0).getTextContent();

                String helyszin = elem.getElementsByTagName("helyszin").item(0).getTextContent();
                String oktato = elem.getElementsByTagName("oktato").item(0).getTextContent();
                String szak = elem.getElementsByTagName("szak").item(0).getTextContent();

                System.out.println("Óra ID: " + oid);
                System.out.println("Típus: " + tipus);
                System.out.println("Tantárgy: " + targy);
                System.out.println("Nap: " + nap);
                System.out.println("Időpont: " + tol + " - " + ig);
                System.out.println("Helyszín: " + helyszin);
                System.out.println("Oktató: " + oktato);
                System.out.println("Szak: " + szak);
            }
        }
    }
}
