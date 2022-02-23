import serial
import time
import random
import struct
import math
from multiprocessing import Process
import multiprocessing
import numpy as np

baud = 9600 # 시리얼 보드레이트(통신속도)
sigma, mu = 0.7, 0.2

def gaussian_random(avg, cov):
    s = 1.0
    v1 = 0
    while(s>=1 or s==0):
        v1 = 2*random.random()-1
        v2 = 2*random.random()-1
        print("v1: ", v1)
        print("v2: ", v2)
        s = v1*v1+v2*v2
    s = math.sqrt((-2*math.log10(s))/s)
    temp = v1*s
    temp = (cov*temp)+avg
    return temp


def sensor_processing(port_name, average):
    ser = serial.Serial(port_name, baud, timeout=1)
    print("port name: ", port_name)
    ser.close()
    ser.open()
    while(True):
        line = ser.read(4).hex()
        start = line[0:4]
        packet = bytearray()
        if(start == "5a04"):
            packet.append(0x5a)
            packet.append(0x04)
            packet.append(0x05)
            packet.append(0x00)
            data = np.random.normal(average, 0.2)
            hex_data = bytearray(struct.pack("f", data))
            for i in range(len(hex_data)):
                packet.append(hex_data[i])
        else:
            continue
        ser.write(packet)
        time.sleep(0.01)


if __name__ == '__main__':
    port1 = "COM10"
    port2 = "COM12"
    sensor1 = multiprocessing.Process(target=sensor_processing, args=(port1, 0.8))
    sensor2 = multiprocessing.Process(target=sensor_processing, args=(port2, 0.3))
    sensor1.start()
    sensor2.start()

