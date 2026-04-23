from common.influxHelper import *

def printHello():
    print("Hello, World!")

def currentData():
    return readLastData()

def getDataByDevice(udpMessage):
    deviceId = udpMessage.split("/")[1]
    return readLastDataForDevice(deviceId)

def getAllDevices():
    return getAllDevicesInInflux()