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

    // Esta sigue siendo la URL interna para que el backend suba archivos
    private final String minioUrlInterna;

    // NUEVA: Esta es la que usaremos para construir el link del navegador
    @Value("${minio.url-publica}")
    private String minioUrlPublica;

    public MinioStorageService(@Value("${minio.url}") String url,
                               @Value("${minio.access-key}") String accessKey,
                               @Value("${minio.secret-key}") String secretKey) {
        this.minioUrlInterna = url; // Guardamos la URL de conexión
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

        // CAMBIO AQUÍ: Usamos minioUrlPublica para el link que va a la base de datos
        return minioUrlPublica + "/" + bucket + "/" + objectName;
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
