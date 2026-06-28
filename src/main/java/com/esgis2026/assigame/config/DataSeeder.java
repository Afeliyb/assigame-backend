package com.esgis2026.assigame.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.esgis2026.assigame.entity.CategorieProduit;
import com.esgis2026.assigame.entity.Produit;
import com.esgis2026.assigame.entity.TypeUtilisateur;
import com.esgis2026.assigame.entity.Utilisateur;
import com.esgis2026.assigame.repository.CategorieProduitRepository;
import com.esgis2026.assigame.repository.ProduitRepository;
import com.esgis2026.assigame.repository.TypeUtilisateurRepository;
import com.esgis2026.assigame.repository.UtilisateurRepository;
import com.esgis2026.assigame.service.ProduitService;
import com.esgis2026.assigame.service.UtilisateurService;

/**
 * Initialise la base avec des données de démonstration réalistes (catégories, types d'utilisateurs,
 * de nombreux vendeurs et annonces) UNIQUEMENT si les tables correspondantes sont vides.
 *
 * Mot de passe de connexion pour tous les comptes de démonstration : Assigame2026!
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TypeUtilisateurRepository typeUtilisateurRepository;
    private final CategorieProduitRepository categorieProduitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurService utilisateurService;
    private final ProduitService produitService;

    public DataSeeder(TypeUtilisateurRepository typeUtilisateurRepository,
            CategorieProduitRepository categorieProduitRepository,
            UtilisateurRepository utilisateurRepository,
            ProduitRepository produitRepository,
            UtilisateurService utilisateurService,
            ProduitService produitService) {
        this.typeUtilisateurRepository = typeUtilisateurRepository;
        this.categorieProduitRepository = categorieProduitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.produitRepository = produitRepository;
        this.utilisateurService = utilisateurService;
        this.produitService = produitService;
    }

    @Override
    public void run(String... args) {
        seedTypesUtilisateur();
        List<CategorieProduit> categories = seedCategories();
        if (utilisateurRepository.count() == 0) {
            List<Utilisateur> vendeurs = seedVendeurs();
            if (produitRepository.count() == 0) {
                seedProduits(categories, vendeurs);
            }
        }
    }

    private void seedTypesUtilisateur() {
        if (typeUtilisateurRepository.count() > 0) return;

        TypeUtilisateur vendeur = new TypeUtilisateur();
        vendeur.setNom_typeutilisateur("Vendeur");
        vendeur.setDescription_typeutilisateur("Particulier vendant des articles sur Assigame");
        typeUtilisateurRepository.save(vendeur);

        TypeUtilisateur acheteur = new TypeUtilisateur();
        acheteur.setNom_typeutilisateur("Acheteur");
        acheteur.setDescription_typeutilisateur("Particulier achetant des articles sur Assigame");
        typeUtilisateurRepository.save(acheteur);
    }

    private List<CategorieProduit> seedCategories() {
        if (categorieProduitRepository.count() > 0) {
            return categorieProduitRepository.findAll();
        }

        String[][] data = {
                {"Vêtements", "Shirt"},
                {"Électronique", "Laptop"},
                {"Maison", "Armchair"},
                {"Sport", "Dumbbell"},
                {"Beauté", "Sparkles"},
                {"Livres", "BookOpen"},
                {"Jouets", "Gamepad2"},
                {"Auto", "Car"},
        };

        return categorieProduitRepository.saveAll(
                List.of(data).stream().map(d -> {
                    CategorieProduit c = new CategorieProduit();
                    c.setNom_categorieproduit(d[0]);
                    c.setIcone(d[1]);
                    return c;
                }).toList());
    }

    private List<Utilisateur> seedVendeurs() {
        String motDePasseDemo = "Assigame2026!";

        Object[][] data = {
                // Vendeurs d'origine
                {"Mensah", "Kodjo", "kodjo@assigame.tg", "+22890010101", "Adidogomé, Lomé",
                        "https://images.unsplash.com/photo-1522529599102-193c0d76b5b6?w=400&q=80",
                        "Passionné d'électronique et de gadgets high-tech."},
                {"Kpodar", "Afi", "afi@assigame.tg", "+22890020202", "Kégué, Lomé",
                        "https://images.unsplash.com/photo-1597393922738-085ea04b5a07?q=80",
                        "Mode et accessoires de seconde main, triés sur le volet."},
                {"Amouzou", "Kossi", "kossi@assigame.tg", "+22890030303", "Agoè, Lomé",
                        "https://images.unsplash.com/photo-1566492031773-4f4e44671857?q=80",
                        "Revendeur d'équipements sportifs d'occasion."},
                {"Tchari", "Amina", "amina@assigame.tg", "+22890040404", "Hédzranawoé, Lomé",
                        "https://images.unsplash.com/photo-1603448460771-a67bd49ed5ca?q=80",
                        "Décoration d'intérieur et mobilier moderne."},
                // Nouveaux vendeurs Togolais (Batch 1)
                {"Akakpo", "Komi", "komi@assigame.tg", "+22890050505", "Bè, Lomé",
                        "https://images.unsplash.com/photo-1617094876531-3ad72ca3306d?q=80",
                        "Spécialiste en engins à deux roues et appareils pratiques."},
                {"Lawson", "Enyonam", "enyonam@assigame.tg", "+22890060606", "Amoutiévé, Lomé",
                        "https://images.unsplash.com/photo-1531123897727-8f129e1688ce?w=400&q=80",
                        "Grossiste en pagnes Wax, mode féminine et articles de beauté."},
                {"Sossou", "Yao", "yao@assigame.tg", "+22890070707", "Totsi, Lomé",
                        "https://images.unsplash.com/photo-1637684666806-53825e71fcac?q=80",
                        "Gamer passionné. Vente et échange de consoles, jeux et équipement informatique."},
                // Encore plus de vendeurs Togolais (Batch 2)
                {"Traoré", "Ibrahim", "ibrahim@assigame.tg", "+22890080808", "Dékon, Lomé",
                        "https://images.unsplash.com/photo-1650667955633-df9d498a8e6b?q=80",
                        "Spécialiste en smartphones et ordinateurs portables de grandes marques."},
                {"Kossiwa", "Chantal", "chantal@assigame.tg", "+22890090909", "Adakpamé, Lomé",
                        "https://images.unsplash.com/photo-1589156280159-27698a70f29e?w=400&q=80",
                        "Équipements pour la maison, ustensiles de cuisine et électroménager."},
                {"Abalo", "Koffi", "koffi@assigame.tg", "+22890101010", "Nyékonakpoé, Lomé",
                        "https://images.unsplash.com/photo-1600630242764-41cf7d951ac4?q=80",
                        "Vêtements pour hommes, chaussures en cuir et accessoires de mode."},
                {"Dossou", "Abra", "abra@assigame.tg", "+22890111111", "Baguida, Lomé",
                        "https://images.unsplash.com/photo-1573497620013-7f7660da1a48?q=80",
                        "Produits de beauté, maquillage et soins capillaires."},
                {"Ali", "Fousseni", "ali@assigame.tg", "+22890121212", "Agoè-Zongo, Lomé",
                        "https://images.unsplash.com/photo-1679480911476-3ee732578062?q=80",
                        "Énergies renouvelables, panneaux solaires et batteries."}
        };

        return List.of(data).stream().map(d -> {
            Utilisateur u = new Utilisateur();
            u.setNom((String) d[0]);
            u.setPrenom((String) d[1]);
            u.setEmail((String) d[2]);
            u.setMotdepasse(motDePasseDemo);
            u.setTelephone((String) d[3]);
            u.setWhatsapp((String) d[3]);
            u.setLocalisation((String) d[4]);
            u.setAvatar((String) d[5]);
            u.setBio((String) d[6]);
            return utilisateurService.register(u);
        }).toList();
    }

    private void seedProduits(List<CategorieProduit> categories, List<Utilisateur> vendeurs) {
        CategorieProduit electronique = parCategorie(categories, "Électronique");
        CategorieProduit maison = parCategorie(categories, "Maison");
        CategorieProduit vetements = parCategorie(categories, "Vêtements");
        CategorieProduit sport = parCategorie(categories, "Sport");
        CategorieProduit beaute = parCategorie(categories, "Beauté");
        CategorieProduit auto = parCategorie(categories, "Auto");

        // Liaison des vendeurs (Indexation stricte basée sur l'ordre d'insertion)
        Utilisateur kodjo = vendeurs.get(0);
        Utilisateur afi = vendeurs.get(1);
        Utilisateur kossi = vendeurs.get(2);
        Utilisateur amina = vendeurs.get(3);
        Utilisateur komi = vendeurs.get(4);
        Utilisateur enyonam = vendeurs.get(5);
        Utilisateur yao = vendeurs.get(6);
        Utilisateur ibrahim = vendeurs.get(7);
        Utilisateur chantal = vendeurs.get(8);
        Utilisateur koffi = vendeurs.get(9);
        Utilisateur abra = vendeurs.get(10);
        Utilisateur fousseni = vendeurs.get(11);

        // --- Articles d'origine ---
        creerProduit("Sony Alpha a7 III - Boîtier Nu", "Appareil photo hybride plein format. Très peu utilisé, compte moins de 5000 déclenchements. Vendu avec boîte d'origine.", 850000, electronique, kodjo, "Très bon état", true, "https://images.unsplash.com/photo-1516724562728-afc824a36e84?w=800&q=80");
        creerProduit("Canapé 3 Places Moderne - Tissu Gris", "Magnifique canapé scandinave, grand confort. Parfait pour un salon moderne. À récupérer sur Hédzranawoé.", 120000, maison, amina, "Bon état", true, "https://images.unsplash.com/photo-1670700143988-85b6e6d73475?q=80");
        creerProduit("Nike Air Max 270 - Taille 42", "Paire de baskets Nike originales. Jamais portées (erreur de pointure).", 45000, vetements, afi, "Neuf", false, "https://images.unsplash.com/photo-1662411198835-c5a151d2af9e?q=80");
        creerProduit("MacBook Pro M1 2020 - 256Go", "L'ordinateur est dans un état impeccable. Batterie à 95% de capacité maximale.", 550000, electronique, kodjo, "Très bon état", false, "https://images.unsplash.com/photo-1553055193-66c49a0334e0?q=80");
        creerProduit("Parfum Dior Sauvage 100ml", "Eau de parfum pour homme, scellé dans sa boîte. Authentique.", 65000, beaute, afi, "Neuf", false, "https://images.unsplash.com/photo-1700522604220-471669e4364c?q=80");

        // --- Articles Populaires au Togo (Batch 1) ---
        creerProduit("Moto Sanya 125", "Moto Sanya 125 très économique. Moteur en parfait état, vidange récemment faite. Idéal pour éviter les bouchons de Lomé, papiers à jour.", 350000, auto, komi, "Bon état", true, "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80");
        creerProduit("Pagne Wax Vlisco - Motif Hollandais", "Véritable Wax Hollandais Vlisco, pièce de 6 yards. Couleurs vives et résistantes au lavage, idéal pour vos tenues de cérémonie.", 45000, vetements, enyonam, "Neuf", true, "https://images.unsplash.com/photo-1552710307-537199cd41c0?q=80");
        creerProduit("Console PlayStation 5 Édition Standard", "PS5 avec lecteur disque, fournie avec 2 manettes DualSense. Parfaite pour l'organisation de tournois ou jouer avec des amis.", 420000, electronique, yao, "Très bon état", true, "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?q=80");
        creerProduit("Ventilateur sur pied Binatone", "Ventilateur robuste avec 3 vitesses de ventilation et fonction oscillante. Indispensable pour rester au frais pendant les saisons chaudes.", 15000, maison, komi, "Satisfaisant", false, "https://images.unsplash.com/photo-1656428005715-74cbf05fdefb?q=80");
        creerProduit("Perruque Lace Frontal 100% Humaine", "Mèches brésiliennes, taille 24 pouces. Texture lisse, soyeuse, ne s'emmêle pas. Prête à être posée.", 60000, beaute, enyonam, "Neuf", false, "https://images.unsplash.com/photo-1519699047748-de8e457a634e?q=80");
        creerProduit("iPhone 13 Pro Max - 256 Go", "Couleur Bleu Alpin. État propre sans rayures, Face ID fonctionnel. Batterie à 94%. Vendu avec coque et câble.", 480000, electronique, komi, "Très bon état", true, "https://images.unsplash.com/photo-1632661674596-df8be070a5c5?q=80");
        creerProduit("Groupe Électrogène 2.5 KVA", "Pratique pour les coupures de courant. Autonomie de 8h avec le plein. Entretien régulier effectué.", 110000, maison, yao, "Bon état", false, "https://images.unsplash.com/photo-1581092160562-40aa08e78837?q=80");
        creerProduit("Clavier Mécanique RGB", "Clavier rétroéclairé avec switchs bleus, parfait pour la saisie de code rapide et le gaming. Connexion USB.", 25000, electronique, yao, "Neuf", false, "https://images.unsplash.com/photo-1595225476474-87563907a212?q=80");

        // --- Encore plus d'articles (Batch 2) ---
        creerProduit("Samsung Galaxy S22 Ultra", "Téléphone presque neuf, 512 Go de stockage. Appareil photo exceptionnel. Vendu avec chargeur rapide et pochette.", 550000, electronique, ibrahim, "Très bon état", true, "https://images.unsplash.com/photo-1707438095902-cc23b01ac7a2?q=80");
        creerProduit("Panneau Solaire 250W Monocristallin", "Idéal pour l'autonomie énergétique ou pour pallier les coupures de la CEET. Très bon rendement même par temps nuageux.", 65000, maison, fousseni, "Neuf", false, "https://images.unsplash.com/photo-1509391366360-2e959784a276?q=80");
        creerProduit("Congélateur Coffre Nasco 200L", "Conserve très bien la glace. Consommation énergétique réduite. Parfait pour une alimentation générale ou une grande famille.", 135000, maison, chantal, "Bon état", true, "https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?q=80");
        creerProduit("Ensemble Pagne Tissé (Kente)", "Magnifique ensemble traditionnel en pagne tissé de qualité supérieure. Fait à la main, idéal pour les mariages traditionnels.", 55000, vetements, koffi, "Neuf", true, "https://images.unsplash.com/photo-1663044022726-889ee51a682e?q=80");
        creerProduit("Mixeur Blender Moulinex 1.5L", "Mixeur puissant avec bol en verre. Parfait pour faire du jus d'ananas, de la purée de tomate ou écraser des condiments.", 22000, maison, chantal, "Neuf", false, "https://images.unsplash.com/photo-1570222094114-d054a817e56b?q=80");
        creerProduit("Chaussures Richelieu en Cuir", "Chaussures habillées pointure 43. Cuir véritable, très élégantes pour le bureau ou les cérémonies.", 35000, vetements, koffi, "Neuf", false, "https://images.unsplash.com/photo-1664505504065-31f8937d2261?q=80");
        creerProduit("Gamme Soins Visage à la Vitamine C", "Sérum et crème hydratante pour un teint éclatant et sans taches. Produits testés dermatologiquement.", 18000, beaute, abra, "Neuf", false, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?q=80");
        creerProduit("TV Smart Samsung 55 Pouces 4K", "Smart TV avec Netflix et YouTube intégrés. Image ultra nette. Légère rayure sur le pied mais écran impeccable.", 250000, electronique, ibrahim, "Très bon état", true, "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?q=80&");
        creerProduit("Voiture Toyota Corolla 2012 (Direct Lomé)", "Moteur essence très silencieux, boîte automatique, climatisation d'origine glaciale. Série plaque TG-xxxx-AQ.", 3800000, auto, fousseni, "Bon état", true, "https://images.unsplash.com/photo-1590362891991-f776e747a588?q=80");
        creerProduit("Machine à coudre Singer", "Machine à coudre mécanique, robuste et fiable. Parfait pour un(e) apprenti(e) tailleur ou pour des retouches à la maison.", 45000, maison, chantal, "Satisfaisant", false, "https://images.unsplash.com/photo-1466027397211-20d0f2449a3f?q=80");
        // --- Articles de Sport (Pour Kossi) ---
        creerProduit("Vélo Tout Terrain (VTT) ROCKRIDER", "VTT adulte idéal pour les pistes d'Agoè. Freins à disque, suspension avant. Très robuste.", 85000, sport, kossi, "Bon état", false, "https://images.unsplash.com/photo-1641858504124-e111c87ec021?q=80");
        creerProduit("Kit Haltères de musculation 20kg", "Set d'haltères ajustables en fonte avec mallette de rangement. Idéal pour s'entraîner à la maison.", 25000, sport, kossi, "Très bon état", false, "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?q=80");
    }

    private void creerProduit(String nom, String description, double prix, CategorieProduit categorie,
            Utilisateur vendeur, String etat, boolean vedette, String... images) {
        Produit p = new Produit();
        p.setNom_produit(nom);
        p.setDescription(description);
        p.setPrix(prix);
        p.setCategorieProduit(categorie);
        p.setUtilisateur(vendeur);
        p.setEtat(etat);
        p.setVedette(vedette);
        p.setStatut("En ligne");
        p.setImages(List.of(images));
        produitService.createProduit(p);
    }

    private CategorieProduit parCategorie(List<CategorieProduit> categories, String nom) {
        return categories.stream()
                .filter(c -> c.getNom_categorieproduit().equals(nom))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Catégorie de seed introuvable : " + nom));
    }
}