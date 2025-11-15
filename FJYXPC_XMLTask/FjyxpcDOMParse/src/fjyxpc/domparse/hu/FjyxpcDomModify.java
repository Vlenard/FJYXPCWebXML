package fjyxpc.domparse.hu;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FjyxpcDomModify {
    // ======= MAGYARÍTOTT ATTRIBÚTUMNEVEK =======
    private static final Map<String, String> kulcsnevek = new HashMap<>();

    static {
        kulcsnevek.put("feId", "Felhasználó azonosító");
        kulcsnevek.put("sId", "Sör azonosító");
        kulcsnevek.put("uId", "Üzlet azonosító");
        kulcsnevek.put("fozdeId", "Főzde azonosító");
        kulcsnevek.put("foId", "Forgalmazó azonosító");
        kulcsnevek.put("cId", "Címke azonosító");
    }

    // ======= MEGJELENÍTENDŐ ENTITÁSOK =======
    // Írd át vagy bővítsd tetszés szerint!
    private static final Set<String> megjelenitendo = new HashSet<>(Arrays.asList(
            "felhasznalo",
            "vasarlas",
            "sor"
    ));

    public static void main(String[] args) {
        try {
            // ===== XML beolvasása =====
            File xmlFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // ====================================================================
            // 1) ÁR MÓDOSÍTÁSA: első felhasználó első vásárlása
            // ====================================================================
            NodeList vasarlasLista = doc.getElementsByTagName("vasarlas");

            for (int i = 0; i < vasarlasLista.getLength(); i++) {
                Element vas = (Element) vasarlasLista.item(i);

                if ("1".equals(vas.getAttribute("uId"))) {
                    Node ar = vas.getElementsByTagName("ar").item(0);
                    if (ar != null) {
                        System.out.println("Eredeti ár: " + ar.getTextContent());
                        ar.setTextContent("999");
                        System.out.println("Új ár beállítva: 999\n");
                    }
                    break;
                }
            }

            // ====================================================================
            // 2) KIVÁLASZTOTT ENTITÁSOK KIÍRÁSA MAGYARÍTOTT KULCSOKKAL
            // ====================================================================
            System.out.println("====== KIVÁLASZTOTT ENTITÁSOK KIÍRÁSA ======\n");

            Node root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) n;

                    // Csak a kiválasztott entitások jelennek meg
                    if (!megjelenitendo.contains(elem.getNodeName()))
                        continue;

                    System.out.println("Elem: " + elem.getNodeName());
                    System.out.println("--------------------------------");

                    // ===== ATTRIBÚTUMOK MAGYARÍTÁSA =====
                    NamedNodeMap attrs = elem.getAttributes();
                    for (int a = 0; a < attrs.getLength(); a++) {
                        Node attr = attrs.item(a);
                        String kulcs = kulcsnevek.getOrDefault(attr.getNodeName(), attr.getNodeName());
                        System.out.println(kulcs + ": " + attr.getNodeValue());
                    }

                    // ===== GYERMEK ELEMEK KIÍRÁSA =====
                    NodeList subs = elem.getChildNodes();
                    for (int j = 0; j < subs.getLength(); j++) {
                        Node sub = subs.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element se = (Element) sub;

                            if (hasElementChild(se)) {
                                System.out.println(se.getNodeName() + ":");
                                NodeList deep = se.getChildNodes();
                                for (int k = 0; k < deep.getLength(); k++) {
                                    Node d = deep.item(k);
                                    if (d.getNodeType() == Node.ELEMENT_NODE) {
                                        System.out.println("        " + d.getNodeName() + ": " + d.getTextContent());
                                    }
                                }
                            } else {
                                System.out.println(se.getNodeName() + ": " + se.getTextContent());
                            }
                        }
                    }

                    System.out.println("--------------------------------\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Segédfüggvény: van-e elem típusú gyermeke
    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }
}
