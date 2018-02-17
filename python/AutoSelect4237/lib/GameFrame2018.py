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
        self.transparentSwitchPixmap = QtGui.QPixmap("img/2018/icons/TransparentSwitchIcon.png")
        self.transparentScalePixmap = QtGui.QPixmap("img/2018/icons/TransparentScaleIcon.png")
        
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
        
        self.targetSwitchButton = RobotPosition.RobotPosition(self.switchPixmap.scaledToWidth(self.iconSize))
        self.targetScaleButton = RobotPosition.RobotPosition(self.scalePixmap.scaledToWidth(self.iconSize))
       
        self.targetSwitchButton.setAlignment(QtCore.Qt.AlignCenter)
        self.targetScaleButton.setAlignment(QtCore.Qt.AlignCenter)
       
        self.targetSwitchButton.clicked.connect(lambda: self.selectTarget(0))
        self.targetScaleButton.clicked.connect(lambda: self.selectTarget(1))
       
        self.backupPlanLabel = QtGui.QLabel("Backup Plan: ")
        self.backupPlanLabel.setAlignment(QtCore.Qt.AlignCenter | QtCore.Qt.AlignVCenter)
       
        self.backupPlan = QtGui.QComboBox()
        self.backupPlan.addItems(["Auto Line", "Scale"])
        self.backupPlan.currentIndexChanged.connect(lambda: self.selectBackupPlan())
       
        self.layout = QtGui.QGridLayout()
        self.layout.addWidget(self.position1Button, 0, 0)
        self.layout.addWidget(self.position2Button, 0, 1)
        self.layout.addWidget(self.position3Button, 0, 2)
        self.layout.addWidget(self.noPositionButton, 0, 3)
        
        self.layout.addWidget(self.leftPositionLabel, 1, 0)
        self.layout.addWidget(self.centerPositionLabel, 1, 1)
        self.layout.addWidget(self.rightPositionLabel, 1, 2)
        self.layout.addWidget(self.noPositionLabel, 1, 3)
        
        self.layout.addWidget(self.targetSwitchButton, 2, 0)
        self.layout.addWidget(self.targetScaleButton, 2, 1)
        self.layout.addWidget(self.backupPlanLabel, 2, 2)
        self.layout.addWidget(self.backupPlan, 2, 3)
        
        self.setLayout(self.layout)

        
    def positionRobot(self, position):
        self.selectedPosition = position
        
        self.position1Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.position2Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.position3Button.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        self.noPositionButton.setPixmap(self.blankPixmap2018.scaledToWidth(75))
        
        self.targetScaleButton.setEnabled(True)
        
        if position is 0:
            self.selectedPosition = "Left"
            self.position1Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is 1:
            self.selectedPosition = "Center"
            self.targetScaleButton.setEnabled(False)
            self.selectTarget(0)
            self.position2Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is 2:
            self.selectedPosition = "Right"
            self.position3Button.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        elif position is "None":
            self.selectedPosition = "None"
            self.noPositionButton.setPixmap(self.robotPixmap2018.scaledToWidth(75))
        
    def selectTarget(self, target):
        self.targetSwitchButton.setPixmap(self.transparentSwitchPixmap.scaledToWidth(self.iconSize))
        self.targetScaleButton.setPixmap(self.transparentScalePixmap.scaledToWidth(self.iconSize))
        if target is 0:
            self.selectedTarget = "Switch"
            self.targetSwitchButton.setPixmap(self.switchPixmap.scaledToWidth(self.iconSize))
        elif target is 1:
            self.selectedTarget = "Scale"
            self.targetScaleButton.setPixmap(self.scalePixmap.scaledToWidth(self.iconSize))
    
    def selectBackupPlan(self):
        self.selectedBackupPlan = str(self.backupPlan.currentText())
        
    def getJsonData(self):
        data = {}
        data["selectedPosition"] = self.selectedPosition
        data["selectedTarget"] = self.selectedTarget
        data["selectedBackupPlan"] = self.selectedBackupPlan
        return data