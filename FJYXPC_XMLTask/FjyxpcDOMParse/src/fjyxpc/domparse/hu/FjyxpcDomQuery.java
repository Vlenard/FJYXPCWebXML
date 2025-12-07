package fjyxpc.domparse.hu;

import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * XML lekérdező program DOM parserrel.
 * Javított verzió: támogatja a többször előforduló tageket is!
 */
public class FjyxpcDomQuery {

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

            // =========================================================================
            // 1) XML DOKUMENTUM BEOLVASÁSA
            // =========================================================================
            File inputFile = new File("FJYXPC_XMLTask/FJYXPC_XML.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // =========================================================================
            // 2) SÖRÖK BEOLVASÁSA
            // =========================================================================
            Map<String, String> sorNevek = new HashMap<>();
            Map<String, Element> sorElements = new HashMap<>();

            NodeList sorLista = doc.getElementsByTagName("sor");

            for (int i = 0; i < sorLista.getLength(); i++) {
                Element s = (Element) sorLista.item(i);

                String sId = s.getAttribute("sId");
                String nev = getSingleChildText(s, "nev");

                sorNevek.put(sId, nev != null ? nev : "Ismeretlen sör");
                sorElements.put(sId, s);
            }

            // =========================================================================
            // 3) CÍMKÉK BEOLVASÁSA — TÖBBSZÖR ELŐFORDULÓ ELEMEK TÁMOGATÁSA
            // =========================================================================
            Map<String, String> cimkeLeirasok = new HashMap<>();
            NodeList cimkeLista = doc.getElementsByTagName("cimke");

            for (int i = 0; i < cimkeLista.getLength(); i++) {
                Element c = (Element) cimkeLista.item(i);

                String cId = c.getAttribute("cId");

                String kaloria = getSingleChildText(c, "kaloria");

                List<String> osszesOsszetevo = getAllChildTexts(c, "osszetevok");

                String nettoT = getSingleChildText(c, "nettoTerfogat");
                String ibu = getSingleChildText(c, "ibu");

                StringBuilder desc = new StringBuilder();

                if (kaloria != null)
                    desc.append("Kalória: ").append(kaloria);

                if (!osszesOsszetevo.isEmpty()) {
                    if (desc.length() > 0)
                        desc.append(", ");
                    desc.append("Összetevők: ");
                    desc.append(String.join(", ", osszesOsszetevo));
                }

                if (nettoT != null) {
                    if (desc.length() > 0)
                        desc.append(", ");
                    desc.append("Netto: ").append(nettoT);
                }

                if (ibu != null) {
                    if (desc.length() > 0)
                        desc.append(", ");
                    desc.append("IBU: ").append(ibu);
                }

                if (desc.length() == 0)
                    desc.append("Nincs címke-információ");

                cimkeLeirasok.put(cId, desc.toString());
            }

            // =========================================================================
            // 4) FORGALMAZÓK
            // =========================================================================
            Map<String, String> forgalmazok = new HashMap<>();
            NodeList forgalmazoLista = doc.getElementsByTagName("forgalmazo");

            for (int i = 0; i < forgalmazoLista.getLength(); i++) {
                Element f = (Element) forgalmazoLista.item(i);

                String foId = f.getAttribute("foId");
                String nev = getSingleChildText(f, "nev");

                forgalmazok.put(foId, nev != null ? nev : "Ismeretlen forgalmazó");
            }

            // =========================================================================
            // 5) FELHASZNÁLÓK
            // =========================================================================
            Map<String, String> felhasznalok = new HashMap<>();
            NodeList felhLista = doc.getElementsByTagName("felhasznalo");

            for (int i = 0; i < felhLista.getLength(); i++) {
                Element fe = (Element) felhLista.item(i);

                String feId = fe.getAttribute("feId");
                String nev = getSingleChildText(fe, "nev");

                felhasznalok.put(feId, nev != null ? nev : "Ismeretlen felhasználó");
            }

            // =========================================================================
            // 6) ÉRTÉKELÉSEK - ÖSSZEKAPCSOLÁS
            // =========================================================================
            Map<String, List<ErtekeltSor>> ertekelesek = new HashMap<>();
            NodeList ertekLista = doc.getElementsByTagName("ertekeles");

            for (int i = 0; i < ertekLista.getLength(); i++) {
                Element e = (Element) ertekLista.item(i);

                String feId = e.getAttribute("feId");
                String sId = e.getAttribute("sId");

                String pontszam = getSingleChildText(e, "pontszam");

                if (!felhasznalok.containsKey(feId))
                    continue;

                ErtekeltSor es = new ErtekeltSor(
                        sId,
                        sorNevek.getOrDefault(sId, "Ismeretlen sör"),
                        pontszam != null ? pontszam : "0");

                Element sorElem = sorElements.get(sId);

                if (sorElem != null) {

                    String foId = sorElem.getAttribute("foId");
                    es.forgalmazoNev = forgalmazok.getOrDefault(foId, "Nincs forgalmazó adat");

                    String cId = sorElem.getAttribute("cId");
                    if (cId == null || cId.isEmpty())
                        cId = sId;

                    es.cimkeId = cId;
                    es.cimkeLeiras = cimkeLeirasok.getOrDefault(cId, "Nincs címke adat");

                } else {
                    es.forgalmazoNev = "Nincs sör elem";
                    es.cimkeLeiras = "Nincs sör elem";
                }

                ertekelesek.computeIfAbsent(feId, k -> new ArrayList<>()).add(es);
            }

            // =========================================================================
            // 7) KIÍRATÁS
            // =========================================================================
            for (String feId : ertekelesek.keySet()) {

                System.out.println("--------------------------------");
                System.out.println("Felhasználó: " + felhasznalok.get(feId));
                System.out.println("Felhasználó azonosító (feId): " + feId);

                List<ErtekeltSor> lista = ertekelesek.get(feId);

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
            ex.printStackTrace();
        }
    }

    // =====================================================================
    // SEGÉDFÜGGVÉNYEK — javított verziók
    // =====================================================================

    /** Egyetlen gyermekelem szövegét adja vissza */
    private static String getSingleChildText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0)
            return null;

        String txt = nl.item(0).getTextContent();
        return txt != null ? txt.trim() : null;
    }

    /** Összegyűjt MINDEN előforduló egyforma taget (pl. több <osszetevok>) */
    private static List<String> getAllChildTexts(Element parent, String tag) {
        List<String> result = new ArrayList<>();

        NodeList nl = parent.getElementsByTagName(tag);
        for (int i = 0; i < nl.getLength(); i++) {
            String txt = nl.item(i).getTextContent();
            if (txt != null && !txt.trim().isEmpty()) {
                result.add(txt.trim());
            }
        }

        return result;
    }
}
