import socket, threading, SocketServer, time

class RoboRIOTransmitter():

    def __init__(self):
        self.JAVA_SERVER_HOST = "10.42.37.2"
        self.JAVA_SERVER_PORT = 5803


    def sendMessage(self, message):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        print message
        s.connect((self.JAVA_SERVER_HOST, self.JAVA_SERVER_PORT))
        try:
            s.sendall(message)
            print "Successfully sent message"
        except:
            s.close()
            print "Error sending message"