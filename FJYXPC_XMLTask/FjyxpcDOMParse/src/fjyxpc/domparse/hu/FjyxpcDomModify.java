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

    // ========== Attribútumnevek magyarítása a megjelenítéshez ==========
    private static final Map<String, String> kulcsnevek = new HashMap<>();
    static {
        kulcsnevek.put("feId", "Felhasználó azonosító");
        kulcsnevek.put("sId", "Sör azonosító");
        kulcsnevek.put("uId", "Üzlet azonosító");
        kulcsnevek.put("fozdeId", "Főzde azonosító");
        kulcsnevek.put("foId", "Forgalmazó azonosító");
        kulcsnevek.put("cId", "Címke azonosító");
    }

    // ========= Mely XML elemek jelenjenek meg a konzolon =========
    private static final Set<String> megjelenitendo = new HashSet<>(Arrays.asList(
            "felhasznalo",
            "vasarlas",
            "sor",
            "fozes"
    ));

    public static void main(String[] args) {
        try {

            // ======================== XML BEOLVASÁS ============================
            File xmlFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            // ===================================================================
            // 1) MÓDOSÍTÁS: első felhasználó első vásárlásának ára -> 999
            // ===================================================================
            NodeList vasarlasLista = doc.getElementsByTagName("vasarlas");

            for (int i = 0; i < vasarlasLista.getLength(); i++) {
                Element vas = (Element) vasarlasLista.item(i);

                // első olyan <vasarlas>, ahol uId = 1
                if ("1".equals(vas.getAttribute("uId"))) {
                    Node ar = vas.getElementsByTagName("ar").item(0);
                    if (ar != null) {
                        System.out.println("Eredeti ár: " + ar.getTextContent());
                        ar.setTextContent("999");
                        System.out.println("Új ár -> 999\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 2) MÓDOSÍTÁS: Sör (sId=1) értékelése -> 1
            // ===================================================================
            NodeList ertekelesLista = doc.getElementsByTagName("ertekeles");
            for (int i = 0; i < ertekelesLista.getLength(); i++) {
                Element ert = (Element) ertekelesLista.item(i);

                if ("1".equals(ert.getAttribute("sId"))) {
                    Node pont = ert.getElementsByTagName("pontszam").item(0);

                    if (pont != null) {
                        System.out.println("Eredeti értékelés: " + pont.getTextContent());
                        pont.setTextContent("1");
                        System.out.println("Új értékelés -> 1\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 3) MÓDOSÍTÁS: Sör (sId=1) műfaja -> "ale"
            // ===================================================================
            NodeList sorLista = doc.getElementsByTagName("sor");
            for (int i = 0; i < sorLista.getLength(); i++) {
                Element sor = (Element) sorLista.item(i);

                if ("1".equals(sor.getAttribute("sId"))) {
                    Node mufaj = sor.getElementsByTagName("mufaj").item(0);

                    if (mufaj != null) {
                        System.out.println("Eredeti műfaj: " + mufaj.getTextContent());
                        mufaj.setTextContent("ale");
                        System.out.println("Új műfaj -> ale\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 4) MÓDOSÍTÁS: főzés (sId=1) főzőmestere -> "Alaktos István"
            // ===================================================================
            NodeList fozesLista = doc.getElementsByTagName("fozes");
            for (int i = 0; i < fozesLista.getLength(); i++) {
                Element fo = (Element) fozesLista.item(i);

                if ("1".equals(fo.getAttribute("sId"))) {
                    Node mester = fo.getElementsByTagName("fozoMester").item(0);

                    if (mester != null) {
                        System.out.println("Eredeti főzőmester: " + mester.getTextContent());
                        mester.setTextContent("Alaktos István");
                        System.out.println("Új főzőmester -> Alaktos István\n");
                    }
                    break;
                }
            }


            // ===================================================================
            // 5) KIVÁLASZTOTT ENTITÁSOK KIÍRÁSA KAPCSOLATOS MAGYAR KULCSOKKAL
            // ===================================================================
            System.out.println("\n====== KIVÁLASZTOTT ENTITÁSOK ======\n");

            Node root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) n;

                    // Csak a kiválasztott elemek
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

                    // ===== CHILD NODES KIÍRÁSA =====
                    NodeList subs = elem.getChildNodes();
                    for (int j = 0; j < subs.getLength(); j++) {
                        Node sub = subs.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element se = (Element) sub;

                            // Ha az elemnek vannak további elemei (pl. cím blokk)
                            if (hasElementChild(se)) {
                                System.out.println(se.getNodeName() + ":");
                                NodeList deep = se.getChildNodes();
                                for (int k = 0; k < deep.getLength(); k++) {
                                    Node d = deep.item(k);
                                    if (d.getNodeType() == Node.ELEMENT_NODE) {
                                        System.out.println("    " + d.getNodeName() + ": " + d.getTextContent());
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

    // ===============================================================
    // Segédfüggvény: megállapítja, hogy van-e további elem gyerek
    // ===============================================================
    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }
}
