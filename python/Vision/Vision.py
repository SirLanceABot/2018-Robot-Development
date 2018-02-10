import cv2
from grip import GripPipeline
from lib.RoboRIOTransmitter import RoboRIOTransmitter
import numpy as np
import os, time, json

r = RoboRIOTransmitter()
idNum = 0

def extra_processing(pipeline):
    global idNum
    global newLogName
    buff = []
    data = {}
    dataToSend = ""
    xsum = 0

    # Find the bounding boxes of the contours to get x, y, width, and height
    i = "0"
    for contour in pipeline.filter_contours_output:
        x, y, w, h = cv2.boundingRect(contour)
        #buff.append(x + w / 2.0)
        #buff.append(h)
        #xsum += (x + w / 2.0) + (h)
        data["idNum"] = idNum
        data["idNum"][i] = {"midpointX" : (x + w / 2.0), "height" : h}
        i = str(int(i) + 1)

    idNum += 1
    #xsum += idNum

    #buff.append(idNum)
    #buff.append(xsum)

    try:
        #table.putNumberArray('Pi TargetInfo', buff)
        r.sendMessage(json.dumps(data))
        
    except Exception as ex:
        print "Exception sending data"
        print str(ex)

    #print str(data)


def main():
    global idNum
    
    while True:
        plDone = False
        didStreamSucceed = False
        while didStreamSucceed is False:
            if plDone is False:
                try:       
                    print "Creating pipeline"
                    pipeline = GripPipeline.GripPipeline()
                    print "Pipeline created"
                    plDone = True

                except Exception as err:
                    print "Error creating pipeline: " + str(err)

            if plDone is True:
                try:
                    print "Opening Stream"
                    stream = cv2.VideoCapture('http://roborio-4237-frc.local:1181/stream.mjpg')
                    #stream = cv2.VideoCapture(1)
                    print "Stream Opened"
                    didStreamSucceed = True

                except Exception as err:
                    print "Error creating stream: " + str(err)

        print "Initialization Complete"

        while stream.isOpened():
                ret, img = stream.read()
                if ret is True:
                    pipeline.process(img)
                    if len(pipeline.filter_contours_output) >= 1 and (idNum % 10) is 0:
                        print "Writing image to usb"
                        #cv2.imwrite("/mnt/usb/" + str(idNum) + "raw.jpg", img)
                        #cv2.imwrite("/mnt/usb/" + str(idNum) + "filtered.jpg", pipeline.hsl_threshold_output)
                    extra_processing(pipeline)
                else:
                    print "Error reading from camera"
                    break
if __name__ == '__main__':
    main()
