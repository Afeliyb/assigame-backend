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
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un message échangé entre deux utilisateurs sur la plateforme.
 *
 * Modélisation d'une conversation :
 * Plutôt que de créer une entité "Conversation" séparée, on identifie
 * une discussion par le couple (id_expediteur, id_destinataire).
 * Une conversation entre Alice et Bob est donc l'ensemble des messages
 * où (expediteur=Alice ET destinataire=Bob) OU (expediteur=Bob ET destinataire=Alice).
 * Cette approche est suffisante pour un système de messagerie directe (1-to-1)
 * et évite une table de jointure supplémentaire.
 *
 * Champ id_produit_ref (optionnel) :
 * Permet de lier un message à une annonce spécifique (ex : "Je vous contacte
 * pour votre annonce iPhone"). Cela permet au frontend d'afficher la carte du
 * produit concerné dans l'en-tête de la conversation.
 */
@Entity
@Getter
@Setter
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_message;

    // L'utilisateur qui envoie le message
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_expediteur", nullable = false)
    private Utilisateur expediteur;

    // L'utilisateur qui reçoit le message
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire", nullable = false)
    private Utilisateur destinataire;

    // Le contenu textuel du message (max 2000 caractères)
    @Column(nullable = false, length = 2000)
    private String contenu;

    // Date et heure d'envoi, initialisée automatiquement côté serveur
    @Column(nullable = false)
    private LocalDateTime date_envoi;

    // Indique si le destinataire a lu ce message.
    // Utilisé pour afficher le badge "non lu" dans la boîte de réception.
    @Column(nullable = false)
    private boolean lu = false;

    // Référence optionnelle à un produit (pour contextualiser la discussion).
    // Si non nul, le frontend affiche la miniature du produit concerné.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit_ref", nullable = true)
    private Produit produitRef;

    @PrePersist
    public void prePersist() {
        this.date_envoi = LocalDateTime.now();
        this.lu = false;
    }
}
