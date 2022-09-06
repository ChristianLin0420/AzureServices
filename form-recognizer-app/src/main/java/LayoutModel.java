
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTable;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentOperationResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;

import java.util.List;

public class LayoutModel {

    public static void Analysis(String endpoint, String sub_key) {

        // create your `DocumentAnalysisClient` instance and `AzureKeyCredential`
        // variable
        DocumentAnalysisClient client = new DocumentAnalysisClientBuilder()
                .credential(new AzureKeyCredential(sub_key))
                .endpoint(endpoint)
                .buildClient();

        // sample document
        String documentUrl = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-REST-api-samples/master/curl/form-recognizer/sample-layout.pdf";
        String modelId = "prebuilt-layout";

        SyncPoller<DocumentOperationResult, AnalyzeResult> analyzeLayoutResultPoller = client
                .beginAnalyzeDocumentFromUrl(modelId, documentUrl);

        AnalyzeResult analyzeLayoutResult = analyzeLayoutResultPoller.getFinalResult();

        // pages
        analyzeLayoutResult.getPages().forEach(documentPage -> {
            System.out.printf("Page has width: %.2f and height: %.2f, measured with unit: %s%n",
                    documentPage.getWidth(),
                    documentPage.getHeight(),
                    documentPage.getUnit());

            // lines
            documentPage.getLines()
                    .forEach(documentLine -> System.out.printf("Line %s is within a bounding polygon %s.%n",
                            documentLine.getContent(),
                            documentLine.getBoundingPolygon().toString()));

            // words
            documentPage.getWords()
                    .forEach(documentWord -> System.out.printf("Word '%s' has a confidence score of %.2f%n",
                            documentWord.getContent(),
                            documentWord.getConfidence()));

            // selection marks
            documentPage.getSelectionMarks()
                    .forEach(documentSelectionMark -> System.out.printf(
                            "Selection mark is %s and is within a bounding polygon %s with confidence %.2f.%n",
                            documentSelectionMark.getSpan().toString(),
                            documentSelectionMark.getBoundingPolygon().toString(),
                            documentSelectionMark.getConfidence()));
        });

        // tables
        List<DocumentTable> tables = analyzeLayoutResult.getTables();
        for (int i = 0; i < tables.size(); i++) {
            DocumentTable documentTable = tables.get(i);
            System.out.printf("Table %d has %d rows and %d columns.%n", i, documentTable.getRowCount(),
                    documentTable.getColumnCount());
            documentTable.getCells().forEach(documentTableCell -> {
                System.out.printf("Cell '%s', has row index %d and column index %d.%n", documentTableCell.getContent(),
                        documentTableCell.getRowIndex(), documentTableCell.getColumnIndex());
            });
            System.out.println();
        }
    }
}
