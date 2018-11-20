2018 used the Raspberry Pi for a camera server only since vision wasn't needed for the Autonomous and the camera server on the roboRIO failed and wasn't fixed (by NI).

Used GitHub repository jacksonliam/mjpg-streamer

The mjpg-streamer starts on Raspberry Pi boot using the /etc/init.d/.depend.start, .depend.stop (herein called dotdepend.start and stop), and livestream.sh.

The simple_mjpeg_streamer_http_server.py and others are included as an example what might be useful in the future.  They had nothing to do with the 2018 season.

Most other program files are from 2017.
