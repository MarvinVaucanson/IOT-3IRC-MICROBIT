import os
import socket
from UDPServerController import *
import json
from datetime import datetime
from zoneinfo import ZoneInfo
from dotenv import load_dotenv
from pathlib import Path
import nacl.secret
import nacl.utils

env_path = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=env_path)

UDP_IP = "0.0.0.0"
UDP_PORT = 10000
ENCRYPTION_KEY = os.getenv("ENCRYPTION_KEY")

def sendData(message: str, addr: tuple):
    data = message.encode("utf-8")
    sock.sendto(data, addr)

# AF_INET = IPv4 | SOCK_DGRAM = UDP
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sock.bind((UDP_IP, UDP_PORT))
secretBox = nacl.secret.SecretBox(ENCRYPTION_KEY.encode())

def ping():
    message = "pong"
    nonce = nacl.utils.random(nacl.secret.SecretBox.NONCE_SIZE)
    data = message.encode("utf-8")
    encryptedData = secretBox.encrypt(data, nonce)
    print("Encypted : " + str(encryptedData), flush=True)
    sock.sendto(encryptedData, addr)


print(f"Serveur UDP prêt sur le port {UDP_PORT}...", flush=True)

ROUTES = {
    "hello": printHello,
    "device": getAllDevices,
    "dataByDevice/": getDataByDevice,
    "configScreen/": setScreenConfig,
    "data": currentData,
    "ping": ping
}

while True:
    # 3. Réception des données (taille du tampon : 1024 octets)
    data, addr = sock.recvfrom(1024)

    now = datetime.now(ZoneInfo("Europe/Paris"))
    date = now.strftime("%d-%m-%Y %H:%M:%S")
    print("{} : Message reçu de {}: {}".format(date, addr, data.decode()), flush=True)
    message = data.decode()

    for route, method in ROUTES.items():
        if message == "ping":
            ping()
            break
        elif message == route:
            data = method()
            sendData(json.dumps(data, ensure_ascii=False), addr)
        elif route[-1] == "/" and message.startswith(route):
            data = method(message)
            sendData(json.dumps(data, ensure_ascii=False), addr)