from common.influxHelper import *

def printHello():
    print("Hello, World!")

def currentData():
    return readLastData()

def getAllDevices():
    return getAllDevicesInInflux()