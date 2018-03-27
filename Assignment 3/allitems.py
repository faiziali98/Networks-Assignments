fob=open('comb.txt','w')
n=0
k=1
nl = "\n"
for a in range(97,122):
	for b in range(97,122):
		for c in range(97,122):
			for d in range(97,122):
				for e in range(97,122):
					n+=1
					fob.writelines(str(chr(a)+chr(b)+chr(c)+chr(d)+chr(e)))
					fob.writelines(nl)
					print (str(chr(a)+chr(b)+chr(c)+chr(d)+chr(e)))
					if chr(a)=='b' and chr(b)=='b' and chr(c)=='b' and chr(d)=='b' and chr(e)=='b':
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

print ("Total words = "+str(n))