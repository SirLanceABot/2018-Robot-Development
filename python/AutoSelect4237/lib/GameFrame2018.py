from PySide import QtGui, QtCore
import RobotPosition

class GameFrame2018(QtGui.QFrame):
    def __init__(self):
        super(GameFrame2018, self).__init__();
        self.iconSize = 150
        self.selectedPosition = "None"
        self.selectedTarget = "Switch"
        self.selectedBackupPlan = "Auto Line"

        '''
        Widgets
        '''
        
        self.robotPixmap2018 = QtGui.QPixmap("img/2018/icons/RobotIcon.png")
        self.blankPixmap2018 = QtGui.QPixmap("img/2018/icons/TransparentRobotIcon.png")
        self.switchPixmap = QtGui.QPixmap("img/2018/icons/SwitchIcon.png")
        self.scalePixmap = QtGui.QPixmap("img/2018/icons/ScaleIcon.png")
        self.autoLinePixmap = QtGui.QPixmap("img/2018/icons/AutoLineIcon.png")
        self.transparentSwitchPixmap = QtGui.QPixmap("img/2018/icons/TransparentSwitchIcon.png")
        self.transparentScalePixmap = QtGui.QPixmap("img/2018/icons/TransparentScaleIcon.png")
        self.transparentAutoLinePixmap = QtGui.QPixmap("img/2018/icons/TransparentAutoLineIcon.png")
        
        self.position1Button = RobotPosition.RobotPosition(self.blankPixmap2018.scaledToWidth(75))
        self.position2Button = RobotPosition.RobotPosition(self.blankPixmap2018.scaledToWidth(75))
        self.position3Button = RobotPosition.RobotPosition(self.blankPixmap2018.scaledToWidth(75))
        self.noPositionButton = RobotPosition.RobotPosition(self.robotPixmap2018.scaledToWidth(75))
        
        self.position1Button.setAlignment(QtCore.Qt.AlignCenter)
        self.position2Button.setAlignment(QtCore.Qt.AlignCenter)
        self.position3Button.setAlignment(QtCore.Qt.AlignCenter)
        self.noPositionButton.setAlignment(QtCore.Qt.AlignCenter)
        
        self.position1Button.clicked.connect(lambda: self.positionRobot(0))
        self.position2Button.clicked.connect(lambda: self.positionRobot(1))
        self.position3Button.clicked.connect(lambda: self.positionRobot(2))
        self.noPositionButton.clicked.connect(lambda: self.positionRobot("None"))

        self.leftPositionLabel = QtGui.QLabel("Left")
        self.leftPositionLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.centerPositionLabel = QtGui.QLabel("Center")
        self.centerPositionLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.rightPositionLabel = QtGui.QLabel("Right")
        self.rightPositionLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.noPositionLabel = QtGui.QLabel("None")
        self.noPositionLabel.setAlignment(QtCore.Qt.AlignCenter)
        
        self.planAComboBox = QtGui.QComboBox()
        self.planBComboBox = QtGui.QComboBox()
        self.planCComboBox = QtGui.QComboBox()
        
        for cb in [self.planAComboBox, self.planBComboBox, self.planCComboBox]:
            cb.addItem(self.switchPixmap, "Left Switch")
            cb.addItem(self.switchPixmap, "Right Switch")
            
            cb.addItem(self.scalePixmap, "Left Scale")
            cb.addItem(self.scalePixmap, "Right Scale")
            
            cb.addItem(self.autoLinePixmap, "Auto Line")
            cb.setIconSize(QtCore.QSize(75, 75))
            
        self.planALabel = QtGui.QLabel("Plan A")
        self.planBLabel = QtGui.QLabel("Plan B")
        self.planCLabel = QtGui.QLabel("Plan C")
        
        self.planALabel.setAlignment(QtCore.Qt.AlignCenter)
        self.planBLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.planCLabel.setAlignment(QtCore.Qt.AlignCenter)
               
        self.layout = QtGui.QGridLayout()
        
        self.layout.addWidget(self.planALabel, 0, 0)
        self.layout.addWidget(self.planBLabel, 0, 1)
        self.layout.addWidget(self.planCLabel, 0, 2)
        
        self.layout.addWidget(self.planAComboBox, 1, 0)
        self.layout.addWidget(self.planBComboBox, 1, 1)
        self.layout.addWidget(self.planCComboBox, 1, 2)
        
        self.layout.addWidget(self.position1Button, 2, 0)
        self.layout.addWidget(self.position2Button, 2, 1)
        self.layout.addWidget(self.position3Button, 2, 2)
        self.layout.addWidget(self.noPositionButton, 2, 3)
        
        self.layout.addWidget(self.leftPositionLabel, 3, 0)
        self.layout.addWidget(self.centerPositionLabel, 3, 1)
        self.layout.addWidget(self.rightPositionLabel, 3, 2)
        self.layout.addWidget(self.noPositionLabel, 3, 3)
        
        self.setLayout(self.layout)

        
    def positionRobot(self, position):
        self.selectedPosition = position
        
        self.position1Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.position2Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.position3Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.noPositionButton.setPixmap(self.blankPixmap2018.scaledToWidth(75))
                
        if position is 0:
            self.selectedPosition = "Left"
            self.position1Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is 1:
            self.selectedPosition = "Center"
            self.position2Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is 2:
            self.selectedPosition = "Right"
            self.position3Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is "None":
            self.selectedPosition = "None"
            self.noPositionButton.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        
    def getJsonData(self):
        data = {}
        data["selectedPosition"] = self.selectedPosition
        data["planA"] = self.planAComboBox.currentText()
        data["planB"] = self.planBComboBox.currentText()
        data["planC"] = self.planCComboBox.currentText()
        return data