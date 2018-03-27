import threading
import socket
import time

host = '127.0.0.1'
port = 5000

WorkerClients = []
client = ('', 0)
job = ''

tLock = threading.Lock()

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind((host, port))
s.setblocking(0)

print "Server Started"

x = 97


def sendingjobs(job):
    try:
        global x
        for worker in WorkerClients:
            print str(job)
            s.sendto(job+" "+str(x)+" "+str(x+1),worker)
            x=x+1
    except:
        pass

while True:
    try:
        data, addr = s.recvfrom(1024)
        tLock.acquire()
        print str(data)
        if "Worker" in str(data):
            WorkerClients.append(addr)
            print("Worker Client Added")

        elif "Client" in str(data):
            print("Client Arrived, job to do is "+str(data[7:]))
            job = str(data[7:])
            client = addr
            if len(WorkerClients) == 0:
                s.sendto("No Worker Client yet", client)
            sendingjobs(job)
        elif "Word Found: " in str(data):
            s.sendto(str(data), client)
            data = ''
            x = 97
        elif "Not" in str(data):
            sendingjobs(job)
        tLock.release()
    except:
        pass

s.close()
