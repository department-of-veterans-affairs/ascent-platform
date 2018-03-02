import mysql.connector
import time
import sys

keepGoing = True
while keepGoing:
	try: 
		if len(sys.argv) < 1:
			sys.exit("should provid ip commandline parameter")
		ip = sys.argv[1]
		cnx = mysql.connector.connect(user='sonar', password='sonar', host=ip, database='sonar')
		isconn = cnx.is_connected()
		if isconn:
			print("database is ready for connections")
			keepGoing = False
		cnx.close()
	except mysql.connector.Error as err:
		print("ERROR: %s" % (err))
		print("sleeping and trying again...")
		time.sleep(5)
