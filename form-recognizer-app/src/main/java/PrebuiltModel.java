
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzedDocument;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentField;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentFieldType;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentOperationResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PrebuiltModel {

    public static void Analysis(String endpoint, String sub_key) throws IOException {

        // create your `DocumentAnalysisClient` instance and `AzureKeyCredential`
        // variable
        DocumentAnalysisClient client = new DocumentAnalysisClientBuilder()
                .credential(new AzureKeyCredential(sub_key))
                .endpoint(endpoint)
                .buildClient();

        // sample document
        String invoiceUrl = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-REST-api-samples/master/curl/form-recognizer/sample-invoice.pdf";
        String modelId = "prebuilt-invoice";

        SyncPoller<DocumentOperationResult, AnalyzeResult> analyzeInvoicePoller = client
                .beginAnalyzeDocumentFromUrl(modelId, invoiceUrl);

        AnalyzeResult analyzeInvoiceResult = analyzeInvoicePoller.getFinalResult();

        for (int i = 0; i < analyzeInvoiceResult.getDocuments().size(); i++) {
            AnalyzedDocument analyzedInvoice = analyzeInvoiceResult.getDocuments().get(i);
            Map<String, DocumentField> invoiceFields = analyzedInvoice.getFields();
            System.out.printf("----------- Analyzing invoice  %d -----------%n", i);
            System.out.printf("Analyzed document has doc type %s with confidence : %.2f%n",
                    analyzedInvoice.getDocType(), analyzedInvoice.getConfidence());

            DocumentField vendorNameField = invoiceFields.get("VendorName");
            if (vendorNameField != null) {
                if (DocumentFieldType.STRING == vendorNameField.getType()) {
                    String merchantName = vendorNameField.getValueAsString();
                    // Float confidence = vendorNameField.getConfidence();
                    System.out.printf("Vendor Name: %s, confidence: %.2f%n",
                            merchantName, vendorNameField.getConfidence());
                }
            }

            DocumentField vendorAddressField = invoiceFields.get("VendorAddress");
            if (vendorAddressField != null) {
                if (DocumentFieldType.STRING == vendorAddressField.getType()) {
                    String merchantAddress = vendorAddressField.getValueAsString();
                    System.out.printf("Vendor address: %s, confidence: %.2f%n",
                            merchantAddress, vendorAddressField.getConfidence());
                }
            }

            DocumentField customerNameField = invoiceFields.get("CustomerName");
            if (customerNameField != null) {
                if (DocumentFieldType.STRING == customerNameField.getType()) {
                    String merchantAddress = customerNameField.getValueAsString();
                    System.out.printf("Customer Name: %s, confidence: %.2f%n",
                            merchantAddress, customerNameField.getConfidence());
                }
            }

            DocumentField customerAddressRecipientField = invoiceFields.get("CustomerAddressRecipient");
            if (customerAddressRecipientField != null) {
                if (DocumentFieldType.STRING == customerAddressRecipientField.getType()) {
                    String customerAddr = customerAddressRecipientField.getValueAsString();
                    System.out.printf("Customer Address Recipient: %s, confidence: %.2f%n",
                            customerAddr, customerAddressRecipientField.getConfidence());
                }
            }

            DocumentField invoiceIdField = invoiceFields.get("InvoiceId");
            if (invoiceIdField != null) {
                if (DocumentFieldType.STRING == invoiceIdField.getType()) {
                    String invoiceId = invoiceIdField.getValueAsString();
                    System.out.printf("Invoice ID: %s, confidence: %.2f%n",
                            invoiceId, invoiceIdField.getConfidence());
                }
            }

            DocumentField invoiceDateField = invoiceFields.get("InvoiceDate");
            if (customerNameField != null) {
                if (DocumentFieldType.DATE == invoiceDateField.getType()) {
                    LocalDate invoiceDate = invoiceDateField.getValueAsDate();
                    System.out.printf("Invoice Date: %s, confidence: %.2f%n",
                            invoiceDate, invoiceDateField.getConfidence());
                }
            }

            DocumentField invoiceTotalField = invoiceFields.get("InvoiceTotal");
            if (customerAddressRecipientField != null) {
                if (DocumentFieldType.FLOAT == invoiceTotalField.getType()) {
                    Float invoiceTotal = invoiceTotalField.getValueAsFloat();
                    System.out.printf("Invoice Total: %.2f, confidence: %.2f%n",
                            invoiceTotal, invoiceTotalField.getConfidence());
                }
            }

            DocumentField invoiceItemsField = invoiceFields.get("Items");
            if (invoiceItemsField != null) {
                System.out.printf("Invoice Items: %n");
                if (DocumentFieldType.LIST == invoiceItemsField.getType()) {
                    List<DocumentField> invoiceItems = invoiceItemsField.getValueAsList();
                    invoiceItems.stream()
                        .filter(invoiceItem -> DocumentFieldType.MAP == invoiceItem.getType())
                        .map(formField -> formField.getValueAsMap())
                        .forEach(formFieldMap -> formFieldMap.forEach((key, formField) -> {
                            // See a full list of fields found on an invoice here:
                            // https://aka.ms/formrecognizer/invoicefields
                            if ("Description".equals(key)) {
                                if (DocumentFieldType.STRING == formField.getType()) {
                                    String name = formField.getValueAsString();
                                    System.out.printf("Description: %s, confidence: %.2fs%n",
                                            name, formField.getConfidence());
                                }
                            }
                            if ("Quantity".equals(key)) {
                                if (DocumentFieldType.FLOAT == formField.getType()) {
                                    Float quantity = formField.getValueAsFloat();
                                    System.out.printf("Quantity: %f, confidence: %.2f%n",
                                            quantity, formField.getConfidence());
                                }
                            }
                            if ("UnitPrice".equals(key)) {
                                if (DocumentFieldType.FLOAT == formField.getType()) {
                                    Float unitPrice = formField.getValueAsFloat();
                                    System.out.printf("Unit Price: %f, confidence: %.2f%n",
                                            unitPrice, formField.getConfidence());
                                }
                            }
                            if ("ProductCode".equals(key)) {
                                if (DocumentFieldType.FLOAT == formField.getType()) {
                                    Float productCode = formField.getValueAsFloat();
                                    System.out.printf("Product Code: %f, confidence: %.2f%n",
                                            productCode, formField.getConfidence());
                                }
                            }
                        }));
                }
            }
        }
    }
}
