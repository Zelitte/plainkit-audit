# Generuje res/values*/strings.xml pre plainkit. audit.
# Poradie jazykov v každom riadku: sk, en, de, fr, es, it
import os, sys

LANGS = ["sk", "en", "de", "fr", "es", "it"]
# values/ (bez prípony) = záložný jazyk, keď by sa nič nenašlo -> angličtina
FOLDER = {"en": "values", "sk": "values-sk", "de": "values-de",
          "fr": "values-fr", "es": "values-es", "it": "values-it"}

S = {}
def s(key, *vals):
    assert len(vals) == 6, key
    S[key] = vals

# ── obrazovka so zoznamom ──
s("search_label", "Hľadať aplikáciu", "Search apps", "Apps durchsuchen", "Rechercher une app", "Buscar apps", "Cerca app")
s("clear", "Zrušiť", "Clear", "Löschen", "Effacer", "Borrar", "Cancella")
s("scan_all", "Skenovať všetky", "Scan all", "Alle scannen", "Tout analyser", "Analizar todas", "Analizza tutte")
s("scanning_one", "Skenujem…", "Scanning…", "Wird gescannt…", "Analyse…", "Analizando…", "Analisi in corso…")
s("scan_progress", "Skenujem %1$d/%2$d…", "Scanning %1$d/%2$d…", "Scanne %1$d/%2$d…", "Analyse %1$d/%2$d…", "Analizando %1$d/%2$d…", "Analisi %1$d/%2$d…")
s("scan_failed",
  "Appku sa nepodarilo prečítať. Skús to neskôr — výsledok sa neuložil.",
  "Could not read this app. Try again later — nothing was saved.",
  "Die App konnte nicht gelesen werden. Versuch es später — es wurde nichts gespeichert.",
  "Impossible de lire cette app. Réessaie plus tard — rien n'a été enregistré.",
  "No se pudo leer esta app. Inténtalo más tarde; no se ha guardado nada.",
  "Impossibile leggere questa app. Riprova più tardi — non è stato salvato nulla.")
s("tile_apps", "aplikácií", "apps", "Apps", "apps", "apps", "app")
s("tile_system", "systémových", "system", "System", "système", "sistema", "di sistema")
s("changes_title", "ČO SA ZMENILO", "WHAT CHANGED", "WAS SICH GEÄNDERT HAT", "CE QUI A CHANGÉ", "QUÉ HA CAMBIADO", "COSA È CAMBIATO")
s("permissions_title", "ČO APPKA MÔŽE", "WHAT THE APP CAN ACCESS", "WORAUF DIE APP ZUGREIFEN KANN", "CE À QUOI L'APP A ACCÈS", "A QUÉ PUEDE ACCEDER LA APP", "A COSA PUÒ ACCEDERE L'APP")
s("no_named_perms",
  "Nežiada nič, čo by sa dalo zrozumiteľne pomenovať.",
  "It requests nothing that can be named in plain language.",
  "Sie fordert nichts an, was sich verständlich benennen lässt.",
  "Elle ne demande rien qui puisse être nommé simplement.",
  "No solicita nada que pueda nombrarse de forma sencilla.",
  "Non richiede nulla che si possa descrivere in parole semplici.")
s("mark_granted", "UDELENÉ", "GRANTED", "ERTEILT", "ACCORDÉE", "CONCEDIDO", "CONCESSA")
s("mark_not_granted", "neudelené", "not granted", "nicht erteilt", "non accordée", "no concedido", "non concessa")
s("mark_install", "automaticky pri inštalácii", "automatic at install", "automatisch bei der Installation", "automatique à l'installation", "automático al instalar", "automatica all'installazione")
s("mark_special", "povoľuje sa zvlášť", "enabled separately", "wird separat erlaubt", "autorisée séparément", "se activa por separado", "si abilita separatamente")
s("tech_perms", "+ %1$d technických povolení", "+ %1$d technical permissions", "+ %1$d technische Berechtigungen", "+ %1$d autorisations techniques", "+ %1$d permisos técnicos", "+ %1$d autorizzazioni tecniche")
s("hide_tech", "skryť technické povolenia", "hide technical permissions", "technische Berechtigungen ausblenden", "masquer les autorisations techniques", "ocultar permisos técnicos", "nascondi autorizzazioni tecniche")
s("summary_perms", "%1$d povolení", "%1$d permissions", "%1$d Berechtigungen", "%1$d autorisations", "%1$d permisos", "%1$d autorizzazioni")
s("summary_trackers", "%1$d trackerov", "%1$d trackers", "%1$d Tracker", "%1$d traceurs", "%1$d rastreadores", "%1$d tracker")

# ── úrovne ──
s("level_alert", "POZOR", "WARNING", "WARNUNG", "ATTENTION", "ATENCIÓN", "ATTENZIONE")
s("level_warn", "VŠIMNI SI", "NOTICE", "HINWEIS", "À NOTER", "AVISO", "NOTA")
s("level_info", "INFO", "INFO", "INFO", "INFO", "INFO", "INFO")
s("level_ok", "OK", "OK", "OK", "OK", "OK", "OK")

# ── nálezy ──
s("no_internet",
  "Nežiada prístup na internet — nemá kam nič odosielať.",
  "Does not request internet access — it has nowhere to send anything.",
  "Fordert keinen Internetzugriff an — sie kann nichts irgendwohin senden.",
  "Ne demande pas l'accès à Internet — elle ne peut rien envoyer nulle part.",
  "No solicita acceso a Internet: no tiene adónde enviar nada.",
  "Non richiede l'accesso a Internet: non può inviare nulla da nessuna parte.")
# %1$s = skupiny povolení, %2$s = názvy SDK
s("marketing_with_granted",
  "Má udelený prístup k %1$s a zároveň obsahuje reklamné/marketingové SDK (%2$s). Práve tu môžu citlivé údaje skončiť v reklamnej sieti.",
  "Has been granted access to %1$s and also contains advertising/marketing SDKs (%2$s). This is where sensitive data can end up in an ad network.",
  "Hat Zugriff auf: %1$s — und enthält zugleich Werbe-/Marketing-SDKs (%2$s). Genau hier können sensible Daten in einem Werbenetzwerk landen.",
  "A accès à : %1$s — et contient aussi des SDK publicitaires/marketing (%2$s). C'est ici que des données sensibles peuvent finir dans un réseau publicitaire.",
  "Tiene acceso a: %1$s, y además contiene SDK de publicidad/marketing (%2$s). Aquí es donde los datos sensibles pueden acabar en una red publicitaria.",
  "Ha accesso a: %1$s — e contiene anche SDK pubblicitari/di marketing (%2$s). È qui che i dati sensibili possono finire in una rete pubblicitaria.")
s("marketing_only",
  "Obsahuje reklamné/marketingové SDK (%1$s). Tvoje správanie v appke sa spája s reklamným profilom.",
  "Contains advertising/marketing SDKs (%1$s). Your behaviour in the app is tied to an advertising profile.",
  "Enthält Werbe-/Marketing-SDKs (%1$s). Dein Verhalten in der App wird mit einem Werbeprofil verknüpft.",
  "Contient des SDK publicitaires/marketing (%1$s). Ton comportement dans l'app est relié à un profil publicitaire.",
  "Contiene SDK de publicidad/marketing (%1$s). Tu comportamiento en la app se vincula a un perfil publicitario.",
  "Contiene SDK pubblicitari/di marketing (%1$s). Il tuo comportamento nell'app viene collegato a un profilo pubblicitario.")
# %1$s = názvy SDK, %2$s = skupiny povolení
s("analytics_with_granted",
  "Obsahuje analytické SDK (%1$s) a má udelený prístup k %2$s. Analytika tieto údaje sama neodosiela, ale appka ich má k dispozícii — kombinácia stojí za pozornosť.",
  "Contains analytics SDKs (%1$s) and has been granted access to %2$s. Analytics does not send this data by itself, but the app has it available — the combination is worth noticing.",
  "Enthält Analyse-SDKs (%1$s) und hat Zugriff auf: %2$s. Die Analyse sendet diese Daten nicht von selbst, aber die App hat sie zur Verfügung — die Kombination verdient Aufmerksamkeit.",
  "Contient des SDK d'analyse (%1$s) et a accès à : %2$s. L'analyse n'envoie pas ces données d'elle-même, mais l'app les a à disposition — la combinaison mérite attention.",
  "Contiene SDK de analítica (%1$s) y tiene acceso a: %2$s. La analítica no envía estos datos por sí sola, pero la app los tiene a su alcance; la combinación merece atención.",
  "Contiene SDK di analisi (%1$s) e ha accesso a: %2$s. L'analisi non invia questi dati da sola, ma l'app li ha a disposizione — la combinazione merita attenzione.")
s("analytics_only",
  "Obsahuje analytické SDK (%1$s). Odosiela, čo v appke robíš — nie nutne obsah, ale správanie.",
  "Contains analytics SDKs (%1$s). It reports what you do in the app — not necessarily the content, but the behaviour.",
  "Enthält Analyse-SDKs (%1$s). Sie melden, was du in der App tust — nicht unbedingt Inhalte, aber das Verhalten.",
  "Contient des SDK d'analyse (%1$s). Ils rapportent ce que tu fais dans l'app — pas forcément le contenu, mais le comportement.",
  "Contiene SDK de analítica (%1$s). Informan de lo que haces en la app: no necesariamente el contenido, pero sí el comportamiento.",
  "Contiene SDK di analisi (%1$s). Riferiscono cosa fai nell'app — non necessariamente i contenuti, ma il comportamento.")
s("fraud",
  "Obsahuje nástroje na detekciu podvodov (%1$s). Zbierajú signály o zariadení a správaní, ale účelom je ochrana účtu, nie reklama.",
  "Contains fraud-detection tools (%1$s). They collect device and behaviour signals, but the purpose is account protection, not advertising.",
  "Enthält Werkzeuge zur Betrugserkennung (%1$s). Sie sammeln Geräte- und Verhaltenssignale, dienen aber dem Kontoschutz, nicht der Werbung.",
  "Contient des outils de détection de fraude (%1$s). Ils collectent des signaux sur l'appareil et le comportement, mais servent à protéger le compte, pas à la publicité.",
  "Contiene herramientas de detección de fraude (%1$s). Recogen señales del dispositivo y del comportamiento, pero su fin es proteger la cuenta, no la publicidad.",
  "Contiene strumenti antifrode (%1$s). Raccolgono segnali sul dispositivo e sul comportamento, ma servono a proteggere l'account, non alla pubblicità.")
s("crash_only",
  "Obsahuje len hlásenie pádov (%1$s). Bežná vývojárska prax, nie sledovanie na reklamu.",
  "Contains only crash reporting (%1$s). Common development practice, not ad tracking.",
  "Enthält nur Absturzberichte (%1$s). Übliche Entwicklerpraxis, kein Werbe-Tracking.",
  "Contient uniquement des rapports de plantage (%1$s). Pratique courante des développeurs, pas du pistage publicitaire.",
  "Solo contiene informes de fallos (%1$s). Práctica habitual de desarrollo, no rastreo publicitario.",
  "Contiene solo la segnalazione dei crash (%1$s). Prassi comune degli sviluppatori, non tracciamento pubblicitario.")
s("too_many",
  "Obsahuje %1$d reklamných a analytických knižníc od rôznych firiem — nadpriemerne veľa.",
  "Contains %1$d advertising and analytics libraries from different companies — above average.",
  "Enthält %1$d Werbe- und Analysebibliotheken verschiedener Firmen — überdurchschnittlich viele.",
  "Contient %1$d bibliothèques publicitaires et d'analyse de différentes entreprises — plus que la moyenne.",
  "Contiene %1$d bibliotecas de publicidad y analítica de distintas empresas: más de lo habitual.",
  "Contiene %1$d librerie pubblicitarie e di analisi di aziende diverse — più della media.")
s("no_trackers", "Žiadne známe trackery.", "No known trackers.", "Keine bekannten Tracker.", "Aucun traceur connu.", "Ningún rastreador conocido.", "Nessun tracker noto.")
s("no_trackers_but_granted",
  "Má udelený prístup k %1$s, ale neobsahuje žiadny známy tracker.",
  "Has been granted access to %1$s, but contains no known tracker.",
  "Hat Zugriff auf: %1$s, enthält aber keinen bekannten Tracker.",
  "A accès à : %1$s, mais ne contient aucun traceur connu.",
  "Tiene acceso a: %1$s, pero no contiene ningún rastreador conocido.",
  "Ha accesso a: %1$s, ma non contiene tracker noti.")
s("pending",
  "Žiada aj prístup k %1$s, ten však zatiaľ udelený nemá. Ak mu ho povolíš, posúdenie sa zmení.",
  "It also requests access to %1$s, but has not been granted it. If you allow it, this assessment will change.",
  "Fordert auch Zugriff auf: %1$s — hat ihn aber noch nicht erhalten. Wenn du ihn erlaubst, ändert sich die Bewertung.",
  "Demande aussi l'accès à : %1$s, mais ne l'a pas encore obtenu. Si tu l'autorises, l'évaluation changera.",
  "También solicita acceso a: %1$s, pero aún no lo tiene. Si lo permites, la valoración cambiará.",
  "Richiede anche l'accesso a: %1$s, ma non l'ha ancora ottenuto. Se lo consenti, la valutazione cambierà.")

# ── log zmien ──
s("change_trackers_added", "pribudli trackery: %1$s", "trackers added: %1$s", "neue Tracker: %1$s", "traceurs ajoutés : %1$s", "rastreadores añadidos: %1$s", "tracker aggiunti: %1$s")
s("change_trackers_removed", "ubudli trackery: %1$s", "trackers removed: %1$s", "entfernte Tracker: %1$s", "traceurs retirés : %1$s", "rastreadores eliminados: %1$s", "tracker rimossi: %1$s")
s("change_perms_added", "pribudli povolenia: %1$s", "permissions added: %1$s", "neue Berechtigungen: %1$s", "autorisations ajoutées : %1$s", "permisos añadidos: %1$s", "autorizzazioni aggiunte: %1$s")
s("change_perms_removed", "ubudli povolenia: %1$s", "permissions removed: %1$s", "entfernte Berechtigungen: %1$s", "autorisations retirées : %1$s", "permisos eliminados: %1$s", "autorizzazioni rimosse: %1$s")
s("change_tech", "technické: %1$s", "technical: %1$s", "technisch: %1$s", "techniques : %1$s", "técnicos: %1$s", "tecniche: %1$s")

# ── nastavenia ──
s("settings", "Nastavenia", "Settings", "Einstellungen", "Paramètres", "Ajustes", "Impostazioni")
s("back", "Späť", "Back", "Zurück", "Retour", "Atrás", "Indietro")
s("language_title", "JAZYK", "LANGUAGE", "SPRACHE", "LANGUE", "IDIOMA", "LINGUA")
s("data_title", "ÚDAJE", "DATA", "DATEN", "DONNÉES", "DATOS", "DATI")
s("clear_changes", "Vymazať log zmien", "Clear change log", "Änderungsprotokoll löschen", "Effacer l'historique des changements", "Borrar registro de cambios", "Cancella registro modifiche")
s("clear_scans", "Vymazať výsledky skenov", "Clear scan results", "Scan-Ergebnisse löschen", "Effacer les résultats d'analyse", "Borrar resultados de análisis", "Cancella risultati delle analisi")
s("clear_scans_note",
  "Zmaže aj základňu na porovnávanie — po ňom prvý sken nemá s čím porovnávať.",
  "This also deletes the comparison baseline — after it, the first scan has nothing to compare against.",
  "Löscht auch die Vergleichsbasis — danach hat der erste Scan nichts zum Vergleichen.",
  "Supprime aussi la base de comparaison — ensuite, la première analyse n'aura rien à comparer.",
  "También borra la base de comparación: después, el primer análisis no tendrá con qué comparar.",
  "Cancella anche la base di confronto — dopo, la prima analisi non avrà nulla con cui confrontarsi.")
s("show_intro", "Znova zobraziť úvodnú obrazovku", "Show the intro screen again", "Startbildschirm erneut anzeigen", "Revoir l'écran d'accueil", "Mostrar de nuevo la pantalla de inicio", "Mostra di nuovo la schermata iniziale")
s("cleared", "Vymazané.", "Cleared.", "Gelöscht.", "Effacé.", "Borrado.", "Cancellato.")
s("about_title", "O APLIKÁCII", "ABOUT", "ÜBER DIE APP", "À PROPOS", "ACERCA DE", "INFORMAZIONI")
s("about_text",
  "Celá analýza prebieha v telefóne. Appka číta nainštalované aplikácie, hľadá v ich kóde známe sledovacie knižnice a spája to s povoleniami. Nič neodosiela.\n\nAppka hovorí, čo je v kóde prítomné — nie čo sa práve deje. Zoznam signatúr nie je úplný, takže skôr podhodnocuje, než preháňa.",
  "The whole analysis runs on your phone. The app reads installed applications, looks for known tracking libraries in their code and combines that with permissions. Nothing is uploaded.\n\nThe app reports what is present in the code — not what is happening right now. The signature list is not complete, so it under-reports rather than exaggerates.",
  "Die gesamte Analyse läuft auf deinem Telefon. Die App liest installierte Anwendungen, sucht in ihrem Code nach bekannten Tracking-Bibliotheken und verknüpft das mit den Berechtigungen. Es wird nichts hochgeladen.\n\nDie App zeigt, was im Code vorhanden ist — nicht, was gerade passiert. Die Signaturliste ist nicht vollständig, daher untertreibt sie eher, als dass sie übertreibt.",
  "Toute l'analyse se fait sur ton téléphone. L'app lit les applications installées, cherche dans leur code des bibliothèques de pistage connues et croise cela avec les autorisations. Rien n'est envoyé.\n\nL'app indique ce qui est présent dans le code — pas ce qui se passe en ce moment. La liste de signatures n'est pas complète : elle sous-estime plutôt qu'elle n'exagère.",
  "Todo el análisis se hace en tu teléfono. La app lee las aplicaciones instaladas, busca en su código bibliotecas de rastreo conocidas y lo cruza con los permisos. No se envía nada.\n\nLa app muestra lo que está presente en el código, no lo que ocurre en este momento. La lista de firmas no está completa, así que tiende a quedarse corta antes que a exagerar.",
  "Tutta l'analisi avviene sul tuo telefono. L'app legge le applicazioni installate, cerca nel loro codice librerie di tracciamento note e incrocia il risultato con le autorizzazioni. Non viene inviato nulla.\n\nL'app indica ciò che è presente nel codice — non ciò che sta succedendo adesso. L'elenco delle firme non è completo, quindi tende a sottostimare piuttosto che a esagerare.")
s("privacy_policy", "Zásady ochrany súkromia", "Privacy policy", "Datenschutzerklärung", "Politique de confidentialité", "Política de privacidad", "Informativa sulla privacy")

# ── úvodná obrazovka ──
s("claims",
  "po slovensky · bez reklám · bez účtu · nič neodosiela",
  "no ads · no signup · nothing is uploaded",
  "ohne Werbung · ohne Konto · nichts wird hochgeladen",
  "sans pub · sans compte · rien n'est envoyé",
  "sin anuncios · sin cuenta · no se envía nada",
  "senza pubblicità · senza account · non invia nulla")
s("disclosure",
  "Aby appka mohla skontrolovať ostatné aplikácie, potrebuje vidieť ich zoznam. Celá analýza prebieha v telefóne. Nič sa neodosiela.",
  "To check your other apps, this app needs to see the list of installed packages. The whole analysis runs on your phone. Nothing is uploaded.",
  "Um deine anderen Apps zu prüfen, muss diese App die Liste der installierten Pakete sehen. Die gesamte Analyse läuft auf deinem Telefon. Es wird nichts hochgeladen.",
  "Pour vérifier tes autres apps, cette app doit voir la liste des applications installées. Toute l'analyse se fait sur ton téléphone. Rien n'est envoyé.",
  "Para revisar tus otras apps, esta app necesita ver la lista de aplicaciones instaladas. Todo el análisis se hace en tu teléfono. No se envía nada.",
  "Per controllare le altre app, questa app deve vedere l'elenco delle applicazioni installate. Tutta l'analisi avviene sul tuo telefono. Non viene inviato nulla.")
s("tap_to_continue", "ťukni pre pokračovanie", "tap to continue", "tippen zum Fortfahren", "touche pour continuer", "toca para continuar", "tocca per continuare")
s("part_of", "súčasť projektu plainkit.app", "part of the plainkit.app project", "Teil des Projekts plainkit.app", "fait partie du projet plainkit.app", "parte del proyecto plainkit.app", "parte del progetto plainkit.app")

# ── názvy povolení (kľúč = perm_ + posledná časť názvu) ──
P = [
 ("ACCESS_FINE_LOCATION", "presná poloha", "precise location", "genauer Standort", "position précise", "ubicación precisa", "posizione precisa"),
 ("ACCESS_COARSE_LOCATION", "približná poloha", "approximate location", "ungefährer Standort", "position approximative", "ubicación aproximada", "posizione approssimativa"),
 ("ACCESS_BACKGROUND_LOCATION", "poloha aj na pozadí", "location in the background", "Standort im Hintergrund", "position en arrière-plan", "ubicación en segundo plano", "posizione in background"),
 ("RECORD_AUDIO", "mikrofón", "microphone", "Mikrofon", "micro", "micrófono", "microfono"),
 ("CAMERA", "kamera", "camera", "Kamera", "appareil photo", "cámara", "fotocamera"),
 ("READ_CONTACTS", "čítanie kontaktov", "read contacts", "Kontakte lesen", "lire les contacts", "leer contactos", "leggere i contatti"),
 ("WRITE_CONTACTS", "úprava kontaktov", "modify contacts", "Kontakte ändern", "modifier les contacts", "modificar contactos", "modificare i contatti"),
 ("GET_ACCOUNTS", "zoznam účtov v telefóne", "accounts on the device", "Konten auf dem Gerät", "comptes de l'appareil", "cuentas del dispositivo", "account del dispositivo"),
 ("READ_SMS", "čítanie SMS", "read SMS", "SMS lesen", "lire les SMS", "leer SMS", "leggere gli SMS"),
 ("RECEIVE_SMS", "príjem SMS", "receive SMS", "SMS empfangen", "recevoir des SMS", "recibir SMS", "ricevere SMS"),
 ("SEND_SMS", "odosielanie SMS", "send SMS", "SMS senden", "envoyer des SMS", "enviar SMS", "inviare SMS"),
 ("READ_CALL_LOG", "zoznam hovorov", "call log", "Anrufliste", "journal d'appels", "registro de llamadas", "registro chiamate"),
 ("WRITE_CALL_LOG", "úprava zoznamu hovorov", "modify call log", "Anrufliste ändern", "modifier le journal d'appels", "modificar registro de llamadas", "modificare registro chiamate"),
 ("CALL_PHONE", "volanie bez opýtania", "place calls directly", "direkt anrufen", "passer des appels directement", "hacer llamadas directamente", "effettuare chiamate direttamente"),
 ("ANSWER_PHONE_CALLS", "dvíhanie hovorov", "answer phone calls", "Anrufe annehmen", "répondre aux appels", "responder llamadas", "rispondere alle chiamate"),
 ("READ_PHONE_STATE", "stav telefónu", "phone status", "Telefonstatus", "état du téléphone", "estado del teléfono", "stato del telefono"),
 ("READ_PHONE_NUMBERS", "tvoje telefónne číslo", "your phone number", "deine Telefonnummer", "ton numéro de téléphone", "tu número de teléfono", "il tuo numero di telefono"),
 ("READ_CALENDAR", "čítanie kalendára", "read calendar", "Kalender lesen", "lire l'agenda", "leer calendario", "leggere il calendario"),
 ("WRITE_CALENDAR", "úprava kalendára", "modify calendar", "Kalender ändern", "modifier l'agenda", "modificar calendario", "modificare il calendario"),
 ("READ_EXTERNAL_STORAGE", "čítanie súborov", "read files", "Dateien lesen", "lire les fichiers", "leer archivos", "leggere i file"),
 ("WRITE_EXTERNAL_STORAGE", "zápis do súborov", "write files", "Dateien schreiben", "écrire des fichiers", "escribir archivos", "scrivere file"),
 ("READ_MEDIA_IMAGES", "fotky", "photos", "Fotos", "photos", "fotos", "foto"),
 ("READ_MEDIA_VIDEO", "videá", "videos", "Videos", "vidéos", "vídeos", "video"),
 ("READ_MEDIA_AUDIO", "hudba a zvuky", "music and audio", "Musik und Audio", "musique et audio", "música y audio", "musica e audio"),
 ("READ_MEDIA_VISUAL_USER_SELECTED", "vybrané fotky a videá", "selected photos and videos", "ausgewählte Fotos und Videos", "photos et vidéos sélectionnées", "fotos y vídeos seleccionados", "foto e video selezionati"),
 ("ACCESS_MEDIA_LOCATION", "miesto, kde vznikli fotky", "where photos were taken", "Aufnahmeort von Fotos", "lieu de prise des photos", "ubicación de las fotos", "luogo di scatto delle foto"),
 ("ACTIVITY_RECOGNITION", "pohybová aktivita", "physical activity", "körperliche Aktivität", "activité physique", "actividad física", "attività fisica"),
 ("BODY_SENSORS", "telesné senzory", "body sensors", "Körpersensoren", "capteurs corporels", "sensores corporales", "sensori corporei"),
 ("BLUETOOTH_CONNECT", "pripájanie Bluetooth zariadení", "connect Bluetooth devices", "Bluetooth-Geräte verbinden", "connecter des appareils Bluetooth", "conectar dispositivos Bluetooth", "connettere dispositivi Bluetooth"),
 ("BLUETOOTH_SCAN", "vyhľadávanie Bluetooth zariadení", "scan for Bluetooth devices", "nach Bluetooth-Geräten suchen", "rechercher des appareils Bluetooth", "buscar dispositivos Bluetooth", "cercare dispositivi Bluetooth"),
 ("NEARBY_WIFI_DEVICES", "zariadenia v okolí cez Wi-Fi", "nearby Wi-Fi devices", "WLAN-Geräte in der Nähe", "appareils Wi-Fi à proximité", "dispositivos Wi-Fi cercanos", "dispositivi Wi-Fi vicini"),
 ("ACCESS_LOCAL_NETWORK", "zariadenia v miestnej sieti", "devices on the local network", "Geräte im lokalen Netzwerk", "appareils du réseau local", "dispositivos de la red local", "dispositivi sulla rete locale"),
 ("POST_NOTIFICATIONS", "notifikácie", "notifications", "Benachrichtigungen", "notifications", "notificaciones", "notifiche"),
 ("MANAGE_EXTERNAL_STORAGE", "prístup ku všetkým súborom", "access all files", "Zugriff auf alle Dateien", "accès à tous les fichiers", "acceso a todos los archivos", "accesso a tutti i file"),
 ("SYSTEM_ALERT_WINDOW", "kreslenie cez iné aplikácie", "draw over other apps", "über anderen Apps einblenden", "afficher par-dessus d'autres apps", "mostrar sobre otras apps", "mostrare sopra altre app"),
 ("REQUEST_INSTALL_PACKAGES", "inštalovanie aplikácií", "install apps", "Apps installieren", "installer des apps", "instalar apps", "installare app"),
 ("PACKAGE_USAGE_STATS", "štatistiky používania aplikácií", "app usage statistics", "App-Nutzungsstatistiken", "statistiques d'utilisation des apps", "estadísticas de uso de apps", "statistiche di utilizzo delle app"),
 ("SCHEDULE_EXACT_ALARM", "presne načasované budíky", "exact alarms", "exakte Wecker", "alarmes exactes", "alarmas exactas", "sveglie esatte"),
 ("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", "beh na pozadí bez obmedzení", "unrestricted background activity", "uneingeschränkte Hintergrundaktivität", "activité en arrière-plan sans restriction", "actividad en segundo plano sin restricciones", "attività in background senza limiti"),
 ("QUERY_ALL_PACKAGES", "zoznam všetkých aplikácií", "list of all installed apps", "Liste aller installierten Apps", "liste de toutes les apps installées", "lista de todas las apps instaladas", "elenco di tutte le app installate"),
 ("RECEIVE_BOOT_COMPLETED", "spustenie po zapnutí telefónu", "start on device boot", "Start beim Einschalten", "démarrage à l'allumage", "inicio al encender el teléfono", "avvio all'accensione"),
 ("DETECT_SCREEN_CAPTURE", "zisťovanie snímok obrazovky", "detect screenshots", "Screenshots erkennen", "détecter les captures d'écran", "detectar capturas de pantalla", "rilevare gli screenshot"),
 ("DOWNLOAD_WITHOUT_NOTIFICATION", "sťahovanie bez upozornenia", "downloads without notification", "Downloads ohne Benachrichtigung", "téléchargements sans notification", "descargas sin notificación", "download senza notifica"),
 ("AD_ID", "reklamný identifikátor", "advertising ID", "Werbe-ID", "identifiant publicitaire", "ID de publicidad", "ID pubblicità"),
]
for row in P:
    s("perm_" + row[0], *row[1:])

# ── skupiny citlivých povolení ──
# SK je v 3. páde (po „prístup k …"), EN s členom; ostatné jazyky holé podstatné mená,
# lebo vety v DE/FR/ES/IT používajú dvojbodku („Zugriff auf: …") — vyhneš sa tým
# stiahnutým tvarom ako fr. „au", tal. „alla", šp. „al".
G = [
 ("location", "polohe", "location", "Standort", "localisation", "ubicación", "posizione"),
 ("contacts", "kontaktom", "contacts", "Kontakte", "contacts", "contactos", "contatti"),
 ("microphone", "mikrofónu", "the microphone", "Mikrofon", "micro", "micrófono", "microfono"),
 ("camera", "kamere", "the camera", "Kamera", "appareil photo", "cámara", "fotocamera"),
 ("sms", "SMS správam", "SMS messages", "SMS", "SMS", "SMS", "SMS"),
 ("call_log", "zoznamu hovorov", "the call log", "Anrufliste", "journal d'appels", "registro de llamadas", "registro chiamate"),
 ("calendar", "kalendáru", "the calendar", "Kalender", "agenda", "calendario", "calendario"),
 ("phone", "údajom o telefóne", "phone identity", "Telefonidentität", "identité du téléphone", "identidad del teléfono", "identità del telefono"),
 ("media", "fotkám a médiám", "photos and media", "Fotos und Medien", "photos et médias", "fotos y multimedia", "foto e contenuti multimediali"),
 ("activity", "pohybovej aktivite", "physical activity", "körperliche Aktivität", "activité physique", "actividad física", "attività fisica"),
]
for row in G:
    s("group_" + row[0], *row[1:])


def esc(v):
    v = v.replace("\\", "\\\\").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    v = v.replace("'", "\\'").replace('"', '\\"').replace("\n", "\\n")
    if v[:1] in ("@", "?"):
        v = "\\" + v
    return v


def main(res_dir):
    for i, lang in enumerate(LANGS):
        d = os.path.join(res_dir, FOLDER[lang])
        os.makedirs(d, exist_ok=True)
        lines = ['<?xml version="1.0" encoding="utf-8"?>',
                 '<!-- Generované skriptom gen_strings.py. Uprav skript, nie tento súbor. -->',
                 '<resources>']
        if lang == "en":
            lines.append('    <string name="app_name" translatable="false">Audit</string>')
        for key, vals in S.items():
            lines.append(f'    <string name="{key}">{esc(vals[i])}</string>')
        lines.append('</resources>')
        with open(os.path.join(d, "strings.xml"), "w", encoding="utf-8") as f:
            f.write("\n".join(lines) + "\n")
    print(len(S), "kľúčov ×", len(LANGS), "jazykov")


if __name__ == "__main__":
    main(sys.argv[1])
