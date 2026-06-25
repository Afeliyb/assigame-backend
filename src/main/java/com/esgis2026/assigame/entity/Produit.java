package com.esgis2026.assigame.entity;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "produit")

public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_produit;
    
    @Column(unique = false, nullable = false, length = 50)
    private String nom_produit;
    
    @Column(unique = false, nullable =true, length = 1000 )
    private String description;
    
    @Column(unique = false, nullable = true)
    private double prix; 

    // Liste des URLs d'images du produit (galerie). Stockée dans une table associée produit_images.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_images", joinColumns = @JoinColumn(name = "id_produit"))
    @Column(name = "url", length = 500)
    @OrderColumn(name = "position")
    private List<String> images = new ArrayList<>();

    @Column(unique = false, nullable = false)
    private LocalDateTime date_ajout; 
    
    
    // Statut de l'annonce : "En ligne", "Vendu", "Hors ligne"
    @Column(unique = false, nullable = false, length = 20)
    private String statut;

    // Etat / condition de l'article : "Neuf", "Très bon état", "Bon état", "Satisfaisant"
    @Column(unique = false, nullable = true, length = 30)
    private String etat;

    // Met le produit en avant sur la page d'accueil (Bento grid).
    // Type Boolean (et non boolean) pour distinguer "non renseigné" de "false" lors d'une mise à jour partielle.
    @Column(nullable = false)
    private Boolean vedette = false;

    // Compteur de vues, incrémenté à chaque consultation de la fiche produit
    @Column(nullable = false)
    private int vues = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategorie_produit", nullable = false)
    private CategorieProduit categorieProduit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @PrePersist
    public void prePersist() {
        if (this.date_ajout == null) {
            this.date_ajout = LocalDateTime.now();
        }
        if (this.statut == null || this.statut.isBlank()) {
            this.statut = "En ligne";
        }
        if (this.vedette == null) {
            this.vedette = false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id_produit == null) ? 0 : id_produit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Produit other = (Produit) obj;
        if (id_produit == null) {
            if (other.id_produit != null)
                return false;
        } else if (!id_produit.equals(other.id_produit))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Produit [id_produit=" + id_produit + ", nom_produit=" + nom_produit + ", description=" + description
                + ", prix=" + prix + ", images=" + images + ", date_ajout=" + date_ajout + ", statut=" + statut
                + ", etat=" + etat + ", vedette=" + vedette + ", vues=" + vues
                + ", categorieProduit=" + categorieProduit + ", utilisateur=" + utilisateur + "]";
    }
    
    


}
