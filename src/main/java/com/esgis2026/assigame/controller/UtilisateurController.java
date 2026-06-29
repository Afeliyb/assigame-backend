package com.esgis2026.assigame.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.service.UtilisateurService;

@RestController
@RequestMapping("/api/utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/list")
    public List<Utilisateur> getAllUtilisateur() {
        return utilisateurService.getAllUtilisateur();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    // Inscription publique (page "Connexion / Inscription" du frontend)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur utilisateur) {
        try {
            return ResponseEntity.ok(utilisateurService.register(utilisateur));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // Conservé pour la création depuis un éventuel back-office ; fait la même chose que /register.
    @PostMapping("/add")
    public ResponseEntity<?> addUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            return ResponseEntity.ok(utilisateurService.createUtilisateur(utilisateur));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUtilisateur(
            @PathVariable Long id,
            @RequestBody Utilisateur utilisateur) {
        try {
            return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, utilisateur));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String motdepasse = credentials.get("motdepasse");

        return utilisateurService.login(email, motdepasse)
                .map(utilisateur -> ResponseEntity.ok((Object) utilisateur))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Email ou mot de passe incorrect.")));
    }

    // IMPORTANT - Exposé séparément du profil public pour protéger les données de contact.
    // Les coordonnées ne transitent jamais dans la réponse de /api/produit/* ni dans
    // celle de /api/utilisateur/{id} afin de ne pas apparaître dans les sources HTML initiales.
    // Désormais accessible publiquement au clic (sans connexion obligatoire).
    @GetMapping("/{id}/contact")
public ResponseEntity<?> getContactInfo(@PathVariable Long id) {
    // Suppression du bloc de vérification 'if (requesterId == null ...)'
    // Ainsi, tout le monde peut accéder au numéro
    return utilisateurService.getContactInfo(id)
            .map(info -> ResponseEntity.ok((Object) info))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Vendeur introuvable.")));
}
}