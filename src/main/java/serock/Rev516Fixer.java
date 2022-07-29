package serock;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class Rev516Fixer implements Runnable {

    private final File inPdf;

    public Rev516Fixer(final String pdfFileName) {
        this.inPdf = new File(pdfFileName);
    }

    public static void main(final String[] args) {
        final Rev516Fixer app = new Rev516Fixer(args[0]);
        app.run();
    }

    @Override
    public void run() {
        try (final PDDocument doc = PDDocument.load(getInFile());) {
            final PDTextField textField = getTextField(doc, "ACCOUNT NUMBER");
            if (textField != null) {
                if (isCapacityIncreased(textField, 20)) {
                    final File outFile = createOutFile();
                    doc.save(outFile);
                    System.out.println("Modified file was saved to " + outFile.toString());
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private File getInFile() {
        return this.inPdf;
    }

    private static boolean isCapacityIncreased(final PDTextField textField, final int desiredMaxLen) {
        final int currentMaxLen = textField.getMaxLen();
        if (currentMaxLen < desiredMaxLen) {
            textField.setMaxLen(desiredMaxLen);
            return true;
        }
        return false;
    }

    private static PDTextField getTextField(final PDDocument doc, final String name) {
        return (PDTextField) doc.getDocumentCatalog().getAcroForm().getField(name);
    }

    private File createOutFile() {
        return new File(getInFile().getParentFile(), getOutFileName());
    }

    private String getOutFileName() {
        final StringBuilder sb = new StringBuilder(getInFile().getName());
        final int offset = sb.lastIndexOf(".pdf");
        sb.insert(offset, "-modified");
        return sb.toString();
    }
}
