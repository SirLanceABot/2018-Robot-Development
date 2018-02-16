from PySide import QtGui, QtCore

class AboutWindow(QtGui.QWidget):
    def __init__(self):
        super(AboutWindow, self).__init__()
        self.LanceABotFullLogoPixmap = QtGui.QPixmap("img/general/logos/LanceABotFullLogo.png")
        self.RobotpyLogoPixmap = QtGui.QPixmap("img/general/logos/RobotpyLogo.png")
        self.AboutWindowVBox = QtGui.QVBoxLayout()
        self.LanceABotFullLogoLabel = QtGui.QLabel()
        self.LanceABotFullLogoLabel.setPixmap(self.LanceABotFullLogoPixmap)
        self.LanceABotFullLogoLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.AboutText = QtGui.QLabel("FRC Team 4237's Dashboard\nCredits:\nMark Washington, for design and programming.\nRandy Fife, for getting me to learn Python.\nJon Woodard, for being a good programming mentor.")
        self.AboutText.setAlignment(QtCore.Qt.AlignCenter)
        self.RobotpyLogoLabel = QtGui.QLabel()
        self.RobotpyLogoLabel.setPixmap(self.RobotpyLogoPixmap.scaledToWidth(300))
        self.RobotpyLogoLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.AboutWindowVBox.addWidget(self.LanceABotFullLogoLabel)
        self.AboutWindowVBox.addWidget(self.RobotpyLogoLabel)
        self.AboutWindowVBox.addWidget(self.AboutText)
        self.setLayout(self.AboutWindowVBox)
        self.setFixedSize(400, 400)
        self.setWindowTitle("About")