from unittest.mock import patch

import pytest
from fastapi.testclient import TestClient

from app.main import app


@pytest.fixture
def client():
    return TestClient(app)


def test_health(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "UP"}


def test_match_rejects_short_payload(client):
    response = client.post("/match", json={"cv_text": "corto", "job_text": "corto"})
    assert response.status_code == 400


@patch("app.main.semantic_score", return_value=0.72)
@patch("app.main.build_explanation", return_value="Explicacion de prueba.")
def test_match_returns_score_and_explanation(mock_explanation, mock_score, client):
    response = client.post(
        "/match",
        json={
            "cv_text": (
                "Desarrollador Java con Spring Boot, REST APIs, MySQL, Git y microservicios "
                "profesionales."
            ),
            "job_text": (
                "Oferta para desarrollador Java con Spring Boot, REST APIs, MySQL y Git en backend."
            ),
        },
    )
    assert response.status_code == 200
    body = response.json()
    assert body["score"] == 0.72
    assert body["explanation"] == "Explicacion de prueba."
    mock_score.assert_called_once()
    mock_explanation.assert_called_once()


def test_match_rejects_cv_without_useful_text_after_fairness(client):
    response = client.post(
        "/match",
        json={
            "cv_text": "juan@x.cl 12345678-5",
            "job_text": "Desarrollador Java Spring Boot REST APIs MySQL Git backend empresarial.",
        },
    )
    assert response.status_code == 422
