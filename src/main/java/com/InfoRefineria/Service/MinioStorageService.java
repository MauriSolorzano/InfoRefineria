package com.InfoRefineria.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    public MinioStorageService(@Value("${minio.url}") String url,
                               @Value("${minio.access-key}") String accessKey,
                               @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String subirArchivo(MultipartFile archivo, String planta, String sector) throws Exception {
        String plantaLimpia  = planta.toUpperCase().trim().replace(" ", "_");
        String sectorLimpio  = sector.toUpperCase().trim().replace(" ", "_");
        String uuid          = UUID.randomUUID().toString();
        String nombreArchivo = uuid + "_" + archivo.getOriginalFilename();
        String objectName    = plantaLimpia + "/" + sectorLimpio + "/" + nombreArchivo;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(archivo.getInputStream(), archivo.getSize(), -1)
                        .contentType(archivo.getContentType())
                        .build()
        );

        // URL pública directa
        return minioUrl + "/" + bucket + "/" + objectName;
    }

    public void eliminarArchivo(String storagePath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(storagePath)
                            .build()
            );
        } catch (Exception e) {
            System.err.println("Error al eliminar archivo de MinIO: " + e.getMessage());
        }
    }
}
