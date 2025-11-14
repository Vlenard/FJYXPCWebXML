package fjyxpc.domparse.hu;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * A teljes dokumentum (NeptunkodXML.xml) feldolgozása DOM-mal és kiírása
 * "blokk" formában a konzolra.
 */
public class FjyxpcDomRead {
    // Elemnevek magyar megfelelői
    private static final Map<String, String> adatNevek = new HashMap<>();

    // Attribútumok magyar megnevezései
    private static final Map<String, String> kulcsNevek = new HashMap<>();

    static {
        // ========= XML elemek magyar nevei =========
        adatNevek.put("felhasznalo", "Felhasználó");
        adatNevek.put("nev", "Név");
        adatNevek.put("elerhetoseg", "Elérhetőség");
        adatNevek.put("email", "E-mail cím");
        adatNevek.put("telefonszam", "Telefonszám");
        adatNevek.put("jelszo", "Jelszó");
        adatNevek.put("profilkep", "Profilkép");

        adatNevek.put("ertekeles", "Értékelés");
        adatNevek.put("pontszam", "Pontszám");
        adatNevek.put("ertekeles_szoveg", "Értékelés szövege");

        adatNevek.put("sor", "Sör");
        adatNevek.put("mufaj", "Műfaj");
        adatNevek.put("alkohol", "Alkoholtartalom");

        adatNevek.put("fozes", "Főzés");
        adatNevek.put("fozoMester", "Főzőmester");
        adatNevek.put("datum", "Dátum");

        adatNevek.put("fozde", "Főzde");
        adatNevek.put("szlogen", "Szlogen");
        adatNevek.put("alapitva", "Alapítva");
        adatNevek.put("cim", "Cím");
        adatNevek.put("varos", "Város");
        adatNevek.put("utca", "Utca");
        adatNevek.put("hazszam", "Házszám");

        adatNevek.put("cimke", "Címke");
        adatNevek.put("kaloria", "Kalória");
        adatNevek.put("osszetevok", "Összetevők");
        adatNevek.put("nettoTerfogat", "Nettó Térfogat (L)");
        adatNevek.put("ibu", "IBU érték");

        adatNevek.put("vasarlas", "Vásárlás");
        adatNevek.put("ar", "Ár");

        adatNevek.put("forgalmazo", "Forgalmazó");
        adatNevek.put("webhely", "Webhely");
        adatNevek.put("szekhely", "Székhely");
        adatNevek.put("vevoszolgalat", "Vevőszolgálat");

        adatNevek.put("uzlet", "Üzlet");
        adatNevek.put("nyitvatartas", "Nyitvatartás");
        adatNevek.put("googleErtekeles", "Google értékelés");

        // ========= Attribútumok magyar nevei =========
        kulcsNevek.put("feId", "Felhasználó azonosító");
        kulcsNevek.put("sId", "Sör azonosító");
        kulcsNevek.put("foId", "Forgalmazó azonosító");
        kulcsNevek.put("fozdeId", "Főzde azonosító");
        kulcsNevek.put("uId", "Üzlet azonosító");
        kulcsNevek.put("cId", "Címke azonosító");
    }

    public static void main(String[] args) {
        try {
            File xmlFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            FileWriter writer = new FileWriter("FJYXPC_XMLTask/FJYXPC_XML_output.txt");

            String rootName = doc.getDocumentElement().getNodeName();

            printLine("Gyökér elem: " + at(rootName), writer);
            printLine("--------------------------------", writer);

            NodeList rootChildren = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node node = rootChildren.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element elem = (Element) node;

                    printLine("\nElem: " + at(elem.getNodeName()), writer);
                    printLine("--------------------------------", writer);

                    // ======== ATTRIBÚTUMOK (kulcsok) kiírása magyar névvel ========
                    NamedNodeMap attributes = elem.getAttributes();
                    if (attributes != null && attributes.getLength() > 0) {
                        for (int a = 0; a < attributes.getLength(); a++) {
                            Node attr = attributes.item(a);

                            String magyarKulcs = kulcsNevek.getOrDefault(attr.getNodeName(), attr.getNodeName());

                            printLine(magyarKulcs + ": " + attr.getNodeValue(), writer);
                        }
                    }

                    // ======== Gyermekelemek bejárása ========
                    NodeList subNodes = elem.getChildNodes();

                    for (int j = 0; j < subNodes.getLength(); j++) {
                        Node sub = subNodes.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElem = (Element) sub;

                            if (subElem.hasChildNodes() && hasElementChild(subElem)) {
                                printLine(at(subElem.getNodeName()) + ":", writer);

                                NodeList deepList = subElem.getChildNodes();
                                for (int k = 0; k < deepList.getLength(); k++) {
                                    Node deep = deepList.item(k);

                                    if (deep.getNodeType() == Node.ELEMENT_NODE) {
                                        printLine("    " + at(deep.getNodeName()) + ": " + deep.getTextContent(),
                                                writer);
                                    }
                                }

                            } else {
                                printLine(at(subElem.getNodeName()) + ": " + subElem.getTextContent(), writer);
                            }
                        }
                    }

                    printLine("--------------------------------", writer);
                }
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }

    // Elemnév magyarítása
    private static String at(String name) {
        return adatNevek.getOrDefault(name, name);
    }

    // Konzol + fájl kiírás
    private static void printLine(String text, FileWriter writer) throws Exception {
        System.out.println(text);
        writer.write(text + System.lineSeparator());
    }
}
