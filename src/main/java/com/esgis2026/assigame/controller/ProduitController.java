package com.esgis2026.assigame.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.service.ProduitService;

@RestController
@RequestMapping("/api/produit")
public class ProduitController {

     private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // Liste tous les produits, avec filtres optionnels par catégorie ou par vendeur.
    // Exemples : /api/produit/list | /api/produit/list?categorieId=2 | /api/produit/list?vendeurId=5
    @GetMapping("/list")
    public List<Produit> getAllProduit(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Long vendeurId) {
        if (categorieId != null) {
            return produitService.getProduitsByCategorie(categorieId);
        }
        if (vendeurId != null) {
            return produitService.getProduitsByVendeur(vendeurId);
        }
        return produitService.getAllProduit();
    }

    @GetMapping("/vedettes")
    public List<Produit> getProduitsVedettes() {
        return produitService.getProduitsVedettes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.getProduitById(id));
    }

    // Incrémente le compteur de vues du produit (appelé par la fiche produit du frontend)
    @PostMapping("/{id}/vue")
    public ResponseEntity<Produit> incrementerVues(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.incrementerVues(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduit(@RequestBody Produit produit) {
        try {
            return ResponseEntity.ok(produitService.createProduit(produit));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduit(
            @PathVariable Long id,
            @RequestBody Produit produit) {
        try {
            return ResponseEntity.ok(produitService.updateProduit(id, produit));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }

}
