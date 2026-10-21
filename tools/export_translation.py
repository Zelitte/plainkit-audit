import sys, re, runpy, datetime
src, out = sys.argv[1], sys.argv[2]
ns = runpy.run_path(src, run_name="lib")
S, LANGS = ns["S"], ns["LANGS"]
order = ["en", "sk", "de", "fr", "es", "it"]
NAMES = {"en":"EN","sk":"SK","de":"DE","fr":"FR","es":"ES","it":"IT"}
sec, cur = {}, "Ostatné"
for ln in open(src, encoding="utf-8"):
    m = re.match(r"# ── (.+?) ──", ln)
    if m: cur = re.sub(r"^v\d+\.\d+: ", "", re.sub(r" \(.*\)$", "", m.group(1))); continue
    m = re.match(r's\("([a-z0-9_]+)"', ln)
    if m: sec[m.group(1)] = cur
def group(k):
    if k in sec: return sec[k]
    if k.startswith("perm_"): return "názvy povolení"
    if k.startswith("tperm_"): return "technické povolenia Androidu: ľudský popis"
    if k.startswith("group_"): return "skupiny citlivých povolení"
    return "Ostatné"
groups = {}
for k in S: groups.setdefault(group(k), []).append(k)
L = ["# plainkit. audit — texty na preklad", "",
 f"Vygenerované {datetime.date.today():%d. %m. %Y} z `tools/gen_strings.py` (verzia appky {sys.argv[3]}). "
 f"**{len(S)} textov**, jazyky v appke: EN, SK, DE, FR, ES, IT. Tento súbor sa pri každej zmene textov v appke pregeneruje.", "",
 "## Pokyny pre prekladateľa", "",
 "- Za riadok **NOVÝ:** napíš preklad. Ak opravuješ existujúci jazyk, prepíš priamo jeho riadok.",
 "- **Zástupné znaky nechaj presne tak:** `%1$s`, `%1$d`, `%2$d` (appka za ne dosadí meno alebo číslo). Môžu sa presunúť v rámci vety.",
 "- Texty v **VEĽKÝCH PÍSMENÁCH** sú nadpisy — prelož tiež veľkými.",
 "- Dlaždice (`tile_*`) a tlačidlá majú byť **krátke** (1–2 slová), inak sa nezmestia.",
 "- Tón: neformálny (tykanie), vecný, bez strašenia. Appka nikdy netvrdí viac, než vie dokázať — „obsahuje knižnicu\" nie „posiela dáta\".",
 "- Názvy firiem a knižníc (Google, Meta, AppsFlyer…) a `INTERNET`, `ADB`, `APK`, `DNS` sa neprekladajú.", ""]
for g, keys in groups.items():
    L += [f"## {g[0].upper()+g[1:]}", ""]
    for k in keys:
        v = dict(zip(LANGS, S[k]))
        L.append(f"### `{k}`")
        L += [f"- **{NAMES[l]}:** {v[l]}" for l in order]
        L += ["- **NOVÝ:** ", ""]
open(out, "w", encoding="utf-8").write("\n".join(L))
print(len(S), "kľúčov,", len(groups), "sekcií")
