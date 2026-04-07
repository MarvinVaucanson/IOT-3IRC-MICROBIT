## Format de stockage des données dans InfluxDB

La base de données InfluxDB suit ce format pour stocker les données. Il y a un seul measurement, 
nommé `data`. Pour des raisons de performance il est préférable d'avoir un seul measurement et ensuite de filtrer à l'aide de tags par exemple.

On a 1 tag :
- __deviceId__ : Il comporte un id permettant d'identifier chaque appareil

On a 3 fields :
- __temperature__ : Il contient la température en °C
- __luminosite__ : Il contient la luminosité en lux
- __humidite__ : Il contient l'humidité en %