import re
from functools import lru_cache
from typing import List, Tuple

from sklearn.metrics.pairwise import cosine_similarity

_STOP = frozenset(
    "el la los las un una unos unas y o de del al a en con por para sin sobre entre "
    "que se es son fue ser esta este esto como mas muy todo todos todas tambien "
    "the and or of to in on at an a be is are was were been being it its if we you "
    "they their them my our your".split()
)


@lru_cache(maxsize=1)
def _nlp():
    import spacy

    return spacy.load("es_core_news_sm")


@lru_cache(maxsize=1)
def _embed_model():
    from sentence_transformers import SentenceTransformer

    return SentenceTransformer("all-MiniLM-L6-v2")


def semantic_score(cv_text: str, job_text: str) -> float:
    model = _embed_model()
    e1 = model.encode([cv_text], normalize_embeddings=True)
    e2 = model.encode([job_text], normalize_embeddings=True)
    raw = float(cosine_similarity(e1, e2)[0][0])
    if raw < 0.0:
        return 0.0
    if raw > 1.0:
        return 1.0
    return raw


def build_explanation(cv_text: str, job_text: str, base_score: float) -> str:
    def tokens(s: str) -> List[str]:
        out = []
        for w in re.findall(r"[A-Za-zÁÉÍÓÚáéíóúÑñ]{4,}", s.lower()):
            if w in _STOP:
                continue
            out.append(w)
        return out

    jt = set(tokens(job_text))
    ct = tokens(cv_text)
    overlap = [w for w in ct if w in jt]
    seen = set()
    picked: List[str] = []
    for w in overlap:
        if w in seen:
            continue
        seen.add(w)
        picked.append(w)
        if len(picked) >= 3:
            break
    if picked:
        joined = ", ".join(picked)
        return f"Coincidencia en temas del texto: {joined}."
    if base_score >= 0.65:
        return "Alta coincidencia semántica entre tu CV y el enunciado del cargo."
    if base_score >= 0.4:
        return "Coincidencia moderada entre tu CV y el perfil descrito en la oferta."
    return "Baja coincidencia entre el contenido de tu CV y el texto permitido de la oferta."
