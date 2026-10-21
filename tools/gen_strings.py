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
s("tile_all", "spolu", "total", "gesamt", "au total", "en total", "in totale")
s("tag_preinstalled", "PREDINŠTALOVANÁ", "PREINSTALLED", "VORINSTALLIERT", "PRÉINSTALLÉE", "PREINSTALADA", "PREINSTALLATA")
s("origin_title", "PÔVOD", "SOURCE", "HERKUNFT", "PROVENANCE", "ORIGEN", "PROVENIENZA")
s("origin_system",
  "prišla s telefónom (súčasť systému)",
  "came with the phone (part of the system)",
  "war auf dem Telefon vorinstalliert (Teil des Systems)",
  "fournie avec le téléphone (fait partie du système)",
  "venía con el teléfono (parte del sistema)",
  "fornita con il telefono (parte del sistema)")
s("origin_play", "z Obchodu Google Play", "from the Google Play Store", "aus dem Google Play Store", "depuis le Google Play Store", "de Google Play Store", "dal Google Play Store")
s("origin_manual",
  "nainštalovaná ručne mimo obchodu (súbor APK)",
  "installed manually outside a store (APK file)",
  "manuell außerhalb eines Stores installiert (APK-Datei)",
  "installée manuellement hors d'un store (fichier APK)",
  "instalada manualmente fuera de una tienda (archivo APK)",
  "installata manualmente fuori da uno store (file APK)")
s("origin_other", "z iného obchodu: %1$s", "from another store: %1$s", "aus einem anderen Store: %1$s", "depuis un autre store : %1$s", "de otra tienda: %1$s", "da un altro store: %1$s")
s("open_settings", "Otvoriť nastavenia aplikácie", "Open app settings", "App-Einstellungen öffnen", "Ouvrir les paramètres de l'app", "Abrir ajustes de la app", "Apri impostazioni dell'app")
s("how_to_remove", "Ako ju odstrániť", "How to remove it", "So entfernst du sie", "Comment la supprimer", "Cómo eliminarla", "Come rimuoverla")
s("remove_intro",
  "Systémovú aplikáciu často nejde odinštalovať v nastaveniach — najprv skús tlačidlo Vypnúť. Ak chýba, dá sa odstrániť pre tvojho používateľa z počítača cez ADB (ladenie USB). Je to vratné. Odstránenie nesprávnej systémovej služby môže narušiť funkcie telefónu; ak si nie si istý, čo appka robí, nechaj ju tak.",
  "A system app often cannot be uninstalled in Settings — try the Disable button first. If it is missing, you can remove it for your user from a computer via ADB (USB debugging). This is reversible. Removing the wrong system service can break phone features; if you are not sure what the app does, leave it alone.",
  "Eine System-App lässt sich in den Einstellungen oft nicht deinstallieren — versuch zuerst die Schaltfläche Deaktivieren. Fehlt sie, kannst du die App über ADB (USB-Debugging) vom Computer aus für deinen Nutzer entfernen. Das ist umkehrbar. Wer den falschen Systemdienst entfernt, kann Funktionen des Telefons beeinträchtigen; wenn du nicht sicher bist, was die App tut, lass sie in Ruhe.",
  "Une app système ne peut souvent pas être désinstallée dans les paramètres — essaie d'abord le bouton Désactiver. S'il manque, tu peux la supprimer pour ton utilisateur depuis un ordinateur via ADB (débogage USB). C'est réversible. Supprimer le mauvais service système peut casser des fonctions du téléphone ; si tu n'es pas sûr de ce que fait l'app, laisse-la.",
  "Una app del sistema a menudo no se puede desinstalar en los ajustes; prueba primero el botón Desactivar. Si no aparece, puedes eliminarla para tu usuario desde un ordenador mediante ADB (depuración USB). Es reversible. Eliminar el servicio del sistema equivocado puede romper funciones del teléfono; si no sabes con seguridad qué hace la app, déjala.",
  "Spesso un'app di sistema non si può disinstallare dalle impostazioni — prova prima il pulsante Disattiva. Se manca, puoi rimuoverla per il tuo utente da un computer tramite ADB (debug USB). È reversibile. Rimuovere il servizio di sistema sbagliato può compromettere funzioni del telefono; se non sei sicuro di cosa faccia l'app, lasciala stare.")
s("remove_cmd", "Odstrániť:", "Remove:", "Entfernen:", "Supprimer :", "Eliminar:", "Rimuovi:")
s("restore_cmd", "Vrátiť späť:", "Undo:", "Rückgängig:", "Annuler :", "Deshacer:", "Annulla:")
s("copy", "Kopírovať", "Copy", "Kopieren", "Copier", "Copiar", "Copia")
s("copied", "Skopírované", "Copied", "Kopiert", "Copié", "Copiado", "Copiato")
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
  "Fordert keinen Internetzugriff an — sie kann nirgendwohin Daten senden.",
  "Ne demande pas l'accès à Internet — elle ne peut rien envoyer nulle part.",
  "No solicita acceso a Internet: no tiene adónde enviar nada.",
  "Non richiede l'accesso a Internet: non può inviare nulla da nessuna parte.")
# %1$s = skupiny povolení, %2$s = názvy SDK
s("marketing_with_granted",
  "Má udelený prístup k %1$s a zároveň obsahuje reklamné/marketingové SDK (%2$s). Práve tu môžu citlivé údaje skončiť v reklamnej sieti.",
  "Has been granted access to %1$s and also contains advertising/marketing SDKs (%2$s). This is where sensitive data can end up in an ad network.",
  "Hat Zugriff auf %1$s und enthält zugleich Werbe-/Marketing-SDKs (%2$s). Genau hier können sensible Daten in einem Werbenetzwerk landen.",
  "A accès à %1$s et contient aussi des SDK publicitaires/marketing (%2$s). C'est ici que des données sensibles peuvent finir dans un réseau publicitaire.",
  "Tiene acceso a %1$s y además contiene SDK de publicidad/marketing (%2$s). Aquí es donde los datos sensibles pueden acabar en una red publicitaria.",
  "Ha accesso a %1$s e contiene anche SDK pubblicitari/di marketing (%2$s). È qui che i dati sensibili possono finire in una rete pubblicitaria.")
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
  "Enthält Analyse-SDKs (%1$s) und hat Zugriff auf %2$s. Die Analyse sendet diese Daten nicht von selbst, aber die App hat sie zur Verfügung — die Kombination verdient Aufmerksamkeit.",
  "Contient des SDK d'analyse (%1$s) et a accès à %2$s. L'analyse n'envoie pas ces données d'elle-même, mais l'app les a à disposition — la combinaison mérite attention.",
  "Contiene SDK de analítica (%1$s) y tiene acceso a %2$s. La analítica no envía estos datos por sí sola, pero la app los tiene a su alcance; la combinación merece atención.",
  "Contiene SDK di analisi (%1$s) e ha accesso a %2$s. L'analisi non invia questi dati da sola, ma l'app li ha a disposizione — la combinazione merita attenzione.")
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
  "Hat Zugriff auf %1$s, enthält aber keinen bekannten Tracker.",
  "A accès à %1$s, mais ne contient aucun traceur connu.",
  "Tiene acceso a %1$s, pero no contiene ningún rastreador conocido.",
  "Ha accesso a %1$s, ma non contiene tracker noti.")
s("pending",
  "Žiada aj prístup k %1$s, ten však zatiaľ udelený nemá. Ak mu ho povolíš, posúdenie sa zmení.",
  "It also requests access to %1$s, but has not been granted it. If you allow it, this assessment will change.",
  "Fordert auch Zugriff auf %1$s, hat ihn aber noch nicht erhalten. Wenn du ihn erlaubst, ändert sich die Bewertung.",
  "Demande aussi l'accès à %1$s, mais ne l'a pas encore obtenu. Si tu l'autorises, l'évaluation changera.",
  "También solicita acceso a %1$s, pero aún no lo tiene. Si lo permites, la valoración cambiará.",
  "Richiede anche l'accesso a %1$s, ma non l'ha ancora ottenuto. Se lo consenti, la valutazione cambierà.")

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
s("report_link", "Nahlásiť nesprávny nález", "Report an incorrect finding", "Falschen Befund melden", "Signaler un résultat erroné", "Informar de un resultado incorrecto", "Segnala un risultato errato")
s("report_note",
  "Otvorí GitHub v prehliadači (vyžaduje účet). Appka sama nič neodosiela.",
  "Opens GitHub in the browser (account required). The app itself sends nothing.",
  "Öffnet GitHub im Browser (Konto erforderlich). Die App selbst sendet nichts.",
  "Ouvre GitHub dans le navigateur (compte requis). L'app elle-même n'envoie rien.",
  "Abre GitHub en el navegador (requiere cuenta). La app no envía nada por sí misma.",
  "Apre GitHub nel browser (serve un account). L'app di per sé non invia nulla.")

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
s("tap_to_continue", "ťukni pre pokračovanie", "tap to continue", "Zum Fortfahren tippen", "Appuie pour continuer", "toca para continuar", "Tocca per continuare")
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
 ("WRITE_CALL_LOG", "úprava zoznamu hovorov", "modify call log", "Anrufliste ändern", "modifier le journal d'appels", "modificar registro de llamadas", "modificare il registro chiamate"),
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
 ("WRITE_SETTINGS", "meniť systémové nastavenia", "change system settings", "Systemeinstellungen ändern", "modifier les paramètres système", "cambiar ajustes del sistema", "modificare le impostazioni di sistema"),
 ("REQUEST_INSTALL_PACKAGES", "inštalovanie aplikácií", "install apps", "Apps installieren", "installer des apps", "instalar apps", "installare app"),
 ("PACKAGE_USAGE_STATS", "štatistiky používania aplikácií", "app usage statistics", "App-Nutzungsstatistiken", "statistiques d'utilisation des apps", "estadísticas de uso de apps", "statistiche di utilizzo delle app"),
 ("SCHEDULE_EXACT_ALARM", "presne načasované budíky", "exact alarms", "exakte Alarme", "alarmes exactes", "alarmas exactas", "allarmi esatti"),
 ("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", "beh na pozadí bez obmedzení", "unrestricted background activity", "uneingeschränkte Hintergrundaktivität", "activité en arrière-plan sans restriction", "actividad en segundo plano sin restricciones", "attività in background senza limiti"),
 ("QUERY_ALL_PACKAGES", "zoznam všetkých aplikácií", "list of all installed apps", "Liste aller installierten Apps", "liste de toutes les apps installées", "lista de todas las apps instaladas", "elenco di tutte le app installate"),
 ("RECEIVE_BOOT_COMPLETED", "spustenie po zapnutí telefónu", "start on device boot", "Start beim Einschalten", "démarrage à l'allumage", "inicio al encender el teléfono", "avvio all'accensione"),
 ("DETECT_SCREEN_CAPTURE", "zisťovanie snímok obrazovky", "detect screenshots", "Screenshots erkennen", "détecter les captures d'écran", "detectar capturas de pantalla", "rilevare gli screenshot"),
 ("DOWNLOAD_WITHOUT_NOTIFICATION", "sťahovanie bez upozornenia", "downloads without notification", "Downloads ohne Benachrichtigung", "téléchargements sans notification", "descargas sin notificación", "download senza notifica"),
 ("AD_ID", "reklamný identifikátor", "advertising ID", "Werbe-ID", "identifiant publicitaire", "ID de publicidad", "ID pubblicità"),

 # ── služby na pozadí ──
 # Samotné povolenie neudeľuje prístup k mikrofónu/kamere/polohe; znamená, že
 # appka smie takú službu držať spustenú, aj keď ju nemáš otvorenú. Preto je
 # text opisný („služba na pozadí: …"), nie „mikrofón na pozadí".
 ("FOREGROUND_SERVICE_MICROPHONE", "služba na pozadí: mikrofón", "background service: microphone", "Hintergrunddienst: Mikrofon", "service en arrière-plan : micro", "servicio en segundo plano: micrófono", "servizio in background: microfono"),
 ("FOREGROUND_SERVICE_CAMERA", "služba na pozadí: kamera", "background service: camera", "Hintergrunddienst: Kamera", "service en arrière-plan : appareil photo", "servicio en segundo plano: cámara", "servizio in background: fotocamera"),
 ("FOREGROUND_SERVICE_LOCATION", "služba na pozadí: poloha", "background service: location", "Hintergrunddienst: Standort", "service en arrière-plan : position", "servicio en segundo plano: ubicación", "servizio in background: posizione"),
 ("FOREGROUND_SERVICE_MEDIA_PROJECTION", "služba na pozadí: snímanie obrazovky", "background service: screen capture", "Hintergrunddienst: Bildschirmaufnahme", "service en arrière-plan : capture d'écran", "servicio en segundo plano: captura de pantalla", "servizio in background: acquisizione schermo"),

 # ── obrazovka a hovory ──
 ("DETECT_SCREEN_RECORDING", "zisťovanie nahrávania obrazovky", "detect screen recording", "Bildschirmaufnahme erkennen", "détection de l'enregistrement de l'écran", "detectar grabación de pantalla", "rilevamento della registrazione dello schermo"),
 ("MANAGE_OWN_CALLS", "hovory cez vlastnú aplikáciu", "calls through its own app", "Anrufe über die eigene App", "appels via sa propre app", "llamadas a través de su propia app", "chiamate tramite la propria app"),

 # ── Google Play ──
 ("BIND_GET_INSTALL_REFERRER_SERVICE", "zisťovanie, odkiaľ si appku nainštaloval", "where you installed the app from", "woher du die App installiert hast", "d'où tu as installé l'app", "desde dónde instalaste la app", "da dove hai installato l'app"),
 ("BILLING", "nákupy v aplikácii", "in-app purchases", "In-App-Käufe", "achats dans l'app", "compras dentro de la app", "acquisti in-app"),

 # ── Privacy Sandbox (reklamné rozhrania Androidu) ──
 ("ACCESS_ADSERVICES_TOPICS", "tvoje reklamné záujmy odvodené Androidom", "your ad interests inferred by Android", "deine von Android abgeleiteten Werbeinteressen", "tes centres d'intérêt publicitaires déduits par Android", "tus intereses publicitarios deducidos por Android", "i tuoi interessi pubblicitari dedotti da Android"),
 ("ACCESS_ADSERVICES_AD_ID", "reklamný identifikátor cez Privacy Sandbox", "advertising ID via Privacy Sandbox", "Werbe-ID über Privacy Sandbox", "identifiant publicitaire via Privacy Sandbox", "ID de publicidad mediante Privacy Sandbox", "ID pubblicità tramite Privacy Sandbox"),
 ("ACCESS_ADSERVICES_ATTRIBUTION", "meranie účinnosti reklamy", "ad attribution measurement", "Messung der Werbewirkung", "mesure d'attribution publicitaire", "medición de atribución publicitaria", "misurazione dell'attribuzione pubblicitaria"),
]
for row in P:
    s("perm_" + row[0], *row[1:])

# Zlúčený riadok namiesto desiatok povolení výrobcov na push správy a na odznak
# s počtom na ikonke. Všetky znamenajú to isté, preto sa ukazujú ako jedna vec.
s("perm_notify_badge",
  "upozornenia a odznak s počtom na ikonke",
  "notifications and the unread count on the icon",
  "Benachrichtigungen und Zähler am App-Symbol",
  "notifications et compteur sur l'icône",
  "notificaciones y contador en el icono",
  "notifiche e contatore sull'icona")

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


# ── technické povolenia Androidu: ľudský popis (kľúč = tperm_ + názov) ──
# Úroveň (bežné / len pre systém) sa NEPÍŠE sem — appka ju číta za behu
# priamo z telefónu (PermissionInfo), takže sedí na každom zariadení.
# Zoznam overený na vivo V50 Lite a Redmi (pm list permissions -f), 21. 10. 2026.
T = [
 ("INTERNET", "prístup na internet", "internet access", "Internetzugriff", "accès à Internet", "acceso a Internet", "accesso a Internet"),
 ("ACCESS_NETWORK_STATE", "zistiť stav pripojenia k sieti", "see network connections", "Netzwerkverbindungen sehen", "voir les connexions réseau", "ver las conexiones de red", "vedere le connessioni di rete"),
 ("ACCESS_WIFI_STATE", "zistiť stav Wi-Fi", "see Wi-Fi state", "WLAN-Status sehen", "voir l'état du Wi-Fi", "ver el estado del Wi-Fi", "vedere lo stato del Wi-Fi"),
 ("CHANGE_NETWORK_STATE", "meniť pripojenie k sieti", "change network connectivity", "Netzwerkverbindung ändern", "modifier la connectivité réseau", "cambiar la conectividad de red", "modificare la connettività di rete"),
 ("CHANGE_WIFI_STATE", "zapínať a meniť Wi-Fi", "turn Wi-Fi on and off", "WLAN ein- und ausschalten", "activer et désactiver le Wi-Fi", "activar y desactivar el Wi-Fi", "attivare e disattivare il Wi-Fi"),
 ("WAKE_LOCK", "nedovoliť telefónu uspať sa", "keep the phone awake", "Ruhezustand verhindern", "empêcher la mise en veille", "evitar que el teléfono entre en reposo", "impedire la sospensione del telefono"),
 ("VIBRATE", "vibrovať", "vibrate", "vibrieren", "faire vibrer", "vibrar", "far vibrare"),
 ("FOREGROUND_SERVICE", "bežať v popredí s trvalou notifikáciou", "run in the foreground", "im Vordergrund laufen", "s'exécuter au premier plan", "ejecutarse en primer plano", "essere eseguita in primo piano"),
 ("KILL_BACKGROUND_PROCESSES", "zatvárať iné appky na pozadí", "close other apps in the background", "andere Apps im Hintergrund schließen", "fermer d'autres apps en arrière-plan", "cerrar otras apps en segundo plano", "chiudere altre app in background"),
 ("RESTART_PACKAGES", "zatvárať iné appky (zastarané)", "close other apps (deprecated)", "andere Apps schließen (veraltet)", "fermer d'autres apps (obsolète)", "cerrar otras apps (obsoleto)", "chiudere altre app (obsoleto)"),
 ("GET_TASKS", "zoznam spustených appiek (dnes vidí len svoje)", "list running apps (now only its own)", "laufende Apps auflisten (heute nur die eigenen)", "lister les apps en cours (aujourd'hui seulement les siennes)", "listar apps en ejecución (hoy solo las propias)", "elencare le app in esecuzione (oggi solo le proprie)"),
 ("SET_WALLPAPER", "nastaviť tapetu", "set the wallpaper", "Hintergrundbild festlegen", "définir le fond d'écran", "establecer el fondo de pantalla", "impostare lo sfondo"),
 ("EXPAND_STATUS_BAR", "rozbaliť panel notifikácií", "expand the notification panel", "Benachrichtigungsleiste aufklappen", "dérouler le volet des notifications", "desplegar el panel de notificaciones", "espandere il pannello delle notifiche"),
 ("DISABLE_KEYGUARD", "vypnúť zámok obrazovky, ak nie je zabezpečený", "disable the screen lock if it is not secure", "Displaysperre deaktivieren, wenn sie nicht gesichert ist", "désactiver le verrouillage s'il n'est pas sécurisé", "desactivar el bloqueo de pantalla si no es seguro", "disattivare il blocco schermo se non è protetto"),
 ("USE_BIOMETRIC", "overiť ťa odtlačkom prsta alebo tvárou", "verify you with fingerprint or face", "dich per Fingerabdruck oder Gesicht prüfen", "te vérifier par empreinte ou visage", "verificarte con huella o rostro", "verificarti con impronta o volto"),
 ("USE_FINGERPRINT", "overiť ťa odtlačkom prsta (zastarané)", "verify you with fingerprint (deprecated)", "dich per Fingerabdruck prüfen (veraltet)", "te vérifier par empreinte (obsolète)", "verificarte con huella (obsoleto)", "verificarti con impronta (obsoleto)"),
 ("ACCESS_NOTIFICATION_POLICY", "meniť režim Nerušiť", "change Do Not Disturb", "„Nicht stören\" ändern", "modifier « Ne pas déranger »", "cambiar «No molestar»", "modificare «Non disturbare»"),
 ("MANAGE_ACCOUNTS", "spravovať účty v telefóne (staré Androidy)", "manage accounts (old Android)", "Konten verwalten (altes Android)", "gérer les comptes (ancien Android)", "gestionar cuentas (Android antiguo)", "gestire gli account (vecchio Android)"),
 ("AUTHENTICATE_ACCOUNTS", "vytvárať účty (staré Androidy)", "create accounts (old Android)", "Konten erstellen (altes Android)", "créer des comptes (ancien Android)", "crear cuentas (Android antiguo)", "creare account (vecchio Android)"),
 ("NFC", "používať NFC", "use NFC", "NFC verwenden", "utiliser le NFC", "usar NFC", "usare l'NFC"),
 ("BLUETOOTH", "používať Bluetooth (staré Androidy)", "use Bluetooth (old Android)", "Bluetooth verwenden (altes Android)", "utiliser le Bluetooth (ancien Android)", "usar Bluetooth (Android antiguo)", "usare il Bluetooth (vecchio Android)"),
 ("BLUETOOTH_ADMIN", "nastavovať Bluetooth (staré Androidy)", "configure Bluetooth (old Android)", "Bluetooth einrichten (altes Android)", "configurer le Bluetooth (ancien Android)", "configurar Bluetooth (Android antiguo)", "configurare il Bluetooth (vecchio Android)"),
 ("TRANSMIT_IR", "vysielať infračerveným lúčom (diaľkové ovládanie)", "transmit infrared (remote control)", "Infrarot senden (Fernbedienung)", "émettre en infrarouge (télécommande)", "emitir infrarrojos (mando a distancia)", "trasmettere a infrarossi (telecomando)"),
 ("USE_FULL_SCREEN_INTENT", "upozornenie cez celú obrazovku (napr. hovor)", "full-screen alerts (e.g. calls)", "Vollbild-Hinweise (z. B. Anrufe)", "alertes plein écran (ex. appels)", "alertas a pantalla completa (p. ej. llamadas)", "avvisi a schermo intero (es. chiamate)"),
 ("INSTALL_PACKAGES", "inštalovať appky bez opýtania", "install apps without asking", "Apps ohne Nachfrage installieren", "installer des apps sans demander", "instalar apps sin preguntar", "installare app senza chiedere"),
 ("DELETE_PACKAGES", "odinštalovať appky bez opýtania", "uninstall apps without asking", "Apps ohne Nachfrage deinstallieren", "désinstaller des apps sans demander", "desinstalar apps sin preguntar", "disinstallare app senza chiedere"),
 ("INSTALL_SELF_UPDATES", "aktualizovať sa sama bez opýtania", "update itself without asking", "sich ohne Nachfrage selbst aktualisieren", "se mettre à jour sans demander", "actualizarse sin preguntar", "aggiornarsi senza chiedere"),
 ("FORCE_STOP_PACKAGES", "násilne ukončiť iné appky", "force-stop other apps", "andere Apps zwangsweise beenden", "forcer l'arrêt d'autres apps", "forzar la detención de otras apps", "forzare l'arresto di altre app"),
 ("CLEAR_APP_USER_DATA", "vymazať dáta iných appiek", "erase other apps' data", "Daten anderer Apps löschen", "effacer les données d'autres apps", "borrar datos de otras apps", "cancellare i dati di altre app"),
 ("WRITE_SECURE_SETTINGS", "meniť chránené systémové nastavenia", "change protected system settings", "geschützte Systemeinstellungen ändern", "modifier les paramètres système protégés", "cambiar ajustes protegidos del sistema", "modificare le impostazioni di sistema protette"),
 ("MANAGE_USERS", "spravovať používateľov telefónu", "manage the phone's users", "Nutzer des Telefons verwalten", "gérer les utilisateurs du téléphone", "gestionar los usuarios del teléfono", "gestire gli utenti del telefono"),
 ("INTERACT_ACROSS_USERS", "pristupovať k iným používateľom a profilom", "access other users and profiles", "auf andere Nutzer und Profile zugreifen", "accéder aux autres utilisateurs et profils", "acceder a otros usuarios y perfiles", "accedere ad altri utenti e profili"),
 ("INTERACT_ACROSS_USERS_FULL", "plný prístup k iným používateľom a profilom", "full access to other users and profiles", "voller Zugriff auf andere Nutzer und Profile", "accès complet aux autres utilisateurs et profils", "acceso total a otros usuarios y perfiles", "accesso completo ad altri utenti e profili"),
 ("READ_LOGS", "čítať systémové záznamy (logy)", "read system logs", "Systemprotokolle lesen", "lire les journaux système", "leer los registros del sistema", "leggere i log di sistema"),
 ("DUMP", "čítať diagnostické výpisy systému", "read system diagnostic dumps", "Systemdiagnose-Ausgaben lesen", "lire les diagnostics du système", "leer volcados de diagnóstico del sistema", "leggere i dump diagnostici del sistema"),
 ("REAL_GET_TASKS", "vidieť, ktoré appky práve bežia", "see which apps are running", "sehen, welche Apps gerade laufen", "voir quelles apps sont en cours", "ver qué apps se están ejecutando", "vedere quali app sono in esecuzione"),
 ("READ_PRIVILEGED_PHONE_STATE", "čítať trvalé identifikátory telefónu (IMEI, sériové číslo)", "read permanent phone IDs (IMEI, serial number)", "dauerhafte Gerätekennungen lesen (IMEI, Seriennummer)", "lire les identifiants permanents (IMEI, n° de série)", "leer identificadores permanentes (IMEI, número de serie)", "leggere gli identificativi permanenti (IMEI, numero di serie)"),
 ("MODIFY_PHONE_STATE", "ovládať hovory a mobilnú sieť", "control calls and the mobile network", "Anrufe und Mobilfunknetz steuern", "contrôler les appels et le réseau mobile", "controlar llamadas y la red móvil", "controllare chiamate e rete mobile"),
 ("CALL_PRIVILEGED", "volať na akékoľvek číslo bez potvrdenia", "call any number without confirmation", "jede Nummer ohne Bestätigung anrufen", "appeler n'importe quel numéro sans confirmation", "llamar a cualquier número sin confirmación", "chiamare qualsiasi numero senza conferma"),
 ("STATUS_BAR", "ovládať stavový riadok", "control the status bar", "Statusleiste steuern", "contrôler la barre d'état", "controlar la barra de estado", "controllare la barra di stato"),
 ("INTERNAL_SYSTEM_WINDOW", "zobrazovať systémové okná", "show system windows", "Systemfenster anzeigen", "afficher des fenêtres système", "mostrar ventanas del sistema", "mostrare finestre di sistema"),
 ("ACCESS_SURFACE_FLINGER", "nízkoúrovňový prístup ku grafike obrazovky", "low-level access to screen graphics", "Low-Level-Zugriff auf die Bildschirmgrafik", "accès bas niveau aux graphismes de l'écran", "acceso de bajo nivel a los gráficos de pantalla", "accesso di basso livello alla grafica dello schermo"),
 ("READ_FRAME_BUFFER", "snímať obsah obrazovky", "capture the screen contents", "Bildschirminhalt erfassen", "capturer le contenu de l'écran", "capturar el contenido de la pantalla", "catturare il contenuto dello schermo"),
 ("CAPTURE_AUDIO_OUTPUT", "nahrávať zvuk, ktorý telefón prehráva", "record audio the phone is playing", "vom Telefon wiedergegebenen Ton aufnehmen", "enregistrer le son joué par le téléphone", "grabar el audio que reproduce el teléfono", "registrare l'audio riprodotto dal telefono"),
 ("MODIFY_AUDIO_ROUTING", "presmerovať zvuk", "reroute audio", "Audio umleiten", "rediriger le son", "redirigir el audio", "reindirizzare l'audio"),
 ("MOUNT_UNMOUNT_FILESYSTEMS", "pripájať a odpájať úložiská", "mount and unmount storage", "Speicher einbinden und trennen", "monter et démonter le stockage", "montar y desmontar almacenamiento", "montare e smontare l'archiviazione"),
 ("CHANGE_CONFIGURATION", "meniť jazyk, veľkosť písma a podobné nastavenia", "change language, font size and similar settings", "Sprache, Schriftgröße u. Ä. ändern", "modifier la langue, la taille du texte, etc.", "cambiar idioma, tamaño de letra y similares", "modificare lingua, dimensione del testo e simili"),
 ("START_ACTIVITIES_FROM_BACKGROUND", "otvárať obrazovky z pozadia", "open screens from the background", "Bildschirme aus dem Hintergrund öffnen", "ouvrir des écrans depuis l'arrière-plan", "abrir pantallas desde segundo plano", "aprire schermate dal background"),
 ("MANAGE_ACTIVITY_STACKS", "spravovať okná iných appiek", "manage other apps' windows", "Fenster anderer Apps verwalten", "gérer les fenêtres d'autres apps", "gestionar ventanas de otras apps", "gestire le finestre di altre app"),
 ("MANAGE_APP_TOKENS", "spravovať okná appiek", "manage app windows", "App-Fenster verwalten", "gérer les fenêtres des apps", "gestionar ventanas de apps", "gestire le finestre delle app"),
 ("REMOVE_TASKS", "zatvárať appky v zozname posledných", "close apps in recents", "Apps in „Zuletzt verwendet\" schließen", "fermer des apps dans les récentes", "cerrar apps en recientes", "chiudere app nelle recenti"),
 ("BROADCAST_CLOSE_SYSTEM_DIALOGS", "zatvárať systémové okná a ponuky", "close system dialogs and menus", "Systemdialoge und -menüs schließen", "fermer les boîtes de dialogue système", "cerrar diálogos y menús del sistema", "chiudere finestre e menu di sistema"),
 ("ACCESS_KEYGUARD_SECURE_STORAGE", "pristupovať k zabezpečeniu zámku obrazovky", "access screen-lock security storage", "auf den Sicherheitsspeicher der Displaysperre zugreifen", "accéder au stockage sécurisé du verrouillage", "acceder al almacén seguro del bloqueo", "accedere all'archivio sicuro del blocco schermo"),
 ("CONNECTIVITY_INTERNAL", "spravovať sieťové pripojenia systému", "manage system network connections", "Netzwerkverbindungen des Systems verwalten", "gérer les connexions réseau du système", "gestionar las conexiones de red del sistema", "gestire le connessioni di rete del sistema"),
 ("SET_PREFERRED_APPLICATIONS", "meniť predvolené appky", "change default apps", "Standard-Apps ändern", "modifier les apps par défaut", "cambiar las apps predeterminadas", "modificare le app predefinite"),
 ("CHANGE_OVERLAY_PACKAGES", "meniť vzhľad systému (témy)", "change the system look (themes)", "Systemoptik ändern (Designs)", "modifier l'apparence du système (thèmes)", "cambiar el aspecto del sistema (temas)", "modificare l'aspetto del sistema (temi)"),
 ("MODIFY_DAY_NIGHT_MODE", "prepínať tmavý režim", "switch dark mode", "Dunkelmodus umschalten", "basculer le mode sombre", "cambiar el modo oscuro", "attivare/disattivare la modalità scura"),
 ("MANAGE_LOW_POWER_STANDBY", "meniť úsporný režim v pohotovosti", "change low-power standby", "Energiespar-Standby ändern", "modifier la veille basse consommation", "cambiar el modo de espera de bajo consumo", "modificare lo standby a basso consumo"),
 ("SET_WALLPAPER_COMPONENT", "nastaviť živú tapetu", "set a live wallpaper", "Live-Hintergrund festlegen", "définir un fond d'écran animé", "establecer un fondo animado", "impostare uno sfondo animato"),
 ("BIND_WALLPAPER", "byť živou tapetou", "act as a live wallpaper", "als Live-Hintergrund dienen", "servir de fond d'écran animé", "funcionar como fondo animado", "fungere da sfondo animato"),
 ("ACCESS_CACHE_FILESYSTEM", "pristupovať k systémovej vyrovnávacej pamäti", "access the system cache", "auf den System-Cache zugreifen", "accéder au cache système", "acceder a la caché del sistema", "accedere alla cache di sistema"),
 ("GRANT_RUNTIME_PERMISSIONS", "udeľovať povolenia iným appkám", "grant permissions to other apps", "anderen Apps Berechtigungen erteilen", "accorder des autorisations à d'autres apps", "conceder permisos a otras apps", "concedere autorizzazioni ad altre app"),
 ("REVOKE_RUNTIME_PERMISSIONS", "odoberať povolenia iným appkám", "revoke permissions from other apps", "anderen Apps Berechtigungen entziehen", "retirer des autorisations à d'autres apps", "revocar permisos a otras apps", "revocare autorizzazioni ad altre app"),
 ("MANAGE_APP_OPS_MODES", "meniť, čo smú iné appky robiť", "change what other apps may do", "ändern, was andere Apps dürfen", "modifier ce que les autres apps peuvent faire", "cambiar lo que pueden hacer otras apps", "modificare cosa possono fare altre app"),
 ("GET_ACCOUNTS_PRIVILEGED", "vidieť všetky účty v telefóne", "see all accounts on the phone", "alle Konten auf dem Telefon sehen", "voir tous les comptes du téléphone", "ver todas las cuentas del teléfono", "vedere tutti gli account del telefono"),
 ("LOCATION_HARDWARE", "priamy prístup k hardvéru polohy", "direct access to location hardware", "direkter Zugriff auf die Standort-Hardware", "accès direct au matériel de localisation", "acceso directo al hardware de ubicación", "accesso diretto all'hardware di posizione"),
]
for row in T:
    s("tperm_" + row[0], *row[1:])

# ── v1.5: technický zoznam ──
s("sys_only", "len pre systém", "system only", "nur System", "système uniquement", "solo sistema", "solo sistema")
# %1$s = predpona výrobcu (com.vivo), %2$d = počet, %3$d = z toho chránené podpisom
s("vendor_group",
  "%1$s · povolenia: %2$d (chránené podpisom: %3$d)",
  "%1$s · permissions: %2$d (signature-protected: %3$d)",
  "%1$s · Berechtigungen: %2$d (signaturgeschützt: %3$d)",
  "%1$s · autorisations : %2$d (protégées par signature : %3$d)",
  "%1$s · permisos: %2$d (protegidos por firma: %3$d)",
  "%1$s · autorizzazioni: %2$d (protette da firma: %3$d)")
s("vendor_group_plain",
  "%1$s · povolenia: %2$d",
  "%1$s · permissions: %2$d",
  "%1$s · Berechtigungen: %2$d",
  "%1$s · autorisations : %2$d",
  "%1$s · permisos: %2$d",
  "%1$s · autorizzazioni: %2$d")
s("sig_note",
  "Povolenie chránené podpisom dostane len systém alebo appka od toho istého autora — cudzia appka nie. Popisy výrobcov k ich vlastným povoleniam nie sú verejné, preto ich neprekladáme.",
  "A signature-protected permission is only granted to the system or to apps by the same author — never to a third-party app. Vendors don't publish what their own permissions do, so we don't guess.",
  "Eine signaturgeschützte Berechtigung erhält nur das System oder eine App desselben Herstellers — nie eine fremde App. Hersteller veröffentlichen nicht, was ihre eigenen Berechtigungen tun, daher raten wir nicht.",
  "Une autorisation protégée par signature n'est accordée qu'au système ou aux apps du même auteur — jamais à une app tierce. Les fabricants ne publient pas le rôle de leurs autorisations, donc nous ne devinons pas.",
  "Un permiso protegido por firma solo se concede al sistema o a apps del mismo autor, nunca a una app ajena. Los fabricantes no publican qué hacen sus propios permisos, así que no adivinamos.",
  "Un'autorizzazione protetta da firma viene concessa solo al sistema o ad app dello stesso autore — mai a un'app di terzi. I produttori non pubblicano cosa fanno le proprie autorizzazioni, quindi non tiriamo a indovinare.")

# ── v1.5: reklamy z aukcie (AdFection, Citizen Lab 2026) ──
s("rtb_note",
  "Reklamy v appke sa predávajú v aukcii v reálnom čase. Tento kanál bol v roku 2026 zneužitý aj na doručenie spyware bez kliknutia (Citizen Lab). Samotná knižnica nie je dôkaz problému, ale zväčšuje plochu útoku.",
  "Ads in this app are sold in real-time auctions. In 2026 this channel was also abused to deliver spyware without a click (Citizen Lab). The library alone is not proof of a problem, but it widens the attack surface.",
  "Werbung in dieser App wird in Echtzeit-Auktionen verkauft. 2026 wurde dieser Kanal auch missbraucht, um Spyware ohne Klick auszuliefern (Citizen Lab). Die Bibliothek allein beweist kein Problem, vergrößert aber die Angriffsfläche.",
  "Les pubs de cette app sont vendues aux enchères en temps réel. En 2026, ce canal a aussi servi à livrer un logiciel espion sans clic (Citizen Lab). La bibliothèque seule ne prouve pas un problème, mais elle élargit la surface d'attaque.",
  "Los anuncios de esta app se venden en subastas en tiempo real. En 2026 este canal también se usó para entregar spyware sin un clic (Citizen Lab). La biblioteca por sí sola no prueba un problema, pero amplía la superficie de ataque.",
  "Gli annunci di questa app sono venduti in aste in tempo reale. Nel 2026 questo canale è stato usato anche per distribuire spyware senza clic (Citizen Lab). La libreria da sola non prova un problema, ma amplia la superficie di attacco.")

# ── v1.5: Súkromné DNS v Nastaveniach ──
s("dns_title", "SÚKROMNÉ DNS", "PRIVATE DNS", "PRIVATES DNS", "DNS PRIVÉ", "DNS PRIVADO", "DNS PRIVATO")
s("dns_on", "Zapnuté: %1$s", "On: %1$s", "An: %1$s", "Activé : %1$s", "Activado: %1$s", "Attivo: %1$s")
s("dns_auto",
  "Automatický režim — šifruje dopyty, ale reklamy neblokuje.",
  "Automatic mode — encrypts lookups but does not block ads.",
  "Automatischer Modus — verschlüsselt Anfragen, blockiert aber keine Werbung.",
  "Mode automatique — chiffre les requêtes mais ne bloque pas les pubs.",
  "Modo automático: cifra las consultas, pero no bloquea anuncios.",
  "Modalità automatica — cifra le richieste ma non blocca gli annunci.")
s("dns_off",
  "Neaktívne — vypnuté, alebo automatický režim, ktorý tvoja sieť práve nepodporuje.",
  "Not active — off, or automatic mode that your current network doesn't support.",
  "Nicht aktiv — aus, oder automatischer Modus, den dein aktuelles Netz nicht unterstützt.",
  "Inactif — désactivé, ou mode automatique non pris en charge par ton réseau actuel.",
  "Inactivo: desactivado, o modo automático que tu red actual no admite.",
  "Non attivo — disattivato, oppure modalità automatica non supportata dalla rete attuale.")
s("dns_unknown",
  "Stav sa nedá zistiť — telefón nie je pripojený k sieti.",
  "Can't tell — the phone is not connected to a network.",
  "Nicht feststellbar — das Telefon ist nicht mit einem Netz verbunden.",
  "Impossible à savoir — le téléphone n'est pas connecté à un réseau.",
  "No se puede saber: el teléfono no está conectado a ninguna red.",
  "Impossibile saperlo — il telefono non è connesso a una rete.")
s("dns_guide",
  "Blokovanie reklám v celom telefóne: Nastavenia → Sieť (alebo Pripojenia) → Súkromné DNS → Názov hostiteľa poskytovateľa → vlož adresu nižšie. Platí pre všetky appky aj prehliadač.",
  "Block ads across the whole phone: Settings → Network (or Connections) → Private DNS → Private DNS provider hostname → paste the address below. Works for all apps and the browser.",
  "Werbung im ganzen Telefon blockieren: Einstellungen → Netzwerk (oder Verbindungen) → Privates DNS → Hostname des Anbieters → Adresse unten einfügen. Gilt für alle Apps und den Browser.",
  "Bloquer les pubs dans tout le téléphone : Paramètres → Réseau (ou Connexions) → DNS privé → Nom d'hôte du fournisseur → colle l'adresse ci-dessous. Vaut pour toutes les apps et le navigateur.",
  "Bloquear anuncios en todo el teléfono: Ajustes → Red (o Conexiones) → DNS privado → Nombre de host del proveedor → pega la dirección de abajo. Funciona en todas las apps y en el navegador.",
  "Bloccare gli annunci in tutto il telefono: Impostazioni → Rete (o Connessioni) → DNS privato → Nome host del provider → incolla l'indirizzo qui sotto. Vale per tutte le app e per il browser.")
s("dns_tradeoff",
  "Pozor: služba AdGuard potom vidí, aké domény telefón navštevuje — namiesto tvojho operátora. Nie je to stopercentná ochrana.",
  "Note: AdGuard then sees which domains the phone visits — instead of your mobile operator. It is not complete protection.",
  "Achtung: AdGuard sieht dann, welche Domains das Telefon aufruft — statt deines Netzbetreibers. Es ist kein vollständiger Schutz.",
  "Attention : AdGuard voit alors quels domaines le téléphone consulte — à la place de ton opérateur. Ce n'est pas une protection totale.",
  "Ojo: AdGuard verá entonces qué dominios visita el teléfono, en lugar de tu operador. No es una protección completa.",
  "Attenzione: AdGuard vedrà quali domini visita il telefono — al posto del tuo operatore. Non è una protezione completa.")
s("dns_open", "Otvoriť nastavenia siete", "Open network settings", "Netzwerkeinstellungen öffnen", "Ouvrir les paramètres réseau", "Abrir ajustes de red", "Apri impostazioni di rete")
s("dns_recheck", "Skontrolovať znova", "Check again", "Erneut prüfen", "Vérifier à nouveau", "Comprobar de nuevo", "Controlla di nuovo")


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
