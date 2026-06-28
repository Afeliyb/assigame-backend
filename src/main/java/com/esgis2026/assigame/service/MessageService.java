package com.esgis2026.assigame.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest; // IMPORT AJOUTÉ POUR RÉSOUDRE L'ERREUR
import org.springframework.stereotype.Service;

import com.esgis2026.assigame.dto.ConversationSummaryDTO;
import com.esgis2026.assigame.dto.MessageDTO;
import com.esgis2026.assigame.entity.Message;
import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.repository.MessageRepository;
import com.esgis2026.assigame.repository.ProduitRepository;
import com.esgis2026.assigame.repository.UtilisateurRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;

    public MessageService(
            MessageRepository messageRepository,
            UtilisateurRepository utilisateurRepository,
            ProduitRepository produitRepository) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.produitRepository = produitRepository;
    }

    // ===========================================================
    // ENVOI D'UN MESSAGE
    // ===========================================================

    public MessageDTO envoyerMessage(
            Long idExpediteur,
            Long idDestinataire,
            String contenu,
            Long idProduitRef) {

        // Correction SonarLint : Utilisation de IllegalArgumentException
        if (contenu == null || contenu.isBlank()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide.");
        }
        if (contenu.length() > 2000) {
            throw new IllegalArgumentException("Le message ne peut pas dépasser 2000 caractères.");
        }
        if (idExpediteur.equals(idDestinataire)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer un message à vous-même.");
        }

        Utilisateur expediteur = utilisateurRepository.findById(idExpediteur)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur introuvable."));
        Utilisateur destinataire = utilisateurRepository.findById(idDestinataire)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire introuvable."));

        Message message = new Message();
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setContenu(contenu.trim());

        if (idProduitRef != null) {
            Optional<Produit> produit = produitRepository.findById(idProduitRef);
            produit.ifPresent(message::setProduitRef);
        }

        Message saved = messageRepository.save(message);
        return toDTO(saved);
    }

    // ===========================================================
    // RÉCUPÉRATION D'UNE CONVERSATION
    // ===========================================================

    public List<MessageDTO> getConversation(Long idLecteur, Long idInterlocuteur) {
        List<Message> messages = messageRepository.findConversation(idLecteur, idInterlocuteur);
        messageRepository.marquerLus(idLecteur, idInterlocuteur);
        return messages.stream().map(this::toDTO).toList();
    }

    // ===========================================================
    // BOÎTE DE RÉCEPTION — liste des conversations
    // ===========================================================

    public List<ConversationSummaryDTO> getBoiteDeReception(Long idUtilisateur) {
        List<Long> interlocuteurIds = messageRepository.findInterlocuteurIds(idUtilisateur);
        List<ConversationSummaryDTO> summaries = new ArrayList<>();

        for (Long interlocuteurId : interlocuteurIds) {
            Optional<Utilisateur> interlocuteurOpt = utilisateurRepository.findById(interlocuteurId);
            
            // Correction SonarLint : Retrait des 'continue' multiples grâce à une condition imbriquée
            if (interlocuteurOpt.isPresent()) {
                Utilisateur interlocuteur = interlocuteurOpt.get();

                // CORRECTION ERREUR DE COMPILATION : Ajout de PageRequest.of(0, 1)
                List<Message> derniers = messageRepository.findLastMessage(idUtilisateur, interlocuteurId, PageRequest.of(0, 1));
                
                if (!derniers.isEmpty()) {
                    Message dernier = derniers.get(0);
                    long nonLus = messageRepository.countNonLus(idUtilisateur, interlocuteurId);

                    ConversationSummaryDTO summary = new ConversationSummaryDTO();
                    summary.setInterlocuteur_id(interlocuteur.getId_utilisateur());
                    summary.setInterlocuteur_nom(interlocuteur.getNom());
                    summary.setInterlocuteur_prenom(interlocuteur.getPrenom());
                    summary.setInterlocuteur_avatar(interlocuteur.getAvatar());
                    summary.setDernier_message(dernier.getContenu());
                    summary.setDate_dernier_message(dernier.getDate_envoi());
                    summary.setNon_lus(nonLus);

                    derniers.stream()
                            .filter(m -> m.getProduitRef() != null)
                            .findFirst()
                            .ifPresent(m -> {
                                summary.setProduit_ref_id(m.getProduitRef().getId_produit());
                                summary.setProduit_ref_nom(m.getProduitRef().getNom_produit());
                                if (!m.getProduitRef().getImages().isEmpty()) {
                                    summary.setProduit_ref_image(m.getProduitRef().getImages().get(0));
                                }
                            });

                    summaries.add(summary);
                }
            }
        }

        summaries.sort((a, b) -> {
            if (a.getDate_dernier_message() == null) return 1;
            if (b.getDate_dernier_message() == null) return -1;
            return b.getDate_dernier_message().compareTo(a.getDate_dernier_message());
        });

        return summaries;
    }

    // ===========================================================
    // NOMBRE TOTAL DE NON-LUS (pour le badge navbar)
    // ===========================================================

    public long getTotalNonLus(Long idUtilisateur) {
        return messageRepository.countTotalNonLus(idUtilisateur);
    }

    // ===========================================================
    // CONVERSION ENTITÉ → DTO (méthode interne)
    // ===========================================================

    private MessageDTO toDTO(Message m) {
        MessageDTO dto = new MessageDTO();
        dto.setId_message(m.getId_message());
        dto.setContenu(m.getContenu());
        dto.setDate_envoi(m.getDate_envoi());
        dto.setLu(m.isLu());

        dto.setExpediteur_id(m.getExpediteur().getId_utilisateur());
        dto.setExpediteur_nom(m.getExpediteur().getNom());
        dto.setExpediteur_prenom(m.getExpediteur().getPrenom());
        dto.setExpediteur_avatar(m.getExpediteur().getAvatar());

        dto.setDestinataire_id(m.getDestinataire().getId_utilisateur());
        dto.setDestinataire_nom(m.getDestinataire().getNom());
        dto.setDestinataire_prenom(m.getDestinataire().getPrenom());
        dto.setDestinataire_avatar(m.getDestinataire().getAvatar());

        if (m.getProduitRef() != null) {
            dto.setProduit_ref_id(m.getProduitRef().getId_produit());
            dto.setProduit_ref_nom(m.getProduitRef().getNom_produit());
            if (!m.getProduitRef().getImages().isEmpty()) {
                dto.setProduit_ref_image(m.getProduitRef().getImages().get(0));
            }
        }

        return dto;
    }
}