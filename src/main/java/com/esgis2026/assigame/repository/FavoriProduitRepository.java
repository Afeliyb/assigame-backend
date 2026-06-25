package com.esgis2026.assigame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esgis2026.assigame.entity.FavoriProduit;

@Repository
public interface FavoriProduitRepository extends JpaRepository<FavoriProduit, Long> {

    // Récupère tous les favoris d'un utilisateur (pour la page "Mes favoris" du dashboard).
    @Query("SELECT f FROM FavoriProduit f WHERE f.utilisateur.id_utilisateur = :idUtilisateur ORDER BY f.date_ajout DESC")
    List<FavoriProduit> findByUtilisateur(@Param("idUtilisateur") Long idUtilisateur);

    // Permet de vérifier rapidement si un produit est déjà dans les favoris d'un utilisateur.
    @Query("SELECT f FROM FavoriProduit f WHERE f.utilisateur.id_utilisateur = :idUtilisateur AND f.produit.id_produit = :idProduit")
    Optional<FavoriProduit> findByUtilisateurAndProduit(
            @Param("idUtilisateur") Long idUtilisateur,
            @Param("idProduit") Long idProduit);

    // Supprime le favori (unlike). @Modifying + @Transactional sont requis pour les DELETE JPQL.
    @Modifying
    @Transactional
    @Query("DELETE FROM FavoriProduit f WHERE f.utilisateur.id_utilisateur = :idUtilisateur AND f.produit.id_produit = :idProduit")
    void deleteByUtilisateurAndProduit(
            @Param("idUtilisateur") Long idUtilisateur,
            @Param("idProduit") Long idProduit);

    // Compte le nombre total de likes d'un produit (utile pour une future stat publique).
    @Query("SELECT COUNT(f) FROM FavoriProduit f WHERE f.produit.id_produit = :idProduit")
    long countByProduit(@Param("idProduit") Long idProduit);
}
