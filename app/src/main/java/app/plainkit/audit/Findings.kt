package app.plainkit.audit

enum class TrackerKind { ADS, ANALYTICS, CRASH, ATTRIBUTION, FRAUD }

data class Tracker(val name: String, val prefix: String, val kind: TrackerKind)

val TRACKERS = listOf(
    // ── Reklamné siete ────────────────────────────────────────────────
    Tracker("Google AdMob", "Lcom/google/android/gms/ads/", TrackerKind.ADS),
    Tracker("Meta Audience Network", "Lcom/facebook/ads/", TrackerKind.ADS),
    Tracker("Unity Ads", "Lcom/unity3d/ads/", TrackerKind.ADS),
    Tracker("Unity Mediation", "Lcom/unity3d/mediation/", TrackerKind.ADS),
    Tracker("AppLovin", "Lcom/applovin/", TrackerKind.ADS),
    Tracker("ironSource", "Lcom/ironsource/", TrackerKind.ADS),
    Tracker("Vungle", "Lcom/vungle/", TrackerKind.ADS),
    Tracker("Chartboost", "Lcom/chartboost/", TrackerKind.ADS),
    Tracker("AdColony", "Lcom/adcolony/", TrackerKind.ADS),
    Tracker("InMobi", "Lcom/inmobi/", TrackerKind.ADS),
    Tracker("Fyber", "Lcom/fyber/", TrackerKind.ADS),
    Tracker("Smaato", "Lcom/smaato/", TrackerKind.ADS),
    Tracker("MoPub", "Lcom/mopub/", TrackerKind.ADS),
    Tracker("Tapjoy", "Lcom/tapjoy/", TrackerKind.ADS),
    Tracker("Pangle (ByteDance)", "Lcom/bytedance/sdk/openadsdk/", TrackerKind.ADS),
    Tracker("Mintegral", "Lcom/mbridge/msdk/", TrackerKind.ADS),
    Tracker("Digital Turbine", "Lcom/digitalturbine/", TrackerKind.ADS),
    Tracker("StartApp", "Lcom/startapp/", TrackerKind.ADS),
    Tracker("PubNative", "Lnet/pubnative/", TrackerKind.ADS),
    Tracker("Criteo", "Lcom/criteo/publisher/", TrackerKind.ADS),
    Tracker("Yandex Ads", "Lcom/yandex/mobile/ads/", TrackerKind.ADS),
    Tracker("Amazon Ads", "Lcom/amazon/device/ads/", TrackerKind.ADS),
    Tracker("Huawei Ads", "Lcom/huawei/hms/ads/", TrackerKind.ADS),
    Tracker("AdMost", "Lcom/admost/sdk/", TrackerKind.ADS),
    Tracker("BidMachine", "Lio/bidmachine/", TrackerKind.ADS),
    Tracker("Ogury", "Lcom/ogury/", TrackerKind.ADS),
    Tracker("Teads", "Ltv/teads/", TrackerKind.ADS),
    Tracker("Appodeal", "Lcom/appodeal/ads/", TrackerKind.ADS),
    Tracker("myTarget (VK)", "Lcom/my/target/", TrackerKind.ADS),
    Tracker("Verizon/Yahoo Ads", "Lcom/verizon/ads/", TrackerKind.ADS),
    Tracker("Adform", "Lcom/adform/sdk/", TrackerKind.ADS),
    Tracker("Nend", "Lnet/nend/android/", TrackerKind.ADS),
    Tracker("Liftoff", "Lcom/liftoff/", TrackerKind.ADS),
    Tracker("Bigo Ads", "Lsg/bigo/ads/", TrackerKind.ADS),

    // ── Atribúcia a marketingová automatizácia ────────────────────────
    Tracker("AppsFlyer", "Lcom/appsflyer/", TrackerKind.ATTRIBUTION),
    Tracker("Adjust", "Lcom/adjust/sdk/", TrackerKind.ATTRIBUTION),
    Tracker("Branch", "Lio/branch/referral/", TrackerKind.ATTRIBUTION),
    Tracker("Kochava", "Lcom/kochava/", TrackerKind.ATTRIBUTION),
    Tracker("Singular", "Lcom/singular/sdk/", TrackerKind.ATTRIBUTION),
    Tracker("Tenjin", "Lcom/tenjin/", TrackerKind.ATTRIBUTION),
    Tracker("Airbridge", "Lco/ab180/airbridge/", TrackerKind.ATTRIBUTION),
    Tracker("Braze", "Lcom/braze/", TrackerKind.ATTRIBUTION),
    Tracker("Appboy (staršia Braze)", "Lcom/appboy/", TrackerKind.ATTRIBUTION),
    Tracker("OneSignal", "Lcom/onesignal/", TrackerKind.ATTRIBUTION),
    Tracker("Leanplum", "Lcom/leanplum/", TrackerKind.ATTRIBUTION),
    Tracker("CleverTap", "Lcom/clevertap/android/sdk/", TrackerKind.ATTRIBUTION),
    Tracker("Iterable", "Lcom/iterable/iterableapi/", TrackerKind.ATTRIBUTION),
    Tracker("Airship", "Lcom/urbanairship/", TrackerKind.ATTRIBUTION),
    Tracker("MoEngage", "Lcom/moengage/", TrackerKind.ATTRIBUTION),
    Tracker("Batch", "Lcom/batch/android/", TrackerKind.ATTRIBUTION),
    Tracker("Pushwoosh", "Lcom/pushwoosh/", TrackerKind.ATTRIBUTION),
    Tracker("Insider", "Lcom/useinsider/insider/", TrackerKind.ATTRIBUTION),
    Tracker("Emarsys", "Lcom/emarsys/", TrackerKind.ATTRIBUTION),
    Tracker("Salesforce Marketing Cloud", "Lcom/salesforce/marketingcloud/", TrackerKind.ATTRIBUTION),
    Tracker("Localytics", "Lcom/localytics/", TrackerKind.ATTRIBUTION),
    Tracker("Swrve", "Lcom/swrve/sdk/", TrackerKind.ATTRIBUTION),
    Tracker("Netmera", "Lcom/netmera/", TrackerKind.ATTRIBUTION),
    Tracker("Exponea / Bloomreach", "Lcom/exponea/sdk/", TrackerKind.ATTRIBUTION),
    Tracker("Dynamic Yield", "Lcom/dynamicyield/", TrackerKind.ATTRIBUTION),
    Tracker("Optimove", "Lcom/optimove/sdk/", TrackerKind.ATTRIBUTION),

    // ── Analytika správania ───────────────────────────────────────────
    Tracker("Firebase Analytics", "Lcom/google/firebase/analytics/", TrackerKind.ANALYTICS),
    Tracker("Google Analytics (staršia)", "Lcom/google/android/gms/analytics/", TrackerKind.ANALYTICS),
    Tracker("Google Tag Manager", "Lcom/google/android/gms/tagmanager/", TrackerKind.ANALYTICS),
    Tracker("Firebase Performance", "Lcom/google/firebase/perf/", TrackerKind.ANALYTICS),
    Tracker("Meta App Events", "Lcom/facebook/appevents/", TrackerKind.ANALYTICS),
    Tracker("Amplitude", "Lcom/amplitude/", TrackerKind.ANALYTICS),
    Tracker("Mixpanel", "Lcom/mixpanel/", TrackerKind.ANALYTICS),
    Tracker("Segment", "Lcom/segment/analytics/", TrackerKind.ANALYTICS),
    Tracker("Flurry", "Lcom/flurry/", TrackerKind.ANALYTICS),
    Tracker("Yandex Metrica", "Lcom/yandex/metrica/", TrackerKind.ANALYTICS),
    Tracker("AppMetrica", "Lio/appmetrica/", TrackerKind.ANALYTICS),
    Tracker("Huawei Analytics", "Lcom/huawei/hms/analytics/", TrackerKind.ANALYTICS),
    Tracker("Umeng", "Lcom/umeng/", TrackerKind.ANALYTICS),
    Tracker("Tencent Stats", "Lcom/tencent/stat/", TrackerKind.ANALYTICS),
    Tracker("Matomo", "Lorg/matomo/", TrackerKind.ANALYTICS),
    Tracker("Piwik (staršia)", "Lorg/piwik/sdk/", TrackerKind.ANALYTICS),
    Tracker("New Relic", "Lcom/newrelic/", TrackerKind.ANALYTICS),
    Tracker("Countly", "Lly/count/android/", TrackerKind.ANALYTICS),
    Tracker("Adobe Experience Platform", "Lcom/adobe/marketing/mobile/", TrackerKind.ANALYTICS),
    Tracker("Heap", "Lcom/heapanalytics/android/", TrackerKind.ANALYTICS),
    Tracker("mParticle", "Lcom/mparticle/", TrackerKind.ANALYTICS),
    Tracker("Snowplow", "Lcom/snowplowanalytics/", TrackerKind.ANALYTICS),
    Tracker("Microsoft App Center", "Lcom/microsoft/appcenter/", TrackerKind.ANALYTICS),
    Tracker("Datadog", "Lcom/datadog/android/", TrackerKind.ANALYTICS),
    Tracker("PostHog", "Lcom/posthog/", TrackerKind.ANALYTICS),
    Tracker("Optimizely", "Lcom/optimizely/ab/", TrackerKind.ANALYTICS),
    Tracker("Apptentive", "Lcom/apptentive/android/", TrackerKind.ANALYTICS),
    Tracker("Smartlook", "Lcom/smartlook/", TrackerKind.ANALYTICS),
    Tracker("FullStory", "Lcom/fullstory/", TrackerKind.ANALYTICS),
    Tracker("UXCam", "Lcom/uxcam/", TrackerKind.ANALYTICS),
    Tracker("Contentsquare", "Lcom/contentsquare/android/", TrackerKind.ANALYTICS),
    Tracker("Quantcast", "Lcom/quantcast/", TrackerKind.ANALYTICS),
    Tracker("comScore", "Lcom/comscore/", TrackerKind.ANALYTICS),
    Tracker("Nielsen", "Lcom/nielsen/app/sdk/", TrackerKind.ANALYTICS),
    Tracker("Mapbox Telemetry", "Lcom/mapbox/android/telemetry/", TrackerKind.ANALYTICS),
    Tracker("Taboola", "Lcom/taboola/android/", TrackerKind.ANALYTICS),
    Tracker("Pendo", "Lsdk/pendo/io/", TrackerKind.ANALYTICS),
    Tracker("Apptimize", "Lcom/apptimize/", TrackerKind.ANALYTICS),

    // ── Detekcia podvodov a zneužitia ─────────────────────────────────
    Tracker("Sift", "Lsiftscience/android/", TrackerKind.FRAUD),
    Tracker("Kount", "Lcom/kount/", TrackerKind.FRAUD),
    Tracker("Arkose Labs", "Lcom/arkoselabs/", TrackerKind.FRAUD),
    Tracker("Incognia", "Lcom/incognia/", TrackerKind.FRAUD),

    // ── Hlásenie pádov a diagnostika ──────────────────────────────────
    Tracker("Firebase Crashlytics", "Lcom/google/firebase/crashlytics/", TrackerKind.CRASH),
    Tracker("Crashlytics (Fabric)", "Lcom/crashlytics/android/", TrackerKind.CRASH),
    Tracker("Firebase Crash (staršia)", "Lcom/google/firebase/crash/", TrackerKind.CRASH),
    Tracker("Sentry", "Lio/sentry/", TrackerKind.CRASH),
    Tracker("Bugsnag", "Lcom/bugsnag/", TrackerKind.CRASH),
    Tracker("ACRA", "Lorg/acra/", TrackerKind.CRASH),
    Tracker("Instabug", "Lcom/instabug/", TrackerKind.CRASH),
    Tracker("Embrace", "Lio/embrace/android/", TrackerKind.CRASH),
    Tracker("Raygun", "Lcom/raygun/raygun4android/", TrackerKind.CRASH),
    Tracker("Bugfender", "Lcom/bugfender/sdk/", TrackerKind.CRASH)
)

data class PermGroup(val sk: String, val en: String, val permissions: Set<String>) {
    fun label(s: S) = if (s.lang == Lang.SK) sk else en
}

val SENSITIVE = listOf(
    PermGroup("polohe", "location", setOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_BACKGROUND_LOCATION")),
    PermGroup("kontaktom", "contacts", setOf(
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.GET_ACCOUNTS")),
    PermGroup("mikrofónu", "the microphone", setOf("android.permission.RECORD_AUDIO")),
    PermGroup("kamere", "the camera", setOf("android.permission.CAMERA")),
    PermGroup("SMS správam", "SMS messages", setOf(
        "android.permission.READ_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.SEND_SMS")),
    PermGroup("zoznamu hovorov", "the call log", setOf(
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG")),
    PermGroup("kalendáru", "the calendar", setOf(
        "android.permission.READ_CALENDAR",
        "android.permission.WRITE_CALENDAR")),
    PermGroup("údajom o telefóne", "phone identity", setOf(
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_PHONE_NUMBERS")),
    PermGroup("fotkám a médiám", "photos and media", setOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO")),
    PermGroup("pohybovej aktivite", "physical activity", setOf(
        "android.permission.ACTIVITY_RECOGNITION",
        "android.permission.BODY_SENSORS"))
)

fun isSensitive(permission: String): Boolean =
    SENSITIVE.any { permission in it.permissions }

enum class Level { OK, INFO, WARN, ALERT }

data class Finding(val level: Level, val text: String)

fun buildFindings(
    permissions: List<String>,
    granted: Set<String>,
    trackers: List<Tracker>,
    s: S
): List<Finding> {
    val out = mutableListOf<Finding>()

    if ("android.permission.INTERNET" !in permissions) {
        out.add(Finding(Level.OK, s.noInternet))
        return out
    }

    val grantedGroups = SENSITIVE.filter { g ->
        permissions.any { it in g.permissions && it in granted }
    }
    val pendingGroups = SENSITIVE.filter { g ->
        permissions.any { it in g.permissions } && g !in grantedGroups
    }
    val grantedText = grantedGroups.joinToString(", ") { it.label(s) }
    val pendingText = pendingGroups.joinToString(", ") { it.label(s) }

    val marketing = trackers.filter {
        it.kind == TrackerKind.ADS || it.kind == TrackerKind.ATTRIBUTION
    }
    val analytics = trackers.filter { it.kind == TrackerKind.ANALYTICS }
    val crash = trackers.filter { it.kind == TrackerKind.CRASH }
    val fraud = trackers.filter { it.kind == TrackerKind.FRAUD }

    fun names(list: List<Tracker>) = list.joinToString(", ") { it.name }

    if (marketing.isNotEmpty()) {
        if (grantedGroups.isNotEmpty()) {
            out.add(Finding(Level.ALERT, s.marketingWithGranted(grantedText, names(marketing))))
        } else {
            out.add(Finding(Level.WARN, s.marketingOnly(names(marketing))))
        }
    }

    if (analytics.isNotEmpty()) {
        if (grantedGroups.isNotEmpty()) {
            out.add(Finding(Level.WARN, s.analyticsWithGranted(names(analytics), grantedText)))
        } else {
            out.add(Finding(Level.INFO, s.analyticsOnly(names(analytics))))
        }
    }

    if (fraud.isNotEmpty()) {
        out.add(Finding(Level.INFO, s.fraud(names(fraud))))
    }

    if (crash.isNotEmpty() && marketing.isEmpty() && analytics.isEmpty()) {
        out.add(Finding(Level.INFO, s.crashOnly(names(crash))))
    }

    if (marketing.size + analytics.size >= 6) {
        out.add(Finding(Level.WARN, s.tooMany(marketing.size + analytics.size)))
    }

    if (trackers.isEmpty()) {
        out.add(Finding(
            if (grantedGroups.isEmpty()) Level.OK else Level.INFO,
            if (grantedGroups.isEmpty()) s.noTrackers else s.noTrackersButGranted(grantedText)
        ))
    }

    if (pendingGroups.isNotEmpty()) {
        out.add(Finding(Level.INFO, s.pending(pendingText)))
    }

    return out
}

fun worstLevel(findings: List<Finding>): Level =
    findings.maxByOrNull { it.level.ordinal }?.level ?: Level.OK