import serial

from backend.common.influxHelper import writeToInfluxDB

# send serial message
SERIALPORT = "COM4"
BAUDRATE = 115200
START_CHAR = "&"
END_CHAR = "$"
DEVICE_ID_INDEX = 0
TEMPERATURE_INDEX = 1
LUMINOSITE_INDEX = 2
HUMIDITY_INDEX = 3
ser = serial.Serial()


def initUART():
    ser.port = SERIALPORT
    ser.baudrate = BAUDRATE
    ser.bytesize = serial.EIGHTBITS  # number of bits per bytes
    ser.parity = serial.PARITY_NONE  # set parity check: no parity
    ser.stopbits = serial.STOPBITS_ONE  # number of stop bits
    ser.timeout = None  # block read

    ser.xonxoff = False  # disable software flow control
    ser.rtscts = False  # disable hardware (RTS/CTS) flow control
    ser.dsrdtr = False  # disable hardware (DSR/DTR) flow control
    print('Starting Up Serial Monitor', flush=True)
    try:
        ser.open()
    except serial.SerialException:
        print("Serial {} port not available".format(SERIALPORT))
        exit()


def sendUARTMessage(msg):
    ser.write(msg.encode())
    print("Message <" + msg + "> sent to micro-controller.")

def sendDataToInflux(string: str):
    data = string[1:len(string)-1].split("|")
    print("Sent data : "+str(data))
    writeToInfluxDB(data[DEVICE_ID_INDEX],
                    float(data[TEMPERATURE_INDEX]), float(data[HUMIDITY_INDEX]),
                    int(data[LUMINOSITE_INDEX]))


# Main program logic follows:
if __name__ == '__main__':
    initUART()
    print('Press Ctrl-C to quit.', flush=True)
    res = ""
    stringBegin = False

    try:
        while ser.isOpen():
            # time.sleep(100)
            if (ser.inWaiting() > 0):  # if incoming bytes are waiting
                data_bytes = ser.read(ser.inWaiting())
                data_str = data_bytes.decode()

                if not stringBegin and START_CHAR in data_str:
                    startCharIndex = data_str.index(START_CHAR)
                    res += data_str[startCharIndex:]
                    stringBegin = True
                elif stringBegin and END_CHAR in data_str:
                    endCharIndex = data_str.index(END_CHAR)
                    res += data_str[:endCharIndex+1]
                    sendDataToInflux(res)
                    res = ""
                    stringBegin = False
                elif stringBegin:
                    res += data_str
    except (KeyboardInterrupt, SystemExit):
        ser.close()
        exit()