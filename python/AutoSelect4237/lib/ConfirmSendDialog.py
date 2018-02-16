from PySide import QtGui, QtCore

class ConfirmSendDialog(QtGui.QWidget):
    def __init__(self):
        super(ConfirmSendDialog, self).__init__()
        self.infoList = QtGui.QPlainTextEdit()
        self.confirmButton = QtGui.QPushButton("Confirm")
        self.cancelButton = QtGui.QPushButton("Cancel")
        
        self.layout = QtGui.QGridLayout()
        self.layout.addWidget(self.infoList, 0, 0, 1, 2)
        self.layout.addWidget(self.cancelButton, 1, 0)
        self.layout.addWidget(self.confirmButton, 1, 1)
    
    def open(a=[]):
        for value in a:
            self.infoList.append(value)
        self.show()
            
    def close():
        self.infoList.clear()
        self.hide()