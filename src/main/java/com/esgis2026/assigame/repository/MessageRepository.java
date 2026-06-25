package com.esgis2026.assigame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esgis2026.assigame.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Récupère l'historique complet d'une conversation entre deux utilisateurs.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE
          (m.expediteur.id_utilisateur = :idA AND m.destinataire.id_utilisateur = :idB)
          OR
          (m.expediteur.id_utilisateur = :idB AND m.destinataire.id_utilisateur = :idA)
        ORDER BY m.date_envoi ASC
        """)
    List<Message> findConversation(
            @Param("idA") Long idA,
            @Param("idB") Long idB);

    /**
     * Récupère les IDs des utilisateurs à qui l'utilisateur courant a envoyé un message.
     */
    @Query("SELECT DISTINCT m.destinataire.id_utilisateur FROM Message m WHERE m.expediteur.id_utilisateur = :idUtilisateur")
    List<Long> findDestinatairesIds(@Param("idUtilisateur") Long idUtilisateur);

    /**
     * Récupère les IDs des utilisateurs qui ont envoyé un message à l'utilisateur courant.
     */
    @Query("SELECT DISTINCT m.expediteur.id_utilisateur FROM Message m WHERE m.destinataire.id_utilisateur = :idUtilisateur")
    List<Long> findExpediteursIds(@Param("idUtilisateur") Long idUtilisateur);

    /**
     * Récupère le dernier message échangé entre deux utilisateurs.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE
          (m.expediteur.id_utilisateur = :idA AND m.destinataire.id_utilisateur = :idB)
          OR
          (m.expediteur.id_utilisateur = :idB AND m.destinataire.id_utilisateur = :idA)
        ORDER BY m.date_envoi DESC
        """)
    List<Message> findLastMessage(
            @Param("idA") Long idA,
            @Param("idB") Long idB);

    /**
     * Compte les messages non lus reçus par un utilisateur.
     */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.destinataire.id_utilisateur = :idDestinataire
          AND m.expediteur.id_utilisateur   = :idExpediteur
          AND m.lu = false
        """)
    long countNonLus(
            @Param("idDestinataire") Long idDestinataire,
            @Param("idExpediteur") Long idExpediteur);

    /**
     * Marque comme "lus" tous les messages reçus par un utilisateur.
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m SET m.lu = true
        WHERE m.destinataire.id_utilisateur = :idDestinataire
          AND m.expediteur.id_utilisateur   = :idExpediteur
          AND m.lu = false
        """)
    void marquerLus(
            @Param("idDestinataire") Long idDestinataire,
            @Param("idExpediteur") Long idExpediteur);

    /**
     * Nombre total de messages non lus pour le badge navbar.
     */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.destinataire.id_utilisateur = :idUtilisateur
          AND m.lu = false
        """)
    long countTotalNonLus(@Param("idUtilisateur") Long idUtilisateur);
}