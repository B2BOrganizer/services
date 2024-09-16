package pro.b2borganizer.services.documents.control;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.UnableToGeneratePreviewsException;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Component
@Slf4j
public class DocumentPreviewsGenerator {

    public List<ManagedFile> generatePreviews(ManagedFile managedFile) throws UnableToGeneratePreviewsException {
        if (managedFile.isPdf()) {
            byte[] pdfBytes = Base64.decodeBase64(managedFile.getContentInBase64());

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                List<ManagedFile> previews = new ArrayList<>();
                for (int page = 0; page < document.getNumberOfPages(); page++) {
                    BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                    String encodeBase64String = encodeToBase64String(image);

                    ManagedFile managedFilePreview = createManagedFile(encodeBase64String, page, managedFile.getFileName());

                    previews.add(managedFilePreview);
                }

                return previews;
            } catch (Exception e) {
                throw new UnableToGeneratePreviewsException(MessageFormat.format("Unable to generate previews for managed file = {0}.", managedFile.getFileName()), e);
            }
        } else {
            log.warn("Managed file = {} is not a PDF file, mime = {}.", managedFile.getFileName(), managedFile.getMimeType());
            return List.of();
        }
    }

    private static String encodeToBase64String(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArrayOutputStream);

        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }

    private static ManagedFile createManagedFile(String encodeBase64String, int count, String parentFileName) {
        String baseParentFileName = FilenameUtils.getBaseName(parentFileName);
        String previewFileName = baseParentFileName + "_" + count + ".png";

        ManagedFile managedFilePreview = new ManagedFile();
        managedFilePreview.setFileName(previewFileName);
        managedFilePreview.setContentInBase64(encodeBase64String);
        managedFilePreview.setMimeType("image/png");

        return managedFilePreview;
    }
}
