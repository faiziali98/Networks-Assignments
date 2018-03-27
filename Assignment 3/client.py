import threading
import socket
import time
import hashlib

tLock=threading.Lock()

host='127.0.0.1'
port=input("Enter Port: ")

server=('127.0.0.1',5000)

s=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
s.bind((host,port))
s.setblocking(0)

message=("Client")
word=raw_input("Enter Word: ")
strng=hashlib.sha1(word).hexdigest()

if message!='':
	s.sendto(message+" "+strng,server)

print ("Waiting for response....")
while True:
	try:
		data,addr=s.recvfrom(1024)
		print (str(data))
		break
	except:
		pass

s.close()
