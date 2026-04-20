FROM python:3.12-slim

WORKDIR /app

ENV PYTHONPATH=/app

COPY common ./common
COPY udpServer ./udpServer

RUN pip install -r common/requirements.txt

CMD ["python", "udpServer/UDPServer.py"]