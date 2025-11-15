package fjyxpc.domparse.hu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FjyxpcDomQuery {
    // Belső osztály az értékelések tárolásához
    static class ErtekeltSor {
        String sorId;
        String sorNev;
        String pontszam;

        ErtekeltSor(String sorId, String sorNev, String pontszam) {
            this.sorId = sorId;
            this.sorNev = sorNev;
            this.pontszam = pontszam;
        }
    }

    public static void main(String[] args) {
        try {
            // XML beolvasása DOM-mel
            File xmlFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // ===== Felhasználók és sörök beolvasása =====
            Map<String, String> felhasznalok = new HashMap<>();
            Map<String, String> sorok = new HashMap<>();

            NodeList felhasznaloLista = doc.getElementsByTagName("felhasznalo");
            for (int i = 0; i < felhasznaloLista.getLength(); i++) {
                Element f = (Element) felhasznaloLista.item(i);
                String feId = f.getAttribute("feId");
                String nev = f.getElementsByTagName("nev").item(0).getTextContent();
                felhasznalok.put(feId, nev);
            }

            NodeList sorLista = doc.getElementsByTagName("sor");
            for (int i = 0; i < sorLista.getLength(); i++) {
                Element s = (Element) sorLista.item(i);
                String sId = s.getAttribute("sId");
                String nev = s.getElementsByTagName("nev").item(0).getTextContent();
                sorok.put(sId, nev);
            }

            // ===== Értékelések felhasználónkénti csoportosítása =====
            Map<String, List<ErtekeltSor>> ertekelesek = new HashMap<>();

            NodeList ertekelesLista = doc.getElementsByTagName("ertekeles");
            for (int i = 0; i < ertekelesLista.getLength(); i++) {
                Element e = (Element) ertekelesLista.item(i);

                String feId = e.getAttribute("feId");
                String sId = e.getAttribute("sId");

                if (!feId.isEmpty()) {
                    String pontszam = e.getElementsByTagName("pontszam").item(0).getTextContent();

                    // Sör neve lekérése
                    String sorNev = sorok.getOrDefault(sId, "Ismeretlen sör");

                    // Értékelés objektum létrehozása
                    ErtekeltSor es = new ErtekeltSor(sId, sorNev, pontszam);

                    // Hozzáadás a megfelelő felhasználó listájához
                    ertekelesek.computeIfAbsent(feId, k -> new ArrayList<>()).add(es);
                }
            }

            // ===== Kiírás strukturált, blokkszerű formában =====
            System.out.println("====== FELHASZNÁLÓK ÁLTAL ÉRTÉKELT SÖRÖK ======\n");

            for (String feId : felhasznalok.keySet()) {

                String felhNev = felhasznalok.get(feId);
                List<ErtekeltSor> lista = ertekelesek.getOrDefault(feId, new ArrayList<>());

                System.out.println("--------------------------------");
                System.out.println("Felhasználó: " + felhNev);
                System.out.println("Felhasználó azonosító (feId): " + feId);

                if (lista.isEmpty()) {
                    System.out.println("Nincs értékelt sör.");
                    System.out.println("--------------------------------\n");
                    continue;
                }

                System.out.print("Értékelt sörök:");
                for (ErtekeltSor s : lista) {
                    System.out.println("\n\tSör neve: " + s.sorNev);
                    System.out.println("\tSör azonosító (sId): " + s.sorId);
                    System.out.println("\tPontszám: " + s.pontszam);
                }
                System.out.println("--------------------------------\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
