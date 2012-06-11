from pyPgSQL import PgSQL
import time
import random

db = PgSQL.connect(host='localhost', database='iritgo', user='iritgo', password='iritgo')
c = db.cursor()

N = 1000

def test1():
    start = time.time()
    for i in range(N):
	id = random.randint(100000, 200000)
	c.execute("select * from phonenumber where internalnumber like '%%%06d'" % id)
	end = time.time()
    print("%f" % (((end - start) / N) * 1000.0))

def test2():
    start = time.time()
    for i in range(N):
	id = random.randint(100000, 200000)
	c.execute("select * from phonenumber where internalnumber like '888%5d%%'" % (id/10))
	end = time.time()
    print("%f" % (((end - start) / N) * 1000.0))

test2()
