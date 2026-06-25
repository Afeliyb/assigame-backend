package com.esgis2026.assigame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.esgis2026.assigame.entity.Produit;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query("SELECT p FROM Produit p ORDER BY p.date_ajout DESC")
    List<Produit> findAllOrderByDateDesc();

    // Annonces d'un vendeur donné (pour le tableau de bord "Mes annonces")
    @Query("SELECT p FROM Produit p WHERE p.utilisateur.id_utilisateur = :idUtilisateur ORDER BY p.date_ajout DESC")
    List<Produit> findByVendeur(@Param("idUtilisateur") Long idUtilisateur);

    // Annonces d'une catégorie donnée (pour le filtre de la page Explorer)
    @Query("SELECT p FROM Produit p WHERE p.categorieProduit.idcategorie_produit = :idCategorie ORDER BY p.date_ajout DESC")
    List<Produit> findByCategorie(@Param("idCategorie") Long idCategorie);

    // Produits mis en avant (Bento grid de la page d'accueil)
    @Query("SELECT p FROM Produit p WHERE p.vedette = true AND p.statut = 'En ligne' ORDER BY p.date_ajout DESC")
    List<Produit> findVedettes();
}
