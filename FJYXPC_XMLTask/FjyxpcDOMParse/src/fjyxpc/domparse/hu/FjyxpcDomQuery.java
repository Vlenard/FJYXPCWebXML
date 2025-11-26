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

/**
 * XML lekérdező program DOM parserrel.
 * Feladata:
 *  - beolvassa a söröket, címkéket, forgalmazókat, felhasználókat és értékeléseket
 *  - összekapcsolja az adatokat sör → címke → forgalmazó szerint
 *  - felhasználónként kiírja az értékelt söröket
 */
public class FjyxpcDomQuery {

    /**
     * Adatszerkezet egy felhasználó által értékelt sörről.
     * Minden mező az XML-ből származik, összekapcsolva.
     */
    static class ErtekeltSor {
        String sorId;
        String sorNev;
        String pontszam;
        String cimkeId;
        String cimkeLeiras;
        String forgalmazoNev;

        ErtekeltSor(String sorId, String sorNev, String pontszam) {
            this.sorId = sorId;
            this.sorNev = sorNev;
            this.pontszam = pontszam;
        }
    }

    public static void main(String[] args) {
        try {
            // =====================================================================
            // 1) XML BEOLVASÁSA
            // =====================================================================
            File inputFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // DOM fa felépítése
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();



            // =====================================================================
            // 2) SÖRÖK BEOLVASÁSA (id + név)
            // =====================================================================
            Map<String, String> sorNevek = new HashMap<>();
            // a teljes sor <Element>-et elmentjük későbbi kapcsolásokhoz
            Map<String, Element> sorElements = new HashMap<>();

            NodeList sorLista = doc.getElementsByTagName("sor");
            for (int i = 0; i < sorLista.getLength(); i++) {
                Element s = (Element) sorLista.item(i);

                String sId = s.getAttribute("sId");
                String nev = getTextContentOfChild(s, "nev");

                // ha nincs név -> alapértelmezett
                sorNevek.put(sId, nev != null ? nev : "Ismeretlen sör");
                sorElements.put(sId, s);
            }



            // =====================================================================
            // 3) CÍMKÉK BEOLVASÁSA
            //    (minden fontos információ egy formázott szövegként)
            // =====================================================================
            Map<String, String> cimkeLeirasok = new HashMap<>();
            NodeList cimkeLista = doc.getElementsByTagName("cimke");

            for (int i = 0; i < cimkeLista.getLength(); i++) {
                Element c = (Element) cimkeLista.item(i);

                String cId = c.getAttribute("cId");
                String kaloria = getTextContentOfChild(c, "kaloria");
                String osszetevok = getTextContentOfChild(c, "osszetevok");
                String nettoT = getTextContentOfChild(c, "nettoTerfogat");
                String ibu = getTextContentOfChild(c, "ibu");

                // címke összefűzött leírás
                String desc = "";
                if (kaloria != null) desc += "Kalória: " + kaloria;
                if (osszetevok != null) desc += (desc.isEmpty() ? "" : ", ") + "Összetevők: " + osszetevok;
                if (nettoT != null) desc += (desc.isEmpty() ? "" : ", ") + "Netto: " + nettoT;
                if (ibu != null) desc += (desc.isEmpty() ? "" : ", ") + "IBU: " + ibu;

                if (desc.isEmpty())
                    desc = "Nincs címke-információ";

                cimkeLeirasok.put(cId, desc);
            }



            // =====================================================================
            // 4) FORGALMAZÓK BEOLVASÁSA
            // =====================================================================
            Map<String, String> forgalmazok = new HashMap<>();
            NodeList forgalmazoLista = doc.getElementsByTagName("forgalmazo");

            for (int i = 0; i < forgalmazoLista.getLength(); i++) {
                Element f = (Element) forgalmazoLista.item(i);

                String foId = f.getAttribute("foId");
                String nev = getTextContentOfChild(f, "nev");

                forgalmazok.put(foId, nev != null ? nev : "Ismeretlen forgalmazó");
            }



            // =====================================================================
            // 5) FELHASZNÁLÓK BEOLVASÁSA
            // =====================================================================
            Map<String, String> felhasznalok = new HashMap<>();
            NodeList felhLista = doc.getElementsByTagName("felhasznalo");

            for (int i = 0; i < felhLista.getLength(); i++) {
                Element fe = (Element) felhLista.item(i);

                String feId = fe.getAttribute("feId");
                String nev = getTextContentOfChild(fe, "nev");

                felhasznalok.put(feId, nev != null ? nev : "Ismeretlen felhasználó");
            }



            // =====================================================================
            // 6) ÉRTÉKELÉSEK ÖSSZEKAPCSOLÁSA
            //     Felhasználó → Lista:Sörök
            // =====================================================================
            Map<String, List<ErtekeltSor>> ertekelesek = new HashMap<>();
            NodeList ertekLista = doc.getElementsByTagName("ertekeles");

            for (int i = 0; i < ertekLista.getLength(); i++) {
                Element e = (Element) ertekLista.item(i);

                String feId = e.getAttribute("feId");
                String sId  = e.getAttribute("sId");
                String pontszam = getTextContentOfChild(e, "pontszam");

                // Üres vagy hibás felhasználó → átugrás
                if (feId == null || feId.trim().isEmpty() || !felhasznalok.containsKey(feId))
                    continue;

                // Új objektum az értékelt sörről
                String sorNev = sorNevek.getOrDefault(sId, "Ismeretlen sör");
                ErtekeltSor sorObj = new ErtekeltSor(
                        sId,
                        sorNev,
                        pontszam != null ? pontszam : "0"
                );

                // ---------------------------------------------------
                // kapcsolt adatok (címke + forgalmazó)
                // ---------------------------------------------------
                Element sorElem = sorElements.get(sId);

                if (sorElem != null) {

                    // Sör forgalmazója
                    String foId = sorElem.getAttribute("foId");
                    sorObj.forgalmazoNev =
                        forgalmazok.getOrDefault(foId, "Nincs forgalmazó adat");

                    // Címke — ha nincs attribútum, fallback
                    String cId = sorElem.getAttribute("cId");
                    if (cId == null || cId.isEmpty())
                        cId = sId;

                    sorObj.cimkeId = cId;
                    sorObj.cimkeLeiras =
                        cimkeLeirasok.getOrDefault(cId, "Nincs címke adat (" + cId + ")");

                } else {
                    // sör elem hiányzik → védőértékek
                    sorObj.forgalmazoNev = "Nincs sör elem";
                    sorObj.cimkeLeiras = "Nincs sör elem";
                }

                // =====================================================
                // felhasználóhoz hozzáfűzés
                // =====================================================
                List<ErtekeltSor> lista = ertekelesek.getOrDefault(feId, new ArrayList<>());
                lista.add(sorObj);

                ertekelesek.put(feId, lista);
            }



            // =====================================================================
            // 7) KONZOL KIÍRATÁS
            // =====================================================================
            for (String feId : ertekelesek.keySet()) {

                String felhaszNev = felhasznalok.get(feId);
                List<ErtekeltSor> lista = ertekelesek.get(feId);

                System.out.println("--------------------------------");
                System.out.println("Felhasználó: " + felhaszNev);
                System.out.println("Felhasználó azonosító (feId): " + feId);

                if (lista.isEmpty()) {
                    System.out.println("Nincs értékelt sör.");
                    continue;
                }

                System.out.println("Értékelt sörök:");

                for (ErtekeltSor s : lista) {
                    System.out.println("\n\tSör neve: " + s.sorNev);
                    System.out.println("\tSör azonosító (sId): " + s.sorId);
                    System.out.println("\tPontszám: " + s.pontszam);
                    System.out.println("\tForgalmazó: " + s.forgalmazoNev);
                    System.out.println("\tCímke (cId): " + s.cimkeId);
                    System.out.println("\tCímke leírás: " + s.cimkeLeiras);
                }

                System.out.println("--------------------------------\n");
            }

        } catch (Exception ex) {
            System.out.println("Hiba történt XML beolvasás/elemzés során:");
            ex.printStackTrace();
        }
    }

    /**
     * Segédfüggvény:
     * Visszaadja egy gyermek elem szövegét, ha létezik.
     * Ha nincs → null
     */
    private static String getTextContentOfChild(Element parent, String childTag) {
        NodeList nl = parent.getElementsByTagName(childTag);
        if (nl.getLength() == 0) return null;
        Element c = (Element) nl.item(0);
        if (c == null) return null;

        String txt = c.getTextContent();
        return txt != null ? txt.trim() : null;
    }
}
