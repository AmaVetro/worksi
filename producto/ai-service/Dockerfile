FROM python:3.11-slim
WORKDIR /app
RUN apt-get update
RUN apt-get install -y --no-install-recommends build-essential
RUN rm -rf /var/lib/apt/lists/*
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN python -m spacy download es_core_news_sm
RUN ["python", "-c", "from sentence_transformers import SentenceTransformer; SentenceTransformer('all-MiniLM-L6-v2')"]
COPY app ./app
EXPOSE 8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
