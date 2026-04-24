import serial
import threading
import socket

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
PRESSURE_INDEX = 4
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
                    int(data[LUMINOSITE_INDEX]), int(data[PRESSURE_INDEX]))

def uart_loop():
    initUART()

    res = ""
    stringBegin = False

    while True:
        if ser.inWaiting() > 0:
            data_bytes = ser.read(ser.inWaiting())
            data_str = data_bytes.decode()

            if not stringBegin and "&" in data_str:
                start = data_str.index("&")
                res += data_str[start:]
                stringBegin = True

            elif stringBegin and "$" in data_str:
                end = data_str.index("$")
                res += data_str[:end+1]
                sendDataToInflux(res)
                res = ""
                stringBegin = False

            elif stringBegin:
                res += data_str

def udp_loop():
    UDP_IP = "0.0.0.0"
    UDP_PORT = 11000

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((UDP_IP, UDP_PORT))

    print("UDP ready...")

    while True:
        data, addr = sock.recvfrom(1024)
        msg = data.decode()

        sendUARTMessage(msg)

# Main program logic follows:
if __name__ == '__main__':
    t1 = threading.Thread(target=uart_loop)
    t2 = threading.Thread(target=udp_loop)

    t1.start()
    t2.start()

    t1.join()
    t2.join()