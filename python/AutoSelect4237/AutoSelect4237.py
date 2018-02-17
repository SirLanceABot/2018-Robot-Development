from PySide import QtGui, QtCore
import sys, threading, time, os, json
from lib import AboutWindow, RobotPosition, ConfirmSendDialog, GameFrame2016, GameFrame2017, GameFrame2018, RoboRIOTransmitter

VERSION = 3.0                                                                #Version number (changes when something new gets added)

class GUI(QtGui.QMainWindow):                                                #Create class "GUI" that inherits QMainWindow
    def __init__(self):
        super(GUI, self).__init__()                                          #Call superclass costructor
        self.initUI()                                                        #Calls GUI.initUI to create the window

    def initUI(self):                                                        #Method to create the window
        self.teamNumber = self.getTeamNumber()                               #Get the number of the team using this program
        print "Found team number " + str(self.teamNumber)
        self.roboRIOIP = "roborio-" + str(self.teamNumber) + "-frc.local"    #Concatenate strings to form an IP address
        self.iconSize = 75                                                   #number used for scaling the images in the comboboxes
        
        #Misc
        self.transmitter = RoboRIOTransmitter.RoboRIOTransmitter()
        self.futureLabel = QtGui.QLabel("Coming Soon")                                   #Displays "coming soon" label for next year when there is a tab but no game yet
        self.futureLabel.setStyleSheet('font-size: 60pt;')                               #Formatting for Futurelabel
        self.futureLabel.setAlignment(QtCore.Qt.AlignCenter | QtCore.Qt.AlignVCenter)    #Align futurelabel
        
        #About window
        self.about = AboutWindow.AboutWindow()
        
        '''
        General
        '''
        
        self.DRMLabel = QtGui.QLabel("<font size=8>This program was made by Team 4237.<font>")
        self.DRMLabel.setStyleSheet("QLabel { color : red;}")
        
        self.teamNotFoundLabel = QtGui.QLabel("A FRC team number could not be found on this computer, make sure the FRC Driver Station is installed")
        self.teamNotFoundLabel.setStyleSheet("QLabel { color : red; }")
                
        #Actions
        self.exitAction = QtGui.QAction('&Exit', self)
        self.exitAction.triggered.connect(self.close)
        
        self.sendToRobotAction = QtGui.QAction('&Send To Robot', self)
        self.sendToRobotAction.triggered.connect(self.sendToRobot)
        

        self.openAboutAction = QtGui.QAction('&About', self)
        self.openAboutAction.triggered.connect(self.about.show)
        
        #Icons
        self.strongholdIcon = QtGui.QIcon("img/2016/logos/Stronghold.png")
        self.steamworksIcon = QtGui.QIcon("img/2017/logos/Steamworks.png")
        self.powerupIcon = QtGui.QIcon("img/2018/logos/Powerup.png")
        self.windowIcon = QtGui.QIcon("img/general/icons/LanceABotIcon.png")
        
        #Tabs and Frames
        self.frame2016 = GameFrame2016.GameFrame2016()
        self.frame2017 = GameFrame2017.GameFrame2017()
        self.frame2018 = GameFrame2018.GameFrame2018()

        #Layout
        self.tabs = QtGui.QTabWidget()
        self.tabs.addTab(self.frame2016, self.strongholdIcon, "")
        self.tabs.addTab(self.frame2017, self.steamworksIcon, "")
        self.tabs.addTab(self.frame2018, self.powerupIcon, "")
        self.tabs.currentChanged.connect(lambda: self.sendToRobot())
        self.tabs.setIconSize(QtCore.QSize(100, 50))
        
        self.layout = QtGui.QVBoxLayout()
        self.layout.addWidget(self.tabs)
        
        if self.teamNumber != 4237:
            if self.teamNumber == None:
                print "Error verifying team number"
                self.layout.addWidget(self.teamNotFoundLabel)
            else:
                print "Program in use by other team"
                self.layout.addWidget(self.DRMLabel)
        
        self.mainFrame = QtGui.QFrame()
        self.mainFrame.setLayout(self.layout)
        menuBar = self.menuBar()
        fileMenu = menuBar.addMenu('&File')
        fileMenu.addAction(self.exitAction)
        
        robotMenu = menuBar.addMenu('&Robot')
        robotMenu.addAction(self.sendToRobotAction)
        
        helpMenu = menuBar.addMenu('&Help')
        helpMenu.addAction(self.openAboutAction)
        
        #Make sure there are things on NetworkTables
        #self.sendToRobot()
        self.setCentralWidget(self.mainFrame)
        self.setWindowTitle('AutoSelect 4237 - ' + "Connected @ " + self.roboRIOIP + ' - Version ' + str(VERSION))
        self.setWindowIcon(self.windowIcon)
        self.show()
        
    def closeEvent(self, event):
        self.about.close()
        print "Close Event received"
        
    def sendToRobot(self): #Sends information to the robot over Networktables
        if self.tabs.currentIndex() == 0:
            self.sendToRobot2016()
        elif self.tabs.currentIndex() == 1:
            self.sendToRobot2017()
        elif self.tabs.currentIndex() == 2:
            self.sendToRobot2018()
            
    def sendToRobot2016(self):
        pass
            
    def sendToRobot2017(self):
        pass
        
    def sendToRobot2018(self):
        print self.frame2018.getJsonData()
        self.transmitter.sendMessage(self.frame2018.getJsonData())

    '''
    Function to get the current team number from the FRC Driver Station
    '''             
    def getTeamNumber(self):
        #Get the team number from the driver station files
        print "Searching for team number"
        try:
            with open("C:\Users\Public\Documents\FRC\FRC DS Data Storage.ini", 'r') as DSFile:
                lines = DSFile.readlines()
                for x in range(0, len(lines)):
                    if "TeamNumber" in lines[x]:
                        split = lines[x].split("= \"")
                        split[1] = split[1].replace("\"", "")
                        teamNumber = (int)(split[1])
                        return teamNumber
                        break
        except:
            print "Team number could not be found"
        

def main():
    app = QtGui.QApplication(sys.argv)
    window = GUI()
    sys.exit(app.exec_())

if __name__ == '__main__':
    main()