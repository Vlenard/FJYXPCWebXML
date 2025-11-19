package fjyxpcjson;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONReadFJYXPC {
    public static void main(String[] args) {
        try {
            InputStream is = JSONReadFJYXPC.class.getResourceAsStream("/orarendFJYXPC.json");
            InputStreamReader isr = new InputStreamReader(is);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject)parser.parse(isr);

            JSONObject root = (JSONObject) jsonObject.get("FJYXPC_orarend");
            JSONArray lessons = (JSONArray) root.get("ora");

            String buffer = "";
            for (int i = 0; i < lessons.size(); i++) {
                JSONObject lesson = (JSONObject) lessons.get(i);

                buffer += "-------------------------\n";

                buffer += "id: " + lesson.get("id") + "\n";
                buffer += "Típus: " + lesson.get("tipus") + "\n";
                buffer += "Tárgy: " + lesson.get("targy") + "\n";
                buffer += "helyszyn: " + lesson.get("helyszin") + "\n";
                buffer += "oktató: " + lesson.get("oktato") + "\n";
                buffer += "szak: " + lesson.get("szak") + "\n";


                JSONObject date = (JSONObject) lesson.get("idopont");
                buffer += "Időpont:\n";
                buffer += "\tNap: " + date.get("nap") + "\n";
                buffer += "\ttol: " + date.get("tol") + "\n";
                buffer += "\tig: " + date.get("ig") + "\n";

                buffer += "------------------------\n";
            }

            System.out.println(buffer);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}