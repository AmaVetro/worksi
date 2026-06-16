from app.fairness import fairness_job_light


def test_fairness_job_light_strips_email_and_rut():
    raw = (
        "Desarrollador Java contacto@demo.cl rut 12.345.678-5 con Spring Boot y REST APIs "
        "para backend empresarial."
    )
    cleaned = fairness_job_light(raw)
    assert "contacto@demo.cl" not in cleaned
    assert "12.345.678-5" not in cleaned
    assert "Spring Boot" in cleaned


def test_fairness_job_light_preserves_job_semantics():
    raw = (
        "Buscamos desarrollador Java con Spring Boot, microservicios, MySQL y Git en modalidad remota."
    )
    cleaned = fairness_job_light(raw)
    assert len(cleaned.strip()) >= 8
    assert "Java" in cleaned
