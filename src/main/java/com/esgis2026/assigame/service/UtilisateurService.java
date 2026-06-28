package com.esgis2026.assigame.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.esgis2026.assigame.entity.TypeUtilisateur;
import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.repository.TypeUtilisateurRepository;
import com.esgis2026.assigame.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final TypeUtilisateurRepository typeUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
            TypeUtilisateurRepository typeUtilisateurRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.typeUtilisateurRepository = typeUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> getAllUtilisateur() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id " + id));
    }

    /**
     * Inscription publique d'un nouvel utilisateur (vendeur). Hash le mot de passe,
     * génère un login technique unique à partir de l'email, et assigne un type par défaut.
     */
    public Utilisateur register(Utilisateur utilisateur) {
        if (utilisateur.getEmail() == null || utilisateur.getEmail().isBlank()) {
            throw new RuntimeException("L'email est obligatoire.");
        }
        if (utilisateur.getMotdepasse() == null || utilisateur.getMotdepasse().length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cet email.");
        }

        utilisateur.setMotdepasse(passwordEncoder.encode(utilisateur.getMotdepasse()));
        utilisateur.setLogin(genererLoginUnique(utilisateur.getEmail()));
        utilisateur.setStatut("actif");

        if (utilisateur.getTypeutilisateur() == null
                || utilisateur.getTypeutilisateur().getId_typeutilisateur() == null) {
            utilisateur.setTypeutilisateur(getOuCreerTypeParDefaut());
        } else {
            TypeUtilisateur type = typeUtilisateurRepository
                    .findById(utilisateur.getTypeutilisateur().getId_typeutilisateur())
                    .orElseGet(this::getOuCreerTypeParDefaut);
            utilisateur.setTypeutilisateur(type);
        }

        return utilisateurRepository.save(utilisateur);
    }

    private TypeUtilisateur getOuCreerTypeParDefaut() {
        return typeUtilisateurRepository.findAll().stream()
                .filter(t -> "Vendeur".equalsIgnoreCase(t.getNom_typeutilisateur()))
                .findFirst()
                .orElseGet(() -> {
                    TypeUtilisateur type = new TypeUtilisateur();
                    type.setNom_typeutilisateur("Vendeur");
                    type.setDescription_typeutilisateur("Particulier vendant des articles sur Assigame");
                    return typeUtilisateurRepository.save(type);
                });
    }

    private String genererLoginUnique(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        if (base.length() > 30) {
            base = base.substring(0, 30);
        }
        if (base.isBlank()) {
            base = "user";
        }
        String login = base;
        int suffixe = 1;
        while (utilisateurRepository.existsByLogin(login)) {
            login = base + suffixe;
            suffixe++;
        }
        return login;
    }

    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        // Conservé pour la gestion interne (back-office) : passe par le même hachage que register.
        return register(utilisateur);
    }

    public void deleteUtilisateur(Long idUtilisateur) {
        utilisateurRepository.deleteById(idUtilisateur);
    }

    public Utilisateur updateUtilisateur(Long idUtilisateur, Utilisateur details) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found with id " + idUtilisateur));

        if (details.getNom() != null) utilisateur.setNom(details.getNom());
        if (details.getPrenom() != null) utilisateur.setPrenom(details.getPrenom());
        if (details.getTelephone() != null) utilisateur.setTelephone(details.getTelephone());
        if (details.getWhatsapp() != null) utilisateur.setWhatsapp(details.getWhatsapp());
        if (details.getLocalisation() != null) utilisateur.setLocalisation(details.getLocalisation());
        if (details.getAvatar() != null) utilisateur.setAvatar(details.getAvatar());
        if (details.getBio() != null) utilisateur.setBio(details.getBio());

        // Le mot de passe n'est changé que s'il est explicitement fourni (et ré-haché).
        if (details.getMotdepasse() != null && !details.getMotdepasse().isBlank()) {
            utilisateur.setMotdepasse(passwordEncoder.encode(details.getMotdepasse()));
        }

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Connexion par email + mot de passe. Le mot de passe est comparé au hash BCrypt stocké en base.
     */
    public Optional<Utilisateur> login(String email, String motdepasse) {
        if (email == null || motdepasse == null) {
            return Optional.empty();
        }
        return utilisateurRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(motdepasse, u.getMotdepasse()));
    }

    /** Vérifie si un utilisateur existe (utilisé par le contrôleur pour valider le requesterId). */
    public boolean existsById(Long id) {
        return utilisateurRepository.existsById(id);
    }

    /**
     * Retourne uniquement les coordonnées de contact d'un vendeur.
     * Appelé exclusivement depuis le endpoint GET /api/utilisateur/{id}/contact,
     * qui exige un requesterId valide (utilisateur authentifié).
     */
    public Optional<java.util.Map<String, String>> getContactInfo(Long id) {
        return utilisateurRepository.findById(id).map(u -> {
            java.util.Map<String, String> info = new java.util.HashMap<>();
            info.put("telephone", u.getTelephone() != null ? u.getTelephone() : "");
            info.put("whatsapp",  u.getWhatsapp()  != null ? u.getWhatsapp()  : "");
            return info;
        });
    }

}