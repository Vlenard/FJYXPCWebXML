package xpathfjyxpc;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xPathQueryFjyxpc {
    public static void main(String[] args) {
        try {

            DocumentBuilderFactory documberBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder DocumentBuilder = documberBuilderFactory.newDocumentBuilder();

            Document document = DocumentBuilder.parse("FJYXPC_1112/xpathFJYXPC/src/studentFJYXPC.xml");

            document.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();

            String neptunkod = "//student";
            NodeList neptunKod = (NodeList) xPath.compile(neptunkod).evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < neptunKod.getLength(); i++) {
                Node node = neptunKod.item(i);
                System.out.println("\nAktuális elem: " + node.getNodeName());

                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("student")) {
                    Element element = (Element) node;

                    System.out.println("Hallgato ID: " + element.getAttribute("id"));
                    System.out.println(
                            "keresztnév: " + element.getElementsByTagName("keresztnev").item(0).getTextContent());
                    System.out.println(
                            "Vezeteknév: " + element.getElementsByTagName("vezeteknev").item(0).getTextContent());
                    System.out.println("Becenév: " + element.getElementsByTagName("becenev").item(0).getTextContent());
                    System.out.println("kor: " + element.getElementsByTagName("kor").item(0).getTextContent());
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
