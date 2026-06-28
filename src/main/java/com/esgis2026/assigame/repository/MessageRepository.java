package com.esgis2026.assigame.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
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
     * Historique complet d'une conversation entre deux utilisateurs (ordre chrono ASC).
     * La condition bidirectionnelle capture les messages dans les deux sens :
     * (A→B) et (B→A) appartiennent à la même conversation.
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
     * IDs de TOUS les interlocuteurs d'un utilisateur (envoyés ET reçus).
     * Utilisation de JPQL pur avec CASE WHEN pour éviter le problème PostgreSQL
     * (qui convertit mal les requêtes natives UNION en BigInteger).
     */
    @Query("""
        SELECT DISTINCT CASE 
            WHEN m.expediteur.id_utilisateur = :idUtilisateur THEN m.destinataire.id_utilisateur 
            ELSE m.expediteur.id_utilisateur 
        END 
        FROM Message m 
        WHERE m.expediteur.id_utilisateur = :idUtilisateur 
           OR m.destinataire.id_utilisateur = :idUtilisateur
        """)
    List<Long> findInterlocuteurIds(@Param("idUtilisateur") Long idUtilisateur);

    /**
     * Dernier message échangé entre deux utilisateurs (ORDER BY DESC, LIMIT 1 via Pageable).
     * On passe PageRequest.of(0, 1) depuis le service pour ne charger qu'une seule ligne.
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
            @Param("idB") Long idB,
            Pageable pageable);

    /**
     * Compte les messages non lus reçus d'un expéditeur précis.
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
     * Marque comme lus tous les messages reçus d'un expéditeur (quand on ouvre la conv).
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
     * Nombre total de messages non lus toutes conversations confondues (badge navbar).
     */
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.destinataire.id_utilisateur = :idUtilisateur
          AND m.lu = false
        """)
    long countTotalNonLus(@Param("idUtilisateur") Long idUtilisateur);
}