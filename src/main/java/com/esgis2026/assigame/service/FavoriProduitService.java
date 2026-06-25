package com.esgis2026.assigame.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.esgis2026.assigame.entity.FavoriProduit;
import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.repository.FavoriProduitRepository;
import com.esgis2026.assigame.repository.ProduitRepository;
import com.esgis2026.assigame.repository.UtilisateurRepository;

@Service
public class FavoriProduitService {

    private final FavoriProduitRepository favoriRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;

    public FavoriProduitService(
            FavoriProduitRepository favoriRepository,
            ProduitRepository produitRepository,
            UtilisateurRepository utilisateurRepository) {
        this.favoriRepository = favoriRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Toggle like/unlike.
     * Si le favori n'existait pas → on le crée et on retourne { "liked": true }.
     * Si le favori existait déjà → on le supprime et on retourne { "liked": false }.
     * Le frontend peut ainsi mettre à jour l'icône du bouton sans second appel.
     */
    public Map<String, Object> toggleFavori(Long idUtilisateur, Long idProduit) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + idUtilisateur));
        Produit produit = produitRepository.findById(idProduit)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + idProduit));

        Optional<FavoriProduit> existing = favoriRepository.findByUtilisateurAndProduit(idUtilisateur, idProduit);

        if (existing.isPresent()) {
            // Favori déjà présent → on supprime (unlike)
            favoriRepository.deleteByUtilisateurAndProduit(idUtilisateur, idProduit);
            return Map.of("liked", false, "idProduit", idProduit);
        } else {
            // Pas encore favori → on crée (like)
            FavoriProduit favori = new FavoriProduit();
            favori.setUtilisateur(utilisateur);
            favori.setProduit(produit);
            favoriRepository.save(favori);
            return Map.of("liked", true, "idProduit", idProduit);
        }
    }

    /**
     * Retourne tous les produits mis en favori par un utilisateur.
     * Le frontend utilisera cette liste pour la page "Mes favoris" du dashboard.
     */
    public List<Produit> getFavorisProduits(Long idUtilisateur) {
        return favoriRepository.findByUtilisateur(idUtilisateur)
                .stream()
                .map(FavoriProduit::getProduit)
                .toList();
    }

    /**
     * Vérifie si un produit est dans les favoris d'un utilisateur.
     * Utilisé par la fiche produit pour pré-initialiser l'état du bouton like.
     */
    public boolean isFavori(Long idUtilisateur, Long idProduit) {
        return favoriRepository.findByUtilisateurAndProduit(idUtilisateur, idProduit).isPresent();
    }

    /** Nombre total de likes d'un produit (stat publique potentielle). */
    public long countFavoris(Long idProduit) {
        return favoriRepository.countByProduit(idProduit);
    }
}
