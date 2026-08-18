# plainkit. audit

**See what your Android apps can actually do — in plain language.**

Most people never find out that a flashlight app has been granted the
microphone, or that a game they installed last year still has access to their
contacts. Audit lists every installed app and tells you, in a normal sentence,
what it can reach and what it carries inside.

It also looks inside each app's APK for known tracking libraries — advertising,
marketing, analytics, fraud detection, crash reporting — and combines that with
the permissions to say something useful, instead of showing a scary number.

**Everything runs on your phone. Nothing is uploaded. No ads, no account.**

## What it does

- Lists installed apps with their permissions **in plain language**
  ("precise location", not `ACCESS_FINE_LOCATION`)
- Distinguishes **granted / not granted / automatic at install** — most apps
  get a pile of permissions without ever asking you
- Scans APKs for ~115 known tracking SDKs, grouped by **purpose**
  (ads, attribution, analytics, fraud detection, crash reporting)
- Explains findings in one sentence, sorted by severity
- Remembers results and reports **what changed after an app update**
- Slovak and English

## What it does not do

- It reports **what is present in an app's code**, not what is happening right
  now. A bundled analytics SDK does not mean data is being sent at this moment.
- The signature list is **not complete**, so it under-reports rather than
  exaggerates.
- It is information to help you decide — not an accusation, not an audit
  in the formal sense.

## How detection works

An APK is a ZIP file. Inside, `classes*.dex` contains class names as readable
text (`Lcom/appsflyer/AppsFlyerLib;`). Audit walks each dex once, collects all
package paths into a hash set, and looks up known signature prefixes. No
decompiler, no network, and scan time does not grow with the size of the
signature list.

## Permissions

`QUERY_ALL_PACKAGES` — required to see the list of installed apps, which is the
entire point of the app. The list never leaves the device.

## Privacy policy

https://plainkit.app/audit

## Build

Android Studio, Kotlin, Jetpack Compose, Room. minSdk 26.

## License

GPLv3 — see [LICENSE](LICENSE).

---

Part of the [plainkit.app](https://plainkit.app) project — small tools,
no ads, no signup, no tracking.

## Po slovensky

Audit ukáže, čo tvoje aplikácie skutočne môžu — zrozumiteľne, bez skratiek typu
`ACCESS_FINE_LOCATION`. Zároveň hľadá v ich kóde známe sledovacie knižnice
a rozlišuje, či ide o reklamu, analytiku, detekciu podvodov alebo hlásenie
pádov. Celá analýza prebieha v telefóne a nič sa neodosiela.
