package com.esgis2026.assigame.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.service.FavoriProduitService;

/**
 * Routes du système de favoris ("likes").
 *
 * GET  /api/favori/list?userId={id}        → liste des produits favoris de l'utilisateur
 * POST /api/favori/toggle/{idProduit}?userId={id} → like/unlike, retourne { liked, idProduit }
 * GET  /api/favori/check/{idProduit}?userId={id}  → { liked } — état initial du bouton
 */
@RestController
@RequestMapping("/api/favori")
public class FavoriProduitController {

    private final FavoriProduitService favoriService;

    public FavoriProduitController(FavoriProduitService favoriService) {
        this.favoriService = favoriService;
    }

    /**
     * Retourne la liste complète des produits favoris d'un utilisateur.
     * Utilisé pour la page "Mes favoris" du tableau de bord.
     */
    @GetMapping("/list")
    public ResponseEntity<List<Produit>> getFavoris(@RequestParam Long userId) {
        return ResponseEntity.ok(favoriService.getFavorisProduits(userId));
    }

    /**
     * Vérifie si un produit est dans les favoris de l'utilisateur.
     * Le frontend l'appelle à l'ouverture de la fiche produit pour initialiser l'état du bouton.
     */
    @GetMapping("/check/{idProduit}")
    public ResponseEntity<Map<String, Object>> checkFavori(
            @PathVariable Long idProduit,
            @RequestParam Long userId) {
        boolean liked = favoriService.isFavori(userId, idProduit);
        return ResponseEntity.ok(Map.of("liked", liked, "idProduit", idProduit));
    }

    /**
     * Toggle like/unlike d'un produit pour un utilisateur.
     * Retourne { liked: true/false } pour que le frontend mette à jour l'UI sans second appel.
     */
    @PostMapping("/toggle/{idProduit}")
    public ResponseEntity<?> toggleFavori(
            @PathVariable Long idProduit,
            @RequestParam Long userId) {
        try {
            Map<String, Object> result = favoriService.toggleFavori(userId, idProduit);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
