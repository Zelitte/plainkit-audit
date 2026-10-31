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
s("hide_tech", "skryť technické povolenia", "hide technical permissions", "technische Berechtigungen ausblenden", "masquer les autorisations techniques", "ocultar permisos técnicos", "nascondi autorizzazioni tecniche")

# ── úrovne ──
s("level_alert", "POZOR", "WARNING", "WARNUNG", "ATTENTION", "ATENCIÓN", "ATTENZIONE")
s("level_warn", "VŠIMNI SI", "NOTICE", "HINWEIS", "À NOTER", "AVISO", "NOTA")
s("level_info", "INFO", "INFO", "INFO", "INFO", "INFO", "INFO")
s("level_ok", "OK", "OK", "OK", "OK", "OK", "OK")

# ── nálezy ──
s("no_internet",
  "Nežiada prístup na internet — sama sa naň pripojiť nemôže.",
  "Does not request internet access — it cannot connect to the internet on its own.",
  "Fordert keinen Internetzugriff an — sie kann sich nicht selbst mit dem Internet verbinden.",
  "Ne demande pas l'accès à Internet — elle ne peut pas s'y connecter elle-même.",
  "No solicita acceso a Internet: no puede conectarse por sí misma.",
  "Non richiede l'accesso a Internet: non può connettersi da sola.")
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
 ("files", "všetkým súborom", "all files", "alle Dateien", "tous les fichiers", "todos los archivos", "tutti i file"),
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


# ── v1.6: presnosť ──
s("mark_unused", "na tomto Androide sa nepoužíva", "not used on this Android", "auf diesem Android ohne Wirkung", "sans effet sur cet Android", "sin efecto en este Android", "senza effetto su questo Android")
s("no_code",
  "Kód tejto appky sa nedá prečítať (je mimo súboru APK alebo zašifrovaný) — nevieme povedať, či obsahuje sledovacie knižnice.",
  "This app's code cannot be read (it is stored outside the APK or encrypted) — we can't tell whether it contains tracking libraries.",
  "Der Code dieser App ist nicht lesbar (außerhalb der APK gespeichert oder verschlüsselt) — wir können nicht sagen, ob er Tracking-Bibliotheken enthält.",
  "Le code de cette app est illisible (stocké hors de l'APK ou chiffré) — impossible de dire s'il contient des bibliothèques de pistage.",
  "El código de esta app no se puede leer (está fuera del APK o cifrado): no podemos saber si contiene bibliotecas de rastreo.",
  "Il codice di questa app non è leggibile (è fuori dall'APK o cifrato) — non possiamo dire se contiene librerie di tracciamento.")
s("js_code",
  "Časť kódu tejto appky je v JavaScripte (React Native, Cordova či Capacitor). Audit ho nečíta — sledovanie napísané v ňom by tu nebolo vidieť.",
  "Part of this app's code is in JavaScript (React Native, Cordova or Capacitor). The audit does not read it — tracking written in it would not show up here.",
  "Ein Teil des Codes dieser App ist in JavaScript (React Native, Cordova oder Capacitor). Den liest der Audit nicht — darin geschriebenes Tracking wäre hier nicht sichtbar.",
  "Une partie du code de cette app est en JavaScript (React Native, Cordova ou Capacitor). L'audit ne le lit pas — un pistage écrit dedans n'apparaîtrait pas ici.",
  "Parte del código de esta app está en JavaScript (React Native, Cordova o Capacitor). La auditoría no lo lee: un rastreo escrito en él no aparecería aquí.",
  "Parte del codice di questa app è in JavaScript (React Native, Cordova o Capacitor). L'audit non lo legge — un tracciamento scritto lì non comparirebbe qui.")
s("summary_no_code", "kód nečitateľný", "code unreadable", "Code nicht lesbar", "code illisible", "código ilegible", "codice illeggibile")
s("shared_title", "ZDIEĽA POVOLENIA A DÁTA S", "SHARES PERMISSIONS AND DATA WITH", "TEILT BERECHTIGUNGEN UND DATEN MIT", "PARTAGE AUTORISATIONS ET DONNÉES AVEC", "COMPARTE PERMISOS Y DATOS CON", "CONDIVIDE AUTORIZZAZIONI E DATI CON")
s("shared_net",
  "Sama nežiada prístup na internet, ale zdieľa povolenia a dáta s appkou %1$s, ktorá ho má.",
  "Does not request internet access itself, but shares permissions and data with %1$s, which has it.",
  "Fordert selbst keinen Internetzugriff an, teilt aber Berechtigungen und Daten mit %1$s, die ihn hat.",
  "Ne demande pas elle-même l'accès à Internet, mais partage autorisations et données avec %1$s, qui l'a.",
  "No solicita acceso a Internet, pero comparte permisos y datos con %1$s, que sí lo tiene.",
  "Non richiede l'accesso a Internet, ma condivide autorizzazioni e dati con %1$s, che lo ha.")

# ── v1.7: mimoriadne schopnosti ──
s("caps_title", "ZAPNUTÉ MIMORIADNE SCHOPNOSTI", "SPECIAL ACCESS TURNED ON", "AKTIVIERTE SONDERZUGRIFFE", "ACCÈS SPÉCIAUX ACTIVÉS", "ACCESOS ESPECIALES ACTIVADOS", "ACCESSI SPECIALI ATTIVI")
s("cap_a11y",
  "služba dostupnosti — vidí, čo je na obrazovke, a môže ťukať za teba",
  "accessibility service — sees what is on the screen and can tap for you",
  "Bedienungshilfe — sieht den Bildschirminhalt und kann für dich tippen",
  "service d'accessibilité — voit l'écran et peut appuyer à ta place",
  "servicio de accesibilidad — ve la pantalla y puede tocar por ti",
  "servizio di accessibilità — vede lo schermo e può toccare al posto tuo")
s("cap_notif",
  "prístup k notifikáciám — číta všetky notifikácie vrátane SMS kódov",
  "notification access — reads all notifications, including SMS codes",
  "Benachrichtigungszugriff — liest alle Benachrichtigungen, auch SMS-Codes",
  "accès aux notifications — lit toutes les notifications, y compris les codes SMS",
  "acceso a notificaciones — lee todas las notificaciones, incluidos los códigos SMS",
  "accesso alle notifiche — legge tutte le notifiche, compresi i codici SMS")
s("cap_ime",
  "zapnutá klávesnica — vidí, čo píšeš, keď ju používaš",
  "enabled keyboard — sees what you type while you use it",
  "aktivierte Tastatur — sieht, was du tippst, während du sie nutzt",
  "clavier activé — voit ce que tu tapes quand tu l'utilises",
  "teclado activado — ve lo que escribes mientras lo usas",
  "tastiera attiva — vede ciò che scrivi mentre la usi")
s("cap_admin",
  "správca zariadenia — môže mať právo zamknúť alebo vymazať telefón",
  "device admin — may be allowed to lock or wipe the phone",
  "Geräteadministrator — darf das Telefon ggf. sperren oder löschen",
  "administrateur de l'appareil — peut avoir le droit de verrouiller ou effacer le téléphone",
  "administrador del dispositivo — puede tener permiso para bloquear o borrar el teléfono",
  "amministratore del dispositivo — può avere il diritto di bloccare o cancellare il telefono")
s("cap_a11y_short", "služba dostupnosti", "accessibility service", "Bedienungshilfe", "service d'accessibilité", "servicio de accesibilidad", "servizio di accessibilità")
s("cap_notif_short", "prístup k notifikáciám", "notification access", "Benachrichtigungszugriff", "accès aux notifications", "acceso a notificaciones", "accesso alle notifiche")
# %1$s = zoznam schopností
s("caps_warn",
  "Má zapnuté: %1$s. Takýto prístup zneužívajú aj škodlivé appky na krádež hesiel a kódov — nechaj ho zapnutý len appkám, ktorým veríš.",
  "Turned on: %1$s. Malicious apps abuse this kind of access to steal passwords and codes — keep it only for apps you trust.",
  "Aktiviert: %1$s. Schadsoftware missbraucht solchen Zugriff, um Passwörter und Codes zu stehlen — erlaube ihn nur Apps, denen du vertraust.",
  "Activé : %1$s. Des apps malveillantes abusent de cet accès pour voler mots de passe et codes — ne l'accorde qu'aux apps de confiance.",
  "Activado: %1$s. Las apps maliciosas abusan de este acceso para robar contraseñas y códigos; déjalo solo a apps de confianza.",
  "Attivo: %1$s. Le app dannose abusano di questo accesso per rubare password e codici — lascialo solo alle app di cui ti fidi.")

# ── v1.7: webová stránka uložená cez Chrome (WebAPK) ──
s("webapk_note",
  "Toto je webová stránka uložená na plochu cez Chrome. Beží v prehliadači, ktorý prístup na internet má — čo stránka robí, sa dá overiť len v prehliadači, nie tu.",
  "This is a website saved to the home screen via Chrome. It runs in the browser, which has internet access — what the site does can only be checked in the browser, not here.",
  "Das ist eine Website, die über Chrome auf dem Startbildschirm gespeichert wurde. Sie läuft im Browser, der Internetzugriff hat — was die Seite tut, lässt sich nur im Browser prüfen, nicht hier.",
  "C'est un site web enregistré sur l'écran d'accueil via Chrome. Il s'exécute dans le navigateur, qui a accès à Internet — ce que fait le site ne se vérifie que dans le navigateur, pas ici.",
  "Es una web guardada en la pantalla de inicio con Chrome. Funciona en el navegador, que tiene acceso a Internet: lo que hace la web solo se puede comprobar en el navegador, no aquí.",
  "È un sito web salvato nella schermata Home tramite Chrome. Funziona nel browser, che ha accesso a Internet — cosa fa il sito si può verificare solo nel browser, non qui.")
s("origin_webapk", "webová stránka nainštalovaná cez Chrome", "website installed via Chrome", "über Chrome installierte Website", "site web installé via Chrome", "web instalada con Chrome", "sito web installato tramite Chrome")

# ── v1.7: zoznam zmien a zoradenie ──
s("changes_more", "zobraziť ďalšie (%1$d) ▸", "show more (%1$d) ▸", "mehr anzeigen (%1$d) ▸", "afficher plus (%1$d) ▸", "mostrar más (%1$d) ▸", "mostra altri (%1$d) ▸")
s("changes_less", "zobraziť menej ▴", "show less ▴", "weniger anzeigen ▴", "afficher moins ▴", "mostrar menos ▴", "mostra meno ▴")
s("sort_label", "Zoradiť:", "Sort:", "Sortieren:", "Trier :", "Ordenar:", "Ordina:")
s("sort_severity", "závažnosť", "severity", "Schweregrad", "gravité", "gravedad", "gravità")
s("sort_name", "názov", "name", "Name", "nom", "nombre", "nome")
s("sort_findings", "počet nálezov", "number of findings", "Anzahl Funde", "nombre de détections", "número de hallazgos", "numero di rilevamenti")
# ── v1.9: menu „Zoradiť a filtrovať" — filter je jedna voľba, povolenia len udelené ──
s("menu_sort", "ZORADIŤ", "SORT", "SORTIEREN", "TRIER", "ORDENAR", "ORDINA")
s("menu_show", "ZOBRAZIŤ", "SHOW", "ANZEIGEN", "AFFICHER", "MOSTRAR", "MOSTRA")
s("filter_label", "Zobraziť:", "Show:", "Anzeigen:", "Afficher :", "Mostrar:", "Mostra:")
s("filter_all", "všetky", "all", "alle", "toutes", "todas", "tutte")
s("filter_warning", "s upozornením", "with a warning", "mit Warnung", "avec un avertissement", "con aviso", "con avviso")
s("filter_ads", "s reklamným trackerom", "with an ad tracker", "mit Werbe-Tracker", "avec un traceur publicitaire", "con rastreador publicitario", "con tracker pubblicitario")
s("filter_trackers", "s akýmkoľvek trackerom", "with any tracker", "mit beliebigem Tracker", "avec un traceur quelconque", "con cualquier rastreador", "con qualsiasi tracker")
s("filter_caps", "mimoriadne schopnosti", "special access", "Sonderzugriffe", "accès spéciaux", "accesos especiales", "accessi speciali")
s("filter_mic", "mikrofón — udelené", "microphone — granted", "Mikrofon — erteilt", "micro — accordé", "micrófono — concedido", "microfono — concesso")
s("filter_location", "poloha — udelené", "location — granted", "Standort — erteilt", "position — accordée", "ubicación — concedida", "posizione — concessa")
s("filter_camera", "kamera — udelené", "camera — granted", "Kamera — erteilt", "appareil photo — accordé", "cámara — concedida", "fotocamera — concessa")
s("filter_files", "všetky súbory — udelené", "all files — granted", "alle Dateien — erteilt", "tous les fichiers — accordé", "todos los archivos — concedido", "tutti i file — concesso")
s("filter_partial", "audit nevidí celý kód", "audit can't see all the code", "Audit sieht nicht den ganzen Code", "l'audit ne voit pas tout le code", "la auditoría no ve todo el código", "l'audit non vede tutto il codice")
s("filter_nonplay", "mimo Google Play", "outside Google Play", "außerhalb von Google Play", "hors Google Play", "fuera de Google Play", "fuori da Google Play")
s("filter_none", "Žiadna appka nezodpovedá filtru.", "No app matches the filter.", "Keine App entspricht dem Filter.", "Aucune app ne correspond au filtre.", "Ninguna app coincide con el filtro.", "Nessuna app corrisponde al filtro.")

# ── v1.7: ďalšie technické povolenia Androidu ──
T2 = [
 ("REORDER_TASKS", "meniť poradie spustených appiek", "reorder running apps", "laufende Apps neu anordnen", "réorganiser les apps en cours", "reordenar apps en ejecución", "riordinare le app in esecuzione"),
 ("GET_PACKAGE_SIZE", "zistiť veľkosť appiek", "see app sizes", "App-Größen sehen", "voir la taille des apps", "ver el tamaño de las apps", "vedere la dimensione delle app"),
 ("FOREGROUND_SERVICE_SPECIAL_USE", "bežať v popredí (špeciálny účel)", "run in the foreground (special use)", "im Vordergrund laufen (Sonderzweck)", "s'exécuter au premier plan (usage spécial)", "ejecutarse en primer plano (uso especial)", "essere eseguita in primo piano (uso speciale)"),
 ("FOREGROUND_SERVICE_DATA_SYNC", "bežať v popredí (synchronizácia dát)", "run in the foreground (data sync)", "im Vordergrund laufen (Datensynchronisierung)", "s'exécuter au premier plan (synchro de données)", "ejecutarse en primer plano (sincronización)", "essere eseguita in primo piano (sincronizzazione)"),
 ("FOREGROUND_SERVICE_MEDIA_PLAYBACK", "bežať v popredí (prehrávanie)", "run in the foreground (media playback)", "im Vordergrund laufen (Wiedergabe)", "s'exécuter au premier plan (lecture)", "ejecutarse en primer plano (reproducción)", "essere eseguita in primo piano (riproduzione)"),
 ("FOREGROUND_SERVICE_CONNECTED_DEVICE", "bežať v popredí (pripojené zariadenie)", "run in the foreground (connected device)", "im Vordergrund laufen (verbundenes Gerät)", "s'exécuter au premier plan (appareil connecté)", "ejecutarse en primer plano (dispositivo conectado)", "essere eseguita in primo piano (dispositivo connesso)"),
 ("FOREGROUND_SERVICE_PHONE_CALL", "bežať v popredí (hovor)", "run in the foreground (phone call)", "im Vordergrund laufen (Anruf)", "s'exécuter au premier plan (appel)", "ejecutarse en primer plano (llamada)", "essere eseguita in primo piano (chiamata)"),
 ("FOREGROUND_SERVICE_HEALTH", "bežať v popredí (zdravie a pohyb)", "run in the foreground (health)", "im Vordergrund laufen (Gesundheit)", "s'exécuter au premier plan (santé)", "ejecutarse en primer plano (salud)", "essere eseguita in primo piano (salute)"),
 ("FOREGROUND_SERVICE_REMOTE_MESSAGING", "bežať v popredí (správy)", "run in the foreground (messaging)", "im Vordergrund laufen (Nachrichten)", "s'exécuter au premier plan (messagerie)", "ejecutarse en primer plano (mensajería)", "essere eseguita in primo piano (messaggi)"),
 ("FOREGROUND_SERVICE_SYSTEM_EXEMPTED", "bežať v popredí (výnimka systému)", "run in the foreground (system exempted)", "im Vordergrund laufen (Systemausnahme)", "s'exécuter au premier plan (exemption système)", "ejecutarse en primer plano (exención del sistema)", "essere eseguita in primo piano (esenzione di sistema)"),
 ("FOREGROUND_SERVICE_MEDIA_PROCESSING", "bežať v popredí (spracovanie médií)", "run in the foreground (media processing)", "im Vordergrund laufen (Medienverarbeitung)", "s'exécuter au premier plan (traitement média)", "ejecutarse en primer plano (procesamiento multimedia)", "essere eseguita in primo piano (elaborazione media)"),
 ("MODIFY_AUDIO_SETTINGS", "meniť nastavenia zvuku", "change audio settings", "Audioeinstellungen ändern", "modifier les paramètres audio", "cambiar ajustes de audio", "modificare le impostazioni audio"),
 ("READ_SYNC_SETTINGS", "čítať nastavenia synchronizácie", "read sync settings", "Synchronisierungseinstellungen lesen", "lire les paramètres de synchro", "leer ajustes de sincronización", "leggere le impostazioni di sincronizzazione"),
 ("WRITE_SYNC_SETTINGS", "zapínať a vypínať synchronizáciu", "turn sync on and off", "Synchronisierung ein- und ausschalten", "activer et désactiver la synchro", "activar y desactivar la sincronización", "attivare e disattivare la sincronizzazione"),
 ("REQUEST_DELETE_PACKAGES", "požiadať o odinštalovanie appky", "ask to uninstall apps", "Deinstallation von Apps anfragen", "demander la désinstallation d'apps", "pedir desinstalar apps", "chiedere di disinstallare app"),
 ("UPDATE_PACKAGES_WITHOUT_USER_ACTION", "aktualizovať appky bez potvrdenia", "update apps without confirmation", "Apps ohne Bestätigung aktualisieren", "mettre à jour des apps sans confirmation", "actualizar apps sin confirmación", "aggiornare app senza conferma"),
 ("CHANGE_WIFI_MULTICAST_STATE", "prijímať skupinové vysielanie cez Wi-Fi", "receive Wi-Fi multicast", "WLAN-Multicast empfangen", "recevoir le multicast Wi-Fi", "recibir multidifusión Wi-Fi", "ricevere multicast Wi-Fi"),
 ("READ_BASIC_PHONE_STATE", "zistiť základný stav telefónu", "read basic phone state", "einfachen Telefonstatus lesen", "lire l'état de base du téléphone", "leer el estado básico del teléfono", "leggere lo stato di base del telefono"),
]
for row in T2:
    s("tperm_" + row[0], *row[1:])
T += T2

# ── v1.8: kontrola telefónu, kto čo smie, odinštalovať ──
s("nav_check", "Kontrola telefónu", "Phone check", "Telefon-Check", "Vérifier le téléphone", "Revisar el teléfono", "Controllo telefono")
s("nav_byperm", "Kto čo smie", "Who can do what", "Wer darf was", "Qui peut faire quoi", "Quién puede qué", "Chi può cosa")
s("check_title", "KONTROLA TELEFÓNU", "PHONE CHECK", "TELEFON-CHECK", "VÉRIFICATION DU TÉLÉPHONE", "REVISIÓN DEL TELÉFONO", "CONTROLLO DEL TELEFONO")
s("check_note",
  "Kontrola ukazuje len to, čo Android dovolí zistiť. Nie je to antivírus.",
  "The check shows only what Android lets an app find out. It is not an antivirus.",
  "Der Check zeigt nur, was Android eine App herausfinden lässt. Er ist kein Virenscanner.",
  "La vérification ne montre que ce qu'Android permet de savoir. Ce n'est pas un antivirus.",
  "La revisión solo muestra lo que Android permite averiguar. No es un antivirus.",
  "Il controllo mostra solo ciò che Android permette di scoprire. Non è un antivirus.")
s("check_patch_ok", "Bezpečnostná aktualizácia: %1$s", "Security update: %1$s", "Sicherheitsupdate: %1$s", "Mise à jour de sécurité : %1$s", "Actualización de seguridad: %1$s", "Aggiornamento di sicurezza: %1$s")
s("check_patch_old",
  "Bezpečnostná aktualizácia: %1$s — staršia ako 3 mesiace. Skontroluj aktualizácie systému.",
  "Security update: %1$s — older than 3 months. Check for system updates.",
  "Sicherheitsupdate: %1$s — älter als 3 Monate. Prüfe auf Systemupdates.",
  "Mise à jour de sécurité : %1$s — plus de 3 mois. Vérifie les mises à jour système.",
  "Actualización de seguridad: %1$s — de hace más de 3 meses. Busca actualizaciones del sistema.",
  "Aggiornamento di sicurezza: %1$s — più vecchio di 3 mesi. Controlla gli aggiornamenti di sistema.")
s("check_patch_unknown", "Dátum bezpečnostnej aktualizácie sa nedá zistiť.", "The security update date can't be read.", "Das Datum des Sicherheitsupdates ist nicht lesbar.", "La date de mise à jour de sécurité est illisible.", "No se puede leer la fecha de la actualización de seguridad.", "Impossibile leggere la data dell'aggiornamento di sicurezza.")
s("check_lock_ok", "Zámok obrazovky je nastavený.", "A screen lock is set.", "Eine Displaysperre ist eingerichtet.", "Un verrouillage d'écran est configuré.", "Hay un bloqueo de pantalla configurado.", "È impostato un blocco schermo.")
s("check_lock_no",
  "Telefón nemá zabezpečený zámok obrazovky — kto ho vezme do ruky, vidí všetko.",
  "The phone has no secure screen lock — anyone who picks it up sees everything.",
  "Das Telefon hat keine sichere Displaysperre — wer es in die Hand nimmt, sieht alles.",
  "Le téléphone n'a pas de verrouillage sécurisé — qui le prend voit tout.",
  "El teléfono no tiene bloqueo seguro: quien lo coja lo ve todo.",
  "Il telefono non ha un blocco sicuro — chi lo prende vede tutto.")
s("check_adb_on",
  "Ladenie USB je zapnuté — cez kábel sa dá do telefónu dostať. Vypni ho, keď ho nepotrebuješ.",
  "USB debugging is on — the phone can be accessed over a cable. Turn it off when you don't need it.",
  "USB-Debugging ist an — über ein Kabel kommt man ins Telefon. Schalte es aus, wenn du es nicht brauchst.",
  "Le débogage USB est activé — on peut accéder au téléphone par câble. Désactive-le si tu n'en as pas besoin.",
  "La depuración USB está activada: se puede acceder al teléfono por cable. Desactívala si no la necesitas.",
  "Il debug USB è attivo — si può accedere al telefono via cavo. Disattivalo se non ti serve.")
s("check_dev_on", "Možnosti pre vývojárov sú zapnuté, ladenie USB vypnuté.", "Developer options are on, USB debugging is off.", "Entwickleroptionen sind an, USB-Debugging ist aus.", "Les options pour les développeurs sont activées, le débogage USB est désactivé.", "Las opciones de desarrollador están activadas, la depuración USB no.", "Le opzioni sviluppatore sono attive, il debug USB no.")
s("check_dev_off", "Možnosti pre vývojárov sú vypnuté.", "Developer options are off.", "Entwickleroptionen sind aus.", "Les options pour les développeurs sont désactivées.", "Las opciones de desarrollador están desactivadas.", "Le opzioni sviluppatore sono disattivate.")
s("check_install_none", "Žiadna nainštalovaná aplikácia nesmie inštalovať ďalšie aplikácie.", "No installed app may install other apps.", "Keine installierte App darf andere Apps installieren.", "Aucune app installée ne peut installer d'autres apps.", "Ninguna app instalada puede instalar otras apps.", "Nessuna app installata può installare altre app.")
s("check_install_some", "Inštalovať ďalšie aplikácie smú: %1$s", "Allowed to install other apps: %1$s", "Dürfen andere Apps installieren: %1$s", "Peuvent installer d'autres apps : %1$s", "Pueden instalar otras apps: %1$s", "Possono installare altre app: %1$s")
s("check_caps_none", "Žiadna aplikácia nemá zapnutú službu dostupnosti ani prístup k notifikáciám.", "No app has an accessibility service or notification access turned on.", "Keine App hat eine Bedienungshilfe oder Benachrichtigungszugriff aktiviert.", "Aucune app n'a de service d'accessibilité ni d'accès aux notifications.", "Ninguna app tiene activado un servicio de accesibilidad ni acceso a notificaciones.", "Nessuna app ha un servizio di accessibilità o l'accesso alle notifiche.")
s("check_caps_some", "Službu dostupnosti alebo prístup k notifikáciám majú: %1$s", "Accessibility service or notification access: %1$s", "Bedienungshilfe oder Benachrichtigungszugriff: %1$s", "Service d'accessibilité ou accès aux notifications : %1$s", "Servicio de accesibilidad o acceso a notificaciones: %1$s", "Servizio di accessibilità o accesso alle notifiche: %1$s")
s("check_root_none", "Známe znaky rootu sa nenašli.", "No known signs of root were found.", "Keine bekannten Root-Anzeichen gefunden.", "Aucun signe connu de root.", "No se encontraron señales conocidas de root.", "Nessun segno noto di root.")
s("check_root_found",
  "Našli sa znaky rootu: %1$s. Aplikácia s rootom môže obísť všetky povolenia.",
  "Signs of root found: %1$s. An app with root can bypass all permissions.",
  "Root-Anzeichen gefunden: %1$s. Eine App mit Root kann alle Berechtigungen umgehen.",
  "Signes de root trouvés : %1$s. Une app avec root peut contourner toutes les autorisations.",
  "Señales de root encontradas: %1$s. Una app con root puede saltarse todos los permisos.",
  "Segni di root trovati: %1$s. Un'app con root può aggirare tutte le autorizzazioni.")
s("check_open", "Otvoriť nastavenia", "Open settings", "Einstellungen öffnen", "Ouvrir les paramètres", "Abrir ajustes", "Apri impostazioni")
s("byperm_title", "KTO ČO SMIE", "WHO CAN DO WHAT", "WER DARF WAS", "QUI PEUT FAIRE QUOI", "QUIÉN PUEDE QUÉ", "CHI PUÒ COSA")
s("byperm_scope", "Rozsah podľa hlavnej obrazovky: %1$s. Ťukni na appku pre detail.", "Scope as on the main screen: %1$s. Tap an app for details.", "Bereich wie auf dem Hauptbildschirm: %1$s. Tippe auf eine App für Details.", "Portée comme sur l'écran principal : %1$s. Appuie sur une app pour le détail.", "Alcance como en la pantalla principal: %1$s. Toca una app para ver el detalle.", "Ambito come nella schermata principale: %1$s. Tocca un'app per i dettagli.")
s("byperm_none", "nikto", "nobody", "niemand", "personne", "nadie", "nessuno")
s("byperm_location", "poloha", "location", "Standort", "position", "ubicación", "posizione")
s("byperm_contacts", "kontakty a účty", "contacts and accounts", "Kontakte und Konten", "contacts et comptes", "contactos y cuentas", "contatti e account")
s("byperm_sms", "SMS", "SMS", "SMS", "SMS", "SMS", "SMS")
s("byperm_calls", "zoznam hovorov", "call log", "Anrufliste", "journal d'appels", "registro de llamadas", "registro chiamate")
s("byperm_calendar", "kalendár", "calendar", "Kalender", "agenda", "calendario", "calendario")
s("byperm_media", "fotky, videá a hudba", "photos, videos and music", "Fotos, Videos und Musik", "photos, vidéos et musique", "fotos, vídeos y música", "foto, video e musica")
s("byperm_phone", "údaje o telefóne", "phone identity", "Telefonidentität", "identité du téléphone", "identidad del teléfono", "identità del telefono")
s("byperm_microphone", "mikrofón", "microphone", "Mikrofon", "micro", "micrófono", "microfono")
s("byperm_camera", "kamera", "camera", "Kamera", "appareil photo", "cámara", "fotocamera")
s("byperm_activity", "pohybová aktivita", "physical activity", "körperliche Aktivität", "activité physique", "actividad física", "attività fisica")
s("byperm_files", "všetky súbory", "all files", "alle Dateien", "tous les fichiers", "todos los archivos", "tutti i file")
s("cap_ime_short", "klávesnica", "keyboard", "Tastatur", "clavier", "teclado", "tastiera")
s("cap_admin_short", "správca zariadenia", "device admin", "Geräteadministrator", "administrateur de l'appareil", "administrador del dispositivo", "amministratore del dispositivo")
s("uninstall", "Odinštalovať", "Uninstall", "Deinstallieren", "Désinstaller", "Desinstalar", "Disinstalla")

# ── v1.7: plurály ──
# Android `plurals`: každý jazyk má vlastné tvary. Kľúče: one, few, many, other.
PL = {}
def pl(key, **langs):
    PL[key] = langs
pl("summary_perms",
   sk={"one": "%1$d povolenie", "few": "%1$d povolenia", "many": "%1$d povolenia", "other": "%1$d povolení"},
   en={"one": "%1$d permission", "other": "%1$d permissions"},
   de={"one": "%1$d Berechtigung", "other": "%1$d Berechtigungen"},
   fr={"one": "%1$d autorisation", "many": "%1$d autorisations", "other": "%1$d autorisations"},
   es={"one": "%1$d permiso", "many": "%1$d permisos", "other": "%1$d permisos"},
   it={"one": "%1$d autorizzazione", "many": "%1$d autorizzazioni", "other": "%1$d autorizzazioni"})
pl("summary_trackers",
   sk={"one": "%1$d tracker", "few": "%1$d trackery", "many": "%1$d trackera", "other": "%1$d trackerov"},
   en={"one": "%1$d tracker", "other": "%1$d trackers"},
   de={"one": "%1$d Tracker", "other": "%1$d Tracker"},
   fr={"one": "%1$d traceur", "many": "%1$d traceurs", "other": "%1$d traceurs"},
   es={"one": "%1$d rastreador", "many": "%1$d rastreadores", "other": "%1$d rastreadores"},
   it={"one": "%1$d tracker", "many": "%1$d tracker", "other": "%1$d tracker"})
pl("tech_perms",
   sk={"one": "+ %1$d technické povolenie", "few": "+ %1$d technické povolenia", "many": "+ %1$d technického povolenia", "other": "+ %1$d technických povolení"},
   en={"one": "+ %1$d technical permission", "other": "+ %1$d technical permissions"},
   de={"one": "+ %1$d technische Berechtigung", "other": "+ %1$d technische Berechtigungen"},
   fr={"one": "+ %1$d autorisation technique", "many": "+ %1$d autorisations techniques", "other": "+ %1$d autorisations techniques"},
   es={"one": "+ %1$d permiso técnico", "many": "+ %1$d permisos técnicos", "other": "+ %1$d permisos técnicos"},
   it={"one": "+ %1$d autorizzazione tecnica", "many": "+ %1$d autorizzazioni tecniche", "other": "+ %1$d autorizzazioni tecniche"})
pl("shared_more",
   sk={"one": "a ďalšia %1$d", "few": "a ďalšie %1$d", "many": "a ďalších %1$d", "other": "a ďalších %1$d"},
   en={"one": "and %1$d more", "other": "and %1$d more"},
   de={"one": "und %1$d weitere", "other": "und %1$d weitere"},
   fr={"one": "et %1$d autre", "many": "et %1$d autres", "other": "et %1$d autres"},
   es={"one": "y %1$d más", "many": "y %1$d más", "other": "y %1$d más"},
   it={"one": "e %1$d altra", "many": "e altre %1$d", "other": "e altre %1$d"})
pl("filter_unscanned",
   sk={"one": "%1$d appka ešte nie je oskenovaná — výsledok je neúplný", "few": "%1$d appky ešte nie sú oskenované — výsledok je neúplný", "many": "%1$d appiek ešte nie je oskenovaných — výsledok je neúplný", "other": "%1$d appiek ešte nie je oskenovaných — výsledok je neúplný"},
   en={"one": "%1$d app not scanned yet — the result is incomplete", "other": "%1$d apps not scanned yet — the result is incomplete"},
   de={"one": "%1$d App noch nicht gescannt — das Ergebnis ist unvollständig", "other": "%1$d Apps noch nicht gescannt — das Ergebnis ist unvollständig"},
   fr={"one": "%1$d app pas encore analysée — résultat incomplet", "many": "%1$d apps pas encore analysées — résultat incomplet", "other": "%1$d apps pas encore analysées — résultat incomplet"},
   es={"one": "%1$d app aún sin analizar: el resultado está incompleto", "many": "%1$d apps aún sin analizar: el resultado está incompleto", "other": "%1$d apps aún sin analizar: el resultado está incompleto"},
   it={"one": "%1$d app non ancora analizzata — risultato incompleto", "many": "%1$d app non ancora analizzate — risultato incompleto", "other": "%1$d app non ancora analizzate — risultato incompleto"})

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
        for key, langs in PL.items():
            lines.append(f'    <plurals name="{key}">')
            for q in ("zero", "one", "two", "few", "many", "other"):
                if q in langs[lang]:
                    lines.append(f'        <item quantity="{q}">{esc(langs[lang][q])}</item>')
            lines.append('    </plurals>')
        lines.append('</resources>')
        with open(os.path.join(d, "strings.xml"), "w", encoding="utf-8") as f:
            f.write("\n".join(lines) + "\n")
    print(len(S), "kľúčov +", len(PL), "plurálov ×", len(LANGS), "jazykov")


if __name__ == "__main__":
    main(sys.argv[1])
