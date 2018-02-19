from PySide import QtGui, QtCore

class AboutWindow(QtGui.QWidget):
    def __init__(self):
        super(AboutWindow, self).__init__()
        self.lanceABotFullLogoPixmap = QtGui.QPixmap("img/general/logos/LanceABotFullLogo.png")
        self.aboutWindowVBox = QtGui.QVBoxLayout()
        self.lanceABotFullLogoLabel = QtGui.QLabel()
        self.lanceABotFullLogoLabel.setPixmap(self.lanceABotFullLogoPixmap)
        self.lanceABotFullLogoLabel.setAlignment(QtCore.Qt.AlignCenter)
        self.aboutText = QtGui.QLabel("FRC Team 4237's Dashboard\nCredits:\nMark Washington: Design and programming.\nMr. Fife: Getting me to learn Python.\nMr. Woodard: Being a great programming mentor.")
        self.aboutText.setAlignment(QtCore.Qt.AlignCenter)
        self.aboutWindowVBox.addWidget(self.lanceABotFullLogoLabel)
        self.aboutWindowVBox.addWidget(self.aboutText)
        self.setLayout(self.aboutWindowVBox)
        #self.setFixedSize(400, 400)
        self.setWindowTitle("About")