package com.esgis2026.assigame.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Table de jointure entre Utilisateur et Produit pour les favoris ("likes").
 * La contrainte unique (id_utilisateur, id_produit) garantit qu'un utilisateur
 * ne peut liker un produit qu'une seule fois, même en cas de double-appel réseau.
 */
@Entity
@Getter
@Setter
@Table(
    name = "favori_produit",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_favori_utilisateur_produit",
        columnNames = { "id_utilisateur", "id_produit" }
    )
)
public class FavoriProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_favori;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private LocalDateTime date_ajout;

    @PrePersist
    public void prePersist() {
        this.date_ajout = LocalDateTime.now();
    }
}
