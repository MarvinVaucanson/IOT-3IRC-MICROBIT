from common.influxHelper import *
import socket
from dotenv import load_dotenv
import os

env_path = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=env_path)

def printHello():
    print("Hello, World!")

def currentData():
    return readLastData()

def getDataByDevice(udpMessage):
    deviceId = udpMessage.split("/")[1]
    return readLastDataForDevice(deviceId)

def getAllDevices():
    return getAllDevicesInInflux()

def setScreenConfig(udpMessage):
    splitedUdpMessage = udpMessage.split("/")

    config = splitedUdpMessage[1]
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    sock.sendto(config.encode(), (os.getenv("UART_SERVER_IP"), 11000))