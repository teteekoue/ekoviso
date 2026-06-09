================================================================================
EKO VISO - IPTV Player & Recorder (Android)
================================================================================

DESCRIPTION
-----------
EkoViso est une application Android native (Kotlin) de lecture et 
d'enregistrement de flux IPTV. L'application se connecte automatiquement a 
la playlist M3U publique de iptv-org et permet a l'utilisateur de :

  - Parcourir toutes les chaines avec une barre de recherche intelligente
  - Lire les flux video en direct avec ExoPlayer
  - Enregistrer des emissions en cours avec choix du format et de la duree
  - Programmer des enregistrements futurs (meme quand l'app est fermee)
  - Gerer les enregistrements (lister, lire, supprimer)
  - Interface sombre moderne avec menu lateral coulissant

SOURCE DES DONNEES
------------------
URL de la playlist M3U : https://iptv-org.github.io/iptv/index.m3u
L'application telecharge et parse automatiquement cette playlist au 
demarrage, puis toutes les 24 heures. L'utilisateur n'a rien a configurer.

INTERFACE UTILISATEUR
---------------------
Theme : Sombre (Dark Theme) avec accents cyan/teal
Logo : EkoViso (assets/logo.png)

Ecrans principaux :
  1. Liste des chaines avec barre de recherche en haut
  2. Menu lateral gauche (Navigation Drawer) avec :
     - Toutes les chaines
     - Enregistrements
     - Programmes
     - Parametres
  3. Lecteur video plein ecran avec :
     - Bouton Enregistrer
     - Informations sur la chaine (nom, groupe, qualite)
     - Controles de lecture (play/pause, volume)
  4. Dialogue d'enregistrement :
     - Duree (en minutes ou illimite)
     - Format de sortie (MKV, MP4, TS)
     - Dossier de destination (demande au premier lancement)
  5. Liste des enregistrements avec actions (lire, supprimer, infos)
  6. Programmation d'enregistrement :
     - Selection de la chaine
     - Date et heure
     - Duree
     - Repetition (une fois, quotidien, hebdomadaire)

ARCHITECTURE TECHNIQUE
----------------------
Langage : Kotlin
SDK minimum : API 26 (Android 8.0)
SDK cible : API 34 (Android 14)

Architecture : MVVM (Model-View-ViewModel)
  - View : Jetpack Compose (UI declarative)
  - ViewModel : Gere l'etat de l'UI et la logique metier
  - Model : Room Database + Repository pattern

Composants principaux :
  - ExoPlayer (Media3) : Lecture des flux HLS/MPEG-TS
  - Room : Base de donnees locale (chaines, enregistrements, programmes)
  - WorkManager : Taches de fond (enregistrements programmes, synchro M3U)
  - Navigation Compose : Navigation entre ecrans
  - Material 3 : Design system avec theme sombre personnalise
  - FFmpeg (mobile-ffmpeg) : Enregistrement des flux
  - Retrofit/OkHttp : Telechargement de la playlist M3U
  - Coroutines/Flow : Programmation asynchrone

Structure du projet :
  app/
    src/main/java/com/ekoviso/app/
      di/              -> Injection de dependances (Hilt)
      data/
        local/         -> Room DAO, entities
        remote/        -> API M3U, parsing
        repository/    -> Implementations repository
      domain/
        model/         -> Classes metier
        repository/    -> Interfaces repository
        usecase/       -> Cas d'utilisation
      ui/
        theme/         -> Couleurs, typographie, theme
        screens/       -> Ecrans Compose
        components/    -> Composants reutilisables
        navigation/    -> Navigation setup
      worker/          -> WorkManager workers
      util/            -> Extensions, constantes
    src/main/res/       -> Ressources (drawables, strings)
  assets/
    logo.png            -> Logo de l'application
  build.gradle.kts      -> Configuration Gradle

DEPENDANCES PRINCIPALES
-----------------------
  - androidx.compose.material3
  - androidx.media3:media3-exoplayer
  - androidx.room:room-runtime
  - androidx.work:work-runtime-ktx
  - com.squareup.retrofit2:retrofit
  - com.squareup.okhttp3:okhttp
  - com.google.dagger:hilt-android
  - com.arthenica:mobile-ffmpeg
  - org.jetbrains.kotlinx:kotlinx-coroutines-android

BUILD ET DISTRIBUTION
---------------------
Build automatise via GitHub Actions (.github/workflows/build.yml)
  - Compilation de l'APK debug a chaque push
  - Compilation de l'APK release sur les tags
  - L'APK est uploadee en artifact

FONCTIONNALITES DETAILLEES
--------------------------
1. CHARGEMENT DES CHAINES
   - Telechargement de la playlist M3U au premier lancement
   - Parsing robuste (supporte #EXTINF, attributs tvg-*)
   - Mise en cache dans Room
   - Rafraichissement automatique toutes les 24h
   - Possibilite de rafraichir manuellement (pull-to-refresh)

2. RECHERCHE
   - Recherche temps reel (debounce 300ms)
   - Insensible a la casse et aux accents
   - Tolere les fautes de frappe (algorithme de similarite)
   - Recherche par nom, groupe, pays

3. LECTURE VIDEO
   - ExoPlayer avec UI personnalisee
   - Support HLS (.m3u8) et MPEG-TS
   - Reconnexion automatique en cas de coupure
   - Mode picture-in-picture
   - Controle du volume et de la luminosite par gestes

4. ENREGISTREMENT
   - Lancement depuis le lecteur video
   - Choix de la duree (definie ou illimitee)
   - Choix du format : MKV (par defaut), MP4, TS
   - Choix du dossier de sauvegarde
   - Notification pendant l'enregistrement
   - Arret manuel possible
   - FFmpeg gere la reconnexion si le flux coupe

5. PROGRAMMATION
   - Programmer un enregistrement futur
   - Selection date/heure via TimePicker
   - Repetition : une fois / quotidien / hebdomadaire
   - Utilise WorkManager pour le declenchement
   - Fonctionne meme si l'application est fermee
   - Liste des programmations avec etat (en attente, termine, echoue)

6. GESTION DES ENREGISTREMENTS
   - Liste des fichiers enregistres
   - Lecture des fichiers locaux
   - Suppression avec confirmation
   - Informations : taille, duree, date

THEME ET DESIGN
---------------
Palette de couleurs :
  - Primary : #0D9488 (Teal 600)
  - Primary Container : #134E4A (Teal 900)
  - Secondary : #F97316 (Orange 500) -> bouton Enregistrer
  - Background : #0F172A (Slate 900)
  - Surface : #1E293B (Slate 800)
  - On Surface : #F8FAFC (Slate 50)
  - Error : #EF4444 (Red 500)

Typographie :
  - Titres : Sans-serif medium
  - Corps : Sans-serif regular
  - Police monospace pour les informations techniques

Iconographie :
  - Material Icons (remplis pour navigation, outlines pour actions)

================================================================================
                 EkoViso - Your TV, Your Way.
================================================================================
