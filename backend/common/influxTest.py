import time
import random
from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
import os
from dotenv import load_dotenv
from pathlib import Path

env_path = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=env_path)

# Configuration (Infos de ton docker-compose)
token = os.getenv("INFLUXDB_TOKEN")
org = "CPE_Lyon"
bucket = "iot_3irc-microbit"
url = "http://192.168.1.132:8086"

client = InfluxDBClient(url=url, token=token, org=org)
write_api = client.write_api(write_options=SYNCHRONOUS)

print("🚀 Démarrage de la simulation d'envoi de données...")

try:
    while True:
        # Simulation de données capteurs (comme demandé dans le projet)
        temperature = random.uniform(19.0, 25.0) # [cite: 39]
        humidite = random.uniform(40.0, 60.0)    # [cite: 39]
        luminosite = random.randint(200, 800)    # [cite: 39]

        office_number = random.randint(1, 4)
        
        # Création du point de donnée avec Tags (pour le multi-objet)
        point = Point("data") \
            .tag("deviceId", f"office_0{office_number}") \
            .field("temperature", temperature) \
            .field("humidite", humidite) \
            .field("luminosite", luminosite) \
            .time(time.time_ns(), WritePrecision.NS)

        # Écriture dans InfluxDB
        write_api.write(bucket=bucket, org=org, record=point)
        
        print(f"✅ Données envoyées : T={temperature:.2f}°C, H={humidite:.2f}%, L={luminosite} pour office_0{office_number}")
        
        # Attendre 5 secondes avant le prochain envoi
        time.sleep(1)

except KeyboardInterrupt:
    print("\n🛑 Simulation arrêtée.")
finally:
    client.close()