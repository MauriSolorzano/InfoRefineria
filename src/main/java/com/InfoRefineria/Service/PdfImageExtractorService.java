package com.InfoRefineria.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfImageExtractorService {

    @Autowired
    private ImagenService imagenService;

    public List<String> extraerImagenesDePdf(MultipartFile pdfFile, String sector, String planta) throws IOException {
        List<String> imagenesGuardadas = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Extraer cada página como imagen
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                // Convertir BufferedImage a MultipartFile
                MultipartFile imageFile = convertirBufferedImageAMultipartFile(
                        image,
                        pdfFile.getOriginalFilename() + "_pagina_" + (page + 1) + ".png"
                );

                // Guardar usando el servicio existente
                String rutaImagen = imagenService.guardarImagenes(imageFile, sector, planta);
                imagenesGuardadas.add(rutaImagen);
            }
        }

        return imagenesGuardadas;
    }

    private MultipartFile convertirBufferedImageAMultipartFile(BufferedImage image, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile(fileName, fileName, "image/png", imageBytes);
    }
}
