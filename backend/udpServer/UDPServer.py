import socket
from UDPServerController import *
import json

UDP_IP = "0.0.0.0"
UDP_PORT = 10000

def sendData(message: str, addr: tuple):
    data = message.encode("utf-8")
    sock.sendto(data, addr)

# AF_INET = IPv4 | SOCK_DGRAM = UDP
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sock.bind((UDP_IP, UDP_PORT))

print(f"Serveur UDP prêt sur le port {UDP_PORT}...")

ROUTES = {
    "hello": printHello,
    "device": getAllDevices,
    "dataByDevice/": getDataByDevice,
    "data": currentData
}

while True:
    # 3. Réception des données (taille du tampon : 1024 octets)
    data, addr = sock.recvfrom(1024)
    print("Message reçu de {}: {}".format(addr, data.decode()), flush=True)
    message = data.decode()

    for route, method in ROUTES.items():
        if message == route:
            data = method()
            sendData(json.dumps(data, ensure_ascii=False), addr)
        elif route[-1] == "/" and message.startswith(route):
            data = method(message)
            sendData(json.dumps(data, ensure_ascii=False), addr)