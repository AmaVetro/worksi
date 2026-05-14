from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from app.fairness import apply_fairness, fairness_job_light
from app.match_core import _nlp, build_explanation, semantic_score


class MatchIn(BaseModel):
    cv_text: str = Field(..., min_length=1)
    job_text: str = Field(..., min_length=1)


class MatchOut(BaseModel):
    score: float
    explanation: str


app = FastAPI()


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/match", response_model=MatchOut)
def match(body: MatchIn):
    cv_raw = body.cv_text.strip()
    job_raw = body.job_text.strip()
    if len(cv_raw) < 8 or len(job_raw) < 8:
        raise HTTPException(status_code=400, detail="cv_text y job_text requieren contenido util")
    cv_fair = apply_fairness(cv_raw, _nlp)
    job_fair = fairness_job_light(job_raw)
    if len(cv_fair.strip()) < 8:
        raise HTTPException(
            status_code=422,
            detail="cv_text no contiene texto util tras depuracion de datos sensibles",
        )
    base = semantic_score(cv_fair, job_fair)
    explanation = build_explanation(cv_fair, job_fair, base)
    return MatchOut(score=base, explanation=explanation)
