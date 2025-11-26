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

/**
 * ========================================================================
 * DOM alapú XML olvasó program
 * 
 * Feladata:
 *  - A teljes XML dokumentum bejárása (FJYXPC_XML.xml)
 *  - Elemenként kiírás magyar megnevezésekkel
 *  - Attribútumok és gyermekelemek megjelenítése blokkszerűen
 *  - Konzolra + TXT fájlba mentés
 * ========================================================================
 */
public class FjyxpcDomRead {

    // ===== Magyarított elemnevek tárolása (pl. "mufaj" -> "Műfaj") =====
    private static final Map<String, String> adatNevek = new HashMap<>();

    // ===== Attribútumnevek magyarítása (sId, feId, stb.) =====
    private static final Map<String, String> kulcsNevek = new HashMap<>();


    // =====================================================================
    // MAPPÁK feltöltése — XML tagek magyar megfelelői
    // =====================================================================
    static {
        // ========= Felhasználók =========
        adatNevek.put("felhasznalo", "Felhasználó");
        adatNevek.put("nev", "Név");
        adatNevek.put("elerhetoseg", "Elérhetőség");
        adatNevek.put("email", "E-mail cím");
        adatNevek.put("telefonszam", "Telefonszám");
        adatNevek.put("jelszo", "Jelszó");
        adatNevek.put("profilkep", "Profilkép");

        // ========= Értékelések =========
        adatNevek.put("ertekeles", "Értékelés");
        adatNevek.put("pontszam", "Pontszám");
        adatNevek.put("ertekeles_szoveg", "Értékelés szövege");

        // ========= Sörök =========
        adatNevek.put("sor", "Sör");
        adatNevek.put("mufaj", "Műfaj");
        adatNevek.put("alkohol", "Alkoholtartalom");

        // ========= Főzések =========
        adatNevek.put("fozes", "Főzés");
        adatNevek.put("fozoMester", "Főzőmester");
        adatNevek.put("datum", "Dátum");

        // ========= Főzdék =========
        adatNevek.put("fozde", "Főzde");
        adatNevek.put("szlogen", "Szlogen");
        adatNevek.put("alapitva", "Alapítva");
        adatNevek.put("cim", "Cím");
        adatNevek.put("varos", "Város");
        adatNevek.put("utca", "Utca");
        adatNevek.put("hazszam", "Házszám");

        // ========= Címkék =========
        adatNevek.put("cimke", "Címke");
        adatNevek.put("kaloria", "Kalória");
        adatNevek.put("osszetevok", "Összetevők");
        adatNevek.put("nettoTerfogat", "Nettó térfogat (L)");
        adatNevek.put("ibu", "IBU érték");

        // ========= Vásárlások =========
        adatNevek.put("vasarlas", "Vásárlás");
        adatNevek.put("ar", "Ár");

        // ========= Forgalmazók =========
        adatNevek.put("forgalmazo", "Forgalmazó");
        adatNevek.put("webhely", "Webhely");
        adatNevek.put("szekhely", "Székhely");
        adatNevek.put("vevoszolgalat", "Vevőszolgálat");

        // ========= Üzletek =========
        adatNevek.put("uzlet", "Üzlet");
        adatNevek.put("nyitvatartas", "Nyitvatartás");
        adatNevek.put("googleErtekeles", "Google értékelés");


        // ========= XML ATTRIBÚTUMOK MAGYARÍTÁSA =========
        kulcsNevek.put("feId", "Felhasználó azonosító");
        kulcsNevek.put("sId", "Sör azonosító");
        kulcsNevek.put("foId", "Forgalmazó azonosító");
        kulcsNevek.put("fozdeId", "Főzde azonosító");
        kulcsNevek.put("uId", "Üzlet azonosító");
        kulcsNevek.put("cId", "Címke azonosító");
    }

    public static void main(String[] args) {

        try {
            // =================================================================
            // 1) XML dokumentum beolvasása
            // =================================================================
            File xmlFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            // =================================================================
            // 2) Kiíró TXT fájl megnyitása
            // =================================================================
            FileWriter writer = new FileWriter("FJYXPC_XMLTask/FJYXPC_XML_output.txt");


            // =================================================================
            // 3) Gyökérelem neve kiírás
            // =================================================================
            String rootName = doc.getDocumentElement().getNodeName();
            printLine("Gyökér elem: " + at(rootName), writer);
            printLine("--------------------------------", writer);


            // =================================================================
            // 4) Gyökér gyermekelemeinek bejárása
            // =================================================================
            NodeList rootChildren = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node node = rootChildren.item(i);

                // Csak ELEMEK (szöveg, whitespace kizárva)
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element elem = (Element) node;

                    // Nyitó blokk
                    printLine("\nElem: " + at(elem.getNodeName()), writer);
                    printLine("--------------------------------", writer);


                    // =================================================================
                    // 4/A) ATTRIBÚTUMOK (pl. sId, feId) magyar névvel
                    // =================================================================
                    NamedNodeMap attributes = elem.getAttributes();

                    if (attributes != null && attributes.getLength() > 0) {

                        for (int a = 0; a < attributes.getLength(); a++) {
                            Node attr = attributes.item(a);

                            // kulcsnév magyarítás
                            String magyarKulcs =
                                kulcsNevek.getOrDefault(attr.getNodeName(), attr.getNodeName());

                            printLine(magyarKulcs + ": " + attr.getNodeValue(), writer);
                        }
                    }


                    // =================================================================
                    // 4/B) Gyermekelemek feldolgozása
                    // =================================================================
                    NodeList subNodes = elem.getChildNodes();

                    for (int j = 0; j < subNodes.getLength(); j++) {
                        Node sub = subNodes.item(j);

                        if (sub.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElem = (Element) sub;

                            // Ha van mélyebb struktúra (pl. <cim> alatt <varos>, <utca>…)
                            if (subElem.hasChildNodes() && hasElementChild(subElem)) {

                                printLine(at(subElem.getNodeName()) + ":", writer);

                                NodeList deepList = subElem.getChildNodes();

                                for (int k = 0; k < deepList.getLength(); k++) {
                                    Node deep = deepList.item(k);

                                    if (deep.getNodeType() == Node.ELEMENT_NODE) {
                                        printLine(
                                            "    " + at(deep.getNodeName()) + ": "
                                            + deep.getTextContent(),
                                            writer
                                        );
                                    }
                                }

                            } else {
                                // sima egy szintű elem: <nev>Golden Ale</nev>
                                printLine(
                                    at(subElem.getNodeName()) + ": "
                                    + subElem.getTextContent(),
                                    writer
                                );
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


    // =========================================================================
    // Segédfüggvény: megállapítja, hogy egy elemnek van-e másik elem gyermeke
    // (tehát összetett-e)
    // =========================================================================
    private static boolean hasElementChild(Element e) {
        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
                return true;
        }
        return false;
    }


    // =========================================================================
    // Elemnév magyarítása (adatNevek → fallback az eredetire)
    // =========================================================================
    private static String at(String name) {
        return adatNevek.getOrDefault(name, name);
    }


    // =========================================================================
    // Kiírás konzolra + fájlba egyszerre
    // =========================================================================
    private static void printLine(String text, FileWriter writer) throws Exception {
        System.out.println(text);
        writer.write(text + System.lineSeparator());
    }
}