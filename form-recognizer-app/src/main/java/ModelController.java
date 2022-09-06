
import java.io.IOException;

public class ModelController {
    public static void main(String[] args) throws IOException {
        
        String endpoint = "https://form-recognizer-westus.cognitiveservices.azure.com/";
        String sub_key = "ac9766c9be2545628287bfb92a96df21";

        if (args[0].equals("GD")) {
            System.out.println("Start analysising the file using general document model!!!");
            GeneralDocumentModel.Analysis(endpoint, sub_key);
        } else if (args[0].equals("L")) {
            System.out.println("Start analysising the file using layer model!!!");
            LayoutModel.Analysis(endpoint, sub_key);
        } else if (args[0].equals("P")) {
            System.out.println("Start analysising the file using prebuilt model!!!");
            PrebuiltModel.Analysis(endpoint, sub_key);
        } else {
            System.out.println("This model has not been implemented!!!");
        }
    }
}