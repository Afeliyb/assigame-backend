package com.esgis2026.assigame.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité Message.
 *
 * Pourquoi un DTO et pas l'entité directement ?
 * L'entité Message contient des relations @ManyToOne vers Utilisateur et Produit.
 * Si on sérialisait l'entité brute en JSON, Jackson chargerait les entités liées,
 * qui elles-mêmes ont des relations, créant un risque de boucle infinie ou de
 * chargement excessif de données (ex : renvoyer tout le Produit juste pour un message).
 * Le DTO aplatit proprement les informations utiles : on ne renvoie que l'id et
 * le nom de l'expéditeur, l'id du destinataire, et la miniature du produit référencé.
 */
public class MessageDTO {

    private Long id_message;
    private String contenu;
    private LocalDateTime date_envoi;
    private boolean lu;

    // --- Expéditeur (aplati) ---
    private Long expediteur_id;
    private String expediteur_nom;
    private String expediteur_prenom;
    private String expediteur_avatar;

    // --- Destinataire (aplati) ---
    private Long destinataire_id;
    private String destinataire_nom;
    private String destinataire_prenom;
    private String destinataire_avatar;

    // --- Produit référencé (optionnel, aplati) ---
    private Long produit_ref_id;
    private String produit_ref_nom;
    private String produit_ref_image;

    // =========================================================
    // Getters & Setters (générés à la main pour ne pas dépendre
    // de Lombok sur ce package, mais vous pouvez ajouter @Data)
    // =========================================================

    public Long getId_message() { return id_message; }
    public void setId_message(Long id_message) { this.id_message = id_message; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDate_envoi() { return date_envoi; }
    public void setDate_envoi(LocalDateTime date_envoi) { this.date_envoi = date_envoi; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public Long getExpediteur_id() { return expediteur_id; }
    public void setExpediteur_id(Long expediteur_id) { this.expediteur_id = expediteur_id; }

    public String getExpediteur_nom() { return expediteur_nom; }
    public void setExpediteur_nom(String expediteur_nom) { this.expediteur_nom = expediteur_nom; }

    public String getExpediteur_prenom() { return expediteur_prenom; }
    public void setExpediteur_prenom(String expediteur_prenom) { this.expediteur_prenom = expediteur_prenom; }

    public String getExpediteur_avatar() { return expediteur_avatar; }
    public void setExpediteur_avatar(String expediteur_avatar) { this.expediteur_avatar = expediteur_avatar; }

    public Long getDestinataire_id() { return destinataire_id; }
    public void setDestinataire_id(Long destinataire_id) { this.destinataire_id = destinataire_id; }

    public String getDestinataire_nom() { return destinataire_nom; }
    public void setDestinataire_nom(String destinataire_nom) { this.destinataire_nom = destinataire_nom; }

    public String getDestinataire_prenom() { return destinataire_prenom; }
    public void setDestinataire_prenom(String destinataire_prenom) { this.destinataire_prenom = destinataire_prenom; }

    public String getDestinataire_avatar() { return destinataire_avatar; }
    public void setDestinataire_avatar(String destinataire_avatar) { this.destinataire_avatar = destinataire_avatar; }

    public Long getProduit_ref_id() { return produit_ref_id; }
    public void setProduit_ref_id(Long produit_ref_id) { this.produit_ref_id = produit_ref_id; }

    public String getProduit_ref_nom() { return produit_ref_nom; }
    public void setProduit_ref_nom(String produit_ref_nom) { this.produit_ref_nom = produit_ref_nom; }

    public String getProduit_ref_image() { return produit_ref_image; }
    public void setProduit_ref_image(String produit_ref_image) { this.produit_ref_image = produit_ref_image; }
}
