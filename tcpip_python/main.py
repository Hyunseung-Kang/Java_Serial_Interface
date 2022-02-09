import socket
import time

HOST = '127.0.0.1'
PORT = 7779

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.
    server = socket.socket(socket.AF_INET)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind((HOST, PORT))
    server.listen(1)
    connect, address = server.accept()
    while 1:
        # print("getting_data")
        receive = connect.recv(1024)
        data = str(receive.decode())
        print("data: ", data)
        time.sleep(1);


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
