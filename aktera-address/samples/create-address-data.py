#!/usr/bin/python

from pyPgSQL import PgSQL

db = PgSQL.connect(host='localhost', database='iritgo', user='iritgo', password='iritgo')
c = db.cursor ()
print c
for i in range (1000, 10000):
    print i
    c.execute ("insert into address (category, lastname) values ('G', '" + str(i) + "')")
    c.execute ("select id from address where lastname = '" + str(i) + "'")
    res = c.fetchone ()
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'P', '111" + str(i) + "', '111" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'PM', '222" + str(i) + "', '222" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'PF', '333" + str(i) + "', '333" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'B', '444" + str(i) + "', '444" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'BM', '555" + str(i) + "', '555" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'BF', '666" + str(i) + "', '666" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'BDD', '777" + str(i) + "', '777" + str(i) + "')")
    c.execute ("insert into phonenumber (addressid, category, number, internalnumber) values (" + str(res[0]) + ", 'VOIP', '888" + str(i) + "', '888" + str(i) + "')")
    db.commit ()
db.close ()
