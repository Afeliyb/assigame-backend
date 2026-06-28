package com.esgis2026.assigame.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Permet au frontend d'envoyer une image (photo de produit ou avatar) et de récupérer une URL
 * publique utilisable ensuite dans Produit.images ou Utilisateur.avatar.
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> EXTENSIONS_AUTORISEES = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    @PostMapping
    public ResponseEntity<?> uploadFichier(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aucun fichier reçu."));
        }

        String nomOriginal = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
        String extension = "";
        int pointIndex = nomOriginal.lastIndexOf('.');
        if (pointIndex >= 0) {
            extension = nomOriginal.substring(pointIndex + 1).toLowerCase();
        }

        if (!EXTENSIONS_AUTORISEES.contains(extension)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("message", "Format d'image non supporté. Utilisez jpg, png, webp ou gif."));
        }

        try {
            Path dossier = Paths.get(uploadDir);
            if (!Files.exists(dossier)) {
                Files.createDirectories(dossier);
            }

            String nomFichier = UUID.randomUUID().toString() + "." + extension;
            Path destination = dossier.resolve(nomFichier);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            String url = baseUrl + "/uploads/" + nomFichier;
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'enregistrement du fichier : " + e.getMessage()));
        }
    }
}
