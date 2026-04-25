from fastapi import FastAPI

app = FastAPI()


@app.get("/health")
def health():
  return {"status": "UP"}


@app.post("/match")
def match(payload: dict):
  return {"score": 0.0, "explanation": "IA stub"}
