package com.esgis2026.assigame.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.esgis2026.assigame.entity.CategorieProduit;
import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.repository.CategorieProduitRepository;
import com.esgis2026.assigame.repository.ProduitRepository;
import com.esgis2026.assigame.repository.UtilisateurRepository;

@Service
public class ProduitService {
    final ProduitRepository produitRepository;
    final CategorieProduitRepository categorieProduitRepository;
    final UtilisateurRepository utilisateurRepository;

    public ProduitService(ProduitRepository produitRepository,
            CategorieProduitRepository categorieProduitRepository,
            UtilisateurRepository utilisateurRepository) {
        this.produitRepository = produitRepository;
        this.categorieProduitRepository = categorieProduitRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Produit> getAllProduit() {
        return produitRepository.findAllOrderByDateDesc();
    }

    public List<Produit> getProduitsByVendeur(Long idVendeur) {
        return produitRepository.findByVendeur(idVendeur);
    }

    public List<Produit> getProduitsByCategorie(Long idCategorie) {
        return produitRepository.findByCategorie(idCategorie);
    }

    public List<Produit> getProduitsVedettes() {
        return produitRepository.findVedettes();
    }

    public Produit getProduitById(Long idProduit) {
        return produitRepository.findById(idProduit)
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + idProduit));
    }

    public Produit incrementerVues(Long idProduit) {
        Produit produit = getProduitById(idProduit);
        produit.setVues(produit.getVues() + 1);
        return produitRepository.save(produit);
    }

    public Produit createProduit(Produit produit) {
        // On s'assure que la catégorie et le vendeur référencés existent vraiment avant d'insérer,
        // pour éviter une erreur SQL peu explicite côté frontend.
        if (produit.getCategorieProduit() == null || produit.getCategorieProduit().getIdcategorie_produit() == null) {
            throw new RuntimeException("La catégorie du produit est obligatoire.");
        }
        if (produit.getUtilisateur() == null || produit.getUtilisateur().getId_utilisateur() == null) {
            throw new RuntimeException("Le vendeur (utilisateur) du produit est obligatoire.");
        }

        CategorieProduit categorie = categorieProduitRepository.findById(produit.getCategorieProduit().getIdcategorie_produit())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable."));
        Utilisateur vendeur = utilisateurRepository.findById(produit.getUtilisateur().getId_utilisateur())
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable."));

        produit.setCategorieProduit(categorie);
        produit.setUtilisateur(vendeur);

        if (produit.getStatut() == null || produit.getStatut().isBlank()) {
            produit.setStatut("En ligne");
        }

        return produitRepository.save(produit);
    }

    public void deleteProduit(Long idProduit) {
        produitRepository.deleteById(idProduit);
    }

    public Produit updateProduit(Long idProduit, Produit details) {
        Produit produit = getProduitById(idProduit);

        if (details.getNom_produit() != null) {
            produit.setNom_produit(details.getNom_produit());
        }
        if (details.getDescription() != null) {
            produit.setDescription(details.getDescription());
        }
        if (details.getPrix() > 0) {
            produit.setPrix(details.getPrix());
        }
        if (details.getImages() != null && !details.getImages().isEmpty()) {
            produit.setImages(details.getImages());
        }
        if (details.getEtat() != null) {
            produit.setEtat(details.getEtat());
        }
        if (details.getStatut() != null && !details.getStatut().isBlank()) {
            produit.setStatut(details.getStatut());
        }
        if (details.getVedette() != null) {
            produit.setVedette(details.getVedette());
        }

        if (details.getCategorieProduit() != null && details.getCategorieProduit().getIdcategorie_produit() != null) {
            CategorieProduit categorie = categorieProduitRepository
                    .findById(details.getCategorieProduit().getIdcategorie_produit())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable."));
            produit.setCategorieProduit(categorie);
        }

        // Le vendeur propriétaire de l'annonce n'est volontairement pas modifiable via cette route.

        return produitRepository.save(produit);
    }

}
