import socket, threading, SocketServer, time

class RoboRIOTransmitter():

    def __init__(self):
        self.JAVA_SERVER_HOST = "roborio-4237-frc.local"
        self.JAVA_SERVER_PORT = 5804


    def sendMessage(self, message):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
         
        print message
        try:
            s.connect((self.JAVA_SERVER_HOST, self.JAVA_SERVER_PORT))
            s.sendall(message)
            print "Successfully sent message"
        except Exception as e:
            s.close()
            print e