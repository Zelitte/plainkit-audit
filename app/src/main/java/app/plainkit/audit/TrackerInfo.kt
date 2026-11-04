package app.plainkit.audit

/** v2.0: popis trackera (vlastník, krajina, účel). Len overené údaje — audit-trackery-popisy.md. */
internal val TRACKER_INFO: Map<String, Int> = mapOf(
    "Firebase Analytics" to R.string.trk_firebase_analytics,
    "Firebase Crashlytics" to R.string.trk_firebase_crashlytics,
    "Google AdMob" to R.string.trk_google_admob,
    "Firebase Performance" to R.string.trk_firebase_performance,
    "AppsFlyer" to R.string.trk_appsflyer,
    "Meta App Events" to R.string.trk_meta_app_events,
    "Google Analytics Services" to R.string.trk_google_analytics_services,
    "Sentry" to R.string.trk_sentry,
    "Microsoft App Center" to R.string.trk_microsoft_app_center,
    "Meta Audience Network" to R.string.trk_meta_audience_network,
    "Google Tag Manager" to R.string.trk_google_tag_manager,
    "Dynatrace" to R.string.trk_dynatrace,
    "Datadog" to R.string.trk_datadog,
    "Segment" to R.string.trk_segment,
    "Branch" to R.string.trk_branch,
    "Salesforce Marketing Cloud" to R.string.trk_salesforce_marketing_cloud,
    "Singular" to R.string.trk_singular,
    "Adjust" to R.string.trk_adjust,
    "Vungle" to R.string.trk_vungle,
    "Huawei Ads" to R.string.trk_huawei_ads,
    "OneSignal" to R.string.trk_onesignal,
    "Braze" to R.string.trk_braze,
    "Mixpanel" to R.string.trk_mixpanel,
    "ACRA" to R.string.trk_acra,
    "ByteDance Pangle" to R.string.trk_bytedance_pangle,
    "Bigo Ads" to R.string.trk_bigo_ads,
    "HockeyApp" to R.string.trk_hockeyapp,
    "AppLovin" to R.string.trk_applovin,
    "Exponea / Bloomreach" to R.string.trk_exponea_bloomreach,
    "Mintegral" to R.string.trk_mintegral,
    "Yandex Ads" to R.string.trk_yandex_ads,
    "Contentsquare" to R.string.trk_contentsquare,
    "Leanplum" to R.string.trk_leanplum,
    "Bugsnag" to R.string.trk_bugsnag,
    "comScore" to R.string.trk_comscore,
    "AppMetrica" to R.string.trk_appmetrica,
    "VK myTarget" to R.string.trk_vk_mytarget,
    "ironSource" to R.string.trk_ironsource,
    "MoEngage" to R.string.trk_moengage,
    "InMobi" to R.string.trk_inmobi,
    "Adobe Experience Platform" to R.string.trk_adobe_experience_platform,
    "Digital Turbine" to R.string.trk_digital_turbine,
    "DataDome" to R.string.trk_datadome
)

/** Staré mená v Room → súčasné (v2.0 zlúčené duplicity). Bez toho by appka po aktualizácii
 * do nového skenu ukázala menej trackerov, než má. */
internal val TRACKER_ALIASES: Map<String, String> = mapOf(
    "Yandex Metrica" to "AppMetrica",
    "Appboy" to "Braze",
    "Piwik" to "Matomo",
    "PerimeterX" to "HUMAN Security"
)
