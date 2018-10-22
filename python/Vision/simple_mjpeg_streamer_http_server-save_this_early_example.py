'''
	Author: Igor Maculan - n3wtron@gmail.com
	A Simple mjpg stream http server
'''
import cv2
import threading
from http.server import BaseHTTPRequestHandler,HTTPServer
from socketserver import ThreadingMixIn
import io
import time
capture=None

class CamHandler(BaseHTTPRequestHandler):
	def do_GET(self):
		print("Path ", self.path)
		if self.path.endswith('.mjpg'):
			print(" Found .mjpg")
			self.send_response(200)
			self.send_header('Content-type','multipart/x-mixed-replace; boundary=--jpgboundary')
			self.end_headers()
			while True:
				try:
					rc,img = capture.read()
					if not rc:
						continue
					# play with image
					img = cv2.line(img,(0,0),(511,511),(255,0,0),5)
					img = cv2.rectangle(img,(384,0),(510,128),(0,255,0),3)
					img = cv2.circle(img,(447,63), 63, (0,0,255), -1)
					img = cv2.ellipse(img,(256,256),(100,50),0,0,180,255,-1)
					font = cv2.FONT_HERSHEY_SIMPLEX
					cv2.putText(img,'OpenCV',(10,500), font, 4,(255,255,255),2,cv2.LINE_AA)
					# done playing with image
					img_str = cv2.imencode('.jpg', img)[1].tostring()
#					print(len(img_str))action=stream
					self.send_header('Content-type','image/jpeg')
					self.send_header('Content-length',str(len(img_str)))
					self.end_headers()
					self.wfile.write(img_str)
					self.wfile.write(b"--jpgboundary")
					#time.sleep(0.05)
				except KeyboardInterrupt:
					break
			return
		# HTML not tested.
		if self.path.endswith('.html'):
			print("Found .html")
			self.send_response(200)
			self.send_header('Content-type','text/html')
			self.end_headers()
			self.wfile.write('<html><head></head><body>')
			self.wfile.write('<img src="http://127.0.0.1:8080/cam.mjpg"/>')
			self.wfile.write('</body></html>')
			return


class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
	"""Handle requests in a separate thread."""

def main():
	global capture
	capture = cv2.VideoCapture(0)
	print("Brightness ", capture.get(cv2.CAP_PROP_BRIGHTNESS))
	print("Exposure ", capture.get(cv2.CAP_PROP_EXPOSURE)) # default Exposure  inf try -9 to 0 and the other one has a range from 10 to 10000 both with a stepping delta of 1
	print("Exposure Auto", capture.get(cv2.CAP_PROP_AUTO_EXPOSURE)) # default Exposure  .75 valid numbers are 1 and 3 which in openCV seem to be .25 (off) and .75 (on)
	capture.set(cv2.CAP_PROP_AUTO_EXPOSURE, 0.25)
	time.sleep(.5)
	print("Exposure Auto", capture.get(cv2.CAP_PROP_AUTO_EXPOSURE)) # default Exposure  .75
#	capture.set(cv2.CAP_PROP_EXPOSURE, .01) # dark
	capture.set(cv2.CAP_PROP_EXPOSURE, .51) # medium
	time.sleep(.5)
	print("Exposure ", capture.get(cv2.CAP_PROP_EXPOSURE)) # default Exposure  inf
#	capture.set(cv2.CAP_PROP_BRIGHTNESS, .4); #1 is bright 0 or-1 is dark .4 is fairly dark default Brightness  0.5019607843137255
#	capture.set(cv2.CAP_PROP_CONTRAST, 1); 
#	capture.set(cv2.CAP_PROP_FRAME_WIDTH, 320); 
#	capture.set(cv2.CAP_PROP_FRAME_WIDTH, 320); 
#	capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 240);
#	capture.set(cv2.CAP_PROP_SATURATION,0.2);
	global img
	try:
		server = ThreadedHTTPServer(('localhost', 1181), CamHandler)
		print("server started")
		server.serve_forever()
	except KeyboardInterrupt:
		capture.release()
		server.socket.close()

if __name__ == '__main__':
	main()

# v4l2-ctl --set-ctrl=exposure_auto=1  makes almost black image


#v4l2-ctl --set-ctrl=exposure_absolute=%d --device=/dev/video1",exposureValue
'''
pi@raspberrypi:~ $ v4l2-ctl --all
Driver Info (not using libv4l2):
	Driver name   : uvcvideo
	Card type     : UVC Camera (046d:081a)pi@raspberrypi:~ $ v4l2-ctl --all
Driver Info (not using libv4l2):
	Driver name   : uvcvideo
	Card type     : UVC Camera (046d:081a)
	Bus info      : usb-3f980000.usb-1.5
	Driver version: 4.14.76
	Capabilities  : 0x84200001
		Video Capture
		Streaming
		Extended Pix Format
		Device Capabilities
	Device Caps   : 0x04200001
		Video Capture
		Streaming
		Extended Pix Format
Priority: 2
Video input : 0 (Camera 1: ok)
Format Video Capture:
	Width/Height      : 640/480
	Pixel Format      : 'YUYV'
	Field             : None
	Bytes per Line    : 1280
	Size Image        : 614400
	Colorspace        : sRGB
	Transfer Function : Default
	YCbCr/HSV Encoding: Default
	Quantization      : Default
	Flags             : 
Crop Capability Video Capture:
	Bounds      : Left 0, Top 0, Width 640, Height 480
	Default     : Left 0, Top 0, Width 640, Height 480
	Pixel Aspect: 1/1
Selection: crop_default, Left 0, Top 0, Width 640, Height 480
Selection: crop_bounds, Left 0, Top 0, Width 640, Height 480
Streaming Parameters Video Capture:
	Capabilities     : timeperframe
	Frames per second: 30.000 (30/1)
	Read buffers     : 0
                     brightness (int)    : min=0 max=255 step=1 default=-8193 value=128
                       contrast (int)    : min=0 max=255 step=1 default=57343 value=32
                     saturation (int)    : min=0 max=255 step=1 default=57343 value=32
 white_balance_temperature_auto (bool)   : default=1 value=1
                           gain (int)    : min=0 max=255 step=1 default=57343 value=0
           power_line_frequency (menu)   : min=0 max=2 default=2 value=2
      white_balance_temperature (int)    : min=0 max=10000 step=10 default=61432 value=4000 flags=inactive
                      sharpness (int)    : min=0 max=255 step=1 default=57343 value=24
         backlight_compensation (int)    : min=0 max=1 step=1 default=57343 value=1
                  exposure_auto (menu)   : min=0 max=3 default=0 value=3
              exposure_absolute (int)    : min=1 max=10000 step=1 default=166 value=166 flags=inactive
         exposure_auto_priority (bool)   : default=0 value=1
pi@raspberrypi:~ $ 

	Bus info      : usb-3f980000.usb-1.5
	Driver version: 4.14.76
	Capabilities  : 0x84200001
		Video Capture
		Streaming
		Extended Pix Format
		Device Capabilities
	Device Caps   : 0x04200001
		Video Capture
		Streaming
		Extended Pix Format
Priority: 2
Video input : 0 (Camera 1: ok)
Format Video Capture:
	Width/Height      : 640/480
	Pixel Format      : 'YUYV'
	Field             : None
	Bytes per Line    : 1280
	Size Image        : 614400
	Colorspace        : sRGB
	Transfer Function : Default
	YCbCr/HSV Encoding: Default
	Quantization      : Default
	Flags             : 
Crop Capability Video Capture:
	Bounds      : Left 0, Top 0, Width 640, Height 480
	Default     : Left 0, Top 0, Width 640, Height 480
	Pixel Aspect: 1/1
Selection: crop_default, Left 0, Top 0, Width 640, Height 480
Selection: crop_bounds, Left 0, Top 0, Width 640, Height 480
Streaming Parameters Video Capture:
	Capabilities     : timeperframe
	Frames per second: 30.000 (30/1)
	Read buffers     : 0
                     brightness (int)    : min=0 max=255 step=1 default=-8193 value=128
                       contrast (int)    : min=0 max=255 step=1 default=57343 value=32
                     saturation (int)    : min=0 max=255 step=1 default=57343 value=32
 white_balance_temperature_auto (bool)   : default=1 value=1
                           gain (int)    : min=0 max=255 step=1 default=57343 value=0
           power_line_frequency (menu)   : min=0 max=2 default=2 value=2
      white_balance_temperature (int)    : min=0 max=10000 step=10 default=61432 value=4000 flags=inactive
                      sharpness (int)    : min=0 max=255 step=1 default=57343 value=24
         backlight_compensation (int)    : min=0 max=1 step=1 default=57343 value=1
                  exposure_auto (menu)   : min=0 max=3 default=0 value=3
              exposure_absolute (int)    : min=1 max=10000 step=1 default=166 value=166 flags=inactive
         exposure_auto_priority (bool)   : default=0 value=1
pi@raspberrypi:~ $ 

v4l2-ctl --set-ctrl=exposure_auto=1 #is off in the tool and use .25 in openCV; 3 is on in the tool and use .75 in openCV

absolute exposure are also small numbers.  looks like openCV likes numbers from 0 to 1 - that is normalized. .5 is medium exposure (first turn off auto exposure)
I suspect that auto white balance also changes the appearance in the first 1 or 2 seconds.  COuld turn that off.  Maybe a value of 0?
'''

