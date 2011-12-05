# Installation #

To get the server ready for editing in Eclipse or running it localy you
will have to use maven on both vysper and konijn directories.

	/vysper/ Run the following command to add it to your local maven repository: mvn install
	/konijn/ Run the following command to get it ready for eclipse: mvn eclipse:eclipse

After compiling, import the GIT-projects (konijn/vysper)  into Eclipse and you should be ready to go!

# Configuration #
No real configuration is needed.

The persistence configuration (hibernnate and jpa) is located in the 
src/main/resources/META-INF directory, named persistence.xml.

Within this configuration we've set up a default in-memory database, so
there is no need to change this untill you want everything to be really
persistent.

# Tips #
The nabaztags connect to port 80, if you run the Start class it will setup the server on port 8080.
In order to get the nabaztag to connect, you have to do the following:

	- linux: Forward port 80 to 8080 (port 80 is a reserved port, hard to get eclipse/jetty to work on that);
	- windows: change the default value of 8080 in the Start class to 80.


# Features #
The current features are in:

	- Setup accounts, which can contain multiple nabaztags;
	- Logging your nabaztag onto the server;
	- Streaming radio from the internet;
	- Sending mp3's to your nabaztag (not from GUI yet).

# Todo #
Lots and lots, for starters:

	- Voice recognizion
	- Weather/rfid etc.

