import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class Main {
    public static void main(String[] args) {
        try{
            Translate.setClientId("Saurabh_Kumar");
            Translate.setClientSecret("7KsEhxcnq2BPiUIb1cFgiy9zgMEfV1qgTO5B+bi+SZE=");
            long t1= System.currentTimeMillis();
            String [] trans={"Bonjour le monde","Bonjour le monde","Bonjour le monde"};
            String[] translatedText = Translate.execute(trans, Language.FRENCH, Language.ENGLISH);
            System.out.println(translatedText[0] + translatedText[1] + translatedText[2]);
            //System.out.println(translatedText);
            long t2=System.currentTimeMillis();
            System.out.println(t2-t1);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
} 
