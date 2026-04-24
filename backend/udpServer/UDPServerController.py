from common.influxHelper import *
import socket

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

    sock.sendto(config.encode(), ("10.42.229.174", 11000))