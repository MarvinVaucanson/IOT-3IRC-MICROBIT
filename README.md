# 🌐 Mini-Architecture IoT — Projet Embarqué & IoT 2026

> Mise en place d'une architecture complète Objet → Passerelle → Serveur → Application Android

---

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture du système](#architecture-du-système)
- [Composants du projet](#composants-du-projet)
  - [Objet connecté (micro:bit)](#1-objet-connecté-microbit)
  - [Passerelle (PC + micro:bit)](#2-passerelle-pc--microbit)
  - [Serveur](#3-serveur)
  - [Application Android](#4-application-android)
- [Prérequis](#prérequis)
- [Installation & Démarrage](#installation--démarrage)
- [Protocole de communication](#protocole-de-communication)
- [Format des données](#format-des-données)
- [Membres de l'équipe](#membres-de-léquipe)

---

## Vue d'ensemble

Ce projet consiste à déployer une mini-infrastructure IoT pour la collecte et l'affichage de données environnementales (température, luminosité, humidité, pression) dans des bureaux d'entreprise.

L'objectif est de connecter des objets intelligents (micro:bit + capteurs météo) à un serveur passerelle, lui-même contrôlable depuis une application Android. L'affichage des données sur chaque objet est configurable à distance via l'application.

---

## Architecture du système

```
[ micro:bit Objet ]  <---RF 2.4GHz--->  [ micro:bit Passerelle ]
     Capteurs                                  | USB (UART)
     Écran OLED                                |
                                          [ PC Serveur ]
                                          Stockage données
                                               | UDP / WiFi
                                          [ App Android ]
                                          Affichage & Contrôle
```

**Flux de communication :**
1. Le micro:bit objet collecte les données capteurs et les envoie via **RF 2.4 GHz**
2. Le micro:bit passerelle reçoit les données RF et les transmet au PC via **UART/USB**
3. Le serveur PC stocke les données et écoute les requêtes **UDP** de l'application Android
4. L'application Android interroge le serveur et lui envoie des ordres d'affichage

---

## Composants du projet

### 1. Objet connecté (micro:bit)

**Rôle :** Collecter les données environnementales, les envoyer à la passerelle, et afficher les informations reçues sur l'écran OLED.

**Matériel utilisé :**
- Micro-contrôleur **micro:bit**
- **Capteur météo** (température, luminosité, pression, humidité)
- **Écran OLED**

**Fonctionnalités :**
- Lecture périodique des capteurs
- Envoi des données brutes à la passerelle via RF 2.4 GHz
- Réception des messages de configuration d'affichage
- Affichage des données dans l'ordre défini par le serveur sur l'écran OLED

**Exemple de configuration d'affichage reçue :**

| Message reçu | Ordre d'affichage |
|---|---|
| `TLH` | Température → Luminosité → Humidité |
| `LTH` | Luminosité → Température → Humidité |
| `TLHP` | Température → Luminosité → Humidité → Pression |

---

### 2. Passerelle (PC + micro:bit)

**Rôle :** Recevoir les données RF des objets déployés et les transmettre au serveur via UART. Également envoyer les configurations d'affichage reçues du serveur vers les objets.

**Fonctionnement :**
- Le micro:bit connecté au PC joue le rôle de **récepteur RF 2.4 GHz**
- Communication bidirectionnelle avec le PC via **port série (UART/USB)**
- Réception des données capteurs → transmission au serveur
- Réception des ordres du serveur → transmission RF aux objets

---

### 3. Serveur

**Rôle :** Centraliser les données reçues, les stocker, et répondre aux requêtes de l'application Android.

**Technologies :** Python (Linux/Windows/macOS)

**Référence du code de base :**
```
https://github.com/CPELyon/4irc-aiot-mini-projet/blob/master/controller.py
```

**Fonctionnalités de base :**
- Écoute sur le **port UDP 10000**
- Stockage des données reçues dans un **fichier texte**
- Réponse à la requête `getValues()` → renvoie les dernières données au client Android
- Lecture/écriture sur le port série du micro:bit passerelle

**Évolutions implémentées :**
- [ ] Remplacement du fichier texte par une base de données (**InfluxDB**)
- [ ] Format d'échange JSON entre tous les composants
- [ ] Interface web de visualisation (**Grafana**)
- [ ] Gestion multi-objets avec protocole d'identification

**Démarrage du serveur :**
```bash
python controller.py
```

> ⚠️ Vérifier le port COM/tty associé au micro:bit passerelle et l'adapter dans le code.

---

### 4. Application Android

**Rôle :** Permettre à l'utilisateur de visualiser les données des capteurs et de configurer l'ordre d'affichage sur les objets connectés.

**Fonctionnalités :**
- **Connexion au serveur** : saisie de l'adresse IP et du port (défaut : `10000`)
- **Affichage des données** : réception et affichage des valeurs capteurs via UDP
- **Configuration de l'affichage** : sélection de l'ordre des données à afficher sur l'écran OLED
- **Communication UDP** : émission vers le serveur + réception des données

**Communication :**
- Protocole : **UDP**
- Envoi de l'ordre d'affichage : lettres majuscules (`TLH`, `LPH`, etc.) ou format JSON
- Pas d'ACK requis pour l'envoi de configuration
- Réception passive des données envoyées par le serveur

**Connexion réseau :**
- Smartphone physique → **WiFi** (même réseau que le PC serveur)
- Simulateur → **réseau interne** de l'ordinateur

---

## Prérequis

### Matériel
- 2× micro:bit (1 objet + 1 passerelle)
- 1 capteur météo compatible micro:bit
- 1 écran OLED compatible micro:bit
- 1 PC (Linux, Windows ou macOS)
- 1 smartphone Android (physique ou simulateur)

### Logiciels
- **Python 3.x** (pour le serveur)
- **Android Studio** (pour l'application Android)
- **MakeCode ou MicroPython** (pour programmer les micro:bit)
- Bibliothèques Python : `pyserial`, `socket` (voir `requirements.txt`)

---

## Installation & Démarrage

### Étape 1 — Programmer les micro:bit

1. Flasher le code **objet** sur le premier micro:bit (collecte capteurs + RF + OLED)
2. Flasher le code **passerelle** sur le second micro:bit (récepteur RF + UART)

### Étape 2 — Lancer le serveur

```bash
# Cloner le dépôt
git clone [https://github.com/MarvinVaucanson/IOT-3IRC-MICROBIT.git](https://github.com/MarvinVaucanson/IOT-3IRC-MICROBIT.git)
cd IOT-3IRC-MICROBIT

# Créer environnement Python et installer les dépendances
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Lancer le serveur de test
cd backend
docker compose up -build

# Envoyer des données de test
python common/influxTest.py
```

### Étape 3 — Démarrer l'application Android

1. Ouvrir le projet Android dans **Android Studio**
2. Lancer l'application sur un appareil ou simulateur
3. Dans l'écran de configuration, saisir l'**adresse IP** du PC serveur et le **port** (`10000`)
4. L'application commence à recevoir les données et permet de configurer l'affichage

---

## Protocole de communication

### RF 2.4 GHz (Objet ↔ Passerelle)

- Communication radio entre les deux micro:bit
- Pensée pour gérer **plusieurs objets** simultanément (identifiant unique par objet)
- Données envoyées en texte brut ou JSON

### UART/USB (Passerelle ↔ Serveur)

- Transmission série entre le micro:bit passerelle et le PC
- Baudrate : à définir selon configuration
- Format : données brutes ou structurées

### UDP (Serveur ↔ Application Android)

| Message | Direction | Description                                                     |
|----|---|-----------------------------------------------------------------|
| `data` | Android → Serveur | Demande les dernières données capteurs                          |
| Données capteurs | Serveur → Android | Réponse avec les valeurs mesurées                               |
| `configScreen/<device_id>:<config>` | Android → Serveur | Ordre d'afficahge des données sur l'écran d'une carte micro:bit |

---

## Format des données

### Données capteurs (exemple brut)

```
&2|23.5|412|58|1013$
```

### Données capteurs (exemple JSON — optionnel)

```json
{
  "temperature": 23.5,
  "luminosite": 412,
  "humidite": 58,
  "pression": 1013
}
```

### Configuration d'affichage

| Code | Signification |
|---|---|
| `T` | Température |
| `L` | Luminosité |
| `H` | Humidité |
| `P` | Pression |

---

## Membres de l'équipe

| Nom | Prénom | Rôle principal |
|---|---|---|
| MORFIN | Camille | micro:bit Objet |
| ROUSSELOT | Baptiste | Passerelle |
| MIFTARI | Dylan | Serveur |
| PHAM HUYNH | Tuong Vy | Application Android |

---
