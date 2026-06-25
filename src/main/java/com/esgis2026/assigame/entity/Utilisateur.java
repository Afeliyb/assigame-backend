package com.esgis2026.assigame.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "utilisateur")
public class Utilisateur {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id_utilisateur;

   @JsonProperty("nom")
   @Column(nullable = false, length = 50)
   private String Nom;

   @JsonProperty("prenom")
   @Column(nullable = false, length = 50)
   private String Prenom;

   @JsonProperty("email")
   @Column(unique = true, nullable = false, length = 100)
   private String Email;

   // Le mot de passe (hashé en BCrypt) ne doit jamais être renvoyé dans une réponse JSON,
   // uniquement accepté en entrée (login / inscription).
   @JsonProperty(value = "motdepasse", access = JsonProperty.Access.WRITE_ONLY)
   @Column(nullable = false, length = 100)
   private String Motdepasse;

   @JsonProperty("login")
   @Column(nullable = false, unique = true, length = 50)
   private String Login;

   @Column(nullable = true, length = 20)
   private String telephone;

   // Numéro WhatsApp au format international (ex: +22890000000) utilisé pour le bouton "Contacter le vendeur".
   // Si non renseigné, on retombe sur le champ telephone.
   @Column(nullable = true, length = 20)
   private String whatsapp;

   // Quartier / ville du vendeur, ex: "Adidogomé, Lomé"
   @Column(nullable = true, length = 100)
   private String localisation;

   // URL de la photo de profil
   @Column(nullable = true, length = 500)
   private String avatar;

   @Column(nullable = true, length = 300)
   private String bio;

   @Column(nullable = false)
   private LocalDateTime date_creation;

   @Column(nullable = false, length = 20)
   private String statut;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_typeutilisateur")
   private TypeUtilisateur typeutilisateur;

   @PrePersist
   public void prePersist() {
      this.date_creation = LocalDateTime.now();
      if (this.statut == null || this.statut.isBlank()) {
         this.statut = "actif";
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id_utilisateur == null) ? 0 : id_utilisateur.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Utilisateur other = (Utilisateur) obj;
      if (id_utilisateur == null) {
         if (other.id_utilisateur != null) return false;
      } else if (!id_utilisateur.equals(other.id_utilisateur)) return false;
      return true;
   }

   @Override
   public String toString() {
      return "Utilisateur [id_utilisateur=" + id_utilisateur + ", Nom=" + Nom
            + ", Prenom=" + Prenom + ", Email=" + Email + ", Login=" + Login
            + ", telephone=" + telephone + ", whatsapp=" + whatsapp + ", localisation=" + localisation
            + ", statut=" + statut + ", date_creation=" + date_creation + "]";
   }
}
