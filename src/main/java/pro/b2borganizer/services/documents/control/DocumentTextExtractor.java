package pro.b2borganizer.services.documents.control;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentTextExtractor {

    /**
     * Extracts text content from a ManagedFile.
     * Supports PDF and plain text files.
     *
     * @param managedFile the file to extract text from
     * @return extracted text content, or empty string if extraction fails
     */
    public String extractText(ManagedFile managedFile) {
        if (managedFile == null || managedFile.getContentInBase64() == null) {
            log.warn("ManagedFile or its content is null");
            return "";
        }

        String extension = FilenameUtils.getExtension(managedFile.getFileName()).toLowerCase();

        try {
            byte[] fileContent = Base64.getDecoder().decode(managedFile.getContentInBase64());

            return switch (extension) {
                case "pdf" -> extractFromPdf(fileContent);
                case "txt" -> extractFromText(fileContent);
                default -> {
                    log.info("Unsupported file type for text extraction: {}", extension);
                    yield "";
                }
            };
        } catch (Exception e) {
            log.error("Failed to extract text from file: {}", managedFile.getFileName(), e);
            return "";
        }
    }

    private String extractFromPdf(byte[] content) throws IOException {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return cleanText(text);
        }
    }

    private String extractFromText(byte[] content) {
        String text = new String(content, StandardCharsets.UTF_8);
        return cleanText(text);
    }

    /**
     * Cleans and normalizes extracted text.
     * Removes excessive whitespace, newlines, and special characters.
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replaceAll("\\s+", " ")  // Replace multiple whitespaces with single space
                .replaceAll("[\\r\\n]+", " ")  // Replace newlines with space
                .trim();
    }
}
