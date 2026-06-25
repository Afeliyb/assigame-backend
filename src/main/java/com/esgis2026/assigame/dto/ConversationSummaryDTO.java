package com.esgis2026.assigame.dto;

import java.time.LocalDateTime;

/**
 * Résumé d'une conversation pour l'affichage dans la boîte de réception.
 *
 * Au lieu de renvoyer tous les messages, on ne renvoie qu'un aperçu :
 * l'interlocuteur, le dernier message échangé, sa date, et le nombre de
 * messages non lus. C'est le même principe que l'écran principal de WhatsApp
 * ou de Gmail : on voit la liste des discussions sans charger tout le contenu.
 */
public class ConversationSummaryDTO {

    // L'identifiant de l'interlocuteur (l'autre utilisateur dans la discussion)
    private Long interlocuteur_id;
    private String interlocuteur_nom;
    private String interlocuteur_prenom;
    private String interlocuteur_avatar;

    // Aperçu du dernier message pour l'affichage dans la liste
    private String dernier_message;
    private LocalDateTime date_dernier_message;

    // Nombre de messages reçus non lus dans cette conversation
    private long non_lus;

    // Produit lié à la conversation (le premier produit mentionné, si applicable)
    private Long produit_ref_id;
    private String produit_ref_nom;
    private String produit_ref_image;

    public Long getInterlocuteur_id() { return interlocuteur_id; }
    public void setInterlocuteur_id(Long interlocuteur_id) { this.interlocuteur_id = interlocuteur_id; }

    public String getInterlocuteur_nom() { return interlocuteur_nom; }
    public void setInterlocuteur_nom(String interlocuteur_nom) { this.interlocuteur_nom = interlocuteur_nom; }

    public String getInterlocuteur_prenom() { return interlocuteur_prenom; }
    public void setInterlocuteur_prenom(String interlocuteur_prenom) { this.interlocuteur_prenom = interlocuteur_prenom; }

    public String getInterlocuteur_avatar() { return interlocuteur_avatar; }
    public void setInterlocuteur_avatar(String interlocuteur_avatar) { this.interlocuteur_avatar = interlocuteur_avatar; }

    public String getDernier_message() { return dernier_message; }
    public void setDernier_message(String dernier_message) { this.dernier_message = dernier_message; }

    public LocalDateTime getDate_dernier_message() { return date_dernier_message; }
    public void setDate_dernier_message(LocalDateTime date_dernier_message) { this.date_dernier_message = date_dernier_message; }

    public long getNon_lus() { return non_lus; }
    public void setNon_lus(long non_lus) { this.non_lus = non_lus; }

    public Long getProduit_ref_id() { return produit_ref_id; }
    public void setProduit_ref_id(Long produit_ref_id) { this.produit_ref_id = produit_ref_id; }

    public String getProduit_ref_nom() { return produit_ref_nom; }
    public void setProduit_ref_nom(String produit_ref_nom) { this.produit_ref_nom = produit_ref_nom; }

    public String getProduit_ref_image() { return produit_ref_image; }
    public void setProduit_ref_image(String produit_ref_image) { this.produit_ref_image = produit_ref_image; }
}
