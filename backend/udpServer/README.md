## Route de l'application

Un mécanisme de routage a été effectué pour le serveur UDP. En envoyant une chaine 
spécifique, on reçoit une réponse spécifique.

### Obtenir la liste de tous les capteurs
Il suffit d'envoyer la chaîne `device` pour récupérer la liste de tous les capteurs. 
La réponse ressemblera à ça :
```json
["office_01", "office_02", "office_03", "office_04"]
```

### Obtention des dernières mesures des capteurs
En envoyant la chaîne `data` on récupère la liste des dernières données pour chaque 
capteur et pour chaque mesure. Voici un exemple de résultat :
```json
[
   {
      "sensor":"humidite",
      "value":52.57644811296619,
      "unit":"%",
      "protocol":"UDP",
      "deviceId":"office_01"
   },
   {
      "sensor":"luminosite",
      "value":313,
      "unit":"lux",
      "protocol":"UDP",
      "deviceId":"office_01"
   },
   {
      "sensor":"temperature",
      "value":24.583219397906085,
      "unit":"\\c2\b0C",
      "protocol":"UDP",
      "deviceId":"office_01"
   },
   {
      "sensor":"humidite",
      "value":52.82701416333847,
      "unit":"%",
      "protocol":"UDP",
      "deviceId":"office_02"
   },
   {
      "sensor":"luminosite",
      "value":455,
      "unit":"lux",
      "protocol":"UDP",
      "deviceId":"office_02"
   },
   {
      "sensor":"temperature",
      "value":21.866232232187027,
      "unit":"\\c2\b0C",
      "protocol":"UDP",
      "deviceId":"office_02"
   },
   {
      "sensor":"humidite",
      "value":50.99352212907614,
      "unit":"%",
      "protocol":"UDP",
      "deviceId":"office_03"
   },
   {
      "sensor":"luminosite",
      "value":365,
      "unit":"lux",
      "protocol":"UDP",
      "deviceId":"office_03"
   },
   {
      "sensor":"temperature",
      "value":21.200708754127945,
      "unit":"\\c2\b0C",
      "protocol":"UDP",
      "deviceId":"office_03"
   }
]
```

### Obtention des dernières mesures d'un capteur spécifique
Pour ce faire, il faut envoyer la chaine `dataByDevice/<device_id>` où `device_id` est 
l'id d'un capteur existant. Voici un exemple de résultat :
```json
[
   {
      "sensor":"humidite",
      "value":40.708910406252755,
      "unit":"%",
      "protocol":"UDP"
   },
   {
      "sensor":"luminosite",
      "value":745,
      "unit":"lux",
      "protocol":"UDP"
   },
   {
      "sensor":"temperature",
      "value":19.077548737795965,
      "unit":"\\c2\b0C",
      "protocol":"UDP"
   }
]
```


### Configuration des écrans des micro:bit
Pour ce faire il faut envoyer le message `configScreen/<office_id>:<config>` où 
`device_id` est l'id d'un capteur existant. `config` est la configuration de l'écran 
sur 3 lettres (T, L et H). Leur ordre définie l'ordre d'affichage des différentes 
mesures sur l'écran de la micro:bit.
T = Température  
L = Luminosité  
H = Humidité  