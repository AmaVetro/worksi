import re
from typing import Callable

_INSTITUTION_HINT = re.compile(
    r"(universidad|instituto|duoc|inacap|facultad|pedagogic|utfsm|usach|uchile|"
    r"\bip\b|college|school|campus|magister|licenciatura|titulo\s+profesional)",
    re.IGNORECASE,
)


def _regex_pass_initial(text: str) -> str:
    s = text
    s = re.sub(
        r"\b[\w.+-]+@[\w.-]+\.[A-Za-z]{2,}\b",
        " ",
        s,
    )
    s = re.sub(
        r"\b\d{1,2}\.\d{3}\.\d{3}-[\dkK]\b|\b\d{7,8}-[\dkK]\b",
        " ",
        s,
    )
    s = re.sub(r"\+?\d[\d\s]{7,}\d", " ", s)
    s = re.sub(
        r"(?i)\b(nacionalidad|nacional)\b\s*[:\-]?\s*[\w\s]{2,40}?(\n|$)",
        " ",
        s,
    )
    s = re.sub(
        r"(?i)\b(g[eé]nero|sexo)\b\s*[:\-]?\s*(masculino|femenino|m|f|otro)\b",
        " ",
        s,
    )
    s = re.sub(
        r"(?i)\b(edad)\b\s*[:\-]?\s*\d{1,3}\b",
        " ",
        s,
    )
    s = re.sub(r"(?i)\bchilena?\b|\bchileno\b", " ", s)
    return s


def _regex_pass_final(text: str) -> str:
    s = re.sub(r"\b[\w.+-]+@[\w.-]+\.[A-Za-z]{2,}\b", " ", text)
    s = re.sub(r"\s+", " ", s).strip()
    return s


def _redact_spacy(text: str, nlp) -> str:
    if not text.strip():
        return text
    doc = nlp(text)
    spans = []
    for ent in doc.ents:
        if ent.label_ == "PERSON":
            spans.append((ent.start_char, ent.end_char))
        elif ent.label_ == "GPE":
            spans.append((ent.start_char, ent.end_char))
        elif ent.label_ == "ORG":
            if _INSTITUTION_HINT.search(ent.text):
                spans.append((ent.start_char, ent.end_char))
    if not spans:
        return text
    spans.sort()
    out = []
    pos = 0
    for a, b in spans:
        if a < pos:
            continue
        out.append(text[pos:a])
        out.append(" ")
        pos = max(pos, b)
    out.append(text[pos:])
    return "".join(out)


def fairness_job_light(text: str) -> str:
    if text is None:
        return ""
    return _regex_pass_final(_regex_pass_initial(text))


def apply_fairness(text: str, nlp_loader: Callable[[], object]) -> str:
    if text is None:
        return ""
    s = _regex_pass_initial(text)
    nlp = nlp_loader()
    s = _redact_spacy(s, nlp)
    return _regex_pass_final(s)
