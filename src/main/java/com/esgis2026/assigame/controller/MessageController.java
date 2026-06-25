package com.esgis2026.assigame.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esgis2026.assigame.dto.ConversationSummaryDTO;
import com.esgis2026.assigame.dto.MessageDTO;
import com.esgis2026.assigame.service.MessageService;

/**
 * Controller REST pour la messagerie interne.
 *
 * Routes exposées :
 *
 *  POST /api/message/envoyer
 *       Body : { "idExpediteur", "idDestinataire", "contenu", "idProduitRef"? }
 *       → Envoie un message. Retourne le MessageDTO du message créé (201 Created).
 *
 *  GET  /api/message/conversation/{idInterlocuteur}?userId={id}
 *       → Retourne l'historique complet d'une discussion, trié chronologiquement.
 *         Marque automatiquement les messages reçus comme "lus".
 *
 *  GET  /api/message/inbox?userId={id}
 *       → Retourne la liste des conversations (résumés) de l'utilisateur,
 *         triée par date du dernier message (plus récent d'abord).
 *
 *  GET  /api/message/non-lus?userId={id}
 *       → Retourne le nombre total de messages non lus. Utilisé pour le badge navbar.
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Envoie un message entre deux utilisateurs.
     *
     * Corps de la requête attendu :
     * {
     *   "idExpediteur"   : 3,
     *   "idDestinataire" : 7,
     *   "contenu"        : "Bonjour, est-ce que l'article est toujours disponible ?",
     *   "idProduitRef"   : 12        ← optionnel
     * }
     *
     * On utilise un body JSON plutôt que des paramètres d'URL car "contenu" peut
     * contenir n'importe quel texte (espaces, caractères spéciaux, accents).
     * Les mettre en query parameter poserait des problèmes d'encodage.
     */
    @PostMapping("/envoyer")
    public ResponseEntity<?> envoyerMessage(@RequestBody Map<String, Object> body) {
        try {
            Long idExpediteur   = Long.valueOf(body.get("idExpediteur").toString());
            Long idDestinataire = Long.valueOf(body.get("idDestinataire").toString());
            String contenu      = body.get("contenu").toString();

            // idProduitRef est optionnel — peut être absent du body
            Long idProduitRef = null;
            if (body.get("idProduitRef") != null) {
                idProduitRef = Long.valueOf(body.get("idProduitRef").toString());
            }

            MessageDTO dto = messageService.envoyerMessage(idExpediteur, idDestinataire, contenu, idProduitRef);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Retourne l'historique d'une conversation entre l'utilisateur connecté
     * et un interlocuteur, et marque les messages comme lus automatiquement.
     *
     * Exemple d'appel : GET /api/message/conversation/7?userId=3
     * → Retourne tous les messages échangés entre l'utilisateur 3 et l'utilisateur 7.
     */
    @GetMapping("/conversation/{idInterlocuteur}")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @PathVariable Long idInterlocuteur,
            @RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getConversation(userId, idInterlocuteur));
    }

    /**
     * Retourne la boîte de réception : liste des conversations de l'utilisateur.
     * Chaque conversation est représentée par un ConversationSummaryDTO.
     *
     * Exemple d'appel : GET /api/message/inbox?userId=3
     */
    @GetMapping("/inbox")
    public ResponseEntity<List<ConversationSummaryDTO>> getInbox(@RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getBoiteDeReception(userId));
    }

    /**
     * Retourne le nombre total de messages non lus de l'utilisateur,
     * toutes conversations confondues.
     * Utilisé pour afficher le badge rouge sur l'icône "Messages" dans la navbar.
     *
     * Exemple d'appel : GET /api/message/non-lus?userId=3
     * Réponse : { "count": 4 }
     */
    @GetMapping("/non-lus")
    public ResponseEntity<Map<String, Long>> getNonLus(@RequestParam Long userId) {
        return ResponseEntity.ok(Map.of("count", messageService.getTotalNonLus(userId)));
    }
}
