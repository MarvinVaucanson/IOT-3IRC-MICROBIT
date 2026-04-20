from influxdb_client import InfluxDBClient, Point, WritePrecision
from influxdb_client.client.write_api import SYNCHRONOUS
import time

# Configuration (Infos de ton docker-compose)
token = "cu088IqkeWN5ozdbLytOLsvXVq4ndI39scBUuGiv3DLEMt6iZHAhSUQdWmB86BypuWmZvRAVE4QKuaA6U4Hedg=="
org = "CPE_Lyon"
bucket = "iot_3irc-microbit"
url = "http://10.42.225.248:8086"

client = InfluxDBClient(url=url, token=token, org=org)
write_api = client.write_api(write_options=SYNCHRONOUS)

UNIT = {
        "temperature": "°C",
        "humidite": "%",
        "luminosite": "lux"
}

def writeToInfluxDB(office_number: int, temperature: float, humidite: float, luminosite: int):
    point = Point("data") \
            .tag("deviceId", f"office_0{office_number}") \
            .field("temperature", temperature) \
            .field("humidite", humidite) \
            .field("luminosite", luminosite) \
            .time(time.time_ns(), WritePrecision.NS)

    write_api.write(bucket=bucket, org=org, record=point)

def readLastData():
        res = []
        query = f'''
                from(bucket: "{bucket}")
                |> range(start: -24h) 
                |> filter(fn: (r) => r["_measurement"] == "data")
                |> last()
        '''
        result = client.query_api().query(query)

        for table in result:
                for record in table.records:
                        res.append({
                                "sensor": record.get_field(),
                                "value": record.get_value(),
                                "unit": UNIT[record.get_field()],
                                "protocol": "UDP",
                                "deviceId": record["deviceId"]
                        })
        return res