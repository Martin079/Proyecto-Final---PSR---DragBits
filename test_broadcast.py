import socket, json
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
s.sendto(b'{"type":"DISCOVER"}', ('255.255.255.255', 54321))
s.settimeout(3)
try:
    data, addr = s.recvfrom(1024)
    print('Respuesta de', addr, ':')
    print(json.dumps(json.loads(data.decode()), indent=2))
except Exception as e:
    print('Sin respuesta:', e)