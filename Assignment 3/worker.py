import threading
import socket
import hashlib
import re

tLock = threading.Lock()
shutdown = False


def receiving (name,sock,server):
    print("I am ready to Work!")
    while not shutdown:
		try:
			tLock.acquire()
			while True:
				data,addr =sock.recvfrom(1024)
				print (str(data))
				p1=str(data[0:40])
				p2=str(data[41:])
				l=map(int, re.findall(r'\d+', p2))
				k=1
				strng=''
				word=''
				for a in range(l[0],l[1]):
					for b in range(97,123):
						for c in range(97,123):
							for d in range(97,123):
								for e in range(97,123):
									word=str(chr(a)+chr(b)+chr(c)+chr(d)+chr(e))
									strng=hashlib.sha1(word).hexdigest()
									if strng in p1:
										k=0
										break
								if k==0:
									break
							if k==0:
								break
						if k==0:
							break
					if k==0:
						break

				if int(k)==0:
					s.sendto("Word Found: "+ word,server)
				elif int(k)==1:
					s.sendto("Not",server)
		except:
			pass
		finally:
			tLock.release()

host='127.0.0.1'
port=input("Enter Port: ")

server=('127.0.0.1',5000)

s=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
s.bind((host,port))
s.setblocking(0)

rT=threading.Thread(target=receiving,args=("RecvThread",s,server))
rT.start()
message="Worker"

if message!='':
	s.sendto(message,server)