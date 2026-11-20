import socket, json, threading, time

s = socket.socket()
s.connect(('127.0.0.1', 54322))

def recv():
    f = s.makefile('r')
    while True:
        line = f.readline()
        if not line: break
        try:
            msg = json.loads(line.strip())
            print('<<', json.dumps(msg, indent=2)[:200])
        except:
            print('<<', line.strip()[:200])

threading.Thread(target=recv, daemon=True).start()

# 1. JOIN
join_msg = {"type": "JOIN", "playerName": "Jugador1", "selectedCar": 2}
s.send((json.dumps(join_msg) + '\n').encode())
time.sleep(0.5)

# 2. READY
ready_msg = {"type": "READY"}
s.send((json.dumps(ready_msg) + '\n').encode())

# Mantener vivo y mostrar mensajes
try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    print("\nCerrando...")
    s.close()