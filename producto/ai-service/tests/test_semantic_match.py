import pytest

from app.fairness import apply_fairness, fairness_job_light
from app.match_core import _nlp, build_explanation, semantic_score

JAVA_CV = (
    "Desarrollador Java senior con Spring Boot, APIs REST, microservicios, MySQL, Git, "
    "JUnit, Maven y cinco años de experiencia en backend empresarial."
)

JAVA_JOB = (
    "Buscamos desarrollador Java con Spring Boot, APIs REST, microservicios, MySQL, Git, "
    "JUnit y experiencia en backend empresarial."
)

CHEF_JOB = (
    "Buscamos chef con experiencia en cocina francesa, pasteleria fina, menu degustacion, "
    "gestion de brigada y normas HACCP en restaurante gourmet."
)


@pytest.mark.slow
def test_apply_fairness_redacts_sensitive_cv_data():
    raw = (
        "Juan Perez desarrollador Java email juan.perez@correo.cl telefono +56912345678 "
        "Spring Boot REST APIs MySQL Git cinco años de experiencia profesional."
    )
    cleaned = apply_fairness(raw, _nlp)
    assert "juan.perez@correo.cl" not in cleaned
    assert "+56912345678" not in cleaned
    assert len(cleaned.strip()) >= 8
    assert "Spring Boot" in cleaned


@pytest.mark.slow
def test_semantic_score_within_bounds_for_aligned_pair():
    cv = apply_fairness(JAVA_CV, _nlp)
    job = fairness_job_light(JAVA_JOB)
    score = semantic_score(cv, job)
    assert 0.0 <= score <= 1.0
    assert score >= 0.35


@pytest.mark.slow
def test_aligned_pair_scores_higher_than_unrelated_pair():
    cv = apply_fairness(JAVA_CV, _nlp)
    aligned_job = fairness_job_light(JAVA_JOB)
    unrelated_job = fairness_job_light(CHEF_JOB)
    aligned = semantic_score(cv, aligned_job)
    unrelated = semantic_score(cv, unrelated_job)
    assert aligned > unrelated


@pytest.mark.slow
def test_build_explanation_not_empty_for_aligned_pair():
    cv = apply_fairness(JAVA_CV, _nlp)
    job = fairness_job_light(JAVA_JOB)
    score = semantic_score(cv, job)
    explanation = build_explanation(cv, job, score)
    assert isinstance(explanation, str)
    assert explanation.strip() != ""
